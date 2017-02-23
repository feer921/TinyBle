package com.tinyble.btmodule;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Message;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 15:33
 * DESC: android5.0(Api21)的BLE扫描相关
 */
@SuppressLint("NewApi")
public class Api21BleScanWorker extends BleScanDock {
    private BleScanCallback4Api21 bleScanCallback4Api21;
    public Api21BleScanWorker(BluetoothAdapter bluetoothAdapter, IBtActions iBtActions, UUID[] scanFilterUuids) {
        super(bluetoothAdapter, iBtActions, scanFilterUuids);
        bleScanCallback4Api21 = new BleScanCallback4Api21();
    }

    @Override
    public boolean startBleScan() {
//        手动关闭蓝牙模块，则得出为null
        if (bluetoothAdapter.getBluetoothLeScanner() != null) {
            bluetoothAdapter.getBluetoothLeScanner().startScan(buildScanFilterBaseUuid(), new ScanSettings.Builder().build(), bleScanCallback4Api21);
        }
        delay2StopBleScan();
        return true;
    }

    private List<ScanFilter> buildScanFilterBaseUuid() {
        if (scanFilterUuids == null || scanFilterUuids.length == 0) {
            return null;
        }
        List<ScanFilter> scanFilters = new ArrayList<>(scanFilterUuids.length);
        for (UUID oneUuid : scanFilterUuids) {
            ScanFilter bleScanFilter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(oneUuid)).build();
            scanFilters.add(bleScanFilter);
        }
        return scanFilters;
    }
    @Override
    public void stopBleScan() {
        mHandler.removeCallbacksAndMessages(null);
        if (bluetoothAdapter.getBluetoothLeScanner() != null) {
            bluetoothAdapter.getBluetoothLeScanner().stopScan(bleScanCallback4Api21);
        }
    }

    private class BleScanCallback4Api21 extends ScanCallback {
        /**
         * Callback when a BLE advertisement has been found.
         *
         * @param callbackType Determines how this callback was triggered. Could be one of
         *                     {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
         *                     {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
         *                     {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
         * @param result       A Bluetooth LE scan result.
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Message msg = new Message();
            msg.what = MSG_WHAT_SCANED_BLE_DEV;
            // TODO: 2017/2/22

        }

        /**
         * Callback when batch results are delivered.
         * @param results List of scan results that are previously scanned.
         */
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
        }

        /**
         * Callback when scan could not be started.
         *
         * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
         */
        @Override
        public void onScanFailed(int errorCode) {
        }
    }
}
