package com.ben.android.hook_cprovider.app;

import android.app.Application;
import android.content.Context;

import com.ben.android.hook_cprovider.util.HookHelper;
import com.ben.android.hook_cprovider.util.PackageHelper;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/24
 */
public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PackageHelper.copyAssetsFileToSD(base,"hook-example-app-debug.apk");
        PackageHelper.parsePackage(getFileStreamPath("hook-example-app-debug.apk"));
        HookHelper.hookBaseDexClassLoader(base, getFileStreamPath("hook-example-app-debug.apk"), getFileStreamPath("hook-example-app-debug.dex"));
        HookHelper.init(base);
    }
}
