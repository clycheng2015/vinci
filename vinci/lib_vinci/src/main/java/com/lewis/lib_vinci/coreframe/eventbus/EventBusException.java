/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.coreframe.eventbus;

/**
 * EventBusException
 * 
 */
public class EventBusException extends RuntimeException {

    private static final long serialVersionUID = -2912559384646531479L;

    /** 构造函数
     * @param detailMessage detailMessage
     */
    public EventBusException(String detailMessage) {
        super(detailMessage);
    }

    /** 构造函数
     * @param throwable throwable
     */
    public EventBusException(Throwable throwable) {
        super(throwable);
    }

    /** 构造函数
     * @param detailMessage detailMessage
     * @param throwable throwable
     */
    public EventBusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
