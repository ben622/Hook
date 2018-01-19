package com.ben.android.hook_breceiver.util;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/19
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
}
