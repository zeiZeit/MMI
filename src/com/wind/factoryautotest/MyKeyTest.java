package com.wind.factoryautotest;

import android.content.Context;
import android.util.Log;

import android.os.SystemProperties;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class MyKeyTest {

	Context mContext = null;
	boolean mKeyTesting = false;
	//private int[] key_array = {3/*HOME*/, 4/*back*/, 24/*vol_up*/, 25/*vol_down*/, 26/*power*/};	//for Z200
	private int[] key_array = {24/*vol_up*/, 25/*vol_down*/, 26/*power*/, 79};	//for E260
	private int array_len = 8;
	
	public MyKeyTest(Context context) {
		mContext = context;
	}

	public void startKeyTest() {
		Log.i("dongxiaotang", "startKeyTest() mKeyTesting: " + mKeyTesting);
		if(!mKeyTesting) {
			mKeyTesting = true;
			android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_key", 2);
                        android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_hook", 2);
			SystemProperties.set("sys.wind.keytest", "1");
			SystemProperties.set("sys.wind.keytestval", "0");
			/*new Thread() {
				public void run() {
					if("0".equals(SystemProperties.get("sys.wind.keytestval", "0"))) {
						android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_key", 1);
					}
					mKeyTesting = false;
				}
			} .start();*/
		}
		//int res = Utils.startKeyTest();
		//Log.i("dongxiaotang", "startKeyTest() res: " + res);
	}

	public String getFailKeys() {
		
		/* String s = null;		
		try {
			String failkeys = "/sdcard/failkeys";
			File pipe = new File(failkeys);
			BufferedReader reader = new BufferedReader(new FileReader(pipe));
			s = reader.readLine();
		} catch(Exception e) {
			e.printStackTrace();
		} 
		int data = s != null ? Integer.parseInt(s) : 0; */

		int data = Integer.parseInt(SystemProperties.get("sys.wind.keytestval", "0"));
		Log.i("dongxiaotang", "getFailKeys(): " + data);
		String failkey = "";
		int[] a = new int[array_len];
		if(data == 255){
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_key", 1);
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_hook", 1);
        }
		
		for(int i = 0; i < array_len; i++) {
			a[i] = data % 2;
			data = data / 2;
		}

		for(int i = 0; i < array_len; i++) {
			if(i%2 == 0) {
				failkey = a[i] == 0 ? (failkey + "+" + key_array[i/2] + ",") : (failkey + "");
			} else {
				failkey = a[i] == 0 ? (failkey + "-" + key_array[i/2] + ",") : (failkey + "");
			}
		}
		          
		return failkey;
	}
}
