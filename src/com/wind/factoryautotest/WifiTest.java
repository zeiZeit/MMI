package com.wind.factoryautotest;

import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class WifiTest {

    private WifiManager mWm = null;
    private boolean mWifiEnable = false;
    private boolean mIsTesting = false;
    private Context mContext = null;
    private Handler mHandler = null;
    private static WifiTest mInstance = null;
    public static WifiTest getInstance(){
        if(mInstance == null){
            mInstance = new WifiTest();
        }
        return mInstance;
    }
    private void countResult(){
        List<ScanResult> list = mWm.getScanResults();
        int size = list.size();
        android.util.Log.i("yangjiajun", "wifi test size: " + size);
        if(size > 0){
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_wifi", 1);
            //mContext.unregisterReceiver(mReceiver);
            mIsTesting = false;
            mWm.setWifiEnabled(mWifiEnable);
        }else{
            mHandler.postDelayed(mThread, 1000);
        }
    }
    /*private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context c, android.content.Intent i) {
            if(i != null){
                if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(i.getAction())){
                    //mHandler.removeCallbacks(mThread);
                    countResult();
                }
            }
        };
    };*/
    private final Thread mThread = new Thread(){
        public void run() {
            countResult();
            Log.i("yangjiajun", "wifi test countResult end.");
        }
    };

    public void test(Context context, int delayTime, Handler handler) {
        if(!mIsTesting){
            mIsTesting = true;
            Log.i("yangjiajun", "start wifi test...");
            mContext = context;
            mHandler = handler;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_wifi", 2);
            mWm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            mWifiEnable = mWm.isWifiEnabled();
            //context.registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if(!mWifiEnable){
                mWm.setWifiEnabled(true);
            }
            handler.postDelayed(mThread, 3000);
        }
    }

}
