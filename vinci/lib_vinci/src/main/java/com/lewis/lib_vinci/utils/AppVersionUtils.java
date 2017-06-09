package com.lewis.lib_vinci.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by ZHT on 2017/6/1.
 * 版本相关工具类
 */

public class AppVersionUtils {

    private AppVersionUtils() {
        throw new IllegalArgumentException("cannot create VersionUtils constructor!");
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            PackageManager manager = getPackageManager(context);
            info = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String getPackageName(Context context) {
        return getPackageInfo(context).packageName;
    }

    /**
     * 获取应用版本名
     *
     * @return 版本名
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 获取应用版本号
     *
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static PackageManager getPackageManager(Context context) {
        return context.getApplicationContext().getPackageManager();
    }
}
