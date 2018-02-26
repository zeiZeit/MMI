package com.wind.factoryautotest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

public class TpFactoryTest {

	private static final String TP_PATH = "/sys/devices/platform/HardwareInfo/01_ctp";//"/proc/ctp_info"
	//private WakeLock mWakeLock = null;
	private Handler mHandler = null;
	private Context mContext = null;
	private String mPath = "";
	private String mResult = "";
	private int result = -1;
	/*private final Thread mThread = new Thread(){
	    public void run() {
	        unRegisterReceiver();
	        android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_tp", 2);
	        Utils.writeResultFile("", "fail");
	    }
	};*/

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//Log.d(LOG_TAG,"action="+action);
			if ("android.intent.action.goodix".equals(action)) {
				//mHandler.removeCallbacks(mThread);
				unRegisterReceiver();
				result = intent.getIntExtra("testResult", -1);
				mResult = "\nTP:" + (result == 0 ? "success" : "fail");
				mHandler.post(new Thread() {
					public void run() {
						android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_tp", result == 0 ? 1:2);
						Utils.appendResult(mPath, mResult);
					}
				});
			} else if("com.focaltech.ft_terminal_test".equals(action)) {
				//mHandler.removeCallbacks(mThread);
				unRegisterReceiver();
				result = intent.getIntExtra("testResult", -1);
				mResult = "\nTP:" + (result == 0 ? "success" : "fail");
				mHandler.post(new Thread() {
					public void run() {
						android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_tp", result == 0 ? 1:2);
						Utils.appendResult(mPath, mResult);
					}
				});
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.goodix");
		filter.addAction("com.focaltech.ft_terminal_test");
		//filter.addAction(ACTION_USB_DISCONNECTED);
		//filter.addAction(ACTION_USB_CONNECTED);
		//filter.addAction(ACTION_UMS_CONNECTED);
		//filter.addAction(ACTION_UMS_DISCONNECTED);
		mContext.registerReceiver(mReceiver, filter);
	}

	private void unRegisterReceiver() {
		mContext.unregisterReceiver(mReceiver);
		//mWakeLock.release();
	}

	public void test(Context context, Handler handler, String path) {
		mHandler = handler;
		mContext = context;
		mPath = path;
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		if(!pm.isScreenOn()) {
			pm.wakeUp(SystemClock.uptimeMillis());
		}
		/*mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "tp_factory_test");
		mWakeLock.acquire();*/
		registerReceiver();
		//handler.postDelayed(mThread, delayTime*1000);
		InputStream is;
		byte[] bytes = new byte[256];
		int count;
		String tpType = "unknown";
		try {
			is = new FileInputStream(TP_PATH);
			count = is.read(bytes);
			is.close();
			tpType = new String(bytes, 0, count);
			tpType = tpType.substring(tpType.indexOf(':') + 1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}

		Intent intent = new Intent();
		tpType=tpType.replace("\n","");
		if(tpType.equals("gt9xx_jinlong") || tpType.equals("gt9xx_each")) {
			intent.setComponent(new ComponentName("com.goodix.rawdata","com.goodix.rawdata.RawDataTest"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("command", 1);
			intent.putExtra("frequences", 1);
			intent.putExtra("autofinish", true);
			intent.putExtra("successfinish", true);
			if(tpType.equals("gt9xx_jinlong")) {
				intent.putExtra("iniformat", "test_sensor_0_lcm");
			} else {
				intent.putExtra("iniformat", "test_sensor_1_lcm");
			}
			intent.putExtra("inidir", "system/etc/");
			//intent.putExtra("resultdir", "sdcard/data/");
			context.startActivity(intent);
		} else if(tpType.equals("fts_each") || tpType.equals("fts_zhenghai")) {
			intent.setComponent(new ComponentName("com.focaltech.ft_terminal_test","com.focaltech.ft_terminal_test.MainActivity"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("command", 1);
			intent.putExtra("view", 1);
			intent.putExtra("autoFinish", 1);
			context.startActivity(intent);
		}
	}
}
