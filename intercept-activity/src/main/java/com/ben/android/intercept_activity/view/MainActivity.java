package com.ben.android.intercept_activity.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ben.android.intercept_activity.R;
import com.ben.android.intercept_activity.util.HookHelper;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String APK_NAME = "hook-example-app.apk";
    private static final String DEX_NAME = "hook-example-app.dex";
    private static final String PLUGIN_PACKAGE_NAME = "com.ben.android.hook_example_app";
    private boolean mHookBaseDexClassLoader;
    private EditText mLoggerView;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoggerView = (EditText) findViewById(R.id.id_logger);
        mLoggerView.append("CurrentPackageName::" + getPackageName());
        mLoggerView.append("\nPluginAPKPackageName::" + PLUGIN_PACKAGE_NAME);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_hook_target1:
                startActivity(new Intent(this, TargetActivity.class));
                break;
            case R.id.id_hook_target2:
                startActivity(new Intent(this, Target2Activity.class));
                break;
            case R.id.id_hook_classloader_before:
                HookHelper.copyAssetsFileToSD(this, APK_NAME);
                File apkFile = getFileStreamPath(APK_NAME);
                File dexFile = getFileStreamPath(DEX_NAME);
                mHookBaseDexClassLoader = HookHelper.hookBaseDexClassLoader(this, apkFile, dexFile);
                mLoggerView.append("\ninit:>>" + mHookBaseDexClassLoader);
                break;
            case R.id.id_hook_classloader:
                if (!mHookBaseDexClassLoader) {
                    mLoggerView.append("\n请先初始化");
                    return;
                }
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(PLUGIN_PACKAGE_NAME, "com.ben.android.hook_example_app.view.StaticActivity"));
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        HookHelper.hookActivityManager();
        HookHelper.hookActivityThreadHandler();
    }
}
