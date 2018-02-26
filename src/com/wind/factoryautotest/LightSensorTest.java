package com.wind.factoryautotest;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

public class LightSensorTest {

    private SensorManager sm = null;
    private Context mContext = null;
    private Handler mHandler = null;

    private int mLightDark = 0;
    private int mLightBright = 0;
    private boolean mIsLightTesting = false;
    private static LightSensorTest mInstance = null;
    public static LightSensorTest getInstance(){
        if(mInstance == null){
            mInstance = new LightSensorTest();
        }
        return mInstance;
    }

    private SensorEventListener mLightListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            int x = (int)event.values[0];
            //Utils.appendResult("/sdcard/lighttest.txt", "\n"+x);
            if(mLightDark < 2 && x < 10){
                mLightDark ++;
            //}else if(mLightCommon < 2 && x > 100 && x < 350){
            //    mLightCommon ++;
            }else if(mLightBright < 2 && x > 300){
                mLightBright ++;
            }
            if(mLightDark >= 2 && /*mLightCommon >= 2 && */mLightBright >= 2){
                sm.unregisterListener(this);
                mIsLightTesting = false;
                android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_light", 1);
            }
        }
        public void onAccuracyChanged(Sensor arg0, int arg1) {}
    };

    public void test(Context context, Handler handler){
        if(sm == null){
            mContext = context;
            mHandler = handler;
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            if(!pm.isScreenOn()){
                pm.wakeUp(SystemClock.uptimeMillis());
            }
        }
        if(!mIsLightTesting){
            mIsLightTesting = true;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_light", 2);
            sm.registerListener(mLightListener, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}
