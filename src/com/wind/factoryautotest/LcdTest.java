package com.wind.factoryautotest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.os.PowerManager;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;

public class LcdTest extends Activity{

    private View mContentView = null;
    private IPowerManager power = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(android.content.Context c, Intent i) {
            if(i != null){
            	  int color = i.getIntExtra("color", 0);
            	  int brightness = i.getIntExtra("brightness", 255);
            	  if(brightness == 0){
                    ((PowerManager)getSystemService(Context.POWER_SERVICE)).goToSleep(SystemClock.uptimeMillis());
                    finish();
                }else{
                    try {
                        power.setTemporaryScreenBrightnessSettingOverride(brightness);
                    } catch (RemoteException e) {
                        Log.d("yangjiajun", "RemoteException");
                    }
                    mContentView.setBackgroundColor(color);
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if(!pm.isScreenOn()){
            pm.wakeUp(SystemClock.uptimeMillis());
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        int color = Color.BLACK;
        int brightness = 255;
        Intent intent = getIntent();
        if(intent != null){
            color = intent.getIntExtra("color", Color.BLACK);
            brightness = intent.getIntExtra("brightness", 255);
        }
        mContentView = new View(this);
        mContentView.setBackgroundColor(color);
        setContentView(mContentView);
        //set brightness
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
            power.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException e) {
            Log.d("yangjiajun", "RemoteException");
        }
		    /*int systemUiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED;
		    systemUiFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		    systemUiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		    getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);*/
        registerReceiver(mReceiver, new IntentFilter("com.wind.factoryautotest.ACTION_LCDTEST"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
