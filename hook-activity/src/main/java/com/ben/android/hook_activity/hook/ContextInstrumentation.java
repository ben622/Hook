package com.ben.android.hook_activity.hook;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/14
 * <p>
 * 对跳转的Activity进行拦截并打印跳转携带的参数以及相关信息
 */
public class ContextInstrumentation extends Instrumentation {
    private static final String TAG = ContextInstrumentation.class.getSimpleName();

    //需要通过系统原生的Instrumentation进行跳转
    private Instrumentation mBaseInstrumentation;

    public ContextInstrumentation(Instrumentation baseInstrumentation) {
        mBaseInstrumentation = baseInstrumentation;
    }



    public ActivityResult execStartActivity(  Context who, IBinder contextThread, IBinder token, Activity target,
                                              Intent intent, int requestCode, Bundle options) {

        outlog(who, contextThread, token, target, intent, requestCode,options);
        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(mBaseInstrumentation, who, contextThread, token, target, intent, requestCode,options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日志输出
     *
     * @param who
     * @param contextThread
     * @param token
     * @param intent
     * @param requestCode
     * @param options
     */
    private void outlog( Context who, IBinder contextThread, IBinder token, Activity target,
                         Intent intent, int requestCode, Bundle options) {
        Log.e(TAG, "outlog: >>>>>>>>>>>>>>>>>>>>>>>跳转Hook>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
        Log.e(TAG, "who: " + who.getClass());
        Log.e(TAG, "requestCode: " + requestCode);

        if (intent != null) {
            Log.e(TAG, "intent>>action: " + intent.getAction());
            Log.e(TAG, "intent>>package: " + intent.getPackage());
            Log.e(TAG, "intent>>scheme: " + intent.getScheme());
            Log.e(TAG, "intent>>data: " + intent.getDataString());

            if (intent.getExtras() != null) {
                for (String item : intent.getExtras().keySet()) {
                    Log.e(TAG, "bundle>>" + item + ": " + intent.getExtras().get(item));
                }
            }
        }

        if (options != null) {
            for (String item : options.keySet()) {
                Log.e(TAG, "bundle>>" + item + ": " + options.get(item));
            }
        }
        Log.e(TAG, "outlog: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
    }
}
