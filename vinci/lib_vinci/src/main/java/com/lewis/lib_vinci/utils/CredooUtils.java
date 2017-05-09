package com.lewis.lib_vinci.utils;

import android.app.Activity;
import android.content.Context;

/**
 * 
 * @author lewis
 * @since 2015-9-27
 */
public final class CredooUtils {
	
	private static int mOpenEnter;
	private static int mOpenExit;
	private static int mCloseEnter;
	private static int mCloseExit;
	private CredooUtils() {
	    
	}
	
	/**
     * 启动activity动画
     * @param context context
     */
    public static void startActivityAnim(final Context context) {
    	if (mOpenEnter == 0 || mOpenExit == 0) {
    		mOpenEnter = ResUtils.anim(context, "slide_in_from_right");
    		mOpenExit = ResUtils.anim(context, "slide_out_to_left");
    	}
    	// 页面的转场动画
    	if (context instanceof Activity) {
        	((Activity) context).overridePendingTransition(mOpenEnter, mOpenExit);
        }
    }
    
    /**
     * 退出activity动画
     * @param context context
     */
    public static void finishActivityAnim(final Context context) {
    	if (mCloseEnter == 0 || mCloseExit == 0) {
    		mCloseEnter = ResUtils.anim(context, "slide_in_from_left");
    		mCloseExit = ResUtils.anim(context, "slide_out_to_right");
    	}
        // 页面的转场动画
    	if (context instanceof Activity) {
    		((Activity) context).overridePendingTransition(mCloseEnter, mCloseExit);
    	}
    }
}
