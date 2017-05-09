package com.lewis.lib_vinci;

import com.lewis.lib_vinci.utils.AppUtils;

/**
 * 定义一般常量
 */
public abstract class LibConstants {

    /**
     * 判断当前APP是否是DEBUG模式，可以用于在lib module中，但是需要在app module 应用入口中，
     * 执行AppUtils.syncIsDebug(getApplicationContext());
     *
     * 原因：
     * BuildConfig.java 是编译时自动生成的，并且每个 Module 都会生成一份，
     * 以该 Module 的 packageName 为 BuildConfig.java 的 packageName。
     * 所以如果你的应用有多个 Module 就会有多个 BuildConfig.java 生成，
     * 而 Lib Module import 的是自己的 BuildConfig.java，
     * “编译时”被依赖的 Module 默认会提供 Release 版给其他 Module 或工程使用，
     * 这就导致该 BuildConfig.DEBUG 会始终为 false。
     */
    public static boolean DEBUG = AppUtils.isDebug();

    /**
     * 渠道号
     */
    public static final String CHANNEL_ID = "";
    /*  内部版本号 */
    public static final String VERSION_NO = "1.0";
    public static final String SDK_VERSION = "vinci-" + VERSION_NO + "-Android-" + CHANNEL_ID;
}
