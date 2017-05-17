package com.lewis.vinci;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lewis.lib_vinci.utils.LogUtil;
import com.lewis.vinci.common.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d(TAG, "onCreate-----------------------------");
        mButton1 = (Button) findViewById(R.id.btn1);
        initListener();
    }

    private void initListener() {
        mButton1.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy-----------------------------");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                //startActivity(new Intent(this, WebHttpsActivity.class));
                break;
        }
    }
}
