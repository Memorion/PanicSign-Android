package com.panic.sign;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

public class PanicSign extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        LeakCanary.install(this);
    }

    static Context getAppContext() {
        return context;
    }
}
