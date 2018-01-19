package com.ben.android.hook_breceiver.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/19
 * 插件包解析
 */
public class PackageHelper {
    private static final String TAG = PackageHelper.class.getSimpleName();
    private static Map<ActivityInfo, List<? extends IntentFilter>> rmaps = new HashMap<ActivityInfo, List<? extends IntentFilter>>();

    /**
     * 解析APK
     * 使用PackageParser API进行解析
     *
     * @param apk
     */
    public static boolean parsePackage(Context context, File apk) {
        try {
            Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
            //public Package parsePackage(File packageFile, int flags)
            Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            Object packageParser = packageParserClass.newInstance();
            //解析APK获得Package对象 >> android.content.pm.PackageParser$Package
            Object packageParserObj = parsePackageMethod.invoke(packageParser, apk, PackageManager.GET_SERVICES);
            //获得Package对象中的receivers
            /**
             public final ArrayList<Permission> permissions = new ArrayList<Permission>(0);
             public final ArrayList<PermissionGroup> permissionGroups = new ArrayList<PermissionGroup>(0);
             public final ArrayList<Activity> activities = new ArrayList<Activity>(0);
             public final ArrayList<Activity> receivers = new ArrayList<Activity>(0);
             public final ArrayList<Provider> providers = new ArrayList<Provider>(0);
             public final ArrayList<Service> services = new ArrayList<Service>(0);
             public final ArrayList<Instrumentation> instrumentation = new ArrayList<Instrumentation>(0);
             */
            Field receiversField = packageParserObj.getClass().getDeclaredField("receivers");
            List receivers = (List) receiversField.get(packageParserObj);
            //需要将ArrayList<Activity> 进行转换
            /**
             * Activity >> Component
             */
            Class<?> componentClass = Class.forName("android.content.pm.PackageParser$Component");
            Field intentsField = componentClass.getDeclaredField("intents");
            for (Object receiver : receivers) {
                List<? extends IntentFilter> intentFilters = (List<? extends IntentFilter>) intentsField.get(receiver);
                Field infoField = receiver.getClass().getDeclaredField("info");
                ActivityInfo activityInfo = (ActivityInfo) infoField.get(receiver);
                Log.e(TAG, "==================================");
                Log.e(TAG, "ActivityInfo Name" + activityInfo.name);
                Log.e(TAG, "ActivityInfo PackageName" + activityInfo.packageName);
                Log.e(TAG, "==================================");
                rmaps.put(activityInfo, intentFilters);
            }
            _init_register(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void _init_register(Context context) throws Exception {
        for (ActivityInfo activityInfo : rmaps.keySet()) {
            for (IntentFilter intentFilter : rmaps.get(activityInfo)) {
                Class<?> classs = Class.forName(activityInfo.name);
                context.registerReceiver((BroadcastReceiver) classs.newInstance(), intentFilter);
            }
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
