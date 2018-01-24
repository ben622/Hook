package com.ben.android.hook_cprovider.util;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/24
 *
 * IDEA
 * 当发起ContentProvider查询的时候首先会在当前进程中查找是否有匹配，若没有则通过AMS进行查找
 * SO可以将插件中的ContentProvider手动注册到进程中，但是这样第三方APP不能通过AMS查找到插件
 * 中的ContentProvider。很显然这样并没有达到我们的目的，如果想要让第三方APP能够匹配到我们的
 * 插件ContentProvider我们可以使用代理分发技术伪造一个StubContentProvider，通过对URI地址
 * 进行特定的约束从而达到第三方APP访问插件ContentProvider
 * 例：
 * 第三方想要访问插件的URI content://com.ben.android.plugin_authority/query 但是这样不能
 * 匹配到因为跨进程问题我们只能通过URI约束将其改为
 * content://com.ben.android.host_authority/com.ben.android.plugin_authority/query
 *
 */
public class HookHelper {
    private static List<ProviderInfo> providerInfos = new ArrayList<ProviderInfo>();

    public static List<ProviderInfo> getProviderInfos() {
        return providerInfos;
    }

    public static void init(Context context) {
        //
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object activityThreadObj = sCurrentActivityThreadField.get(null);

            Method installContentProvidersMethod = activityThreadClass.getDeclaredMethod("installContentProviders", Context.class, List.class);
            installContentProvidersMethod.setAccessible(true);
            Log.e("HookHelper", "init: " + Arrays.toString(providerInfos.toArray()));
            for (ProviderInfo providerInfo : providerInfos) {
                providerInfo.applicationInfo.packageName = context.getPackageName();
            }
            installContentProvidersMethod.invoke(activityThreadObj, context, providerInfos);

        } catch (Exception e) {
            e.printStackTrace();
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
