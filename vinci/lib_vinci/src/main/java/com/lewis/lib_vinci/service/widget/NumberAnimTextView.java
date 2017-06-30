package com.lewis.lib_vinci.service.widget;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * 数字增加动画的　TextView
 */
@SuppressLint("AppCompatCustomView")
public class NumberAnimTextView extends TextView {

    private String mNumStart = "0";  // 起始值 默认 0
    private String mNumEnd; // 结束值
    private long mDuration = 2000; // 动画总时间 默认 2000 毫秒
    private String mPrefixString = ""; // 前缀
    private String mPostfixString = ""; // 后缀
    private boolean isEnableAnim = true; // 是否开启动画
    private TimeInterpolator mTimeInterpolator = new LinearInterpolator(); // 动画速率
    private boolean isInt; // 是否是整数
    private AnimEndListener mEndListener; // 动画正常结束监听事件
    private ValueAnimator animator;

    public NumberAnimTextView(Context context) {
        super(context);
    }

    public NumberAnimTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberAnimTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setNumberString(String number) {
        setNumberString("0", number);
    }

    public void setNumberString(String numberStart, String numberEnd) {
        mNumStart = numberStart;
        mNumEnd = numberEnd;
        if (checkNumString(numberStart, numberEnd)) {
            // 数字合法　开始数字动画
            start();
        } else {
            // 数字不合法　直接调用　setText　设置最终值
            setText(mPrefixString + numberEnd + mPostfixString);
        }
    }

    public void setEnableAnim(boolean enableAnim) {
        isEnableAnim = enableAnim;
    }

    public void setDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public void setPrefixString(String mPrefixString) {
        this.mPrefixString = mPrefixString;
    }

    public void setPostfixString(String mPostfixString) {
        this.mPostfixString = mPostfixString;
    }

    /**
     * 设置动画速率，默认为LinearInterpolator。需要在setNumberWithAnim之前设置才有效
     *
     * @param timeInterpolator
     */
    public void setAnimInterpolator(TimeInterpolator timeInterpolator) {
        mTimeInterpolator = timeInterpolator;
    }

    /**
     * 校验数字的合法性
     *
     * @param numberStart 　开始的数字
     * @param numberEnd   　结束的数字
     * @return 合法性
     */
    private boolean checkNumString(String numberStart, String numberEnd) {

        String regexInteger = "-?\\d*";
        isInt = numberEnd.matches(regexInteger) && numberStart.matches(regexInteger);
        if (isInt) {
            BigInteger start = new BigInteger(numberStart);
            BigInteger end = new BigInteger(numberEnd);
            return end.compareTo(start) >= 0;
        }
        String regexDecimal = "-?[1-9]\\d*.\\d*|-?0.\\d*[1-9]\\d*";
        if ("0".equals(numberStart)) {
            if (numberEnd.matches(regexDecimal)) {
                BigDecimal start = new BigDecimal(numberStart);
                BigDecimal end = new BigDecimal(numberEnd);
                return end.compareTo(start) > 0;
            }
        }
        if (numberEnd.matches(regexDecimal) && numberStart.matches(regexDecimal)) {
            BigDecimal start = new BigDecimal(numberStart);
            BigDecimal end = new BigDecimal(numberEnd);
            return end.compareTo(start) > 0;
        }
        return false;
    }

    private void start() {
        if (!isEnableAnim) { // 禁止动画
            setText(mPrefixString + format(new BigDecimal(mNumEnd)) + mPostfixString);
            return;
        }
        animator = ValueAnimator.ofObject(new BigDecimalEvaluator(), new BigDecimal(mNumStart), new BigDecimal(mNumEnd));
        animator.setDuration(mDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BigDecimal value = (BigDecimal) valueAnimator.getAnimatedValue();
                setText(mPrefixString + format(value) + mPostfixString);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (null != mEndListener) { // 动画不是中途取消，而是正常结束
                    mEndListener.onAnimFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
    }

    //界面的onDestroy方法中调用clearAnimator方法
    public void clearAnimator() {
        if (null != animator) {
            if (animator.isRunning()) {
                animator.removeAllListeners();
                animator.removeAllUpdateListeners();
                animator.cancel();
            }
            animator = null;
        }
    }

    // 暂停动画,API>19
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onPause() {
        if (null != animator && animator.isRunning()) {
            animator.pause(); // API 不低于19
        }
    }

    // 继续执行动画 API>19
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onResume() {
        if (null != animator && animator.isRunning()) {
            animator.resume();
        }
    }

    /**
     * 格式化 BigDecimal ,小数部分时保留两位小数并四舍五入
     *
     * @param bd 　BigDecimal
     * @return 格式化后的 String
     */
    private String format(BigDecimal bd) {
        StringBuilder pattern = new StringBuilder();
        if (isInt) {
            pattern.append("#,###");
        } else {
            int length = 0;
            String decimals = mNumEnd.split("\\.")[1];
            if (decimals != null) {
                length = decimals.length();
            }
            pattern.append("#,##0");
            if (length > 0) {
                pattern.append(".");
                for (int i = 0; i < length; i++) {
                    pattern.append("0");
                }
            }
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(bd);
    }

    // 不加 static 关键字，也不会引起内存泄露，因为这里也没有开启线程
    // 加上 static 关键字，是因为该内部类不需要持有外部类的引用，习惯加上
    private static class BigDecimalEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            BigDecimal start = (BigDecimal) startValue;
            BigDecimal end = (BigDecimal) endValue;
            BigDecimal result = end.subtract(start);
            return result.multiply(new BigDecimal("" + fraction)).add(start);
        }
    }

    public void setAnimEndListener(AnimEndListener listener) {
        mEndListener = listener;
    }

    // 动画显示数字的结束监听，当动画结束显示正确的数字时，可能需要做些处理
    public interface AnimEndListener {
        void onAnimFinish();
    }
}
