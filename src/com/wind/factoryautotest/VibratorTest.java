package com.wind.factoryautotest;

import android.content.Context;
import android.os.Vibrator;

public class VibratorTest {

    public void test(Context context, boolean enable){
        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        android.util.Log.i("yangjiajun", "VibratorTest: " + enable);
        if(enable){
            v.vibrate(new long[]{200,2000}, 0);//new long[]{500, 1000, 500, 1000}, 1
        }else{
            v.cancel();
        }
    }
}
