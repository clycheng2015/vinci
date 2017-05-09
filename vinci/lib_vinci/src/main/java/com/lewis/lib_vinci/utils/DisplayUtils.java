/*
 * Copyright (C) 2013 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 屏幕显示相关工具类
 */
public final class DisplayUtils {

    /**  */
    private static final float DELTA = 0.5f;

    /**
     * default constructor
     */
    private DisplayUtils() {
        super();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context {@linkplain Context}
     * @param dpValue dp
     * @return px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + DELTA);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context {@linkplain Context}
     * @param pxValue px
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) { // NO_UCD (unused code)
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + DELTA);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue   px
     * @param fontScale ({@link DisplayMetrics DisplayMetrics}
     *                  类中属性scaledDensity)
     * @return sp
     */
    public static int px2sp(float pxValue, float fontScale) { // NO_UCD (unused code)
        return (int) (pxValue / fontScale + DELTA);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue   sp
     * @param fontScale ({@link DisplayMetrics DisplayMetrics}
     *                  类中属性scaledDensity)
     * @return px
     */
    public static int sp2px(float spValue, float fontScale) { // NO_UCD (unused code)
        return (int) (spValue * fontScale + DELTA);
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * ACTIVITY 背景色变亮
     *
     * @param activity
     */
    public static void beginLight(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = 1f;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * PopupWindow弹出 ACTIVITY 背景色变暗
     *
     * @param activity
     */
    public static void beginDeep(Activity activity, PopupWindow popwin) {
        ColorDrawable cd = new ColorDrawable(0x000000);
        popwin.setBackgroundDrawable(cd);
        // 产生背景变暗效果
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = 0.4f;
        activity.getWindow().setAttributes(lp);
    }
}