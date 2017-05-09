package com.lewis.vinci;

import android.app.Application;

import com.lewis.lib_vinci.service.network.NetStateReceiver;
import com.lewis.lib_vinci.utils.AppUtils;


/**
 * 项目名称：vinci
 * 类描述：程序入口配置
 *
 * 创建人：Administrator
 * 创建时间：2017-05-09
 *
 * @version ${1.0}
 */

//测试分支修改
public class VinciApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //配置全局（尤其是lib module）中的全局调试开关
        configGlobleDebug();
        //初始化监听网络广播
        initNetWorkListen();
    }

    /**
     * 开启网络广播监听,注意反注册
     */
    private void initNetWorkListen() {
        NetStateReceiver.registerNetworkStateReceiver(this);
    }

    /**
     * 关联全局调试开关
     */
    private void configGlobleDebug() {
        AppUtils.syncIsDebug(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        NetStateReceiver.unRegisterNetworkStateReceiver(this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
