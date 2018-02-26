package com.wind.factoryautotest;

import android.app.Activity;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

public class KeyTest extends Activity {

    private HashMap<Integer, Boolean> mKeyMap = new HashMap<Integer, Boolean>();

    boolean hasTpKey = false;;
    boolean signalHomeKey = false;
    boolean homeKeyDisable = false;
    private String mPath = null;

    Thread mReadThread = new Thread(){
        public void run() {
            int newValue = 0;
            int oldValue = 0;
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (newValue == 0) {
                    newValue = readFileByLines();
                }
                Log.d("duanzhanyang", "current is " + oldValue
                        + " newValue is " + newValue);
                oldValue = newValue;

                newValue = readFileByLines();
                if (isSensitivityEnable(newValue, oldValue)) {
                    homeKeyDisable = true;
                    mKeyMap.put(Integer.MAX_VALUE, true);
                    Log.d("duanzhanyang",
                            "send message Calibration success");

                }
                if (homeKeyDisable == true) {
                    break;
                }
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(android.content.Context c, Intent i) {
            StringBuffer failKeys = new StringBuffer();
            Iterator<HashMap.Entry<Integer, Boolean>> it = mKeyMap.entrySet().iterator();
            HashMap.Entry<Integer, Boolean> entry = null;
            while(it.hasNext())
            {
                entry = it.next();
                if(!entry.getValue().booleanValue()){
                    failKeys.append(getKeyName(entry.getKey()) + ",");
                }
            }
            if("".equals(failKeys.toString())){
                android.provider.Settings.System.putInt(c.getContentResolver(), "emode_pac_flag_key", 1);
                Utils.writeResultFile(mPath, "success");
            }else{
                String result = failKeys.toString();
                result = result.substring(0, result.length() - 1);
                android.provider.Settings.System.putInt(c.getContentResolver(), "emode_pac_flag_key", 2);
                Utils.writeResultFile(mPath, result);
            }
            if(signalHomeKey){
                if(!homeKeyDisable){
                    homeKeyDisable = true;
                }
            }
            finish();
        };
    };
    
    private String getKeyName(int key){
        switch(key){
        	  case KeyEvent.KEYCODE_VOLUME_DOWN:
        	      return "vol_sub_down";
        	  case -KeyEvent.KEYCODE_VOLUME_DOWN:
        	      return "vol_sub_up";
        	  case KeyEvent.KEYCODE_VOLUME_UP:
        	      return "vol_add_down";
        	  case -KeyEvent.KEYCODE_VOLUME_UP:
        	      return "vol_add_up";
        	  case KeyEvent.KEYCODE_HOME:
        	      return "home_down";
        	  case -KeyEvent.KEYCODE_HOME:
        	      return "home_up";
        	//weihongxi  20161031 begin
		/* case KeyEvent.KEYCODE_MENU:
        	      return "menu_down";
        	  case -KeyEvent.KEYCODE_MENU:
        	      return "menu_up";
        	  */
                  case KeyEvent.KEYCODE_APP_SWITCH:
                      return "app_switch_down";
                  case -KeyEvent.KEYCODE_APP_SWITCH:
                      return "app_switch_up";
		 //weihongxi  20161031 end
		  case KeyEvent.KEYCODE_BACK:
        	      return "back_down";
        	  case -KeyEvent.KEYCODE_BACK:
        	      return "back_up";
        	  case 0:
        	      return "finger_down";
        	  case Integer.MIN_VALUE:
        	      return "finger_up";
        	  case Integer.MAX_VALUE:
        	      return "sensibility";
        	  default:
        	      return "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent == null){
            Log.i("yangjiajun", "KeyTest fail");
            finish();
        }else{
            mPath = "";//intent.getStringExtra("path");
        }
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if(!pm.isScreenOn()){
            pm.wakeUp(SystemClock.uptimeMillis());
        }
        getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED/* FLAG_HOMEKEY_DISPATCHED */);
        TextView tv = new TextView(this);
        tv.setText("Key Testing...");
        setContentView(tv);

        mKeyMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, false);
        mKeyMap.put(KeyEvent.KEYCODE_VOLUME_UP, false);
        mKeyMap.put(-KeyEvent.KEYCODE_VOLUME_DOWN, false);
        mKeyMap.put(-KeyEvent.KEYCODE_VOLUME_UP, false);
        hasTpKey = getResources().getBoolean(R.bool.hasTpKey);
        if (hasTpKey) {
            mKeyMap.put(KeyEvent.KEYCODE_HOME, false);
            mKeyMap.put(-KeyEvent.KEYCODE_HOME, false);
            //mKeyMap.put(KeyEvent.KEYCODE_MENU, false);
            //mKeyMap.put(-KeyEvent.KEYCODE_MENU, false);
            mKeyMap.put(KeyEvent.KEYCODE_APP_SWITCH, false);
            mKeyMap.put(-KeyEvent.KEYCODE_APP_SWITCH, false);
            mKeyMap.put(KeyEvent.KEYCODE_BACK, false);
            mKeyMap.put(-KeyEvent.KEYCODE_BACK, false);
        }
        signalHomeKey = getResources().getBoolean(R.bool.signalHomeKey);
        if (signalHomeKey) {
            //int systemUiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED;
            int systemUiFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            systemUiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
            mKeyMap.put(KeyEvent.KEYCODE_HOME, false);
            mKeyMap.put(-KeyEvent.KEYCODE_HOME, false);
            mKeyMap.put(KeyEvent.KEYCODE_BACK, false);//finger down
            mKeyMap.put(-KeyEvent.KEYCODE_BACK, false);//finger up
            //mKeyMap.put(Integer.MAX_VALUE, false);//lin ming du
            //mReadThread.start();
        }
        registerReceiver(mReceiver, new IntentFilter("com.wind.factoryautotest.ACTION_KEYTEST"));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        Log.i("yangjiajun", "------ key code:" + keyCode);
        int cs = ((event.getAction() == KeyEvent.ACTION_DOWN)?1:-1);
        int key = keyCode*cs;
        if(keyCode == 0){
            key = (cs>0?0:Integer.MIN_VALUE);
        }
        if(mKeyMap.get(key) != null && !mKeyMap.get(key)){
            mKeyMap.put(key, true);
            StringBuffer failKeys = new StringBuffer();
            Iterator<HashMap.Entry<Integer, Boolean>> it = mKeyMap.entrySet().iterator();
            HashMap.Entry<Integer, Boolean> entry = null;
            while(it.hasNext())
            {
                entry = it.next();
                if(!entry.getValue().booleanValue()){
                    failKeys.append(getKeyName(entry.getKey()) + ",");
                }
            }
        Log.i("yangjiajun", "------ fails keys:" + failKeys.toString());
            if("".equals(failKeys.toString())){
                android.provider.Settings.System.putInt(getContentResolver(), "emode_pac_flag_key", 1);
            }else{
                String result = failKeys.toString();
                result = result.substring(0, result.length() - 1);
                android.provider.Settings.System.putInt(getContentResolver(), "emode_pac_flag_key", 2);
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        //unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public int readFileByLines() {
        File file = new File("/sys/devices/virtual/input/input2/rawdata");
        BufferedReader reader = null;
        String tempString = null;
        String data = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((tempString = reader.readLine()) != null) {
                data = tempString;
                Log.d("duanzhanyang", "tempString " + tempString);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        Log.d("duanzhanyang", "data is " + data);
        return Integer.parseInt(data.substring(0, data.indexOf(",")));

    }

    private boolean isSensitivityEnable(int newValue, int oldValue) {
        return (newValue > 100 && newValue < 900) ? (oldValue > 100
                && oldValue < 900 ? (newValue - oldValue > 15 ? true : false)
                : false) : false;
    }
}
