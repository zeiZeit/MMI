package com.wind.factoryautotest;

import java.io.IOException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryTest {

    public void test(Context context, /*String path*/java.io.OutputStream os){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                             status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        String result = (isCharging && (usbCharge || acCharge)) ? "YES" : "NO";
        result = result + "," + batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 10);
        Log.i("yangjiajun", result);
        //Utils.writeResultFile(path, result);
        android.provider.Settings.System.putInt(context.getContentResolver(), "emode_pac_flag_battery", (isCharging && (usbCharge || acCharge)) ? 1:2);
        try{
            os.write(result.getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
