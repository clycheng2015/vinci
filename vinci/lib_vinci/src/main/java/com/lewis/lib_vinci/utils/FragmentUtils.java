package com.lewis.lib_vinci.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.lewis.lib_vinci.LibConstants;


/**
 * Fragment工具类
 *
 * @author yulei
 * @since 2014-4-15
 */
public final class FragmentUtils {
    /**
     *
     */
    private static final boolean DEBUG = LibConstants.DEBUG;

    private FragmentUtils() {

    }

    /**
     * 启动Fragment
     *
     * @param ctx      context
     * @param fm       FragmentManager
     * @param fragment Fragment
     * @param fragname fragment名字
     * @param anim     是否要过场动画
     */
    public static void startFragment(Context ctx, FragmentManager fm, Fragment fragment, String fragname,
                                     boolean anim) {
        FragmentTransaction ft = fm.beginTransaction();

        // 设置动画，必须在replace之前
        if (anim) {
            ft.setCustomAnimations(ResUtils.anim(ctx, "slide_in_from_right"),
                    ResUtils.anim(ctx, "slide_out_to_left"),
                    ResUtils.anim(ctx, "slide_in_from_left"),
                    ResUtils.anim(ctx, "slide_out_to_right"));
        } else {
            ft.setCustomAnimations(0, 0,
                    ResUtils.anim(ctx, "slide_in_from_left"),
                    ResUtils.anim(ctx, "slide_out_to_right"));
        }

        ft.replace(android.R.id.primary, fragment);
        ft.addToBackStack(fragname);
        ft.commitAllowingStateLoss();
    }


    /**
     * 后退操作
     *
     * @param fm FragmentManager
     * @return 是否成功
     */
    public static boolean popBackStackImmediate(FragmentManager fm) {
        if (fm == null || fm.getBackStackEntryCount() <= 0) {
            return false;
        }

        return fm.popBackStackImmediate();
    }

    /**
     * 回到第一个fragment
     *
     * @param fragmentManager FragmentManager
     */
    public static void goBackToFirstFragment(FragmentManager fragmentManager) { // NO_UCD (unused code)
        if (fragmentManager != null) {
            logFragmentStack(fragmentManager);

            int count = fragmentManager.getBackStackEntryCount();
            // 栈内所有的fragment都弹出，返回主界面
            if (count > 0) {
                while (count > 0) {
                    count--;
                    fragmentManager.popBackStackImmediate();
                }
            }
        }
    }

    /**
     * 输出FragmentStack的内容
     *
     * @param fmr FragmentManager
     */
    public static void logFragmentStack(FragmentManager fmr) {
        if (DEBUG) {
            final String TAG = "FragmentStatck";
            StringBuilder builder = new StringBuilder();
            if (null != fmr) {
                int count = fmr.getBackStackEntryCount();
                builder.append("FragmentStatck[count=").append(count).append(", [");
                for (int i = 0; i < count; i++) {
                    FragmentManager.BackStackEntry entry = fmr.getBackStackEntryAt(i);
                    builder.append("(id=").append(entry.getId()).append(", name=").append(entry.getName()).append(") ");
                }
                builder.append("]]");
                Log.i(TAG, builder.toString());
            } else {
                Log.i(TAG, "FragmentManager fmr == null");
            }
        }
    }
}
