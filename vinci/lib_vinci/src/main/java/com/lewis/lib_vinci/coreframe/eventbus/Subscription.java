/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.coreframe.eventbus;


import com.lewis.lib_vinci.coreframe.eventbus.EventBus.ThreadMode;

import java.lang.reflect.Method;


/** 事件注册类, 代表一次注册
 * 
 * @author yulei 
 * @since 2014/10/13
 */
final class Subscription {
    /** 注册类实例 */
    final Object mSubscriber;
    /** 注册方法 */
    final Method mSubscriberMethod;
    /** 优先级 */
    final int mPriority;
    /** 注册的事件名 */
    final String mEventKey;
    /**subscriber 执行事件回调所在的线程*/
    final EventBus.ThreadMode mThreadMode;

    /**
     * EventBus#unregister 的时候置为false, 在事件delivery的时候会check
     */
    volatile boolean active;

    Subscription(Object subscriber, Method subscriberMethod, String eventName, int priority, ThreadMode threadMode) {
        this.mSubscriber = subscriber;
        this.mSubscriberMethod = subscriberMethod;
        this.mEventKey = eventName;
        this.mPriority = priority;
        this.mThreadMode = threadMode;
        active = true;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Subscription) {
            Subscription otherSubscription = (Subscription) other;
            return mSubscriber == otherSubscription.mSubscriber
                    && mSubscriberMethod.equals(otherSubscription.mSubscriberMethod);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return mSubscriber.hashCode() + mSubscriberMethod.hashCode();
    }
}