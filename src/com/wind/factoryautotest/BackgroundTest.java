package com.wind.factoryautotest;

import java.text.DecimalFormat;
import java.io.File;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.content.Context;
import android.os.SystemProperties;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.BatteryManager;
//import com.mediatek.telephony.TelephonyManagerEx;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.wind.emode.SensorCaliUtil;

public class BackgroundTest {

    public static boolean IS_TEST_HEADSET = false;
    private boolean[] mStates = {false, false};
    private boolean mIsHeadsetTesting = false;

    private final BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver(){
        public void onReceive(Context c, android.content.Intent i) {
            if(i != null && Intent.ACTION_HEADSET_PLUG.equals(i.getAction())){
                int state = i.getIntExtra("state", 0);
                if(!mStates[state]){
                    mStates[state] = true;
                    Log.i("yangjiajun", "state: " + state);
                    if(mStates[0] && mStates[1]){
                        android.provider.Settings.System.putInt(c.getContentResolver(), "emode_pac_flag_audioloop", 1);
                        c.unregisterReceiver(mHeadsetReceiver);
                        mIsHeadsetTesting = false;
                    }
                }
            }
        }
    };

    private String batteryTest(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                             status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        int voltage = batteryStatus.getIntExtra("voltage", 0);
        boolean isChargingOk = (isCharging && (usbCharge || acCharge));
        boolean isVoltageOk = (voltage >= 3400 && voltage <= 4400);
        String result = isChargingOk ? "YES" : "NO";
        result = result + "," + batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 10) + "," + voltage;
        Log.i("yangjiajun", result);
        android.provider.Settings.System.putInt(context.getContentResolver(), "emode_pac_flag_battery", (isChargingOk && isVoltageOk) ? 1:2);
        return result;
    }

    public void test(Context context, Handler handler){
        Resources res = context.getResources();
        ContentResolver cr = context.getContentResolver();
        android.provider.Settings.System.putInt(cr, "emode_pac_flag_cali", 2);
        context.sendBroadcast(new Intent("com.wind.factoryautotest.action.SENSOR_CALI"));
        checkStorageInfo(context);
        ActivityManager.MemoryInfo meminfo = new ActivityManager.MemoryInfo();
        ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(meminfo);
        boolean memoryAvailable = (meminfo.totalMem/1024.0f/1024 > 10) && (meminfo.availMem/1024.0f/1024 > 1);
        android.provider.Settings.System.putInt(cr, "emode_pac_flag_memory", memoryAvailable?1:2);
        TelephonyManager tm = TelephonyManager.getDefault();
        int sim1State = tm.getSimState(0);
        sim1State = (sim1State == TelephonyManager.SIM_STATE_ABSENT || sim1State == TelephonyManager.SIM_STATE_UNKNOWN) ? 0 : 1;
        android.provider.Settings.System.putInt(cr, "emode_pac_flag_sim1", sim1State==1?1:2);
        batteryTest(context);
        OTGTest.getInstance().test(context);
        if(IS_TEST_HEADSET){
            if(!mIsHeadsetTesting){
                mIsHeadsetTesting = true;
                android.provider.Settings.System.putInt(cr, "emode_pac_flag_audioloop", 2);
                context.registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
            }
        }
		
		//changed by dongxiaotang@wind-mobi.com 20170828 start
        /* try{
            android.os.IFactoryAutoTest.Stub.asInterface(android.os.ServiceManager.getService("factory_auto_test")).startKeyTest();
        }catch(android.os.RemoteException e){
            e.printStackTrace();
        } */
		new MyKeyTest(context).startKeyTest();
		//changed by dongxiaotang@wind-mobi.com 20170828 end
		
        if(context.getPackageManager().queryIntentActivities(new Intent("com.wind.factoryautotest.action.TP_AUTO_TEST"),  
                    PackageManager.MATCH_DEFAULT_ONLY).size() > 0){
            Intent tpIntent = new Intent("com.wind.factoryautotest.action.TP_AUTO_TEST");
            tpIntent.putExtra("is_from_factoryautotest", true);
            tpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(tpIntent);
        }
        GpsTest.getInstance().test(context, handler);
        WifiTest.getInstance().test(context, 50, handler);
        BtTest.getInstance().test(context, 50, handler);
        SensorTest.getInstance().test(context, handler);
    }

    private void checkStorageInfo(Context context){
        String externalPath = "";
        String internalPath = "";
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = mStorageManager.getVolumeList();
        for (StorageVolume volume : volumes) {
            if(Environment.MEDIA_MOUNTED.equals(mStorageManager.getVolumeState(volume.getPath()))){
                if(volume.isRemovable()){
                    if(volume.getPath().indexOf("otg") == -1){
                        externalPath = volume.getPath();
                    }
                }else{
                    internalPath = volume.getPath();
                }
            }
        }

        StatFs stat = null;
        if(!"".equals(externalPath)){
            try{
                stat = new StatFs(externalPath);
            }catch(Exception e){
                Log.i("yangjiajun", "Create StatFs exception!");
                e.printStackTrace();
            }
        }else{
             Log.i("yangjiajun", "sdcard not insert!");
        }
        long blockSize = 0;
        long totalBlocks = 0;
        long availableBlocks = 0;
        if(stat != null){
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        boolean storageAvailable = (totalBlocks * blockSize/1024.0f/1024 > 10) && (availableBlocks * blockSize/1024.0f/1024 > 1);
        android.provider.Settings.System.putInt(context.getContentResolver(), "emode_pac_flag_sdcard", storageAvailable?1:2);
        stat = null;
        if(!"".equals(internalPath)){
            try{
                stat = new StatFs(internalPath);
            }catch(Exception e){
                Log.i("yangjiajun", "Create StatFs exception!");
                e.printStackTrace();
            }
        }else{
             Log.i("yangjiajun", "phone storage path error!");
        }
        if(stat != null){
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        storageAvailable = (totalBlocks * blockSize/1024.0f/1024 > 10) && (availableBlocks * blockSize/1024.0f/1024 > 1);
        android.provider.Settings.System.putInt(context.getContentResolver(), "emode_pac_flag_storage", storageAvailable?1:2);
    }

    private String formatSize(long size) {
        if (size <= 0)
            return "0";
        DecimalFormat format = new DecimalFormat(".00");
        return format.format(size/1024.0f/1024/1024)+"G";
    }
}
