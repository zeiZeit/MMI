package com.wind.factoryautotest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class HeadsetTest {

    private Context mContext = null;
    private Handler mHandler = null;
    private boolean[] mStates = {false, false};
    private String mPath = null;
    private Thread mThread = new Thread(){
        public void run() {
            Log.i("yangjiajun", "HeadsetTest fail");
            Utils.writeResultFile(mPath, "fail");
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_headset", 2);
            mContext.unregisterReceiver(mReceiver);
        };
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent i) {
            if(i != null && Intent.ACTION_HEADSET_PLUG.equals(i.getAction())){
                int state = i.getIntExtra("state", 0);
                if(!mStates[state]){
                    mStates[state] = true;
                    Log.i("yangjiajun", "state: " + state);
                    if(mStates[0] && mStates[1]){
                        mHandler.removeCallbacks(mThread);
                        Log.i("yangjiajun", "success");
                        Utils.writeResultFile(mPath, "success");
                        android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_headset", 1);
                        mContext.unregisterReceiver(mReceiver);
                    }
                }
            }
        }
    };

    public void test(Context context, int delayTime, String path, Handler handler){
        mContext = context;
        mPath = path;
        mHandler = handler;
        context.registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        handler.postDelayed(mThread, delayTime*1000);
    }
}
