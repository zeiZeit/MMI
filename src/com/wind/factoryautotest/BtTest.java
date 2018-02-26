package com.wind.factoryautotest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class BtTest {

    private BluetoothAdapter mAdapter = null;
    private Context mContext = null;
    private Handler mHandler = null;
    private boolean mIsTesting = false;
    private static BtTest mInstance = null;
    public static BtTest getInstance(){
        if(mInstance == null){
            mInstance = new BtTest();
        }
        return mInstance;
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context c, android.content.Intent i) {
            if(i != null){
                if(BluetoothDevice.ACTION_FOUND.equals(i.getAction())){
                    Log.i("yangjiajun", "ACTION_FOUND");
                    BluetoothDevice device = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                        mHandler.removeCallbacks(mThread);
                        Log.i("yangjiajun", "name: " + device.getName());
                        android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_bt", 1);
                        mAdapter.disable();
                        c.unregisterReceiver(mReceiver);
                        mIsTesting = false;
                    }
                }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(i.getAction())){
                    if(BluetoothAdapter.STATE_ON == i.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)){
                        if(mAdapter.isDiscovering()){
                            mAdapter.cancelDiscovery();
                        }
                        mAdapter.startDiscovery();
                    }
                }
            }
        };
    };
    private final Thread mThread = new Thread(){
        public void run() {
            Log.i("yangjiajun", "BtTest fail");
            mContext.unregisterReceiver(mReceiver);
            mIsTesting = false;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_bt", 2);
            mAdapter.disable();
        }
    };

    public void test(Context context, int delayTime, Handler handler) {
        if(!mIsTesting){
            mIsTesting = true;
            mContext = context;
            mHandler = handler;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_bt", 2);
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, filter);
            if(!mAdapter.isEnabled()){
                mAdapter.enable();
            }else if(mAdapter.getState() == BluetoothAdapter.STATE_ON){
                if(mAdapter.isDiscovering()){
                    mAdapter.cancelDiscovery();
                }
                mAdapter.startDiscovery();
            }
            Log.i("yangjiajun", "start tick...");
            handler.postDelayed(mThread, delayTime*1000);
        }
    }
}
