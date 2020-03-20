package com.everonet.demo.miniprograms;

import android.app.Application;
import android.content.Context;

import com.facebook.soloader.SoLoader;

public class BaseApp extends Application {

    private static BaseApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
        instance = this;
    }


    public static BaseApp getInstance() {
        return instance;
    }


    public static Context getAppContext() {
        return instance;
    }

    public static Context getContext() {
        return getAppContext();
    }

}
