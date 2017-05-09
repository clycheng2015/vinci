package com.lewis.lib_vinci.coreframe.eventbus;


import com.lewis.lib_vinci.coreframe.taskmanager.Task;
import com.lewis.lib_vinci.coreframe.taskmanager.TaskManager;
import com.lewis.lib_vinci.coreframe.eventbus.EventBus.Event;

/**
 * 用于不同activity的线程,或者两个完全独立线程之间的事件通信
 * 
 * @author CLY
 * @since 2014/10/14
 */
class AsyncPoster implements Runnable {

    private final PendingPostQueue mPostQueue;

    private final EventBusController mEbController;

    AsyncPoster(EventBusController eventBusController) {
        mEbController = eventBusController;
        mPostQueue = new PendingPostQueue();
    }

    public void enqueue(Subscription subscription, Event event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        mPostQueue.enqueue(pendingPost);
        TaskManager tskMgr = TaskManager.getInstance("EBTaskManager");
        Task tsk = new Task(0, 0, false, "AsyncPost" + "_" + System.currentTimeMillis(), this);
        tskMgr.addTask(tsk, "AsyncPost");
    }

    @Override
    public void run() {
        PendingPost pendingPost = mPostQueue.poll();
        if (pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        mEbController.invokeSubscriber(pendingPost);
    }

}
