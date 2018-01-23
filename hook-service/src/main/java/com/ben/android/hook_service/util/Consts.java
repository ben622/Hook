package com.ben.android.hook_service.util;

import android.os.Build;

import com.ben.android.hook_service.BuildConfig;
import com.ben.android.hook_service.proxy.ProxyService;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/20
 */
public class Consts {
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String PROXY_CLASS = ProxyService.class.getName();
    public static final String SERVICE_KEY = "SERVICE_KEY";
}
