package com.ben.android.hook_service.handler;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.ben.android.hook_service.util.Consts;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/20
 * <p>
 * IActivityManager代理
 */
public class IActivityManagerHandler implements InvocationHandler {
    private static final String TAG = IActivityManagerHandler.class.getSimpleName();
    private Object mIActivityManager;

    public IActivityManagerHandler(Object IActivityManager) {
        mIActivityManager = IActivityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**
         *    public int bindService(IApplicationThread caller, IBinder token, Intent service,
         240            String resolvedType, IServiceConnection connection, int flags,
         241            String callingPackage, int userId) throws RemoteException;
         */
        if ("startService".equals(method.getName())
                || "bindService".equals(method.getName())
                || "stopService".equals(method.getName())) {
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
                Log.e(TAG, "origin Intent>>" + rawIntent.getComponent().toString());
            }
            if (index != -1) {
                Intent newIntent = new Intent();
                ComponentName componentName = new ComponentName(Consts.PACKAGE_NAME, Consts.PROXY_CLASS);
                newIntent.setComponent(componentName);
                newIntent.putExtra(Consts.SERVICE_KEY, rawIntent);

                args[index] = newIntent;
                Log.e(TAG, "new Intent>>" + newIntent.getComponent().toString());
            }
        }


        return method.invoke(mIActivityManager, args);
    }
}
