package com.lewis.vinci.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lewis.lib_vinci.service.network.NetChangeObserver;
import com.lewis.lib_vinci.service.network.NetStateReceiver;
import com.lewis.lib_vinci.service.network.NetUtils;
import com.lewis.lib_vinci.utils.GlobalUtils;


/**
 * Created by deng on 2016/9/30.
 */

public abstract class BaseActivity extends AppCompatActivity implements NetChangeObserver {

    /**
     * 网络观察者
     */
    protected NetChangeObserver mNetChangeObserver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //开启广播去监听 网络 改变事件
        NetStateReceiver.registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetStateReceiver.removeRegisterObserver(this);
    }

    @Override
    public void onNetConnected(NetUtils.NetType type) {
        GlobalUtils.toast(this, "onNetConnected" + type.name());
    }

    @Override
    public void onNetDisConnect() {
        GlobalUtils.toast(this, "onNetDisConnect");
    }
}
