package com.lewis.vinci.common.manager;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.lewis.lib_vinci.utils.LogUtil;

import java.util.Iterator;
import java.util.Stack;

public class AppManager {
    private static final String TAG = AppManager.class.getSimpleName();
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static synchronized AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆�?
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        //如果存在，先删除
        if (activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
        activityStack.add(activity);
        //打印出当前Activity堆栈
        publishActivityStack();
    }

    /**
     * 获取当前Activity（堆栈中�?���?��压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity
     */
    public void finishActivity() {
        if (activityStack != null && activityStack.size() > 0) {
            Activity activity = activityStack.lastElement();
            finishActivity(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> clazz) {
        if (activityStack != null && activityStack.size() > 0) {
            Activity activity;
            Iterator<Activity> it = activityStack.iterator();
            while (it.hasNext()) {
                activity = it.next();
                if (activity.getClass().equals(clazz)) {
                    it.remove();
                    activity.finish();
                }
            }
        }
    }

    public void setTopActivity(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            if (activityStack.search(activity) == -1) {
                activityStack.push(activity);
                return;
            }
            int location = activityStack.search(activity) - 1;
            if (location != 0) {
                activityStack.push(activityStack.remove(location));
            }
        }
    }

    public Activity getTopActivity() {
        if (activityStack != null && activityStack.size() > 0) {
            return activityStack.peek();
        }
        return null;
    }

    public void finishTopActivity() {
        if (activityStack != null && activityStack.size() > 0) {
            Activity activity = activityStack.pop();
            activity.finish();
        }
    }

    /**
     * 结束所有的Activity
     */
    public void finishAllActivity() {
        if (activityStack != null && activityStack.size() > 0) {
            while (!activityStack.empty()) {
                activityStack.pop().finish();
            }
            activityStack.clear();
            activityStack = null;
        }
    }

    /**
     * @param clazz 关闭除指定Activity外的所有
     */
    public void finishActivityExcept(Class<?> clazz) {
        if (activityStack != null && activityStack.size() > 0) {
            Activity activity;
            Iterator<Activity> it = activityStack.iterator();
            while (it.hasNext()) {
                activity = it.next();
                if (activity.getClass().equals(clazz)) {

                } else {
                    activity.finish();
                }
            }
        }
    }

    /**
     * @param activity 关闭除指定Activity外的所有
     */
    public void finishOtherActivites(Activity activity) {
        finishAllActivity();
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.push(activity);
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            //有多个Activity时，必须调用下面方法，当前只有一个MainActivity,基本不需要下面的代码
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            //在调用System.exit(0);前需要调用下面的方法，保存友盟统计的数据。
            //MobclickAgent.onKillProcess(context);
            //标准的退出方法
            System.exit(0);
            //关闭分配的Dalvik VM的本地方法
            Process.killProcess(Process.myPid());
        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
    }

    /**
     * 获取指定Activity所在栈中的索引
     *
     * @param acitivty
     * @return
     */
    public int getActivityPosition(Activity acitivty) {
        return activityStack.search(acitivty);
    }

    /**
     * 打印当前ActivityStack堆栈信息
     */
    public void publishActivityStack() {
        Iterator<Activity> it = activityStack.iterator();
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityStack:\n");
        while (it.hasNext()) {
            builder.append(it.next().getClass().getName() + "\n");

        }
        Log.i(TAG, builder.toString());
    }
}