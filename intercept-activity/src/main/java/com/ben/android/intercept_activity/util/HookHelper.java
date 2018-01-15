package com.ben.android.intercept_activity.util;


import android.os.Build;
import android.os.Handler;
import android.os.IInterface;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/15
 * <p>
 * * IActivityManager 代理
 * 不论是使用context.startActivity方式进行跳转Activity或是使用activity.startActivity方式进行跳转，都是通过ActivityManagerNative.getDef获取服务
 * 顺着源码继续往下走调用了ActivityManager.getService获取服务 如下
 * private static final Singleton<IActivityManager> IActivityManagerSingleton =
 * new Singleton<IActivityManager>() {
 * @Override protected IActivityManager create() {
 * final IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
 * final IActivityManager am = IActivityManager.Stub.asInterface(b);
 * return am;
 * }
 * };
 * 根据上述的源码我们的Hook点可以针对静态变量IActivityManagerSingleton进行操作。
 * todo：通过动态代理方式构造一个和IActivityManager类似的Iinterface，通过对IActivityManager.startActivity进行拦截进行我们的Activity替换操作
 * todo：从而达到StubActivity没有在清单文件中注册也能跳转的功能
 */
public class HookHelper {
    public static final String ORIGIN_INTENT_KEY = "ORIGIN_INTENT_KEY";

    public static void hookActivityManager() {
        try {
            Field iActivityManagerSingletonField;
            if (Build.VERSION.SDK_INT >= 26) {
                Class<?> activityManager = Class.forName("android.app.ActivityManager");
                iActivityManagerSingletonField = activityManager.getDeclaredField("IActivityManagerSingleton");
            } else {
                Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
                iActivityManagerSingletonField = activityManagerNativeClass.getDeclaredField("gDefault");
            }
            iActivityManagerSingletonField.setAccessible(true);

            Object def = iActivityManagerSingletonField.get(null);

            //构建SingletonClass
            Class<?> singletonCalss = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonCalss.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            //获取系统的IActivityManager
            Object baseIActivityManager = mInstanceField.get(def);
            //使用动态代理方式替换
            Object proxy = Proxy.newProxyInstance(singletonCalss.getClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new IActivityManagerHandler(baseIActivityManager));
            mInstanceField.set(def, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookActivityThreadHandler() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadField.setAccessible(true);
            Object currentActivityThread = currentActivityThreadField.get(null);

            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mH = (Handler) mHField.get(currentActivityThread);

            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(mH, new ActivityThreadHandler(mH) );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
