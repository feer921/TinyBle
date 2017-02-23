package com.tinyble.btmodule;

import android.bluetooth.BluetoothDevice;
import com.tinyble.btmodule.utils.TinyUtil;
import java.io.Serializable;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 17:10
 * DESC: 对系统的BluetoothDevice进行包裹封装
 */
public class ExtendedBluetoothDev implements Serializable{
    private final BluetoothDevice delegate;
    /** 接收信号强度**/
    private int rssi;
    private String name;
    private byte[] scanRecord;
    public ExtendedBluetoothDev(BluetoothDevice delegate) {
        this.delegate = delegate;
    }

    public void setDevName(String devName) {
        this.name = devName;
    }
    public String getName() {
        if (!TinyUtil.isEmpty(name)) {
            return name;
        }
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

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public int getBtType() {
        if (TinyUtil.isApiCompatible(18)) {
            return delegate.getType();
        }
        return BluetoothDevice.DEVICE_TYPE_UNKNOWN;
    }

    public String getBtTypeDesc() {
        String desc = "unknown";
        switch (getBtType()) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                desc = "BR/EDR";
                break;
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                desc = "BR/EDR/LE";
                break;
            case BluetoothDevice.DEVICE_TYPE_LE:
                desc = "BLE";
                break;
        }
        return desc;
    }

    public int getDevBondedState() {
        return delegate.getBondState();
    }

    public String getBondedStateDesc() {
        switch (getDevBondedState()) {
            case BluetoothDevice.BOND_NONE:
                return "未匹配";
            case BluetoothDevice.BOND_BONDING:
                return "正在匹配";
            case BluetoothDevice.BOND_BONDED:
                return "已匹配";
        }
        return "匹配状态未知";
    }

    public String getAddress() {
        return delegate.getAddress();
    }
}
