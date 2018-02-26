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
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SensorTest {

    public static int PROXIMITY_FAST = 1;
    private SensorManager sm = null;
    private Context mContext = null;
    private Handler mHandler = null;

    private int mProximityNear = 0;
    private int mProximityFast = 0;
    private int mLightDark = 0;
    private int mLightCommon = 0;
    private int mLightBright = 0;
    private int mAccHorizontalX = 0;
    private int mAccHorizontalY = 0;
    private int mAccHorizontalZ = 0;
    private int mAccLeanX = 0;
    private int mAccLeanY = 0;
    private int mAccLeanZ = 0;
    private int mGyroActiveX = 0;
    private int mGyroActiveY = 0;
    private int mGyroActiveZ = 0;
    private int mGyroStaticX = 0;
    private int mGyroStaticY = 0;
    private int mGyroStaticZ = 0;
    private boolean mOrienInit = false;
    private float mOrienXMax = 0;
    private float mOrienXMin = 0;
    private int mOrienLeanY = 0;
    private int mOrienHorizontalY = 0;
    private int mOrienLeanZ = 0;
    private int mOrienHorizontalZ = 0;

    private int mCompassParams[] = {0, 0, 0, 0};
    private float mGsensorParams[] = new float[17];
    private float mGyroParams[] = new float[17];

    private boolean mIsProximityTesting = false;
    private boolean mIsLightTesting = false;
    private boolean mIsAccTesting = false;
    private boolean mIsGyroTesting = false;
    private boolean mIsOrienTesting = false;
    private static SensorTest mInstance = null;
    public static SensorTest getInstance(){
        if(mInstance == null){
            mInstance = new SensorTest();
        }
        return mInstance;
    }

    private SensorEventListener mProximityListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if(mProximityNear < 2 && event.values[0] == 0.0f){
                mProximityNear ++;
            }else if(mProximityFast < 2 && event.values[0] == PROXIMITY_FAST){
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
    private SensorEventListener mAccListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            /*if(mAccLeanX < 3 && (x < -1 || x > 1)){
                mAccLeanX ++;
            }else if(mAccHorizontalX < 3 && x > -0.5f && x < 0.5f){
                mAccHorizontalX ++;
            }
            if(mAccLeanY < 3 && (y < -1 || y > 1)){
                mAccLeanY ++;
            }else if(mAccHorizontalY < 3 && y > -0.5f && y < 0.5f){
                mAccHorizontalY ++;
            }
            if(mAccLeanZ < 3 && z <= 9.6f){
                mAccLeanZ ++;
            }else if(mAccHorizontalZ < 3 && z >= 9.8f){
                mAccHorizontalZ ++;
            }
            if(mAccLeanX >= 3 && mAccHorizontalX >= 3 && mAccLeanY >= 3 && mAccHorizontalY >= 3 && mAccLeanZ >= 3 && mAccHorizontalZ >= 3){*/
            if(Utils.checkGsensor(x, y, z, mGsensorParams)){
                sm.unregisterListener(this);
                mIsAccTesting = false;
                android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_gsensor", 1);
            }
        }
        public void onAccuracyChanged(Sensor arg0, int arg1) {}
    };
    private SensorEventListener mGyroListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            /*if(mGyroActiveX < 3 && (x > 0.1f || x < -0.1f)){
                mGyroActiveX ++;
            }else if(mGyroStaticX < 3 && x > -0.05f && x < 0.05f){
                mGyroStaticX ++;
            }
            if(mGyroActiveY < 3 && (y > 0.1f || y < -0.1f)){
                mGyroActiveY ++;
            }else if(mGyroStaticY < 3 && y > -0.05f && y < 0.05f){
                mGyroStaticY ++;
            }
            if(mGyroActiveZ < 3 && (z > 0.1f || z < -0.1f)){
                mGyroActiveZ ++;
            }else if(mGyroStaticZ < 3 && z > -0.05f && z < 0.05f){
                mGyroStaticZ ++;
            }
            if(mGyroActiveX >= 3 && mGyroStaticX >= 3 && mGyroActiveY >= 3 && mGyroStaticY >= 3 && mGyroActiveZ >= 3 && mGyroStaticZ >= 3){*/
            if(Utils.checkGyro(x, y, z, mGyroParams)){
                sm.unregisterListener(this);
                mIsGyroTesting = false;
                android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_gyoscope", 1);
            }
        }
        public void onAccuracyChanged(Sensor arg0, int arg1) {}
    };
    private SensorEventListener mOrienListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            int x = (int)event.values[0];
            int y = (int)event.values[1];
            int z = (int)event.values[2];
            /*float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if(!mOrienInit){
                mOrienInit = true;
                mOrienXMax = x;
                mOrienXMin = x;
            }else{
                if(x > mOrienXMax){
                    mOrienXMax = x;
                }else if(x < mOrienXMin){
                    mOrienXMin = x;
                }
            }
            if(mOrienLeanY < 2 && (y > 8 || y < -8)){
                mOrienLeanY ++;
            }else if(mOrienHorizontalY < 2 && y > -2.5f && y < 2.5f){
                mOrienHorizontalY ++;
            }
            if(mOrienLeanZ < 2 && (z > 8 || z < -8)){
                mOrienLeanZ ++;
            }else if(mOrienHorizontalZ < 2 && z > -2.5f && z < 2.5f){
                mOrienHorizontalZ ++;
            }
            if((mOrienXMax - mOrienXMin > 5) && mOrienLeanY >= 2 && mOrienHorizontalY >= 2 && mOrienLeanZ >= 2 && mOrienHorizontalZ >= 2){*/
            if(Utils.checkCompass(x, y, z, mCompassParams)){
                sm.unregisterListener(this);
                mIsOrienTesting = false;
                android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_compass", 1);
            }
        }
        public void onAccuracyChanged(Sensor arg0, int arg1) {}
    };

    public void test(){
        try{
        OutputStream os = new FileOutputStream("/sdcard/sensortest.txt");
        String logstr = "mProximityNear = "+mProximityNear+
        "\nmProximityFast = "+mProximityFast+
        "\nmLightDark = "+mLightDark+
        "\nmLightCommon = "+mLightCommon+
        "\nmLightBright = "+mLightBright+
        "\nmAccHorizontalX = "+mAccHorizontalX+
        "\nmAccHorizontalY = "+mAccHorizontalY+
        "\nmAccHorizontalZ = "+mAccHorizontalZ+
        "\nmAccLeanX = "+mAccLeanX+
        "\nmAccLeanY = "+mAccLeanY+
        "\nmAccLeanZ = "+mAccLeanZ+
        "\nmGyroActiveX = "+mGyroActiveX+
        "\nmGyroActiveY = "+mGyroActiveY+
        "\nmGyroActiveZ = "+mGyroActiveZ+
        "\nmGyroStaticX = "+mGyroStaticX+
        "\nmGyroStaticY = "+mGyroStaticY+
        "\nmGyroStaticZ = "+mGyroStaticZ+
        "\nmOrienXMax = "+mOrienXMax+
        "\nmOrienXMin = "+mOrienXMin+
        "\nmOrienLeanY = "+mOrienLeanY+
        "\nmOrienHorizontalY = "+mOrienHorizontalY+
        "\nmOrienLeanZ = "+mOrienLeanZ+
        "\nmOrienHorizontalZ = "+mOrienHorizontalZ;
        os.write(logstr.getBytes());
        os.close();
        }catch(Exception e){e.printStackTrace();}
        //Utils.appendResult("/sdcard/sensortest.txt", "\n"+m);
    }

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
        /*if(!mIsProximityTesting){
            mIsProximityTesting = true;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_proximity", 2);
            sm.registerListener(mProximityListener, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        }*/
        /*if(!mIsLightTesting){
            mIsLightTesting = true;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_light", 2);
            sm.registerListener(mLightListener, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        }*/
        if(!mIsAccTesting){
            mIsAccTesting = true;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_gsensor", 2);
            mGsensorParams[0] = 20;
            for(int i = 1; i < 17; i ++){
                mGsensorParams[i] = 0;
            }
            sm.registerListener(mAccListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(!mIsGyroTesting){
            mIsGyroTesting = true;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_compass", 2);
            mGyroParams[0] = 20;
            for(int i = 1; i < 17; i ++){
                mGyroParams[i] = 0;
            }
            sm.registerListener(mGyroListener, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(!mIsOrienTesting){
            mIsOrienTesting = true;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_gyoscope", 2);
            sm.registerListener(mOrienListener, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}
