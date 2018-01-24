package com.ben.android.hook_cprovider;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ben.android.hook_cprovider.provider.StubContentProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        getContentResolver().query(Uri.parse(StubContentProvider.HOST_URI+ "/com.ben.android.plugin_authority"),null,null,null,null);

    }
}
