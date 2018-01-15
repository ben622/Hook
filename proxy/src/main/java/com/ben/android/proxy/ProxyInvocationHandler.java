package com.ben.android.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by 12532 on 2018/1/14.
 */

public class ProxyInvocationHandler implements InvocationHandler {
    private Object mObject;

    public ProxyInvocationHandler(Object object) {
        mObject = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("doShopping".equals(method.getName())) {
            //黑钱
            double money = (double) args[0];
            money-= 100;
            Object invoke = method.invoke(mObject, money);
            return invoke;
        }
        return null;
    }
}
