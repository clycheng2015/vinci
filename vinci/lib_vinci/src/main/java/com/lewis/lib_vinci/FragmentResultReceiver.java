/*
 * Copyright (C) 2014 Credoo Inc. All rights reserved.
 */
package com.lewis.lib_vinci;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * 用于fragment之间数据传递
 * 
 * @author yulei
 * @since 2014/4/11
 */
public class FragmentResultReceiver extends ResultReceiver {

    public FragmentResultReceiver(Handler handler) {
        super(handler);
    }

    private Receiver mReceiver;

    public void clearReceiver() { // NO_UCD (unused code)
        mReceiver = null;
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
