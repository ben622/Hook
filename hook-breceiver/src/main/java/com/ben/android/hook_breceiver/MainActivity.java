package com.ben.android.hook_breceiver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ben.android.hook_breceiver.util.HookHelper;
import com.ben.android.hook_breceiver.util.PackageHelper;

public class MainActivity extends AppCompatActivity {

    private EditText mLoggerView;
    private boolean mHookBaseDexClassLoader;
    private boolean mParsePackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoggerView = (EditText) findViewById(R.id.id_logger);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_breceiver_init:
                if (mHookBaseDexClassLoader) {
                    printLog("init", String.valueOf(mHookBaseDexClassLoader));
                    return;
                }
                PackageHelper.copyAssetsFileToSD(this, "hook-example-app-debug.apk");
                mHookBaseDexClassLoader = HookHelper.hookBaseDexClassLoader(this, getFileStreamPath("hook-example-app-debug.apk"), getFileStreamPath("hook-example-app-debug.dex"));
                printLog("init", String.valueOf(mHookBaseDexClassLoader));
                break;
            case R.id.id_breceiver_register:
                if (!mHookBaseDexClassLoader) {
                    printLog("init", "请先执行Initi ClassLoader");
                    return;
                }
                if (mParsePackage) {
                    printLog("init register", String.valueOf(mParsePackage));
                    return;
                }
                mParsePackage = PackageHelper.parsePackage(this, getFileStreamPath("hook-example-app-debug.apk"));
                break;
            case R.id.id_breceiver_send:
                if (!mHookBaseDexClassLoader) {
                    printLog("init", "请先执行LoadClassLoader");
                    return;
                }
                if (!mParsePackage) {
                    printLog("init register", String.valueOf(mParsePackage));
                    return;
                }
                sendBroadcast(new Intent("com.ben.android.broadcastreceiver"));
                break;
        }
    }

    private void printLog(String tag,String msg) {
        mLoggerView.append("\n"+tag+"::"+msg);
    }
}
