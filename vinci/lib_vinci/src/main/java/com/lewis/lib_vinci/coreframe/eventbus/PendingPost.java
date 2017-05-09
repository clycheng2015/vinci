/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.coreframe.eventbus;

import com.lewis.lib_vinci.coreframe.eventbus.EventBus.Event;


/**
 * PendingPost 要post的event, 和调用subscriber的什么事件处理方法
 * 
 * @author yulei
 * @since 2014/10/13
 */
final class PendingPost {
    /** 要post的事件 */
    Event event;
    /** 注册该事件的 subscription*/
    Subscription subscription;

    private PendingPost(Event ev, Subscription subscrib) {
        this.event = ev;
        this.subscription = subscrib;
    }

    static PendingPost obtainPendingPost(Subscription subscription, Event event) {
        return new PendingPost(event, subscription);
    }
}