package com.tinyble.btmodule;

import android.bluetooth.BluetoothDevice;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 17:10
 * DESC: 对系统的BluetoothDevice进行再封装
 */
public class ExtendedBluetoothDev {
    private final BluetoothDevice delegate;
    /** 接收信号强度**/
    private int rssi;
    private String name;

    public ExtendedBluetoothDev(BluetoothDevice delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.getName();
    }

    public BluetoothDevice getDevice() {
        return delegate;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
