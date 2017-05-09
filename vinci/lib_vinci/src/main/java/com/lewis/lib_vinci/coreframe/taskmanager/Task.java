/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.coreframe.taskmanager;

import android.text.TextUtils;


/**
 * 要执行的任务
 * 
 * @author yulei
 * @since 2014-3-20
 */
public class Task  {
    /** 如果提供，可防止相同task加入多次.*/
    String mTaskKey;
    /** 执行runnable.*/
    Runnable mRunnable;
    long mPeriod;
    long mDelay;
    boolean mIsSerial;
    long mNextRunTime;

    /**
     * 任务构造器
     *  
     * @param delay 任务要延迟执行的时间
     * @param period 任务要周期执行的周期
     * @param isSerial 任务是否要串行执行
     * @param taskKey 任务的key
     * @param r 任务要执行的runnable 
     */
    public Task(long delay, long period, boolean isSerial, String taskKey, Runnable r) {
        mDelay = delay;
        mNextRunTime = System.currentTimeMillis() + delay;
        mPeriod = period;
        mIsSerial = isSerial;
        mRunnable = r;
        mTaskKey = taskKey;
    }
 
    @Override
    public boolean equals(Object other) {
        if (other instanceof Task) {
            Task otherTask = (Task) other;
            return (TextUtils.equals(mTaskKey, otherTask.mTaskKey));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return mTaskKey.hashCode() + mRunnable.hashCode();
    }
}