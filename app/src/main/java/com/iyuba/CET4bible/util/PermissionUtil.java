package com.iyuba.CET4bible.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

public class PermissionUtil {
    public static boolean checkStoragePermission(Context context) {
        if(context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            return false ;
        }
        return true;
    }
    public static void requestPermission(Context context){
        ((Activity)context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }
}
