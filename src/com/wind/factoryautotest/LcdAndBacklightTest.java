package com.wind.factoryautotest;

import android.os.PowerManager;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.RemoteException;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.graphics.Color;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class LcdAndBacklightTest {

    public static void test(Context context, int color, int brightness){
        IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if(!pm.isScreenOn()){
            pm.wakeUp(SystemClock.uptimeMillis());
        }
        // notStart = "0".equals(SystemProperties.get("sys.wind.color", "0"));
        SystemProperties.set("sys.wind.color", ""+color);
		SystemProperties.set("sys.wind.lcdtest", "" + 1);
/* 		try {
			String lcdtest_fifo = "/data/lcdtest_fifo";
			File pipe = new File(lcdtest_fifo);
			BufferedWriter writer = new BufferedWriter(new FileWriter(pipe));
			writer.write("1");  
		} catch(Exception e) {
			e.printStackTrace();
		} */
        /* if(color != 0 && notStart){
            try{
                IFactoryAutoTest.Stub.asInterface(ServiceManager.getService("factory_auto_test")).startLcdTest();
            }catch(RemoteException e){
                e.printStackTrace();
            }
        } */
        //set brightness
        if(brightness == 0){
            pm.goToSleep(SystemClock.uptimeMillis());
        }else{
            int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            try {
                brightnessMode = Settings.System.getInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (SettingNotFoundException snfe) {
                Log.d("yangjiajun", "SettingNotFoundException");
            }
            if(brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
                Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
            try {
                power.setTemporaryScreenBrightnessSettingOverride(brightness);
            } catch (RemoteException e) {
                Log.d("yangjiajun", "RemoteException");
            }
        }
    }
}
