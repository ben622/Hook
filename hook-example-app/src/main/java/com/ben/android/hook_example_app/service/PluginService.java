package com.ben.android.hook_example_app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class PluginService extends Service {

    private static final String TAG = PluginService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind" );
        return new PluginBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate" );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand" );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy" );
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind" );
        return super.onUnbind(intent);
    }

    public class PluginBinder extends Binder{

    }
}
