package com.tinyble.btmodule;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-21
 * Time: 18:51
 * DESC: 专门负责接收有关蓝牙各种状态的广播接收者
 */
public class BtInfosReceiver extends BroadcastReceiver {
    IBtActions btActions;
    IntentFilter intentFilter;
    boolean isRegistered;
    public BtInfosReceiver(IBtActions iBtActions) {
        this.btActions = iBtActions;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            //扫描开始了
            if (null != btActions) {
                btActions.scanWorkState(IBtActions.SCAN_WORK_ING);
            }
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //扫描结束了
            if (null != btActions) {
                btActions.scanWorkState(IBtActions.SCAN_WORK_OVER);
            }
        }
        else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            //蓝牙模块的状态发生了变化:开启了，关闭了等
            int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);
            int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            if (null != btActions) {
                btActions.btModuleEnableState(previousState,newState);
            }
        }
        else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //扫描到了一个蓝牙设备
            BluetoothDevice theScanedOne = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
            ExtendedBluetoothDev wrapperBTdev = new ExtendedBluetoothDev(theScanedOne);
            wrapperBTdev.setRssi(rssi);
            if (btActions != null) {
                btActions.scanedABtDev(wrapperBTdev);
            }
        }
        //该广播一般可以不用理会，直接在蓝牙端对端连接时的逻辑里回调出来
        else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            //于远程蓝牙设备端的连接状态变化
            int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_DISCONNECTING);
            BluetoothDevice theRemoteOne = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (null != btActions) {
                btActions.remoteBtConnState(theRemoteOne, connState);
            }
        }
    }

    /**
     * 添加相应的与蓝牙有关的广播Action
     * 参见onReceive()所接收到并处理的Action
     * @param aboutBtActions 与蓝牙相关的广播Action
     * @return 自身
     */
    public BtInfosReceiver appendBtActions(String aboutBtActions) {
        if (intentFilter == null) {
            intentFilter = new IntentFilter();
        }
        if (!TextUtils.isEmpty(aboutBtActions)) {
            intentFilter.addAction(aboutBtActions);
        }
        return this;
    }

    public BtInfosReceiver appendDefActions() {
        return appendBtActions(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                .appendBtActions(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                .appendBtActions(BluetoothAdapter.ACTION_STATE_CHANGED)
                .appendBtActions(BluetoothDevice.ACTION_FOUND)
                .appendBtActions(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
    }

}
