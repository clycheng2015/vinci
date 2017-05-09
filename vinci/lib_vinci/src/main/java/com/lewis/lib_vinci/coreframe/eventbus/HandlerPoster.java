/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.coreframe.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.lewis.lib_vinci.coreframe.eventbus.EventBus.Event;

/**
 * 用于一个activity的非主线程和主线程的事件通信
 * 
 * @author yulei
 * @since 2014/10/14
 */
final class HandlerPoster extends Handler {

    private final PendingPostQueue mPostQueue;
    private final int maxMillisInsideHandleMessage;
    private final EventBusController mEbController;
    private boolean handlerActive;

    HandlerPoster(EventBusController eventBusController, Looper looper, int maxMillis) {
        super(looper);
        this.mEbController = eventBusController;
        this.maxMillisInsideHandleMessage = maxMillis;
        mPostQueue = new PendingPostQueue();
    }

    void enqueue(Subscription subscription, Event event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            mPostQueue.enqueue(pendingPost);
            if (!handlerActive) {
                handlerActive = true;
                if (!sendMessage(obtainMessage())) {
                    throw new EventBusException("Could not send handler message");
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        boolean rescheduled = false;
        try {
            long started = SystemClock.uptimeMillis();
            while (true) {
                PendingPost pendingPost = mPostQueue.poll();
                if (pendingPost == null) {
                    synchronized (this) {
                        // Check again, this time in synchronized
                        pendingPost = mPostQueue.poll();
                        if (pendingPost == null) {
                            handlerActive = false;
                            return;
                        }
                    }
                }
                mEbController.invokeSubscriber(pendingPost);
                long timeInMethod = SystemClock.uptimeMillis() - started;
                if (timeInMethod >= maxMillisInsideHandleMessage) {
                    if (!sendMessage(obtainMessage())) {
                        throw new EventBusException("Could not send handler message");
                    }
                    rescheduled = true;
                    return;
                }
            }
        } finally {
            handlerActive = rescheduled;
        }
    }
}