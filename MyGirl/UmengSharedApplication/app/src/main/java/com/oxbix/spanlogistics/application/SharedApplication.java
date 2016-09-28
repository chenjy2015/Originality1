package com.oxbix.spanlogistics.application;

import android.app.Application;

import com.oxbix.spanlogistics.util.ShareHelper;

public class SharedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ShareHelper.init();
    }
}