package com.lewis.vinci.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lewis.lib_vinci.utils.LogUtil;
import com.lewis.vinci.R;
import com.lewis.vinci.common.base.NetworkBaseActivity;
import com.lewis.vinci.ui.widget.FadeInTextView;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends NetworkBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.btn1)
    Button mButtonCarema;
    @BindView(R.id.btn2)
    Button mButtonPrinttext;
    @BindView(R.id.btn3)
    Button mButtonPop;
    @BindView(R.id.tv_printtext)
    FadeInTextView mTvPrintText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "afterCreate");
        //相当于onCreat()
        mTvPrintText.setTextString("中华人民共和国财政部终于成立了")
                .setTextAnimationListener(new FadeInTextView.TextAnimationListener() {
                    @Override
                    public void animationFinish() {

                    }
                });
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.btn2:
                mTvPrintText.startFadeInAnimation();
                break;
            case R.id.btn3:
                startActivity(new Intent(this, PopWindowActivity.class));
                break;
        }
    }
}
