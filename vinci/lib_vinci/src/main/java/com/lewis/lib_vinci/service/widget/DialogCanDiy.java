package com.lewis.lib_vinci.service.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.lewis.lib_vinci.utils.DisplayUtils;


/**
 * 可以自定义布局的弹窗
 */
public class DialogCanDiy extends Dialog {
    private int layoutRes;//布局文件
    private Context context;
    private WindowManager.LayoutParams lp;
    private Window dialogWindow;

    public DialogCanDiy(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 自定义布局的构造方法
     *
     * @param context
     * @param resLayout
     */
    public DialogCanDiy(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * 自定义主题及布局的构造方法
     *
     * @param context
     * @param theme
     * @param resLayout
     */
    public DialogCanDiy(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        initAttribute();
    }

    private void initAttribute() {
        dialogWindow = this.getWindow();
        lp = dialogWindow.getAttributes();
    }

    /**
     * 设置当前弹窗显示的位置
     *
     * @param gravity
     */
    public void setShowGravity(int gravity) {
        if (gravity != -1) {
            dialogWindow.setGravity(gravity);
        }
    }

    /**
     * 设置当前对话框的宽高
     *
     * @param width  宽度值
     * @param height 高度值
     */
    public void setWidthHeight(int width, int height) {
        if (width == -1 || height == -1) {
            return;
        }
        lp.height = height;
        lp.width = width;
        dialogWindow.setAttributes(lp);
    }

    /**
     * 设置当前对话框的宽高
     *
     * @param widthRate  宽度与当前屏幕的比例
     * @param heightRate 高度与当前屏幕的比例
     */
    public void setWidthHeightRate(float widthRate, float heightRate) {
        if (widthRate == -1 || heightRate == -1) {
            return;
        }
        lp.height = (int) (DisplayUtils.getScreenHeight(context) * heightRate);
        lp.width = (int) (DisplayUtils.getScreenHeight(context) * widthRate);
        dialogWindow.setAttributes(lp);
    }
}