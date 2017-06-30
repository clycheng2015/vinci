package com.lewis.vinci.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 项目名称：vinci
 * 类描述：可以嵌套在Listview里面的Listview,
 * 解决嵌套在内部的ListView不能滑动的问题（小的listview不能滑动）
 * 创建人：Administrator
 * 创建时间：2017-06-16
 *
 * @version ${VSERSION}
 */


public class InnerListview extends ListView {
    public InnerListview(Context context) {
        super(context);
    }

    public InnerListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerListview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 当手指触摸listview时，让父控件交出ontouch权限,不能滚动
            case MotionEvent.ACTION_DOWN:
                setParentScrollAble(false);
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 当手指松开时，让父控件重新获取onTouch权限
                setParentScrollAble(true);
                break;

        }
        return super.onInterceptTouchEvent(ev);

    }

    // 设置父控件是否可以获取到触摸处理权限
    private void setParentScrollAble(boolean flag) {
        getParent().requestDisallowInterceptTouchEvent(!flag);
    }
}
