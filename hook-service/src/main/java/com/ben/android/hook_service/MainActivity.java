package com.ben.android.hook_service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ben.android.hook_service.util.HookHelper;
import com.ben.android.hook_service.util.PackageHelper;

public class MainActivity extends AppCompatActivity {
    private static final String CLASS_NAME = "com.ben.android.hook_example_app.service.PluginService";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        try {
            intent = new Intent(this, Class.forName(CLASS_NAME));
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, "ClassNotFound@" + CLASS_NAME + "，若该Service在插件包中请确认是否已经将插件包加载到宿主进程中!", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.id_hook_service_start:
                startService(intent);
                //startService(new Intent(this,TestService.class));
                break;
            case R.id.id_hook_service_bind:
                bindService(intent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        Log.e(TAG, "onServiceConnected" );
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        Log.e(TAG, "onServiceConnected" );
                    }
                },BIND_AUTO_CREATE);
                break;
            case R.id.id_hook_service_stop:
                stopService(intent);
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        //将assets目录中的插件包copy到sd
        PackageHelper.copyAssetsFileToSD(this, "hook-example-app-debug.apk");
        //init ClassLoader
        HookHelper.hookBaseDexClassLoader(this, getFileStreamPath("hook-example-app-debug.apk"), getFileStreamPath("hook-example-app-debug.dex"));
        //解析插件包
        PackageHelper.parsePackage(getFileStreamPath("hook-example-app-debug.apk"));
        //hook ActivityServiceManagerNative
        HookHelper.hookIActivityManager();

    }
}
