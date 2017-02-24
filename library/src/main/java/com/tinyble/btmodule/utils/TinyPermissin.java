package com.tinyble.btmodule.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.Arrays;
import java.util.List;

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

    public static boolean callAndBackMyPermissionResult(Activity curActivity, String toCallPermissionName) {
        //调用该方法从现象上去看，是需要系统回复一下，想要的权限的授予结果
        //因为每次调用了一次后会在Activity的onRequestPermissionsResult()回调一下目标权限的状态
        return curActivity.checkCallingPermission(toCallPermissionName) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断某权限是否被允许了，如果没有被允许，是否需要立即去申请该权限,当前如果该权限被用户手动在管理/设置界面中禁止了，再请求也没有用
     * @param curActivity 当前Activity
     * @param permissionName 目标权限
     * @param isJustApplyIfDenied 如果目标权限被是被系统拒绝的状态，是否需要立即申请，true:需要去申请；false:不需要去申请
     * @param requestCode 需要去申请时的权限请求区分码
     * @return true:目标权限被允许的；false:目标权限是被拒绝的
     */
    public static boolean isPermissionGranted(Activity curActivity, String permissionName, boolean isJustApplyIfDenied, int requestCode) {
        //这样子，调用checkCallingPermission()系统也会从Activity 的onRequestPermissionsResult()方法回调一次
        // checkCallingPermission()估计是判断是否 呼叫了某权限
        boolean isThePermissionGranted = curActivity.checkCallingOrSelfPermission(permissionName) == PackageManager.PERMISSION_GRANTED;
        if (!isThePermissionGranted && isJustApplyIfDenied) {
            //该权限未被授予，并且希望立刻去申请,如果用户手动在管理/设置界面中禁止了该权限，请求也没用(没反应)
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
//        int targetPermissionIndex = Arrays.binarySearch(permissions, targetPermission);//这个有bug，二分查找只适合有序数组
        List<String> listPermissions = Arrays.asList(permissions);
        int targetPermissionIndex = listPermissions.indexOf(targetPermission);
        if (targetPermissionIndex >= 0 && targetPermissionIndex <= grantResults.length - 1) {
            return grantResults[targetPermissionIndex] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}
