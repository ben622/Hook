package com.ben.android.hook_clipservice;

import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/14
 *
 * 伪造剪切板Binder
 */
public class ClipHookHandler implements InvocationHandler {
    private IBinder mBaseBinder;

    public ClipHookHandler(IBinder baseBinder) {
        mBaseBinder = baseBinder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("queryLocalInterface".equals(method.getName())) {
            //通过动态代理方式 构造一个和剪切板Binder一摸一样的Binder
            return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                    new Class[]{IBinder.class, IInterface.class, Class.forName("android.content.IClipboard")},
                    new ClipHandler(mBaseBinder));
        }
        return method.invoke(mBaseBinder,args);
    }
}
