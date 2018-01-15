package com.ben.android.intercept_activity.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ben.android.intercept_activity.R;

public class TargetActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ((TextView) findViewById(R.id.textView)).setText("插件化Activity1");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy" );
    }
}
