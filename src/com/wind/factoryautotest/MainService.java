package com.wind.factoryautotest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.app.Activity;
import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.KeyguardManager;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.IWindowManager;
import android.widget.RemoteViews;
import android.bluetooth.BluetoothAdapter;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.Looper;
import android.content.Context;
import android.content.ContentResolver;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.view.SurfaceView;
import com.wind.emode.SensorCaliUtil;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class MainService extends Service {

    private WakeLock mWakeLock = null;
    private boolean mRunning = false;
    private Handler mHandler = new Handler();
    private SurfaceView mSurface = null;
    public static String result = "";
    private static final int NOTIFICATION_ID = 1312;
    private Notification mNotification = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;
	
    /*private void createNotification(){
        mNotification = new Notification();
        mNotification.tickerText = getString(R.string.app_name);
        mNotification.icon = R.drawable.ic_launcher;
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        RemoteViews contentView = new RemoteViews(getPackageName(),android.R.layout.simple_list_item_1);
        contentView.setTextViewText(android.R.id.text1, getString(R.string.tip_service));
        mNotification.contentView = contentView;
        //Intent notificationIntent = new Intent(this, MainActivity.class);
        //PendingIntent contentIntent = ;
        mNotification.contentIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.wind.factoryautotest.action.SWITCH_NAVIGATION_VISIBLE"), 0);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("yangjiajun", "MainService onCreate()...");
        //createNotification();
        //startForeground(NOTIFICATION_ID, mNotification);
        //Settings.System.putInt(getContentResolver(), "navigation_invisible", 1);
        /*try{
            IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE)).setWindImmersive(true);
        }catch(RemoteException e){
            e.printStackTrace();
        }*/
        mWakeLock = ((PowerManager)this.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "factory_auto_test");
        mWakeLock.acquire();
        KeyguardManager km = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = km.newKeyguardLock("factory_auto_test");
        mKeyguardLock.disableKeyguard();
        mSurface = new SurfaceView(this);
				
        new Thread(){
            public void run() {
                ServerSocket server= null;
                try{
                    server= new ServerSocket(8086);//>1024 Linux port
                }catch(IOException e){
                    e.printStackTrace();
                }
                if(server != null){
                    mRunning = true;
                    Socket client = null;
                    InputStream is = null;
                    OutputStream os = null;
                    byte[] buffer = new byte[256];
                    int count = 0;
                    String cmd = null;
                    Log.i("yangjiajun", "MainService begin while...");
                    while(mRunning){
                        try{
                            client = server.accept();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        if(client != null){
                            try{
                                is = client.getInputStream();
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            if(is != null){
                                try{
                                    count = is.read(buffer);
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                                if(count > 0){
                                    cmd = new String(buffer, 0, count);
                                }
                                if(cmd != null && !"".equals(cmd.trim())){
                                    try{
                                        os = client.getOutputStream();
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                    if(os != null){
                                        processCmd(cmd, os);
                                        try{
                                            os.close();
                                        }catch(IOException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                try{
                                    is.close();
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                            try{
                                client.close();
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    try{
                        server.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    Log.i("yangjiajun", "MainService socket thread exit...");
                    stopSelf();
                }else{
                    Log.i("yangjiajun", "MainService create server fail...");
                }
            }
        }.start();
    }

    private boolean isActivityOnForeground(String activityName) {
        List<RunningTaskInfo> tasksInfo = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            Log.i("yangjiajun", "MainService running tasks size > 0 topActivity name is " + tasksInfo.get(0).topActivity.getShortClassName());
            if (activityName.equals(tasksInfo.get(0).topActivity.getShortClassName())) {
                Log.i("yangjiajun", "MainService activityName: " + activityName + " is the topActivity!");
                return true;
            }
        }
        Log.i("yangjiajun", "MainService activityName: " + activityName + " is not the topActivity!");
        return false;
    }

    private String[] getRemoteStringArray(String name){
        String[] strs = null;
        try {
			Log.i("yangjiajun", "MainService getRemoteStringArray: String name " + name );
            Resources res = getPackageManager().getResourcesForApplication("com.wind.emode");
			Log.i("yangjiajun", "MainService getRemoteStringArray: res :" + res.toString());
            int resourceId = res.getIdentifier(name, "array", "com.wind.emode");
			Log.i("yangjiajun", "MainService getRemoteStringArray: resourceId: " + resourceId );
            if(0 != resourceId) {
                strs = res.getStringArray(resourceId);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return strs;
    }

    private void setPacMMIResult(OutputStream os){
        android.content.ContentResolver cr = getContentResolver();
        String[] allKeys = getRemoteStringArray("emode_pac_flag_keys");
        if(allKeys == null){
            try{
                os.write("fail".getBytes());
            }catch(IOException e){
                e.printStackTrace();
            }
            return;
        }
        int len = allKeys.length;
        boolean flag = true;
        //checkMMIResult
        for(int i = 0; i < len; i ++){
            if(Settings.System.getInt(cr, allKeys[i], 0) != 1){
                flag = false;
                break;
            }
        }
        Log.i("yangjiajun", "pac mmi check result: " + flag + "   len: " + len);//25 or 26
            //Log.d("yangjiajun", "buff["+index+"]="+buff[index]);
            if(!PartitionUtils.writeFile(flag)||(!flag)){
                android.util.Log.e("yangjiajun", "MainService - setAutoMMIResult writeFile fail!");
                try{
                    os.write("fail".getBytes());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }else{
                try{
                    os.write("success".getBytes());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
    }

    private void processCmd(String cmd, OutputStream os){
        Log.i("yangjiajun", "cmd: " + cmd);
        if("exit".equals(cmd)){
            mRunning = false;
            try{
                os.write("success".getBytes());
            }catch(IOException e){
                e.printStackTrace();
            }
        }else{
            String[] params = cmd.split(" ");
            int cmd_num = -1;
            try{
                cmd_num = Integer.parseInt(params[0]);
            }catch(NumberFormatException e){
                e.printStackTrace();
            }
            switch(cmd_num){
                case 1:
                    AudioLoopTest.getInstance(getApplicationContext(),mHandler).test(params[1]);
                    break;
                case 2:
                    try{
                        Log.i("yangjiajun", "cmd 2  freq: " + params[1]);
                        float freq = Float.parseFloat(params[1]);
						FmTest.getInstance(getApplicationContext(),mHandler).test( freq);
                        Log.i("yangjiajun", "cmd 2 end!");
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 3:
/* 					if(params[1].equals("2")){
						try{
							 CameraTest.getInstance(Integer.parseInt(params[1])).test(getApplicationContext(), params[2], mSurface, false);
							}catch(NumberFormatException e){
							 e.printStackTrace();
						} 
					}else{ */
						Intent camintent = new Intent(this, com.wind.factoryautotest.CameraTakePicActivity.class);
						
						camintent.putExtra("camera_id",Integer.parseInt(params[1]));
						camintent.putExtra("file_path",params[2]);
						this.startActivity(camintent); 
//					}
					
                    break;
                case 4:
					//FlashLightTest.test(params[1], params[2]);
                    /* try{
                        Intent flIntent = new Intent("com.wind.factoryautotest.action.FLASH_LIGHT_TEST");
                        flIntent.putExtra("flashlight_id", Integer.parseInt(params[1]));
                        flIntent.putExtra("flashlight_value", params[2]);
                        this.sendBroadcast(flIntent);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    } */
					try{
                        FlashLightTest.getInstance(getApplicationContext()).test(params[1],params[2]);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    try{
                        CameraTest.getInstance(0).test(getApplicationContext(),params[1], mSurface, true);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    /* try{
                        int delay_time = Integer.parseInt(params[1]);
                        new BtTest().test(getApplicationContext(), delay_time, mHandler);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    } */
/* 					Intent fm_intent = new Intent("android.intent.action.MAIN");
                    fm_intent.setClassName("com.wind.fmradio", "com.android.fmradio.FmMainActivity");
                    fm_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					fm_intent.putExtra("is_from_factoryautotest", true);
                    this.startActivityAsUser(fm_intent, android.os.UserHandle.CURRENT); */
                    break;
                case 7:
                    new GpsTest().test(getApplicationContext(), mHandler);
                    Utils.getInstance().writeResult(os);
                    /*try{
                        int delay_time = Integer.parseInt(params[1]);
                        Intent intent = new Intent(this, com.wind.factoryautotest.GpsTest.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("delay_time", delay_time);
                        //intent.putExtra("path", params[2]);
                        this.startActivityAsUser(intent, android.os.UserHandle.CURRENT);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }*/
                    break;
                case 8:
                    try{
                        int color = Integer.parseInt(params[1]);
                        int brightness = Integer.parseInt(params[2]);
                        /*if(color != 0){
                            mWakeLock.acquire();
                        }else{
                            mWakeLock.release();
                        }*/
                        LcdAndBacklightTest.test(getApplicationContext(), color, brightness);
                        /*if(isActivityOnForeground(".LcdTest")){
                            Intent i = new Intent("com.wind.factoryautotest.ACTION_LCDTEST");
                            i.putExtra("color", color);
                            i.putExtra("brightness", brightness);
                            this.sendBroadcast(i);
                        }else{
                            Intent intent = new Intent(this, com.wind.factoryautotest.LcdTest.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("color", color);
                            intent.putExtra("brightness", brightness);
                            this.startActivity(intent);
                        }*/
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 9:
                    //FmTest.getInstance().finishFM(getApplicationContext());
                    break;
                case 10:
                    try{
                        int value = Integer.parseInt(params[1]);
                        new ButtonLightTest().test(value == 1);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 11:
                    if(params.length > 1 && "auto".equals(params[1])){
                        Intent tpIntent = new Intent("com.wind.factoryautotest.action.TP_AUTO_TEST");
                        tpIntent.putExtra("is_from_factoryautotest", true);
                        tpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(tpIntent);
                    }else{
                        Intent intent = new Intent(this, com.wind.emode.TouchScreenTest.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this.startActivity(intent);
                    }
                    break;
                case 12:
                    /*this.sendBroadcast(new Intent("com.wind.factoryautotest.ACTION_TPTEST"));
                    try{
                        os.write((TouchScreenTest.mTestResult?"success":"fail").getBytes());
                    }catch(IOException e){
                        e.printStackTrace();
                    }catch(Settings.SettingNotFoundException se){
                        se.printStackTrace();
                    }*/
                    break;
                case 13:
                    try{
                        int state = Integer.parseInt(params[1]);
                        new VibratorTest().test(getApplicationContext(), state==1);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 14:
                    LightSensorTest.getInstance().test(getApplicationContext(), mHandler);
                    break;
                case 15:
                    try{
                        int delay_time = Integer.parseInt(params[1]);
                        new HeadsetTest().test(getApplicationContext(), delay_time, ""/*params[2]*/, mHandler);
                        Utils.getInstance().writeResult(os);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 16:
                    Intent inte = new Intent(this, com.wind.factoryautotest.KeyTest.class);
                    inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivityAsUser(inte, android.os.UserHandle.CURRENT);
                    break;
                case 17:
                    //this.sendBroadcast(new Intent("com.wind.factoryautotest.ACTION_KEYTEST"));
                    //Utils.getInstance().writeResult(os);
                    String retStr = "fail";					
					//changed by dongxiaotang@wind-mobi.com 20170828 start
                    /* try{
                        retStr = android.os.IFactoryAutoTest.Stub.asInterface(ServiceManager.getService("factory_auto_test")).getFailKeyValues();
                    }catch(RemoteException e){
                        e.printStackTrace();
                    } */
					retStr = new MyKeyTest(getApplicationContext()).getFailKeys();
					//changed by dongxiaotang@wind-mobi.com 20170828 end					
                    if("".equals(retStr)){
                        retStr = "success";
                    }
                    int retLen = retStr.length();
                    if(retLen > 0 && retStr.charAt(retLen - 1) == ','){
                        retStr = retStr.substring(0, retLen - 1);
                    }
                    android.util.Log.i("yangjiajun", "retStr: " + retStr);
                    try{
                        os.write(retStr.getBytes());
                    }catch(Exception ioe){
                        ioe.printStackTrace();
                    }
                    break;
                case 18:
                    // try{
                        int ct = Integer.parseInt(params[1]);
                        //int status = Integer.parseInt(params[2]);
                        new LEDTest().test(params[1]);
                        //android.os.IFactoryAutoTest.Stub.asInterface(ServiceManager.getService("factory_auto_test")).testLed(ct, status);
                    // }catch(NumberFormatException e){
                        // e.printStackTrace();
                    // }catch(RemoteException re){
                        // re.printStackTrace();
                    // }
                    break;
                case 19:
                    try{
                        int delay_time = Integer.parseInt(params[1]);
                        Intent intt = new Intent(this, com.wind.factoryautotest.OTGTest2.class);
                        intt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intt.putExtra("delay_time", delay_time);
                        this.startActivity(intt);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    //new OTGTest().test(getApplicationContext(), os/*params[1]*/);
                    break;
                case 20:
                    this.sendBroadcast(new Intent("com.wind.factoryautotest.action.SENSOR_CALI"));
                    break;
                case 21:
                    try{
                        int value = Integer.parseInt(params[1]);
                        new BreathLightTest().test(value == 1);
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 22:
                    try{
                        os.write((Settings.System.getInt(getContentResolver(), params[1])==1?"success":"fail").getBytes());
                    }catch(IOException e){
                        e.printStackTrace();
                    }catch(Settings.SettingNotFoundException se){
                        se.printStackTrace();
                    }
                    break;
                case 23:
                    ((PowerManager)this.getSystemService(Context.POWER_SERVICE)).reboot("factoryautotest");
                    break;
                case 24:
                    try{
                        int flag_idx = Integer.parseInt(params[1]);
                        //os.write((PartitionUtils.writeFile(flag_idx, params[2])?"success":"fail").getBytes());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 25:
                    if(params.length > 1){
                        try{
                            SensorTest.PROXIMITY_FAST = Integer.parseInt(params[1]);
                            if(params.length > 2){
                                BackgroundTest.IS_TEST_HEADSET = "1".equals(params[2]);
                            }
                        }catch(NumberFormatException e){
                            e.printStackTrace();
                        }
                    }
                    new BackgroundTest().test(getApplicationContext(), mHandler);
                    break;
                case 26:
                    //String sn = android.os.SystemProperties.get("gsm.serial", "");
                    int len = 12;//get from config.xml
					byte[] buffer = PartitionUtils.readWholeProinfo();
					StringBuilder sb = new StringBuilder();
					String sn;
					sn = new String(buffer, 0, 20).trim();
					
                    if(sn.length() >= len){
                        sn = sn.substring(0, len);
                    }else{
                        sn = "unknown";
                    }
                    try{
                        os.write(sn.getBytes());
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    break;
                case 27:
				    try{
                        LightAndProximitySensorTest.getInstance().test(getApplicationContext(), mHandler, Integer.parseInt(params[1]));
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 28:
                    try{
                        ProximitySensorTest.getInstance().test(getApplicationContext(), mHandler, Integer.parseInt(params[1]));
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }
                    break;
                case 29:
                    Intent fpIntent = new Intent("com.wind.factoryautotest.action.FINGER_TEST");
                    fpIntent.putExtra("is_from_factoryautotest", true);
                    fpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(fpIntent);
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), "emode_pac_flag_finger", 2);
                    break;
                case 30:
                    try{
                        os.write((android.provider.Settings.System.getInt(getApplicationContext().getContentResolver(), params[1], 0)==1?"success":"fail").getBytes());
                    }catch(Exception ie){
                        ie.printStackTrace();
                    }
                    break;
                case 31:
                    try{
                        android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), params[1], Integer.parseInt(params[2]));
                        try{
                            os.write("success".getBytes());
                        }catch(Exception ie){
                            ie.printStackTrace();
                        }
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                        try{
                            os.write("fail".getBytes());
                        }catch(Exception oe){
                            oe.printStackTrace();
                        }
                    }
                    break;
                case 32:
                    setPacMMIResult(os);
                    break;
                case 33:
                    IODeviceTest.getInstance().test(params, os);
                    break;
                case 34:
				    int len1 = 12;//get from config.xml
					byte[] buffer1 = PartitionUtils.readWholeProinfo();
					String msn;
					msn = new String(buffer1, 0, 20).trim();
                    if(msn.length() >= len1){
                        msn = msn.substring(0, len1);
                    }else{
                        msn = "unknown";
                    }
					
                    String[] items = getRemoteStringArray("emode_pac_flag_keys");
                    ContentResolver cr = getApplicationContext().getContentResolver();
                    int ilen = items.length;
                    StringBuffer content = new StringBuffer();
                    
/* 					String msn = android.os.SystemProperties.get("gsm.serial", "");
                    if(msn.indexOf(' ') >= 0){
                        msn = msn.substring(0, msn.indexOf(' '));
                    }else{
                        msn = "unknown";
                    } */
					
                    String version = android.os.SystemProperties.get("ro.build.version.incremental", "unknown");
                    content.append("sn:" + msn + ",version:" + version + ",");
                    for(int i = 0; i < ilen; i ++){
                        content.append(items[i].substring(15) + ":" + android.provider.Settings.System.getInt(cr, items[i], 0) + ",");
                    }
                    try{
                        os.write(content.substring(0, content.length()-1).getBytes());
                    }catch(Exception ie){
                        ie.printStackTrace();
                    }
                    break;
                case 35:
                    SensorTest.getInstance().test();
                    break;
				case 36:
					Intent psIntent = new Intent("com.wind.factoryautotest.action.PRESSURE_SENSOR_TEST");
					psIntent.putExtra("TestType", "FactoryAutoTest");
					startActivity(psIntent);
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mRunning = false;
        //stopForeground(true);
        mWakeLock.release();
        //Settings.System.putInt(getContentResolver(), "navigation_invisible", 0);
        /*try{
            IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE)).setWindImmersive(false);
        }catch(RemoteException e){
            e.printStackTrace();
        }*/
        mKeyguardLock.reenableKeyguard();
        new Thread(){
        	  public void run(){
                try {
                    new Socket("localhost", 8086);//
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        super.onDestroy();
    }

}
