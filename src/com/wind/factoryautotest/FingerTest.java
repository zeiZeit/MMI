package com.wind.factoryautotest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.os.Handler;
import android.util.Log;

import java.io.File;

public class FingerTest {

    private Context mContext = null;
    private Handler mHandler = null;
    private boolean[] mStates = {false, false};
	
	private final String FP_SILEAD_FACTORY_TEST_RESULT = "www.silead.factory.action.RESULT";
    private final String FP_MADEV_FACTORY_TEST_RESULT = "www.madev.factory.action.RESULT";
    private final String FP_GOODIX_FACTORY_TEST_RESULT = "www.goodix.factory.action.RESULT";
    private final String FP_WIND_FPSENSOR = "com.wind.fpsensor.RESULT";
	
    private Thread mThread = new Thread(){
        public void run() {
            Log.i("yangjiajun", "FingerTest fail");
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 2);
            Utils.writeResultFile(null, "fail");
            mContext.unregisterReceiver(mReceiver);
        };
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
		//M:weihongxi@wind-mobi.com 160920 delete begin
		/*
            if(i != null && "com.wind.adbtest".equals(i.getAction())){
                int value = i.getIntExtra("result", -1);
                mHandler.removeCallbacks(mThread);
                if(value == 1){
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 1);
                    Utils.writeResultFile(null, "success");
                }else{
                    Log.i("yangjiajun", "FingerTest mReceiver fail");
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 2);
                    Utils.writeResultFile(null, "fail");
                }
                mContext.unregisterReceiver(mReceiver);
            }
		*///M:weihongxi@wind-mobi.com 160920 delete end
			String action = intent.getAction();
            Log.i("weihongxi", "fingerTest onReceive action = " + action);
            if (intent != null && action.equals(FP_SILEAD_FACTORY_TEST_RESULT)) {
                int result = intent.getIntExtra("result", -1);
				mHandler.removeCallbacks(mThread);
                if (result == 0) {
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 1);
                    Utils.writeResultFile(null, "success");					
                    Log.i("weihongxi", "FingerTest.FP_SILEAD_FACTORY_TEST_RESULT mReceiver success");
                } else {                    
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 2);
                    Utils.writeResultFile(null, "fail");
					Log.i("weihongxi", "FingerTest.FP_SILEAD_FACTORY_TEST_RESULT mReceiver fail");
                }
                mContext.unregisterReceiver(mReceiver);
            } else if (intent != null && action.equals(FP_MADEV_FACTORY_TEST_RESULT)) {
                String result = intent.getStringExtra("result");
				mHandler.removeCallbacks(mThread);
                if (result.equals("pass") || result.equals("PASS")) {
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 1);
                    Utils.writeResultFile(null, "success");
					Log.i("weihongxi", "FingerTest.FP_MADEV_FACTORY_TEST_RESULT mReceiver success");
                } else {
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 2);
                    Utils.writeResultFile(null, "fail");
					Log.i("weihongxi", "FingerTest.FP_MADEV_FACTORY_TEST_RESULT mReceiver fail");
                }
                mContext.unregisterReceiver(mReceiver);
            } else if (intent != null && action.equals(FP_GOODIX_FACTORY_TEST_RESULT)) {
                String result = intent.getStringExtra("result");
				mHandler.removeCallbacks(mThread);
                if (result.equals("pass") || result.equals("PASS")) {
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 1);
                    Utils.writeResultFile(null, "success");
					Log.i("weihongxi", "FingerTest.FP_GOODIX_FACTORY_TEST_RESULT mReceiver success");
                } else {
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 2);
                    Utils.writeResultFile(null, "fail");
					Log.i("weihongxi", "FingerTest.FP_GOODIX_FACTORY_TEST_RESULT mReceiver fail");
                }
                mContext.unregisterReceiver(mReceiver);
            } else if (intent != null && action.equals(FP_WIND_FPSENSOR)) {
                int result = intent.getIntExtra("result", -1);
				mHandler.removeCallbacks(mThread);
                if (result == 1) {
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 1);
                    Utils.writeResultFile(null, "success");
					Log.i("weihongxi", "FingerTest.FP_WIND_FPSENSOR mReceiver success");
                } else {
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_finger", 2);
                    Utils.writeResultFile(null, "fail");
					Log.i("weihongxi", "FingerTest.FP_WIND_FPSENSOR mReceiver fail");
                }
                mContext.unregisterReceiver(mReceiver);
			} else {
		        Log.i("weihongxi","FingerTest.onReceive.error");
			}
        }
    };

    public void test(Context context, int delayTime, Handler handler){
    /*    mContext = context;
        mHandler = handler;
        context.registerReceiver(mReceiver, new IntentFilter("com.wind.adbtest"));
        handler.postDelayed(mThread, delayTime*1000);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.wind.fingerprint","com.wind.fingerprint.TestActivity"));
        intent.putExtra("test_type", "auto");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
	*/	
		mContext = context;
        mHandler = handler;
		IntentFilter filter = new IntentFilter();
        filter.addAction(FP_SILEAD_FACTORY_TEST_RESULT);
        filter.addAction(FP_MADEV_FACTORY_TEST_RESULT);
        filter.addAction(FP_GOODIX_FACTORY_TEST_RESULT);
        filter.addAction(FP_WIND_FPSENSOR);
        context.registerReceiver(mReceiver, filter);
		handler.postDelayed(mThread, delayTime*1000);

        File file_microarray = new File("/dev/madev0");
        File file_microarray_tee = new File("/dev/madev");
        File file_silead = new File("/dev/silead_fp_dev");
		File file_fpsensor = new File("/dev/fpsensor");
            Log.i("yangjiajun", "FingerTest start");
       
        if (file_microarray.exists()) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("ma.factory",
                    "ma.factory.MainActivity"));
            Log.i("weihongxi","FingerTest.test.file_microarray");
			intent.putExtra("test_type", "auto");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);			
        } else if (file_microarray_tee.exists()) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("ma.fprint.factory",
                    "test.fprint.MaTest"));
			Log.i("weihongxi","FingerTest.test.file_microarray_tee");
            intent.putExtra("test_type", "auto");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
        } else if (file_silead.exists()) {
			Intent intent = new Intent();
            intent.setComponent(new ComponentName("www.silead",
                    "www.silead.SileadFpTestService"));
			Log.i("weihongxi","FingerTest.test.file_silead");
			intent.putExtra("test_type", "auto");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (file_fpsensor.exists()){
			Log.i("weihongxi","FingerTest.test.file_fpsensor");
			/*try{
            Intent  intent = new Intent();
            intent.putExtra("config_autoexit", true); //控制是否测试完成后自动退出activity
            intent.putExtra("config_autotest", true);//控制是否启动activity自动执行测试
            intent.putExtra("config_showcapturedImg", true);//控制是否显示采集的图片
 
            intent.setClassName("com.fpsensor.fpSensorExtension", "com.fpsensor.sensortesttool.sensorTestActivity");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);		
			}catch(Exception e){
				e.printStackTrace();
			}*/
            Log.i("yangjiajun", "FingerTest fpsensor");
			Intent intent = new Intent(context, com.wind.factoryautotest.FingerTestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//|Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent);  
			Log.i("weihongxi", "FingerTest.test.file_fpsensor.end");
			
		} else {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.goodix.test",
                    "com.goodix.mptool.testActivity.MpTestActivity"));
            Log.i("weihongxi","FingerTest.test.com.goodix");
			intent.putExtra("test_type", "auto");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
        }
    }
}
