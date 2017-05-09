/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.coreframe.eventbus;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.lewis.lib_vinci.BuildConfig;
import com.lewis.lib_vinci.coreframe.eventbus.EventBus.Event;
import com.lewis.lib_vinci.coreframe.eventbus.EventBus.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/** EventBus 所有逻辑实现和数据结构管理
 * 
 * @author yulei
 * @since 2014/10/14
 */
public class EventBusController {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = EventBusController.class.getSimpleName();
    /** 存放sticky events */
    private final Map<String, EventBus.Event> mStickyEvents;

    /** poster 决定了在那个线程调用subscriber的事件处理函数*/
    private final HandlerPoster mMainPoster;
    private final AsyncPoster mAsyncPoster;

    /** 注册一个event的所有Subscriptions, eventKey作为map key */
    private final Map<String, CopyOnWriteArrayList<Subscription>> mSubscriptionsByEvent;
    /** 一个subscriber 注册的所有event type, subscriber 作为map key */
    private final Map<Object, List<String>> mEventsBySubscriber;
    /** 根据subscriber Class来获得subscriber 的事件处理方法 */
    private final SubscriberMethodFinder mSubscriberMethodFinder;

    /** 线程独有*/
    private final ThreadLocal<PostingThreadState> mCurrentPostingThreadState = new ThreadLocal<PostingThreadState>() {
        @Override
        protected PostingThreadState initialValue() {
            return new PostingThreadState();
        }
    };

    /** 构造函数
     */
    public EventBusController() {
        mSubscriptionsByEvent = new HashMap<String, CopyOnWriteArrayList<Subscription>>();
        mEventsBySubscriber = new HashMap<Object, List<String>>();
        mStickyEvents = new ConcurrentHashMap<String, Event>();
        mMainPoster = new HandlerPoster(this, Looper.getMainLooper(), 10);
        mAsyncPoster = new AsyncPoster(this);
        mSubscriberMethodFinder = new SubscriberMethodFinder();
    }

    /** 向event bus 注册一系列事件
     * 
     * @param subscriber 注册的subscriber对象
     * @param eventKeys 注册的事件key
     * @param priority 注册的优先级
     * @param sticky 是否注册sticky事件
     * @param mode poster要调用subscriber事件处理函数所在的线程
     */
    public synchronized void register(Object subscriber, String[] eventKeys, int priority,
                                      boolean sticky, ThreadMode mode) {
        Method subscriberMethod = mSubscriberMethodFinder.findSubscriberMethods(subscriber.getClass());
        subscribe(subscriber, subscriberMethod, sticky, priority, eventKeys, mode);
    }
    
    /** 向event bus 注册一系列事件
     * 
     * @param subscriber 注册的subscriber对象
     * @param eventKey 注册的事件key
     * @param priority 注册的优先级
     * @param sticky 是否注册sticky事件
     * @param mode poster要调用subscriber事件处理函数所在的线程
     */
    public synchronized void register(Object subscriber, String eventKey, int priority,
                                      boolean sticky, ThreadMode mode) {
        Method subscriberMethod = mSubscriberMethodFinder.findSubscriberMethods(subscriber.getClass());
        subscribe(subscriber, subscriberMethod, sticky, priority, eventKey, mode);
    }
    
    private void subscribe(Object subscriber, Method subscriberMethod, boolean sticky, int priority, String[] eventKeys,
                           ThreadMode threadMode) {
        for (String eventKey : eventKeys) {
            subscribe(subscriber, subscriberMethod, sticky, priority, eventKey, threadMode);
        }
    }
    private void subscribe(Object subscriber, Method subscriberMethod, boolean sticky, int priority, String eventKey,
                           ThreadMode threadMode) {
        CopyOnWriteArrayList<Subscription> subscriptions = mSubscriptionsByEvent.get(eventKey);

        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<Subscription>();
            mSubscriptionsByEvent.put(eventKey, subscriptions);
        } else {
            for (Subscription subscription : subscriptions) {
                if (subscription.mSubscriber.equals(subscriber)) {
                    if (DEBUG) {
                        Log.d(TAG, "Subscriber " + subscriber.getClass() + " already registered to event " + eventKey);
                    }

                    return;
                }
            }
        }
        Subscription newSubscription = new Subscription(subscriber, subscriberMethod, eventKey, priority, threadMode);
        //按优先级排序
        int size = subscriptions.size();
        if (size > 0) {
            for (int i = 0; i <= size; i++) {
                if (i == size || newSubscription.mPriority > subscriptions.get(i).mPriority) {
                    subscriptions.add(i, newSubscription);
                    break;
                }
            }
        } else {
            subscriptions.add(newSubscription);
        }

        List<String> subscribedEvents = mEventsBySubscriber.get(subscriber);
        if (subscribedEvents == null) {
            subscribedEvents = new ArrayList<String>();
            mEventsBySubscriber.put(subscriber, subscribedEvents);
        }
        subscribedEvents.add(eventKey);

