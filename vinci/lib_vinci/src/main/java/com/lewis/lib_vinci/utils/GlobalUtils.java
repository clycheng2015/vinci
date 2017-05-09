package com.lewis.lib_vinci.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lewis.lib_vinci.LibConstants;
import com.lewis.lib_vinci.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public final class GlobalUtils {
    /**
     * 要展示的文案
     */
    public static String showStr = "";
    private static Toast mToast;

    /**
     * default constructor
     */
    private GlobalUtils() {
        super();
    }

    /**
     * show dialog
     *
     * @param activity the activity to show dialog
     * @param id       dialog id
     * @param str      str
     */
    @SuppressWarnings("deprecation")
    public static void safeShowDialog(Activity activity, int id, String str) {
        try {
            showStr = str;
            if (LibConstants.DEBUG) {
                LogUtil.logd("safeShowDialog showStr=" + showStr);
            }

            //activity.getActivity().showDialog(id);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * dismiss dialog
     *
     * @param activity the activity to dismiss dialog
     * @param id       dialog id
     */
    @SuppressWarnings("deprecation")
    public static void safeDismissDialog(Activity activity, int id) {
        try {
            if (LibConstants.DEBUG) {
                LogUtil.logd("safeDismissDialog showStr=" + showStr);
            }
            showStr = "";
            //activity.getActivity().removeDialog(id);
        } catch (Exception e) {
            Log.e("globalUtils", "dialog Exception", e);
        }
    }

    /**
     * dismiss dialog
     *
     * @param activity
     * the activity to dismiss dialog
     * @param id
     * dialog id
     */
    /*
     * @SuppressWarnings("deprecation")
     * public static void safeDismissDialog(BaseDialogActivity_bak activity, int id) {
     * try {
     * showStr = "";
     * activity.getActivity().removeDialog(id);
     * } catch (Exception e) {
     * Log.e("globalUtils", "dialog Exception", e);
     * }
     * }
     */

    private static LayoutInflater mInflater;

    /**
     * make toast
     *
     * @param context context to dismiss dialog
     * @param text    content want to toast
     */
    public static void toast(Context context, CharSequence text) {
        toast(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * make toast
     *
     * @param context  context to dismiss dialog
     * @param text     content want to toast
     * @param duration duration
     */
    public static void toast(Context context, CharSequence text, int duration) {
        toast(context, text, -1, duration);
    }

    /**
     * Make a toast.
     *
     * @param context   The context to use. Usually your {@link android.app.Application} or {@link Activity}
     *                  object.
     * @param text      The text to show. Can be formatted text.
     * @param iconResId The image to show.
     * @param duration  How long to display the message. Either {@link #}
     */
    public static void toast(Context context, CharSequence text, int iconResId, int duration) { // NO_UCD (use private)
        // View v = context.getLayoutInflater().inflate(Res.layout(context,
        // "credoo_toast"), null);
        if (mInflater == null) {
            mInflater = LayoutInflater.from(context);
        }
        View v = mInflater.inflate(R.layout.credoo_base_toast, null);
        if (null != v) {
            TextView msg = (TextView) v.findViewById(R.id.credoo_base_toast_message);
            if (null == msg) {
                return;
            }
            msg.setText(text);
            ImageView icon = (ImageView) v.findViewById(R.id.credoo_base_toast_icon);
            if (null != icon && iconResId > 0) {
                icon.setImageResource(iconResId);
                icon.setVisibility(View.VISIBLE);
            }
            if (mToast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
                mToast = new Toast(context);
                mToast.setDuration(duration);
            }
            mToast.setView(v);
            mToast.show();
        }
    }

    /**
     * hide keyboard
     *
     * @param activity the activity to hide keyboard
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        View v = activity.getCurrentFocus();
        if (v == null) {
            return;
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideKeyboardNow(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * Show the input method.
     *
     * @param context context
     * @param view    The currently focused view, which would like to receive soft
     *                keyboard input
     * @return success or not.
     */
    public static boolean showInputMethod(final Context context, final View view) {
        if (context == null || view == null) {
            return false;
        }
        view.requestFocusFromTouch();
        view.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, 0);
            }
        }, 100);
        return true;
    }

    /**
     * hide the input method.
     *
     * @param context context
     * @param view    The currently focused view, which would like to receive soft
     *                keyboard input
     * @return success or not.
     */
    public static boolean hideInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return false;
    }

    public static void closeSafely(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeSafely(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
            }
        }
    }
}