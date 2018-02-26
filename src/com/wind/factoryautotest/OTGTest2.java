package com.wind.factoryautotest;

import android.app.Activity;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.database.ContentObserver; 
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

import java.io.OutputStream;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;

public class OTGTest2 extends Activity {

    private TextView tv;
    private ContentObserver mDatabaseListener = null;  
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x88:
                    new Thread(){
        	              public void run(){
                            try {
                                Socket sk = new Socket("localhost", 8086);
                                OutputStream os = sk.getOutputStream();
                                os.write("exit".getBytes());
                                os.close();
                                sk.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    finish();
                    break;
            }
        }
    };

    private void setBrightness(){
        int brightness = 255;
        int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        try {
            brightnessMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (SettingNotFoundException snfe) {
            Log.d("yangjiajun", "SettingNotFoundException");
        }
        if(brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
            Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
        try {
            IPowerManager.Stub.asInterface(ServiceManager.getService("power")).setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException e) {
            Log.d("yangjiajun", "RemoteException");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int delay_time = 15;
        if(intent == null){
            Log.i("yangjiajun", "OTGTest2 fail");
            finish();
        }else{
            delay_time = intent.getIntExtra("delay_time", 15);
        }
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if(!pm.isScreenOn()){
            pm.wakeUp(SystemClock.uptimeMillis());
        }
        getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.WHITE);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(38);
        tv.setText(R.string.usb_otg_not_plugin);
        setContentView(tv);
        //int systemUiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED;
		    int systemUiFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		    systemUiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		    getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
        setBrightness();
        mDatabaseListener = new ContentObserver(mHandler)  
        {
            @Override
            public void onChange(boolean selfChange)
            {
                if(android.provider.Settings.System.getInt(getContentResolver(), "emode_pac_flag_otg", 2) == 1){
                    tv.setText(R.string.usb_otg_plugin);
                }
                super.onChange(selfChange);
            }
        };
        getContentResolver().registerContentObserver(android.provider.Settings.System.getUriFor("emode_pac_flag_otg"), true, mDatabaseListener);
        mHandler.sendEmptyMessageDelayed(0x88, delay_time*1000);
    }

    @Override  
    protected void onDestroy()  
    {  
        super.onDestroy();  
        getContentResolver().unregisterContentObserver(mDatabaseListener);  
    }
}
