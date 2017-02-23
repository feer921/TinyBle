package com.tinyble.btmodule.utils;

import android.os.Build;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 14:09
 * DESC:
 */
public class TinyUtil {
    public static boolean isEmpty(CharSequence charSequence) {
        return null == charSequence
                || charSequence.length() == 0
                || charSequence.toString().trim().length() == 0
                ;
    }

    public static boolean isApiCompatible(int targetApiLevel){
        if (targetApiLevel <= 0) {
            return false;
        }
        //当前系统的SDK版本大于或者等于目标版本,eg:. 目标版本为18(android 4.3),当前系统版本为18或者19则兼容目标版本
        return Build.VERSION.SDK_INT >= targetApiLevel;
    }
}
