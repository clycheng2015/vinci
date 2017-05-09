package com.lewis.lib_vinci.coreframe.taskmanager;

import android.text.TextUtils;
import android.util.Log;

import com.lewis.lib_vinci.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务管理类，支持以下特性:
 *
 * <p>1. 支持任务周期定时执行
 * <p>2. 支持任务延迟执行
 * <p>2. 支持任务串行阻塞执行
 * <p>3. 支持未执行的任务被取消
 *
 * @author lewis
 * @since 2017-2-20
 */
public final class TaskManager {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = TaskManager.class.getSimpleName();
    
    /** 任务管理器map, 一个应用程序可能有多个任务管理器,比如用于图片下载的,用于业务层面网络请求的等 */
    public static HashMap<String, TaskManager> mTskMgrMap = new HashMap<String, TaskManager>();
    /** 时间片，每次执行都会把此时间内的任务一次执行. 因此如果是周期任务，此数值是最小周期, 此值可根据实际情况调节*/
    public static final long TIMESLICE = 1000 * 1;
    /** 默认调度周期.*/
    private static final long DEFAULT_SCHEDULE_TIME = Long.MAX_VALUE;
    /** 下次调度时间.*/
    private long mNextScheduleTime = DEFAULT_SCHEDULE_TIME;
    /** 定时器*/
    private Timer mTaskTimer = new Timer();
    private static TaskManager mTaskMgr = null;
    private final HashMap<String, ArrayList<Task>> mKeyTaskList;
    private final HashMap<Runnable, Future<?>> mFutureMap = new HashMap<Runnable, Future<?>>();
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 1;

    private final BlockingQueue<Runnable> mPoolQueue = new LinkedBlockingQueue<Runnable>(10);

