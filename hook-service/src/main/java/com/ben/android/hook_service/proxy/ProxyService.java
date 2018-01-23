package com.ben.android.hook_service.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ben.android.hook_service.manager.ServiceManager;


/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/20
 * 服务代理分发，Service生命周期由此控制
 */
public class ProxyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //因为在ActivityThread创建Service的时候已经回掉过onCreate方法  所以这里不需要做处理
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Service service = getService(intent);
        if (service != null) {
            return  service.onStartCommand(intent, flags, startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Service service = getService(intent);
        if (service != null) {
            return service.onBind(intent);
        }
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Service service = getService(intent);
        if (service != null) {
            return service.onUnbind(intent);
        }
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceManager.onDestory();
    }

    private Service getService(Intent intent) {
        try {
            return ServiceManager.createService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
