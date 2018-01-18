package com.ben.android.hook_example_app.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;


public class StaticActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("com.ben.android.hook_example_app.view.StaticActivity");
        setContentView(textView);
    }
}
