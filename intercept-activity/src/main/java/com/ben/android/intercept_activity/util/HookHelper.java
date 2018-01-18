package com.ben.android.intercept_activity.util;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.IInterface;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

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
            mCallbackField.set(mH, new ActivityThreadHandler(mH));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyAssetsFileToSD(Context context, String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(fileName);
            File extractFile = context.getFileStreamPath(fileName);
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(fos);
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignore
        }
    }

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
}
