package blessedness.com.tinyble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tinyble.btmodule.ExtendedBluetoothDev;
import com.tinyble.btmodule.IBtActions;
import com.tinyble.btmodule.TinyBle;
import com.tinyble.btmodule.utils.CommonLog;
import com.tinyble.btmodule.utils.TinyPermissin;

import java.util.List;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 19:11
 * DESC:
 */
public class BtDevChooseActivity extends ListActivity implements IBtActions,View.OnClickListener{
    TinyBle tinyBle;
    BTDeviceAdapter btDeviceAdapter;
    TextView tvEmptyView;
    ProgressBar pbScan;
    TextView tvBtState;
    TextView tvOtherState;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_ble_layout);
        CommonLog.logEnable(true);
        tinyBle = TinyBle.getMe();
        tinyBle.configBtActionsListener(this);
        setListAdapter(btDeviceAdapter = new BTDeviceAdapter(this, null));
        tvEmptyView = (TextView) findViewById(android.R.id.empty);
        pbScan = (ProgressBar) findViewById(R.id.pb_scan);
        tvBtState = (TextView) findViewById(R.id.tv_bt_state);
        findViewById(R.id.btn_rescan).setOnClickListener(this);
        findViewById(R.id.btn_common_scan).setOnClickListener(this);
        findViewById(R.id.btn_ble_scan).setOnClickListener(this);
        tvOtherState = (TextView) findViewById(R.id.tv_other_state);
        //非BLE扫描时需要
        tinyBle.registerBtActionReceiver(this);
        if (!tinyBle.isBtModuleEnabled()) {
            tvBtState.setText("蓝牙模块未开启");
            if (!tinyBle.switchBtModule(true)) {
                tinyBle.action2EnableBT(this, 100);
            }
        }
        else{
            tvBtState.setText("蓝牙模已开启");
        }
        if (!TinyPermissin.isPermissionGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            findViewById(R.id.btn_ble_scan).setEnabled(false);
            tvOtherState.setText("未获取定位权限");
            TinyPermissin.requestAPermission(this, REQUEST_ACCESS_LOCATION_PERMISSION_CODE, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        else{
            tvOtherState.setText("已获取定位权限");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tinyBle.workOver(this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

    }

    @Override
    public void scanedABtDev(ExtendedBluetoothDev scanedOne) {
        addANewScanedBtDevice(scanedOne);
    }
    private void addANewScanedBtDevice(ExtendedBluetoothDev theScanedOne) {
        boolean isExistedAlready = compareData(btDeviceAdapter.getDatas(), theScanedOne);
        if (!isExistedAlready) {
            btDeviceAdapter.addItems(theScanedOne);
        }
    }
    private boolean compareData(List<ExtendedBluetoothDev> oldDatas, ExtendedBluetoothDev willAddedOne) {
        if (oldDatas == null) {
            return false;
        }
        if (willAddedOne == null) {
            return true;
        }
        int oldSize = oldDatas.size();
        for(int i = 0; i < oldSize; i++) {
            if (oldDatas.get(i).getAddress().equals(willAddedOne.getAddress())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void scanWorkState(int scanWorkState) {
        CommonLog.i("info", "-->scanWorkState() scanWorkState = " + scanWorkState);
        switch (scanWorkState) {
            case SCAN_WORK_ING:
                tvBtState.setText("正在扫描...");
                break;
            case SCAN_WORK_OVER:
                tvBtState.setText("扫描结束...");
                scanProgress(false);
                break;
        }
    }

    @Override
    public void remoteBtConnState(BluetoothDevice remoteBtDev, int connState) {
        //do nothing here...
    }

    @Override
    public void btModuleEnableState(int previousState, int nowEnableState) {
        String btModuleState= "已关闭";
        CommonLog.i("info", "-->btModuleEnableState() nowEnableState = " + nowEnableState);
        switch (nowEnableState) {
            case STATE_ON:
                btModuleState = "蓝牙模块已打开";
                break;
            case STATE_OFF:
                btModuleState = "蓝牙模块已关闭";
                break;
            case STATE_TURNING_OFF:
                btModuleState = "蓝牙模块正在关闭";
                break;
            case STATE_TURNING_ON:
                btModuleState = "蓝牙模块正在打开";
                break;
        }
        tvBtState.setText(btModuleState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            CommonLog.e("info", "-->onActivityResult() resultCode= " + resultCode);
            switch (resultCode) {
                case RESULT_OK:
                    tvBtState.setText("用户开启了蓝牙...");
                    break;
                case RESULT_CANCELED:
                    tvBtState.setText("用户取消关闭了蓝牙...");
                    break;
            }
        }
    }

    private void scanProgress(boolean toStart) {
        pbScan.setVisibility(toStart ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_rescan:
                break;
            case R.id.btn_common_scan:
                btDeviceAdapter.clearData();
                tinyBle.startScanCommonBt();
                break;
            case R.id.btn_ble_scan:
                btDeviceAdapter.clearData();
                tinyBle.startBleScan();
                break;
        }
    }
    private static final int REQUEST_ACCESS_LOCATION_PERMISSION_CODE = 1000;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CommonLog.e("info", "-->onRequestPermissionsResult() requestCode = " + requestCode + " permissions[0] = " + permissions[0]
         +" grantResults[0]=" + grantResults[0]
        );
        if (requestCode == REQUEST_ACCESS_LOCATION_PERMISSION_CODE) {
            if (TinyPermissin.isThePermissionGrantedInResults(Manifest.permission.ACCESS_COARSE_LOCATION, permissions, grantResults)) {
                findViewById(R.id.btn_ble_scan).setEnabled(true);
                tvOtherState.setText("已获取定位权限");
            }
        }
    }
}
