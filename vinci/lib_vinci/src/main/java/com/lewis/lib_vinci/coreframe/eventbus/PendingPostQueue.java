/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.coreframe.eventbus;


import java.util.LinkedList;

/**
 * 先进先出queue数据结构,存放pendingpost,用于接收线程处理
 * 
 * @author yulei
 * @since 2014/10/14
 */
final class PendingPostQueue {
    private final LinkedList<PendingPost> mPostQueue = new LinkedList<PendingPost>();

    synchronized void enqueue(PendingPost pendingPost) {
        if (pendingPost == null) {
            throw new NullPointerException("null cannot be enqueued");
        }
        mPostQueue.offer(pendingPost);
        notifyAll();
    }

    synchronized PendingPost poll() {
        return mPostQueue.poll();
    }

    synchronized PendingPost poll(int maxMillisToWait) throws InterruptedException {
        PendingPost post = poll();
        if (post == null) {
            wait(maxMillisToWait);
            post = poll();
        }

        return post;
    }

}
