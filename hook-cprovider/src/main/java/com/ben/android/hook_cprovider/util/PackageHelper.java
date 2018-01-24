package com.ben.android.hook_cprovider.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.AssetManager;


import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/20
 * APK解析
 */
public class PackageHelper {

    public static final void parsePackage(File apk) {
        try {
            Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
            Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            Object packageParser = packageParserClass.newInstance();

            Object packageObj = parsePackageMethod.invoke(packageParser, apk, PackageManager.GET_SERVICES);
            Class<?> packageClass = Class.forName("android.content.pm.PackageParser$Package");
            Field providersField = packageClass.getDeclaredField("providers");
            List<Object> providers = (List<Object>) providersField.get(packageObj);
            for (Object provider : providers) {
                Field infoField = provider.getClass().getDeclaredField("info");
                infoField.setAccessible(true);
                ProviderInfo providerInfo = (ProviderInfo) infoField.get(provider);
                HookHelper.getProviderInfos().add(providerInfo);
            }
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

}
