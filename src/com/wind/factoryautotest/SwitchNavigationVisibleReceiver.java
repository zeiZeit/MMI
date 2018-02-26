package com.wind.factoryautotest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.os.RemoteException;

public class SwitchNavigationVisibleReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if("com.wind.factoryautotest.action.SWITCH_NAVIGATION_VISIBLE".equals(intent.getAction())){
            IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
            try{
                wm.setWindImmersive(!wm.getWindImmersive());
            }catch(RemoteException e){
                e.printStackTrace();
            }
            //int val = Settings.System.getInt(context.getContentResolver(), "navigation_invisible", 0);
            //Settings.System.putInt(context.getContentResolver(), "navigation_invisible", (val == 1 ? 0 : 1));
        }*/
    }
}
