package com.wind.factoryautotest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.app.Service;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class FactoryReceiver extends BroadcastReceiver {	

	//private IFactoryAutoTest mIFactoryAutoTest;
	//mIFactoryAutoTest = android.os.IFactoryAutoTest.Stub.asInterface(ServiceManager.getService("factory_auto_test"));

	@Override
	public void onReceive(Context context, Intent intent) {
            if(intent != null && "com.mmi.helper.request".equals(intent.getAction())){
				android.util.Log.i("zuozhuang","come here !!");
		String type = intent.getExtras().getString("type");
		String value = intent.getExtras().getString("value");
		String pac_flag = intent.getExtras().getString("pac_flag");
		Log.i("dongxiaotang", "type: " + type + " value: " + value);
		
		Intent fmIntent = new Intent("com.wind.factoryautotest.action.FmService");
		fmIntent.setPackage("com.wind.factoryautotest");
		
		Intent reIntent = new Intent();
		reIntent.setAction("com.mmi.helper.response");
		
		switch(type){
			case "led_open": 
				new LEDTest().test("11");
				break;

			case "led_shutdown":
				new LEDTest().test("10");			
				break;

			case "vibrator_play":
				new VibratorTest().test(context, true);
				break;
			case "vibrator_stop":
				new VibratorTest().test(context, false);
				break;
				
		    case "set_pacmmi_result":
				boolean i = false;
			    if(value.equals("pass")){
				    i = PartitionUtils.writeFile(true);
				}else if( value.equals("fail")){
					i = PartitionUtils.writeFile(false);
				}
				
				if (!i) reIntent.putExtra("result","fail");
				if (i) reIntent.putExtra("result","success");
				context.sendBroadcast(reIntent);
				break;
				
			case "set_pacflag_result":

                try{
                    android.provider.Settings.System.putInt(context.getApplicationContext().getContentResolver(), pac_flag, Integer.parseInt(value));
					reIntent.putExtra("result","success");
					context.sendBroadcast(reIntent);
                }catch(NumberFormatException e){
					reIntent.putExtra("result","fail");
					context.sendBroadcast(reIntent);
                }
			break;
			
			case "get_pacflag_result":
                try{
                    String result = android.provider.Settings.System.getInt(context.getApplicationContext().getContentResolver(), pac_flag, 0)==1?"success":"fail";
					reIntent.putExtra("result",result);
					context.sendBroadcast(reIntent);
                }catch(NumberFormatException e){
					reIntent.putExtra("result","fail");
					context.sendBroadcast(reIntent);
                }
			break;
			
			case "get_all_pacflag":
                String[] items = getRemoteStringArray("emode_pac_flag_keys",context);
                ContentResolver cr = context.getApplicationContext().getContentResolver();
                int ilen = items.length;
                StringBuffer content = new StringBuffer();
                String msn = android.os.SystemProperties.get("gsm.serial", "");
                if(msn.indexOf(' ') >= 0){
                    msn = msn.substring(0, msn.indexOf(' '));
                }else{
                    msn = "unknown";
                }
                String version = android.os.SystemProperties.get("ro.build.version.incremental", "unknown");
                content.append("sn:" + msn + ",version:" + version + ",");
                for(int j = 0; j < ilen; j ++){
                    content.append(items[j].substring(15) + ":" + android.provider.Settings.System.getInt(cr, items[j], 0) + ",");
                }
				String result = content.substring(0, content.length()-1);
				reIntent.putExtra("result",result);
			    context.sendBroadcast(reIntent);
			break;
				
			default:
                break;
		}
	    }
	}
	
	
	private String[] getRemoteStringArray(String name,Context context){
	    String[] strs = null;
        try {
            Resources res = context.getPackageManager().getResourcesForApplication("com.wind.emode");
            int resourceId = res.getIdentifier(name, "array", "com.wind.emode");
            if(0 != resourceId) {
                strs = res.getStringArray(resourceId);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            }
        return strs;
    }
	
}
