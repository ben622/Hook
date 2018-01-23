package com.ben.android.hook_service.util;

import android.content.Context;
import android.os.Build;

import com.ben.android.hook_service.handler.IActivityManagerHandler;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/20
 */
public class HookHelper {
    public static boolean hookBaseDexClassLoader(Context context, File apk, File dex) {
        /**将插件添加到宿主BaseDexClassLoader >> pathList >> dexElements*/
        try {
            // TODO: 2018/1/18 获取BaseDexClassLoader
            Field pathListField = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathList = pathListField.get(context.getClassLoader());
            // TODO: 2018/1/18 获取dexElements
            Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object[] rawElements = (Object[]) dexElementsField.get(pathList);
            // TODO: 2018/1/18 解析插件APK，构建Element
            Class<?> elementType = rawElements.getClass().getComponentType();
            //通过Element的构造函数进行构造
            //public Element(DexFile dexFile, File dexZipPath)
            //public Element(File dir, boolean isDirectory, File zip, DexFile dexFile)
            Constructor<?> constructor = elementType.getConstructor(File.class, boolean.class, File.class, DexFile.class);
            DexFile dexFile = DexFile.loadDex(apk.getCanonicalPath(), dex.getAbsolutePath(), 0);
            Object element = constructor.newInstance(null,false,apk,dexFile);
            // TODO: 2018/1/18 将新的Element添加到原来的数组中
            Object newElements = Array.newInstance(elementType, rawElements.length + 1);
            // TODO: 2018/1/18 将原来的Element复制到新的集合中
            Object[] elements = {element};
            System.arraycopy(rawElements, 0, newElements, 0, rawElements.length);
            System.arraycopy(elements, 0, newElements, rawElements.length, elements.length);
            // TODO: 2018/1/18  替换
            dexElementsField.set(pathList, newElements);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Hook掉AMS的远程代理本地Binder，对startService进行拦截替换成ProxyService
     * @return
     */
    public static boolean hookIActivityManager() {
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

            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
