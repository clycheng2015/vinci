package com.lewis.lib_vinci.coreframe.taskmanager;

import android.util.Log;

import com.lewis.lib_vinci.BuildConfig;


import java.util.LinkedList;

/**
 * 随app启动一起执行的异步任务调度器。<br>
 * 
 * 因为我们app启动会有很多任务需要异步线程执行，但是为了减少对app启动速度的影响（减小启动过程中cpu消耗），
 * 都采用延时方式。
 * 
 * 但是延时方式有个弊端，不同性能的设备延时多少也不合适。
 * 另外第一次安装，有引导界面，第一次启动速度也慢，导致延时时间相对较短，在app还没有初始化完毕，这些异步线程
 * 在预期之前执行，过多占用cpu。
 * 
 * 为此我们设计该类，app初始化完毕后才开始执行这些异步任务，而不用考虑到底延时多少的问题。<br>
 * 
 * app 需要在合适的地方调用 {@link #appReady()}，此时会安排队列中的任务开始顺序执行。
 * 如果app不调用 {@link #appReady()}, 则默认 {@link #GURANTEE_DELAY_MS} 毫秒以后执行。 
 * 
 * @since 2013-1-22
 */
public final class LaunchTaskExecutor {
    /**
     * debug 开关。
     */
    private static final boolean DEBUG = BuildConfig.DEBUG;
    
    /**
     * Logcat tag.
     */
    private static final String TAG = "LaunchTaskExecutor";
    
    /**
     * 排队等候需要执行的消息队列。
     */
    private static LinkedList<Task> sQueue = new LinkedList<Task>();
    
    /**
     * app 是否已经初始化完毕，完毕后才安排执行 {@link #sQueue} 中的任务。
     */
    private static boolean sAppReady = false;
    
    /**
     * 为了防止app忘记调用 {@link #appReady()}, 而导致任务执行。我们建立一个保证任务。
     * 30秒以后如果app没有{@link #appReady()}, 我们自动执行。
     */
    
    private static final long GURANTEE_DELAY_MS = 30 * 1000;
    
    /**
     * {@link #sGuaranteeRunnable} 只需要执行一次，用来标记是否已经安排执行。
     */
    private static boolean sGuaranteeRunnableScheduled = false;
    
    private static TaskManager mTskMgr = null;
    
    private static final String LAUNCH_TASK_KEY = "LaunchTask";
    /**
     * @see #GURANTEE_DELAY_MS
     */
    private static Runnable sGuaranteeRunnable = new Runnable() {
        @Override
        public void run() {
            appReady(true);
        }
    };
    
    
    /** 工具类，私有化构造函数。 */
    private LaunchTaskExecutor() {
        mTskMgr = TaskManager.getInstance("LaunchTaskManager");
    }
 
    /**
     * 标记app初始化完毕，能够在不影响自身启动（抢占cpu资源）的前提下执行异步任务。
     * 但是还有一个情况。当app只是推出Activity，但是进程没有杀死。
     * 等一下次Activity进入的时候静态变量标记为已经ready，任务就会立刻执行。所以还是有问题的。
     * 
     * 这时候需要一个 notready，把状态给位 notready。 随后等初始化完毕后再次标记为 ready。
     * 
     * 建议在 Activity onCreae最开始 标记为 notready。
     * 
     * @param readyOrNot 参考函数说明。
     * 
     * 
     */
    public static synchronized void appReady(boolean readyOrNot) {
        
        if (DEBUG) {
            Log.d(TAG, "appReady or not : " + readyOrNot);
        }
        
        if (!readyOrNot) {
            sAppReady = false;
            sGuaranteeRunnableScheduled = false;
            return;
        }
        
        if (sAppReady) {
            if (DEBUG) {
                Log.d(TAG, "appReady: already ready.. return.");
            }
            return;
        }
        
        sAppReady = true;
        
        // 执行等待队列中的任务。
        while (true) {
            Task task = sQueue.poll();
            
            if (task != null && task.mRunnable != null) {
                if (DEBUG) {
                    Log.d(TAG, "execute task : " + task.mTaskKey);
                }
                mTskMgr.addTask(task, LAUNCH_TASK_KEY);
            } else {
                // 队列为空，中断循环。
                break;
            }
        }
    }
    /**
     * 执行随 app 启动的异步任务，如果app还没有初始化完毕{@link #appReady()}，任务暂时直接放到等待队列。
     * 如果app已经初始化完毕，则直接安排通过 {@link AsyncTaskAssistant#execute(Runnable)} 执行，顺序执行。
     * 
     * @param runnable 需要执行的 runnable 任务
     * @param taskName 任务名字，目前只用来调试，log输出作用。
     */
    public static synchronized void execute(Runnable runnable, String taskName) {
        execute(runnable, taskName, 0L);
    }
    
    /**
     * 执行随 app 启动的异步任务，如果app还没有初始化完毕{@link #appReady()}，任务暂时直接放到等待队列。
     * 如果app已经初始化完毕，则直接安排通过 {@link AsyncTaskAssistant#execute(Runnable)} 执行，顺序执行。
     * 
     * @param runnable 需要执行的 runnable 任务
     * @param taskName 任务名字，目前只用来调试，log输出作用。
     * @param delay 任务延迟时间（毫秒）
     */
    public static synchronized void execute(Runnable runnable, String taskName, long delay) {
        if (sAppReady) {
            
            if (DEBUG) {
                Log.d(TAG, "app is ready, execute task :" + taskName);
            }
   
            Task tsk = new Task(delay, 0, true, taskName, runnable);
            mTskMgr.addTask(tsk, LAUNCH_TASK_KEY); 
        } else {
            Task tsk = new Task(delay, 0, true, taskName, runnable);
            
            if (DEBUG) {
                Log.d(TAG, "app not ready, add task to queue : " + taskName);
            }
            
            sQueue.add(tsk);
            
            /*
             * 为了防止app忘记调用 appReady() 函数，我们设计一个最后保证执行的任务。
             */
            if (!sGuaranteeRunnableScheduled) {
                sGuaranteeRunnableScheduled = true;
 
                mTskMgr.addTask(
                        new Task(GURANTEE_DELAY_MS, 0, true, taskName, sGuaranteeRunnable),
                        LAUNCH_TASK_KEY);
            }
        }
    }
}
