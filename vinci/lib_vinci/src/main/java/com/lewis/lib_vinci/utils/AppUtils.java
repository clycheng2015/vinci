package com.lewis.lib_vinci.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class AppUtils {
 
    private static Boolean isDebug = null;
 
    public static boolean isDebug() {
        return isDebug == null ? false : isDebug.booleanValue();
    }
 
    /**
     * Sync lib debug with app's debug value. Should be called in module Application
     *
     * @param context
     */
    public static void syncIsDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }
}