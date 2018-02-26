package com.wind.factoryautotest;

import java.io.IOException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class OTGTest {

    private boolean[] mStates = {false, false};
    private boolean mIsRunning = false;
    private static OTGTest mInstance = null;
    public static OTGTest getInstance(){
        if(mInstance == null){
            mInstance = new OTGTest();
        }
        return mInstance;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context c, Intent i) {
            if(i != null){
                if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(i.getAction())){
                    Log.i("yangjiajun", "usb device attached!");
                    mStates[0] = true;
                }else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(i.getAction())){
                    Log.i("yangjiajun", "usb device NOT attached!");
                    mStates[1] = true;
                }
		if(mStates[0] && mStates[1]){
		    android.provider.Settings.System.putInt(c.getContentResolver(), "emode_pac_flag_otg", 1);
                    c.unregisterReceiver(this);              
                    mIsRunning = false;
		}
            }
        };
    };

    public void test(Context context){
        if(!mIsRunning){
            mIsRunning = true;
            mStates[0] = false;
            mStates[1] = false;
            android.provider.Settings.System.putInt(context.getContentResolver(), "emode_pac_flag_otg", 2);
            IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            context.registerReceiver(mReceiver, filter);
        }
    }
}
