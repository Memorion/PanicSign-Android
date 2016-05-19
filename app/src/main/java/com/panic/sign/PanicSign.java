package com.panic.sign;

import android.app.Application;
import android.content.Context;

public class PanicSign extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    static Context getAppContext() {
        return context;
    }
}
