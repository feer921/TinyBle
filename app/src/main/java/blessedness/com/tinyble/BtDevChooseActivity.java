package blessedness.com.tinyble;

import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import com.tinyble.btmodule.IBtActions;
import com.tinyble.btmodule.TinyBle;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2017-02-22
 * Time: 19:11
 * DESC:
 */
public class BtDevChooseActivity extends ListActivity implements IBtActions{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyBle.getMe().configBtActionsListener(this);
    }

    @Override
    public void scanedABtDev(BluetoothDevice scanedOne, Intent extraIntent) {

    }

    @Override
    public void scanWorkState(int scanWorkState) {

    }

    @Override
    public void remoteBtConnState(BluetoothDevice remoteBtDev, int connState) {

    }

    @Override
    public void btModuleEnableState(int previousState, int nowEnableState) {

    }
}
