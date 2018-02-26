package com.wind.factoryautotest;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.View;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.IPowerManager;
import android.util.Log;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.content.Context;
import android.view.WindowManager;
import android.graphics.Color;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class BacklightTest extends Activity {

    private IPowerManager power = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(android.content.Context c, Intent i) {
            if(i != null){
            	  int brightness = i.getIntExtra("brightness", 0);
            	  if(brightness == 0){
                    ((PowerManager)BacklightTest.this.getSystemService(Context.POWER_SERVICE)).goToSleep(SystemClock.uptimeMillis());
                    finish();
                }else{
                    try {
                        power.setTemporaryScreenBrightnessSettingOverride(brightness);
                    } catch (RemoteException e) {
                        Log.d("yangjiajun", "RemoteException");
                    }
                }
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        if(!pm.isScreenOn()){
            pm.wakeUp(SystemClock.uptimeMillis());
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        View v = new View(this);
        v.setBackgroundColor(Color.WHITE);
        setContentView(v);
		    /*int systemUiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED;
		    systemUiFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		    systemUiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		    getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);*/
        registerReceiver(mReceiver, new IntentFilter("com.wind.factoryautotest.ACTION_BACKLIGHTTEST"));
        int brightness = 255;
        Intent intent = getIntent();
        if(intent != null){
            brightness = intent.getIntExtra("brightness", 255);
        }

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
        power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        try {
            power.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException e) {
            Log.d("yangjiajun", "RemoteException");
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
