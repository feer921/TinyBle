package com.tinyble.btmodule;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;

import com.tinyble.btmodule.utils.TinyUtil;

import java.util.UUID;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 15:30
 * DESC: 抽象意为Ble扫描码头(对接),则会依据当前的系统版本来分配相应的码头工人来进行扫描,见子类
 * 注：兼容了BT3.0的扫描
 */
public class BleScanDock {
    protected BluetoothAdapter bluetoothAdapter;
    protected IBtActions iBtActions;
    /**
     * 扫描过程中对扫描出的设备过滤的UUID条件
     */
    protected UUID[] scanFilterUuids;
    /**
     * 默认推迟6秒钟自动停止BLE扫描
     */
    protected int durationOfScan = 6000;
    /**
     * 是否需要经典的扫描操作,本类使用
     */
    private boolean isNeedClassicScan = false;
    /**
     * 扫描到一个BLE设备，因为非Android5.0系统的BLE扫描的回调方法，即{@link Api18BleScanWorker#onLeScan(BluetoothDevice, int, byte[])}
     * 是运行在非主线程之中的，所以需要切换回主线程
     */
    protected static final int MSG_WHAT_SCANED_BLE_DEV = 1;
    private static final int MSG_WHAT_DELAY_TO_STOP_SCAN = 2;


    public BleScanDock(BluetoothAdapter bluetoothAdapter, IBtActions iBtActions, UUID[] scanFilterUuids) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.iBtActions = iBtActions;
        this.scanFilterUuids = scanFilterUuids;
    }

    /**
     * 获取执行蓝牙扫描的对接者
     * @param curBluetoothAdapter
     * @param iBtActions
     * @param scanFilterUuids
     * @return 依据当前系统版本返回满足的扫描工作者
     */
    public static BleScanDock getBleScanWorker(BluetoothAdapter curBluetoothAdapter, IBtActions iBtActions,UUID[] scanFilterUuids) {
        BleScanDock bleScanDocker = null;
        if (TinyUtil.isApiCompatible(21)) {//android 5.0的系统
            bleScanDocker = new Api21BleScanWorker(curBluetoothAdapter, iBtActions, scanFilterUuids);
        }
        else if(TinyUtil.isApiCompatible(18)){//android 4.3系统 -- android5.0以下
            bleScanDocker = new Api18BleScanWorker(curBluetoothAdapter, iBtActions, scanFilterUuids);
        }
        else{//开启经典蓝牙的扫描了 android 4.3系统以下，只能是经典蓝牙模块了
            bleScanDocker = new BleScanDock(curBluetoothAdapter, iBtActions, scanFilterUuids);
            bleScanDocker.setNeedClassicScan(true);
            bleScanDocker.setDurationOfScan(0);
        }
        return bleScanDocker;
    }
    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgWhat = msg.what;
            switch (msgWhat) {
                case MSG_WHAT_SCANED_BLE_DEV:
                    // TODO: 2017/2/22

                    break;
                case MSG_WHAT_DELAY_TO_STOP_SCAN:
                    stopBleScan();
                    break;
            }
        }
    };
    public boolean startBleScan(){
        boolean startScanSuc = false;
        if (isNeedClassicScan) {
            startScanSuc = bluetoothAdapter.startDiscovery();
        }
        delay2StopBleScan();
        return startScanSuc;
    }
    public void stopBleScan(){
        if (isNeedClassicScan) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void setDurationOfScan(int bleScanDurationMills) {
        this.durationOfScan = bleScanDurationMills;
    }

    public void setNeedClassicScan(boolean isNeedClassicScan) {
        this.isNeedClassicScan = isNeedClassicScan;
    }

    public void configScanFiltersUuids(UUID[] scanFilterUuids) {
        this.scanFilterUuids = scanFilterUuids;
    }
    void delay2StopBleScan() {
        mHandler.removeCallbacksAndMessages(null);
        if (durationOfScan > 0) {
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_DELAY_TO_STOP_SCAN, durationOfScan);
        }
    }

    /**
     * 工作结束
     */
    public void workOver() {
        mHandler.removeCallbacksAndMessages(null);
//        stopBleScan();
    }
}
