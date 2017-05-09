package com.lewis.vinci;

import android.os.Bundle;
import android.view.View;

import com.lewis.lib_vinci.utils.GlobalUtils;
import com.lewis.lib_vinci.utils.LogUtil;
import com.lewis.vinci.common.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d(TAG, "onCreate-----------------------------");
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalUtils.toast(MainActivity.this, "xxxxxxxxxxxxxxxxxxxxx");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy-----------------------------");
    }
}
