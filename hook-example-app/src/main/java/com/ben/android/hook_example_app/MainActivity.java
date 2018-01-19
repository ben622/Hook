package com.ben.android.hook_example_app;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ben.android.hook_example_app.receiver.PluginBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(new PluginBroadcastReceiver(), new IntentFilter("com.ben.android.broadcastreceiver"));
    }
    public void onClick(View view) {
        sendBroadcast(new Intent("com.ben.android.broadcastreceiver"));
    }
}
