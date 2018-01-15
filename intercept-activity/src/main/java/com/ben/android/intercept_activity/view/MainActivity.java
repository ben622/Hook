package com.ben.android.intercept_activity.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ben.android.intercept_activity.R;
import com.ben.android.intercept_activity.util.HookHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HookHelper.hookActivityManager();
        HookHelper.hookActivityThreadHandler();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_hook_target1:
                startActivity(new Intent(this,TargetActivity.class));
                break;
            case R.id.id_hook_target2:
                startActivity(new Intent(this,Target2Activity.class));
                break;
        }
    }
}
