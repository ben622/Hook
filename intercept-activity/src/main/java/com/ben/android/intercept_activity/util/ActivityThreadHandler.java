package com.ben.android.intercept_activity.util;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/15
 * <p>
 * 通过对Handler的代理 将原来的替换的Activity进行替换
 */
public class ActivityThreadHandler implements Handler.Callback {
    private Handler mActivityHandler;


    public ActivityThreadHandler(Handler activityHandler) {
        mActivityHandler = activityHandler;
    }
    @Override
    public boolean handleMessage(Message message) {
        try {
            if (message.what == 100) {

                Field intentField = message.obj.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                Intent intent = (Intent) intentField.get(message.obj);
                Intent originIntent = intent.getParcelableExtra(HookHelper.ORIGIN_INTENT_KEY);
                if (originIntent != null) {
                    intent.setComponent(originIntent.getComponent());
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mActivityHandler.handleMessage(message);

        return true;
    }
}
