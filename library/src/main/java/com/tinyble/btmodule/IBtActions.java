package com.tinyble.btmodule;

import android.bluetooth.BluetoothDevice;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-21
 * Time: 19:07
 * DESC:
 */
public interface IBtActions {
    /**
     * 正在扫描
     */
    int SCAN_WORK_ING = 1;
    /**
     * 扫描结束
     */
    int SCAN_WORK_OVER = SCAN_WORK_ING + 1;
    //连接相关的状态
    int STATE_DISCONNECTED = 0;
    /**
     * The profile is in connecting state
     */
    int STATE_CONNECTING = 1;
    /**
     * The profile is in connected state
     */
    int STATE_CONNECTED = 2;
    /**
     * The profile is in disconnecting state
     */
    int STATE_DISCONNECTING = 3;

    //模块使能相关的状态
    /**
     * Indicates the local Bluetooth adapter is off.
     */
    int STATE_OFF = 10;
    /**
     * Indicates the local Bluetooth adapter is turning on. However local
     * clients should wait for {@link #STATE_ON} before attempting to
     * use the adapter.
     */
    int STATE_TURNING_ON = 11;
    /**
     * Indicates the local Bluetooth adapter is on, and ready for use.
     */
    int STATE_ON = 12;
    /**
     * Indicates the local Bluetooth adapter is turning off. Local clients
     * should immediately attempt graceful disconnection of any remote links.
     */
    int STATE_TURNING_OFF = 13;


    /**
     * 扫描到了一个设备
     * @param scanedOne 扫描到的设备，因系统原因，可能会扫描到重复设备，需要使用者自己去重复
     */
    void scanedABtDev(ExtendedBluetoothDev scanedOne);

    /**
     * 蓝牙扫描工作的状态
     * @param scanWorkState 参考:
     * {@link #SCAN_WORK_ING}
     *  or
     * {@link #SCAN_WORK_OVER}
     */
    void scanWorkState(int scanWorkState);

    /**
     * 与远程蓝牙设备端的连接状态回调
     * @param remoteBtDev 当前连接的远程蓝牙设备
     * @param connState 连接的状态，参照:
     *<P>
     *     {@link #STATE_CONNECTED}<br>
     *     {@link #STATE_CONNECTING}<br>
     *     {@link #STATE_DISCONNECTED}<br>
     *     {@link #STATE_DISCONNECTING}
     *</P>
     *或者参考{@link android.bluetooth.BluetoothAdapter}中定义的状态
     *<P>
     *     {@link android.bluetooth.BluetoothAdapter#STATE_CONNECTED}<br>
     *     {@link android.bluetooth.BluetoothAdapter#STATE_CONNECTING}<br>
     *     {@link android.bluetooth.BluetoothAdapter#STATE_DISCONNECTED}<br>
     *     {@link android.bluetooth.BluetoothAdapter#STATE_DISCONNECTING}
     *</P>
     */
    void remoteBtConnState(BluetoothDevice remoteBtDev, int connState);

    /**
     * 蓝牙模块的开启、关闭的状态回调
     * @param previousState 上一次的状态
     * @param nowEnableState 最新的状态
     * 参数中的两个状态,参照：
     * <p>
     *   {@link #STATE_OFF}<br>
     *   {@link #STATE_TURNING_OFF}<br>
     *   {@link #STATE_ON}<br>
     *   {@link #STATE_TURNING_ON}<br>
     * </p>
     * 或者参照{@link android.bluetooth.BluetoothAdapter}中定义的状态
     * <p>
     *   {@link android.bluetooth.BluetoothAdapter#STATE_OFF}<br>
     *   {@link android.bluetooth.BluetoothAdapter#STATE_TURNING_OFF}<br>
     *   {@link android.bluetooth.BluetoothAdapter#STATE_ON}<br>
     *   {@link android.bluetooth.BluetoothAdapter#STATE_TURNING_ON}<br>
     * </p>
     *
     */
    void btModuleEnableState(int previousState, int nowEnableState);
}
