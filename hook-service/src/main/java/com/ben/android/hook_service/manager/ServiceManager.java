package com.ben.android.hook_service.manager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ben.android.hook_service.app.App;
import com.ben.android.hook_service.util.Consts;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/20
 * <p>
 */
public class ServiceManager {
    private static Map<String, ServiceInfo> mPluginServices = new HashMap<String, ServiceInfo>();
    private static Map<String, Service> mServices = new HashMap<String, Service>();

    public static Map<String, ServiceInfo> getPluginServices() {
        return mPluginServices;
    }

    public static Map<String, Service> getServices() {
        return mServices;
    }

    public static void onDestory() {
        for (String key : mServices.keySet()) {
            mServices.get(key).onDestroy();
        }
    }

    public static Service createService(Intent intent) throws Exception {
        if (intent == null) {
            return null;
        }
        Intent rawIntent = intent.getParcelableExtra(Consts.SERVICE_KEY);
        if (rawIntent == null) {
            return null;
        }
        try {
            Class<?> serviceClass = Class.forName(rawIntent.getComponent().getClassName());
            if (serviceClass.getSuperclass() == null ||
                    !serviceClass.getSuperclass().equals(Service.class)) {
                throw new Exception("目标Class非Service组件");
            }
            if (getServices().get(rawIntent.getComponent().getClassName()) != null) {
                return getServices().get(rawIntent.getComponent().getClassName());
            }

            //通过ActivityThread创建Service
            Class<?> createServiceDataClass = Class.forName("android.app.ActivityThread$CreateServiceData");

            //构造CreateServiceData
            Constructor<?> constructor = createServiceDataClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object createServiceDataObj = constructor.newInstance();

            //对CreateServiceData初始化
            Field tokenField = createServiceDataClass.getDeclaredField("token");
            tokenField.setAccessible(true);
            Binder token = new Binder();
            tokenField.set(createServiceDataObj, token);

            Field infoField = createServiceDataClass.getDeclaredField("info");
            infoField.setAccessible(true);
            ServiceInfo serviceInfo = mPluginServices.get(rawIntent.getComponent().getClassName());
            if (serviceInfo == null) {
                return null;
            }
            //如果这里不使用宿主的包，则需要Hook掉系统的ClassLoader使用自己的ClassLoader进行加载
            serviceInfo.applicationInfo.packageName = App.getContext().getPackageName();
            infoField.set(createServiceDataObj, serviceInfo);

            Field compatInfoField = createServiceDataClass.getDeclaredField("compatInfo");
            compatInfoField.setAccessible(true);
            Class<?> compatibilityClass = Class.forName("android.content.res.CompatibilityInfo");
            Field defaultCompatibilityField = compatibilityClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
            Object defaultCompatibility = defaultCompatibilityField.get(null);
            compatInfoField.set(createServiceDataObj, defaultCompatibility);

            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object activityThreadObj = sCurrentActivityThreadField.get(null);
            //private void handleCreateService(CreateServiceData data)

            try {
                Method handleCreateServiceMethod = activityThreadClass.getDeclaredMethod("handleCreateService", createServiceDataClass);
                handleCreateServiceMethod.setAccessible(true);
                handleCreateServiceMethod.invoke(activityThreadObj, createServiceDataObj);
            } catch (Exception e) {
                throw new Exception("暂未对当前系统进行适配");
            }
            //移除在ActivityThread mServices中添加的Serivce实例并返回

            Field mServicesField = activityThreadClass.getDeclaredField("mServices");
            mServicesField.setAccessible(true);
            Map<IBinder, Service> serviceMap = (Map<IBinder, Service>) mServicesField.get(activityThreadObj);
            Service service = serviceMap.get(token);
            serviceMap.remove(token);
            mServices.put(rawIntent.getComponent().getClassName(), service);
            return service;
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("ClassNotFound@" + rawIntent.getComponent().getClassName() + ",若目标在插件中请先将插件加载到宿主中");
        }
    }
}
