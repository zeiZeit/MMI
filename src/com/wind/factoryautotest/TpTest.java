package com.wind.factoryautotest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.WindowManager;
import android.view.View;
import android.os.PowerManager;
import android.content.Context;
import android.os.SystemClock;
import android.os.Bundle;

public class TpTest extends Activity{

    private MyComplexView mView = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(android.content.Context c, Intent i) {
            android.util.Log.i("yangjiajun", "ret: " + mView.isAllTouch());
            Utils.writeResultFile(""/*i.getStringExtra("path")*/, mView.isAllTouch()?"success":"fail");
            finish();
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        if(!pm.isScreenOn()){
            pm.wakeUp(SystemClock.uptimeMillis());
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        mView = new MyComplexView(this);
        setContentView(mView);
		    //int systemUiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED;
		    int systemUiFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		    systemUiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		    getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
        registerReceiver(mReceiver, new IntentFilter("com.wind.factoryautotest.ACTION_TPTEST"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
