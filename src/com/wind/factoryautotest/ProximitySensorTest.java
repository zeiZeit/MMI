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

public class ProximitySensorTest {

    private int PROXIMITY_FAST = 1;
    private SensorManager sm = null;
    private Context mContext = null;
    private Handler mHandler = null;

    private int mProximityNear = 0;
    private int mProximityFast = 0;
    private boolean mIsProximityTesting = false;
    private static ProximitySensorTest mInstance = null;
    public static ProximitySensorTest getInstance(){
        if(mInstance == null){
            mInstance = new ProximitySensorTest();
        }
        return mInstance;
    }

    private SensorEventListener mProximityListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
			android.util.Log.d("zuozhuang", "event.values[0]="+event.values[0]);
            if(mProximityNear < 2 && event.values[0] == 0.0f){
                mProximityNear ++;
            }else if(mProximityFast < 2 && (int)event.values[0] == PROXIMITY_FAST){
                mProximityFast ++;
            }
            if(mProximityNear >= 2 && mProximityFast >= 2){
                sm.unregisterListener(this);
                mIsProximityTesting = false;
                android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_proximity", 1);
            }
        }
        public void onAccuracyChanged(Sensor arg0, int arg1) {}
    };

    public void test(Context context, Handler handler, int val){
		PROXIMITY_FAST = val;
        if(sm == null){
            mContext = context;
            mHandler = handler;
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            if(!pm.isScreenOn()){
                pm.wakeUp(SystemClock.uptimeMillis());
            }
        }
        if(!mIsProximityTesting){
            mIsProximityTesting = true;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_proximity", 2);
            sm.registerListener(mProximityListener, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}
