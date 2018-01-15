package com.ben.android.intercept_activity.util;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.ben.android.intercept_activity.view.StubActivity;
import com.ben.android.intercept_activity.view.TargetActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/15
 * <p>
 */
public class IActivityManagerHandler implements InvocationHandler {
    private static final String PACKAGE_NAME = "com.ben.android.intercept_activity";
    private static final Class TARGET_CLASS = StubActivity.class;
    private static final String TAG = IActivityManagerHandler.class.getName();

    private Object mActivityManager;

    public IActivityManagerHandler(Object activityManager) {
        mActivityManager = activityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            //对Intent进行处理 将目标Activity替换成我们需要的Activity
            Intent rawIntent = null;
            int index = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    rawIntent = (Intent) args[i];
                    index = i;
                    break;
                }
            }

            if (rawIntent != null) {
                Log.e(TAG, "origin Intent>>"+rawIntent.getComponent().toString());
            }

            Intent newIntent = new Intent();
            ComponentName componentName = new ComponentName(PACKAGE_NAME, TARGET_CLASS.getName());
            newIntent.setComponent(componentName);
            newIntent.putExtra(HookHelper.ORIGIN_INTENT_KEY, rawIntent);
            //替换掉Intent
            if (index != -1) {
                args[index] = newIntent;
                Log.e(TAG, "new Intent>>"+newIntent.getComponent().toString());
            }
            return method.invoke(mActivityManager, args);
        }
        return method.invoke(mActivityManager,args);
    }
}