    private final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "CredooTask #" + mCount.getAndIncrement());
        }
    };

    /** 线程池并行执行器 */
    private final ParallelExecutor mPoolExecutor = new ParallelExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, mPoolQueue, mThreadFactory);

    private class ParallelExecutor extends ThreadPoolExecutor {

        public ParallelExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            Future<?> futuretsk = mFutureMap.get(r);
            if (futuretsk != null) {
                mFutureMap.remove(r);
            }
            super.afterExecute(r, t);
        }
    }

    /** 串行执行器*/
    private final SerialExecutor mSerialExecutor = new SerialExecutor();

    private class SerialExecutor implements Executor {
        final LinkedList<Runnable> mRunnables = new LinkedList<Runnable>();
        Runnable mActive;

        @Override
        public synchronized void execute(final Runnable r) {
            mRunnables.offer(new Runnable() {
                @Override
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mRunnables.poll()) != null) {
                mPoolExecutor.execute(mActive);
            }
        }

        public synchronized void cancel(final Runnable r) {
            mRunnables.remove(r);
        }
    }

    /**
     * 私有构造函数
     */
    private TaskManager() {
        mKeyTaskList = new HashMap<String, ArrayList<Task>>();
    }

    /**
     * 获取TaskMananger单例方法
     * @param tskMgrName 任务管理器名
     * @return TaskManager
     */
    public static synchronized TaskManager getInstance(String tskMgrName) {
        if (mTskMgrMap.get(tskMgrName) == null) {
            mTaskMgr = new TaskManager();
            mTskMgrMap.put(tskMgrName, mTaskMgr);
        }
        return mTaskMgr;
    }

    /**
     * 添加一个要执行的任务
     *  
     * @param task 要添加的任务
     * @param key 要添加的任务的key
     * @return true: 增加成功
     */
    public boolean addTask(Task task, String key) {
        if (task == null || task.mRunnable == null) {
            return false;
        }
        synchronized (mKeyTaskList) {
            ArrayList<Task> tasklist = getTaskListByKey(key);
            if (!hasTask(task, tasklist)) {
                if (task.mNextRunTime <= System.currentTimeMillis()) {
                    // 时间已过,立即执行
                    executeTask(task);
                } else if (task.mDelay > 0) {
                    executeTask(task);
                }
                if (task.mPeriod > 0) {
                    task.mNextRunTime = System.currentTimeMillis() + task.mDelay + task.mPeriod;
                    tasklist.add(task);
                    updateAlarm(task);
                }
            } else {
                updateTask(task, key);
            }
            return true;
        }
    }

    /**
     * 取消所有任务的执行
     * @param key 要取消执行的任务的key
     */
    public void cancelAllTasks(String key) {
        synchronized (mKeyTaskList) {
            ArrayList<Task> tasklist = getTaskListByKey(key);
            for (Task tsk : tasklist) {
                cancelTask(tsk);
            }
            tasklist.clear();
            tasklist = null;
            mKeyTaskList.remove(key);
        }
    }

    /**
     * 通过taskKey获取Task
     * @param taskKey String
     * @param taskList tasklist
     * @return task
     */
    private Task getTask(String taskKey, ArrayList<Task> taskList) {
        for (Task task : taskList) {
            if (TextUtils.equals(taskKey, task.mTaskKey)) {
                return task;
            }
        }
        return null;
    }

    private ArrayList<Task> getTaskListByKey(String key) {
        ArrayList<Task> taskList = mKeyTaskList.get(key);
        if (taskList == null) {
            taskList = new ArrayList<Task>();
        }
        mKeyTaskList.put(key, taskList);
        return taskList;
    }

    /**
     * 更新task
     * @param task {@link Task}
     * @return true: 更新成功
     */
    private boolean updateTask(Task task, String key) {
        if (task == null || task.mRunnable == null) {
            return false;
        }

        synchronized (mKeyTaskList) {
            ArrayList<Task> tasklist = getTaskListByKey(key);
            Task t = getTask(task.mTaskKey, tasklist);
            if (t != null) {
                cancelTask(t);
                tasklist.remove(t);
                addTask(task, key);
                return true;
            }
        }
        return false;
    }

    /**
     * 根据参数task，更新alarm调度时间
     * @param task {@link  }
     */
    private void updateAlarm(Task task) {
        if (task.mNextRunTime < mNextScheduleTime) {
            startAlarm(Math.max(task.mNextRunTime - System.currentTimeMillis(), TIMESLICE));
        }
    }

    /**
     * 启动alarm
     * @param intervalMillis 在多长时间后回调   
     */
    private void startAlarm(long intervalMillis) {
        if (DEBUG) {
            Log.d(TAG, "intervalMillis: " + intervalMillis);
        }

        if (mTaskTimer != null) {
            mTaskTimer.cancel();
            mTaskTimer = null;
        }
        mTaskTimer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                scheduleForPeriodTasks();
            }

        };
        mTaskTimer.schedule(timerTask, intervalMillis);

    }

    /**
     * 任务调度执行.
     */
    private void scheduleForPeriodTasks() {
        if (DEBUG) {
            Log.d(TAG, "scheduleForPeriodTasks run");
        }
        synchronized (mKeyTaskList) {
            long current = System.currentTimeMillis();
            mNextScheduleTime = DEFAULT_SCHEDULE_TIME;
            Iterator<String> iterator = mKeyTaskList.keySet().iterator();

            while (iterator.hasNext()) {
                String key = iterator.next();
                ArrayList<Task> list = new ArrayList<Task>();
                ArrayList<Task> taskList = getTaskListByKey(key);
                for (Task task : taskList) {
                    if (task.mNextRunTime - current < TIMESLICE) { // 一段时间内的任务都执行了
                        if (DEBUG) {
                            Log.d(TAG, "task.mNextRunTime - current = " + (task.mNextRunTime - current));
                        }
                        executeTask(task);
                        if (task.mPeriod > 0) {
                            task.mNextRunTime = current + task.mPeriod;
                            list.add(task);
                        }
                    }
                    if (task.mNextRunTime < mNextScheduleTime) {
                        mNextScheduleTime = task.mNextRunTime;
                    }
                }

                if (mNextScheduleTime < DEFAULT_SCHEDULE_TIME) {
                    startAlarm(mNextScheduleTime - current); // 启动下次alarm
                }
            }
        }
    }

    /**
     * 是否已有此任务
     * @param task {@link Task}
     * @return true: 已有该任务
     */
    private boolean hasTask(Task task, ArrayList<Task> taskList) {
        for (Task t : taskList) {
            if (TextUtils.equals(t.mTaskKey, task.mTaskKey)) {
                return true;
            }
        }
        return false;
    }

    private void cancelTask(Task task) {
        if (task.mIsSerial) {
            mSerialExecutor.cancel(task.mRunnable);
        } else {
            Future<?> futuretsk = mFutureMap.get(task.mRunnable);
            if (futuretsk != null && (!futuretsk.isCancelled() || !futuretsk.isDone())) {
                futuretsk.cancel(true);
            }
        }
    }

    private void executeTask(final Task task) {
        if (task.mDelay > 0) {
            long delayTime = task.mNextRunTime - System.currentTimeMillis();
            executeWithDelay(task, delayTime);
        } else {
            execute(task);
        }
    }

    private void executeWithDelay(final Task task, final long delayTime) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                execute(task);
            }
        };
        mPoolExecutor.execute(r);
    }

    /**
     * java任务执行器执行任务
     * @param task {@link Task}
     */
    private void execute(Task task) {
        if (task.mPeriod > 0) {
            // 如果是周期任务,只有第一执行的时候会延时,其他时候周期到了就执行
            task.mDelay = 0;
        }
        if (task.mIsSerial) {
            mSerialExecutor.execute(task.mRunnable);
        } else {
            Future<?> future = mPoolExecutor.submit(task.mRunnable);
            mFutureMap.put(task.mRunnable, future);
            if (DEBUG) {
                Log.d(TAG, "execute task, " + task.mTaskKey + " execute time is " + System.currentTimeMillis());
            }
        }
    }
}
