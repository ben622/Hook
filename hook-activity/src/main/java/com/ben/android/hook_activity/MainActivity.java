package com.ben.android.hook_activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ben.android.hook_activity.util.HookHelper;
import com.ben.android.hook_activity.view.Test1Activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/14
 *
 * context>>startActivity 方式
 * ContextImpl.startActivity >> ActivityThread.getInstrumentation().execStartActivity()
 * Hook点在于getInstrumentation().execStartActivity()，通过对Instrumentation对象进行代理从而实现跳转过程的偷梁换柱
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, Test1Activity.class);
        intent.putExtra("params1", "hook_activity_1");
        intent.putExtra("params2", "hook_activity_2");
        intent.putExtra("params3", 3);
        intent.putExtra("params4", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (view.getId()) {
            case R.id.id_use_context_redirect:
                HookHelper.attackContext();
                getApplicationContext().startActivity(intent);
                break;
            case R.id.id_use_activity_redirect:
                HookHelper.attackActivity(this);
                startActivity(intent);
                break;
        }
    }
}
