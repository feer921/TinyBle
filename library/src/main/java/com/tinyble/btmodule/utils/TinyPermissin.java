package com.tinyble.btmodule.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.Arrays;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-23
 * Time: 17:14
 * DESC: Android6.0+ 的系统权限的检查与申请等
 */
public class TinyPermissin {
    /**
     * 只查询某个权限是否被授予了
     * @param curActivity 当前Activity
     * @param permissionName 权限名，eg:. {@link android.Manifest.permission#ACCESS_FINE_LOCATION} and so on...
     * @return true:该权限被授予了；false:该权限被拒绝了
     */
    public static boolean isPermissionGranted(Activity curActivity, String permissionName) {
        return isPermissionGranted(curActivity, permissionName, false, 0);
    }

    public static boolean isPermissionGranted(Activity curActivity, String permissionName, boolean isJustApplyIfDenied, int requestCode) {
        //这样子，调用checkCallingPermission()系统也会从Activity 的onRequestPermissionsResult()方法回调一次
        boolean isThePermissionGranted = curActivity.checkCallingPermission(permissionName) == PackageManager.PERMISSION_GRANTED;
        if (!isThePermissionGranted && isJustApplyIfDenied) {
            //该权限未被授予，并且希望立刻去申请
            requestAPermission(curActivity,requestCode,permissionName);
        }
        return isThePermissionGranted;
    }

    @SuppressLint("NewApi")
    public static void requestAPermission(Activity curActivity,int requestCode, String... permissions) {
        if (TinyUtil.isApiCompatible(23)) {
            curActivity.requestPermissions(permissions,requestCode);
        }
    }

    /**
     * 由于android 6.0+ (>= api23)的系统对于权限的申请结果返回时是通过{@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * 方法返回，本方法为方便挑出单个权限的被授予情况
     * @param targetPermission 要查询的目标权限名
     * @param permissions 系统返回的所有申请的权限
     * @param grantResults 对所申请的权限所对应的 被授予还是被拒绝的值的集
     * @return true:该权限被授予了；false:该权限被拒绝了
     */
    public static boolean isThePermissionGrantedInResults(String targetPermission, String[] permissions, int[] grantResults) {
        if (TinyUtil.isEmpty(targetPermission)) {
            return false;
        }
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        if (grantResults == null || grantResults.length == 0) {
            return false;
        }
        int targetPermissionIndex = Arrays.binarySearch(permissions, targetPermission);
        if (targetPermissionIndex >= 0 && targetPermissionIndex <= grantResults.length - 1) {
            return grantResults[targetPermissionIndex] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}
