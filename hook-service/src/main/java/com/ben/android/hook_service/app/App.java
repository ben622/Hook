package com.ben.android.hook_service.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhang on 2018/1/23.
 */

public class App extends Application {
    private static Context mContext;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = base;
    }

    public static Context getContext() {
        return mContext;
    }
}
