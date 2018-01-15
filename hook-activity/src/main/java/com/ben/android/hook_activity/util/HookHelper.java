package com.ben.android.hook_activity.util;


import android.app.Activity;
import android.app.Instrumentation;
import android.util.Log;

import com.ben.android.hook_activity.hook.ContextInstrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/14
 * <p>
 * Hook初始化  将ActivityThread中的Instrumentation替换成我们自己的Instrumentation
 */
public class HookHelper {
    private static final String TAG = HookHelper.class.getSimpleName();

    public static void attackActivity(Activity activity) {
        try {
            Field mInstrumentationField= getInstrumentationField(activity.getClass());
            if (mInstrumentationField == null) {
                Log.e(TAG, "attackActivity: Not found Activity");
                return;
            }
            mInstrumentationField.setAccessible(true);
            Instrumentation baseInstrumentation = (Instrumentation) mInstrumentationField.get(activity);
            //替换
            ContextInstrumentation contextInstrumentation = new ContextInstrumentation(baseInstrumentation);
            mInstrumentationField.set(activity, contextInstrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Field getInstrumentationField(Class classs) throws Exception {
        if (classs.getName().equals(Activity.class.getName())) {
            return classs.getDeclaredField("mInstrumentation");
        }
        if (classs.getSuperclass() != null) {
            return getInstrumentationField(classs.getSuperclass());
        }
        return null;
    }

    public static void attackContext() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            //获取静态ActivityThread对象
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object activityThread = currentActivityThreadMethod.invoke(null);
            //对ActivityThread中的Instrumentation替换
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation baseInstrumentation = (Instrumentation) mInstrumentationField.get(activityThread);
            //替换
            ContextInstrumentation contextInstrumentation = new ContextInstrumentation(baseInstrumentation);
            mInstrumentationField.set(activityThread, contextInstrumentation);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "init: " + e.getMessage());
        }
    }

}