        //sticky事件会在注册的时候获取
        if (sticky) {
            Event stickyEvent;
            synchronized (mStickyEvents) {
                stickyEvent = mStickyEvents.get(eventKey);
            }
            if (stickyEvent != null) {
                postToSubscription(newSubscription, stickyEvent, Looper.getMainLooper() == Looper.myLooper());
            }
        }
    }
 
    /** 向event bus 取消注册所有事件
     * 
     * @param subscriber 注册的subscriber对象
     */
    public synchronized void unregister(Object subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("Provide at least one event class");
        }
        List<String> events = mEventsBySubscriber.get(subscriber);
        if (events != null) {
            for (String eventName : events) {
                unubscribeByEventKey(subscriber, eventName);
            }
            mEventsBySubscriber.remove(subscriber);
        } else {
            Log.w(TAG, "Subscriber to unregister was not registered before: " + subscriber.getClass());
        }
    }

    /** 向event bus 取消注册一个事件
     * 
     * @param subscriber 注册的subscriber对象
     * @param eventKey 注册的事件key
     */
    public synchronized void unregister(Object subscriber, String eventKey) {
        if (TextUtils.isEmpty(eventKey)) {
            throw new IllegalArgumentException("Provide at least one event class");
        }
        unubscribeByEventKey(subscriber, eventKey);
    }

    private void unubscribeByEventKey(Object subscriber, String eventKey) {
        List<Subscription> subscriptions = mSubscriptionsByEvent.get(eventKey);
        if (subscriptions != null) {
            int size = subscriptions.size();
            for (int i = 0; i < size; i++) {
                Subscription subscription = subscriptions.get(i);
                if (subscription.mSubscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
            }
        }
    }

    /** 向event bus post一个事件
     * 
     * @param event 一个event的数据结构,包括eventKey和一个eventObj
     */
    public void post(Event event) {
        PostingThreadState postingState = mCurrentPostingThreadState.get();

        if (postingState.isPosting) {
            return;
        } else {
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if (postingState.canceled) {
                postingState.isPosting = false;
                if (DEBUG) {
                    Log.d(TAG, "Event has already been cancelled");
                }
                return;
            }
            try {
                postSingleEvent(event, postingState);
            } finally {
                postingState.isPosting = false;
                postingState.isMainThread = false;
            }
        }
    }

    /** 向event bus post一个sticky事件
     * 
     * @param event 一个event的数据结构,包括eventKey和一个eventObj
     */
    public void postSticky(Event event) {
        synchronized (mStickyEvents) {
            mStickyEvents.put(event.mEventKey, event);
        }
        // Should be posted after it is putted, in case the subscriber wants to remove immediately
        post(event);
    }

    /**
     * cancel事件
     * @param event 事件
     */
    public void cancelEventDelivery(Event event) {
        PostingThreadState postingState = mCurrentPostingThreadState.get();
        if (!postingState.isPosting) {
            throw new EventBusException(
                    "This method may only be called from inside event handling methods on the posting thread");
        } else if (event == null) {
            throw new EventBusException("Event may not be null");
        } else if (postingState.event != event) {
            throw new EventBusException("Only the currently handled event may be aborted");
        }  

        postingState.canceled = true;
    }
 
    /**
     * remove所有sticky event 
     * @param eventKey eventKey
     */
    public void removeStickyEvent(String eventKey) {
        synchronized (mStickyEvents) {
            mStickyEvents.remove(eventKey);
        }
    }

    /**
     * Removes all sticky events.
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEvents) {
            mStickyEvents.clear();
        }
    }

    private void postSingleEvent(Event event, PostingThreadState postingState) throws Error {
        boolean subscriptionFound = false;

        CopyOnWriteArrayList<Subscription> subscriptions;
        synchronized (this) {
            subscriptions = mSubscriptionsByEvent.get(event.mEventKey);
        }
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                postingState.event = event;
                postingState.subscription = subscription;
                boolean aborted = false;
                try {
                    postToSubscription(subscription, event, postingState.isMainThread);
                    aborted = postingState.canceled;
                } finally {
                    postingState.event = null;
                    postingState.subscription = null;
                    postingState.canceled = false;
                }
                if (aborted) {
                    break;
                }
            }
            subscriptionFound = true;
        }
        if (!subscriptionFound) {
            if (DEBUG) {
                Log.d(TAG, "No subscribers registered for event " + event.mEventKey);
            } 
        }
    }

    /** 
     * 事件分发至subscriptor而处理
     */
    private void postToSubscription(Subscription subscription, Event event, boolean isMainThread) {
        switch (subscription.mThreadMode) {
            case PostThread:
                invokeSubscriber(subscription, event);
                break;
            case MainThread:
                if (isMainThread) {
                    invokeSubscriber(subscription, event);
                } else {
                    mMainPoster.enqueue(subscription, event);
                }
                break;
            case Async:
                mAsyncPoster.enqueue(subscription, event);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + subscription.mThreadMode);
        }
    }

    /** 
     * async方式调用subscriber处理
     * @param  pendingPost pendingPost
     */
    protected void invokeSubscriber(PendingPost pendingPost) {
        Event event = pendingPost.event;
        Subscription subscription = pendingPost.subscription;
        if (subscription.active) {
            invokeSubscriber(subscription, event);
        }
    }

    /** 
     * raw调用事件处理回掉逻辑
     */
    private void invokeSubscriber(Subscription subscription, Event event) throws Error {
        try {
            subscription.mSubscriberMethod.invoke(subscription.mSubscriber, event);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Log.e(TAG, "Could not dispatch event: " + event.getClass() + " to subscribing class "
                    + subscription.mSubscriber.getClass(), cause);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unexpected exception", e);
        }
    }

    /** post 事件的线程specific 变量*/
    static final class PostingThreadState {
        /** 是否在post状态*/
        boolean isPosting;
        /** 是否是main thread*/
        boolean isMainThread;
        /** 当前处理事件的subscription*/
        Subscription subscription;
        /** post 线程当前post的事件, 可被取消*/
        Event event;
        boolean canceled;
    }
}