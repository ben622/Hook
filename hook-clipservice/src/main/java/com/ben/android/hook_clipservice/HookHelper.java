package com.ben.android.hook_clipservice;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * Created by 12532 on 2018/1/14.
 */

public class HookHelper {
    public static void attack() {
        try {
            // TODO: 2018/1/14 获取ServiceManager
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            // TODO: 2018/1/14 获取系统原来的Binder
            Method getServiceMethod = serviceManagerClass.getDeclaredMethod("getService", String.class);
            IBinder clipBaseBinder = (IBinder) getServiceMethod.invoke(serviceManagerClass, Context.CLIPBOARD_SERVICE);
            // TODO: 2018/1/14 通过动态代理构造一个HookBinder
            IBinder hookBinder = (IBinder) Proxy.newProxyInstance(serviceManagerClass.getClassLoader(), new Class[]{IBinder.class}, new ClipHookHandler(clipBaseBinder));
            // TODO: 2018/1/14 将ServiceManager中的cache binder替换成HookBinder
            Field sCacheField = serviceManagerClass.getDeclaredField("sCache");
            sCacheField.setAccessible(true);
            HashMap<String, IBinder> map = (HashMap<String, IBinder>) sCacheField.get(null);
            map.put(Context.CLIPBOARD_SERVICE, hookBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
