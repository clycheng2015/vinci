package com.lewis.lib_vinci.coreframe.https.webview;

/**
 * 项目名称：vinci
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2017-05-17
 *
 * @version ${VSERSION}
 */


public interface LoadedListener {
    void loaded(String url);

    void pinningPreventedLoading(String host);
}
