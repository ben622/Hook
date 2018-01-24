package com.ben.android.hook_cprovider.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/24
 *
 * 插件ContentProvider中转，通过对URI进行特定解析
 */
public class StubContentProvider extends ContentProvider {
    public static final String AUTHORITIES = "com.ben.android.host_authority";
    public static final String HOST_URI = "content://"+AUTHORITIES;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return getContext().getContentResolver().query(parseUri(uri),projection,selection,selectionArgs,sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return getContext().getContentResolver().insert(parseUri(uri),values);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return getContext().getContentResolver().delete(parseUri(uri),selection,selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return getContext().getContentResolver().update(parseUri(uri), values, selection, selectionArgs);
    }

    /**
     * content://com.ben.android.host_authority/com.ben.android.plugin_authority/1
     * @param uri
     * @return
     */
    private Uri parseUri(Uri uri) {
        String authority = uri.getAuthority();
        String uristr = uri.toString();
        String newUri = uristr.replaceAll(authority + "/", "");
        Log.e("StubContentProvider", "newUri: "+newUri );
        return Uri.parse(newUri);
    }
}
