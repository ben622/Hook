package com.ben.android.hook_example_app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/19
 */
public class PluginBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "PluginBroadcastReceiver", Toast.LENGTH_SHORT).show();
    }
}
