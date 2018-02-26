package com.wind.factoryautotest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;

public class FingerTestActivity extends Activity{

	private int mTestResult = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);       
		Log.i("weihongxi","FingerTestActivity.onCreate");
		startActivity();
		
    }
		
	private void startActivity(){
		Intent  intent = new Intent();
        intent.putExtra("config_autoexit", true); //控制是否测试完成后自动退出activity
        intent.putExtra("config_autotest", true);//控制是否启动activity自动执行测试
        intent.putExtra("config_showcapturedImg", true);//控制是否显示采集的图片
		intent.setClassName("com.fpsensor.fpSensorExtension", "com.fpsensor.sensortesttool.sensorTestActivity");
        startActivityForResult(intent,1);
		Log.i("weihongxi","FingerTestActivity.startActivity.end");
		
	}

	/**
     * 接收测试返回结果进行相应处理
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("weihongxi","FingerTestActivity.onActivityResult.resultCode = "+resultCode);
        switch(resultCode){
		    case RESULT_OK:
                mTestResult = 1;
                break;				
		    default:
                mTestResult = 0;		
                break;
		}
		
		Intent myBroadcast = new Intent();
		myBroadcast.setAction("com.wind.fpsensor.RESULT");
		myBroadcast.putExtra("result",mTestResult);
		sendBroadcast(myBroadcast);
		Log.i("weihongxi","FingerTestActivity.onActivityResult.sendBroadcast.end");
		
		this.finish();
    }
	
	@Override
    public void onDestroy() {
		Log.i("weihongxi","FingerTestActivity.onDestroy.sendBroadcast.end");
    	super.onDestroy();
    }	
	
}
