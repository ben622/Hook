package com.ben.android.hook_clipservice;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/14
 */
public class ClipHandler implements InvocationHandler {
    private Object mClipManager;

    public ClipHandler(IBinder baseBinder) {
        //需要通过XXX.Stub.asInterface(b)方式获取ClipManager
        try {
            Method method = Class.forName("android.content.IClipboard$Stub").getDeclaredMethod("asInterface", IBinder.class);
            //通过asInterface
            mClipManager = method.invoke(null, baseBinder);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getPrimaryClip".equals(method.getName())) {
            return ClipData.newPlainText(null, "剪切板服务已被Hook");
        }

        if ("setPrimaryClip".equals(method.getName())) {
            return ClipData.newPlainText(null, "剪切板服务已被Hook");
        }
        return method.invoke(mClipManager, args);
    }
}
