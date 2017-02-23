package com.tinyble.btmodule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.tinyble.btmodule.utils.TinyUtil;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-21
 * Time: 15:33
 * DESC:
 */
public class TinyBle {
    private BluetoothAdapter bluetoothAdapter;
    private IBtActions btActionsCallback;
    private BtInfosReceiver btInfosReceiver;
    private BleScanDock bleScanDock;
    private int curScanMode;
    private static final int SCAN_MODE_NONE = 0;
    private static final int SCAN_MODE_CLASSIC = 1;
    private static final int SCAN_MODE_BLE = 2;
    private static volatile TinyBle tinyBleInstance;
    private TinyBle() {
    }
    public static TinyBle getMe() {
        if (tinyBleInstance == null) {
            synchronized (TinyBle.class) {
                if (tinyBleInstance == null) {
                    tinyBleInstance = new TinyBle();
                }
            }
        }
        return tinyBleInstance;
    }

    public void configBtActionsListener(IBtActions btActionsCallback) {
        this.btActionsCallback = btActionsCallback;
    }
    /**
     * 代码直接开启或关闭蓝牙模块
     * @param isToEnable
     * @return false：操作失败，此时需要调用使用意图的方式让用户开启；true:操作成功
     */
    public boolean switchBtModule(boolean isToEnable) {
        return switchBtModule(isToEnable, true);
    }

    private boolean switchBtModule(boolean isToEnable, boolean directedToEnable) {
//        if (bluetoothAdapter == null) {
//            return false;
//        }
        checkAdapterNotNull();
        boolean isCurBtEnabled = bluetoothAdapter.isEnabled();
        boolean isOptSuc = false;
        if (isToEnable != isCurBtEnabled) {
            //状态不一致，才去操作
            if (!isToEnable) {//去关闭则直接关闭蓝牙模块
                isOptSuc = bluetoothAdapter.disable();
            }
            else{
                if (directedToEnable) {
                    isOptSuc = bluetoothAdapter.enable();
                }
                else{
                    //发送意图去让用户选择开启
                    // to do something?????
                }
            }
        }
        return isOptSuc;
    }

    /**
     * 通过意图的方式启动系统界面让用户去开启蓝牙模块
     * @param curActivity 当前的Activity
     * @param requestCode 区分用的请求码
     * 调用此方法跳转到系统界面让用户自己去开启蓝牙模块后，需要在发起的Activity里的{@link Activity#onActivityResult(int, int, Intent)} 方法中来接收
     * 是否被用户开启了，区分的条件为所传入的requestCode,开启的结果以{@link Activity#RESULT_OK}
     * 以及{@link Activity#RESULT_CANCELED}
     * 或者从广播接收者来接收蓝牙模块状态的更改
     */
    public void action2EnableBT(Activity curActivity, int requestCode) {
        Intent actionIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        curActivity.startActivityForResult(actionIntent, requestCode);
    }

    /**
     * 非BLE扫描时的扫描操作，需要先调用一下这个方法
     * @param context
     */
    public void registerBtActionReceiver(Context context) {
        if (btInfosReceiver == null) {
            btInfosReceiver = new BtInfosReceiver(btActionsCallback);
        }
        if (!btInfosReceiver.isRegistered) {//没有被注册
            btInfosReceiver.appendDefActions();
            context.registerReceiver(btInfosReceiver, btInfosReceiver.intentFilter);
            btInfosReceiver.isRegistered = true;
        }
    }

    /**
     * 启动蓝牙模块的扫描，扫描一切蓝牙设备，不区分是BT3.0的还是BLE设备
     * @return true:启动成功；false:启动失败
     */
    public boolean startScanCommonBt() {
        curScanMode = SCAN_MODE_CLASSIC;
        if (null != bluetoothAdapter) {
            if (bluetoothAdapter.isDiscovering()) {
                return true;
            }
            return bluetoothAdapter.startDiscovery();
        }
        return false;
    }

    /**
     * 获取当前系统内已经绑定过的相关蓝牙设备
     * @param justOnlyBleTypeDev true:只挑出BLE设备；false:挑出非BLE设备，当然会受到当前系统版本的影响
     * @return 已经绑定过的并且满足蓝牙类型的设备集
     */
    @SuppressLint("NewApi")
    public ArrayList<BluetoothDevice> getBondedBtDevices(boolean justOnlyBleTypeDev) {
        checkAdapterNotNull();
        Set<BluetoothDevice> hasBondedBtDevs = bluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> needToPickBondedBtDevs = new ArrayList<BluetoothDevice>(hasBondedBtDevs.size());
        if (!TinyUtil.isApiCompatible(18)) {
            //如果当前系统不是Android4.3的话，则区分不出来所绑定的设备的类型，即区分不出是BLE设备还是BT3.0设备
            needToPickBondedBtDevs.addAll(hasBondedBtDevs);
        }
        else {
            for (BluetoothDevice curBondedOne : hasBondedBtDevs) {
                int btDevType = curBondedOne.getType();
                if (justOnlyBleTypeDev) {//只需要BLE类型的设备
                    if (btDevType == BluetoothDevice.DEVICE_TYPE_LE || btDevType == BluetoothDevice.DEVICE_TYPE_DUAL) {
                        needToPickBondedBtDevs.add(curBondedOne);
                    }
                }
                else{
                    //只需要BT3.0设备
                    if (btDevType != BluetoothDevice.DEVICE_TYPE_LE) {
                        //???是否要排除UNKNOWN类型
                        needToPickBondedBtDevs.add(curBondedOne);
                    }
                }
            }
        }
        return needToPickBondedBtDevs;
    }

    private void checkAdapterNotNull() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    /**
     * 开始BLE的扫描
     * @return true:操作成功；false:操作失败
     */
    public boolean startBleScan() {
        return startBleScan(null);
    }

    /**
     * 开始BLe的扫描(带过滤条件)
     * @param scanFiltersUuids 在扫描BLE设备时需要的设备具备的UUID的条件
     * @return true:操作成功；false:操作失败
     */
    public boolean startBleScan(UUID[] scanFiltersUuids) {
        curScanMode = SCAN_MODE_BLE;
        checkBleScanDockNotNull();
        bleScanDock.configScanFiltersUuids(scanFiltersUuids);
        return bleScanDock.startBleScan();
    }

    /**
     * 主动停止Ble的扫描
     */
    public void stopBleScan() {
        curScanMode = SCAN_MODE_NONE;
        checkBleScanDockNotNull();
        bleScanDock.stopBleScan();
    }
    private void checkBleScanDockNotNull() {
        if (bleScanDock == null) {
            bleScanDock = BleScanDock.getBleScanWorker(bluetoothAdapter, btActionsCallback, null);
        }
    }
    public void stopCommonScan() {
        curScanMode = SCAN_MODE_NONE;
        checkAdapterNotNull();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * 查询蓝牙模块是否开启了
     * @return
     */
    public boolean isBtModuleEnabled() {
        checkAdapterNotNull();
        return bluetoothAdapter.isEnabled();
    }
    /**
     * 工作结束
     * @param context
     */
    public void workOver(Context context) {
        //1、把广播接收者给 注销
        if (btInfosReceiver != null && btInfosReceiver.isRegistered) {
            try {
                context.unregisterReceiver(btInfosReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            btInfosReceiver.isRegistered = true;
        }
        if (curScanMode == SCAN_MODE_BLE) {
            stopBleScan();
            bleScanDock.workOver();
        }
        else if (curScanMode == SCAN_MODE_CLASSIC) {
            stopCommonScan();
        }
    }
}
