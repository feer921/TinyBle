package blessedness.com.tinyble;

import android.annotation.TargetApi;
import android.content.Context;

import com.tinyble.btmodule.ExtendedBluetoothDev;

import java.util.Collection;



/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2015-12-31
 * Time: 14:42
 * DESC:
 */
public class BTDeviceAdapter extends BaseCommonAdapter<ExtendedBluetoothDev> {

    public BTDeviceAdapter(Context curContext, Collection<ExtendedBluetoothDev> data) {
        super(curContext, data);
    }

    /**
     * 获取当前item的显示布局资源ID，各子类可以根据当前item数据(Mode对象类型,比如第一行显示一张网页图片，
     * 第二行就显示一个APk下载)
     *
     * @param itemPosition
     * @return 当前位置需要显示的布局资源ID
     */
    @Override
    protected int getItemLayoutResId(int itemPosition) {
        return R.layout.bt_device_item_layout;
    }

    @TargetApi(18)
    @Override
    public void convert(AdapterViewHolder viewHolder, ExtendedBluetoothDev itemData, boolean isScrolling) {
        super.convert(viewHolder, itemData, isScrolling);
        String btDevName = "设备名：" + itemData.getName();
        String btTypeName = "         蓝牙类型:" + itemData.getBtTypeDesc();
        viewHolder.setText(R.id.tv_dev_name, btDevName + btTypeName);
        viewHolder.setText(R.id.tv_dev_mac,itemData.getAddress());
        viewHolder.setText(R.id.tv_dev_mathch_state,itemData.getBondedStateDesc());
    }
}
