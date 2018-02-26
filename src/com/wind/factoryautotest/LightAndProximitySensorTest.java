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

public class LightAndProximitySensorTest {

    private int PROXIMITY_FAST = 1;
    private SensorManager sm = null;
    private Context mContext = null;
    private Handler mHandler = null;
	
	//add by zuozhuang 20171127 begin 
	private int mLightDark = 0;
    private int mLightBright = 0;
    private boolean mIsLightTesting = false;
	//add by zuozhuang 20171127 end 
	
    private int mProximityNear = 0;
    private int mProximityFast = 0;
    private boolean mIsProximityTesting = false;
    private static LightAndProximitySensorTest mInstance = null;
    public static LightAndProximitySensorTest getInstance(){
        if(mInstance == null){
            mInstance = new LightAndProximitySensorTest();
        }
        return mInstance;
    }

    private SensorEventListener mProximityListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
			android.util.Log.i("yangjiajun", "proximity sensor test event.values: " + event.values[0]);
            if(mProximityNear < 2 && event.values[0] == 0.0f){
                mProximityNear ++;
            }else if(mProximityFast < 2 && (int)event.values[0] == PROXIMITY_FAST){
                mProximityFast ++;
            }
            android.util.Log.i("yangjiajun", "proximity sensor test --- mProximityNear:" + mProximityNear+"    mProximityFast:"+mProximityFast);
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
        android.util.Log.i("yangjiajun", "proximity sensor test mIsProximityTesting: " + mIsProximityTesting);
        if(!mIsProximityTesting){
            mIsProximityTesting = true;
            mProximityNear = 0;
            mProximityFast = 0;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_proximity", 2);
            sm.registerListener(mProximityListener, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        }
	    //add by zuozhuang 20171127 begin 
		android.util.Log.i("yangjiajun", "light sensor test mIsLightTesting: " + mIsLightTesting);
        if(!mIsLightTesting){
            mIsLightTesting = true;
            mLightDark = 0;
            mLightBright = 0;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_light", 2);
            sm.registerListener(mLightListener, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        }
		//add by zuozhuang 20171127 end
    }
	
	
	//add by zuozhuang 20171127 begin 
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
                    android.util.Log.i("yangjiajun", "light sensor test mIsLightTesting: " + mIsLightTesting + "       x: " + x + "        mLightDark: " + mLightDark + "         mLightBright: " + mLightBright);
            if(mLightDark >= 2 && /*mLightCommon >= 2 && */mLightBright >= 2){
                sm.unregisterListener(this);
                mIsLightTesting = false;
                android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_light", 1);
            }
        }
        public void onAccuracyChanged(Sensor arg0, int arg1) {}
    };
	//add by zuozhuang 20171127 end
}
