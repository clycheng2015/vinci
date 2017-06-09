package com.lewis.vinci.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lewis.vinci.R;
import com.lewis.vinci.common.base.NetworkBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends NetworkBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.btn1)
    Button mButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        //相当于onCreat()
    }

    @OnClick({R.id.btn1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                startActivity(new Intent(this, CameraActivity.class));
                break;
        }
    }
}
