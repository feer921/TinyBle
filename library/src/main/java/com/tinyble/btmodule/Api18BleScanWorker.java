package com.tinyble.btmodule;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Message;

import java.util.UUID;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 15:35
 * DESC: android4.3 ~ android5.0之间的BLE扫描模式
 */
@SuppressLint("NewApi")
public class Api18BleScanWorker extends BleScanDock implements BluetoothAdapter.LeScanCallback {

    public Api18BleScanWorker(BluetoothAdapter bluetoothAdapter, IBtActions iBtActions, UUID[] scanFilterUuids) {
        super(bluetoothAdapter, iBtActions, scanFilterUuids);
    }

    @Override
    public boolean startBleScan() {
        if (curScanState == SCAN_STATE_ING) {
            return true;
        }
        curScanState = SCAN_STATE_ING;
        //注：BLE的开始扫描 的开始、结束状态并不会通过广播接收者那接收到
        if (null != iBtActions) {
            iBtActions.scanWorkState(IBtActions.SCAN_WORK_ING);
        }
        boolean optSuc = bluetoothAdapter.startLeScan(scanFilterUuids,this);
        delay2StopBleScan();
        return optSuc;
    }

    @Override
    public void stopBleScan() {
        curScanState = SCAN_STATE_OVER;
        mHandler.removeCallbacksAndMessages(null);
        bluetoothAdapter.stopLeScan(this);
        if (null != iBtActions) {
            iBtActions.scanWorkState(IBtActions.SCAN_WORK_OVER);
        }
    }
    /**
     * Callback reporting an LE device found during a device scan initiated
     * by the {@link BluetoothAdapter#startLeScan} function.
     * 注意该回调接口运行在非主线程中，需要再切换到主线程中
     * @param device     Identifies the remote device
     * @param rssi       The RSSI value for the remote device as reported by the
     *                   Bluetooth hardware. 0 if no RSSI value is available.
     * @param scanRecord The content of the advertisement record offered by
     */
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        ExtendedBluetoothDev curScanedOne = new ExtendedBluetoothDev(device);
        curScanedOne.setRssi(rssi);
        curScanedOne.setScanRecord(scanRecord);
        Message msg = new Message();
        msg.obj = curScanedOne;
        msg.what = MSG_WHAT_SCANED_BLE_DEV;
        mHandler.sendMessage(msg);
    }
}
