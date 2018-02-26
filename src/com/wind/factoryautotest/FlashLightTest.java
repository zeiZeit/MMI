package com.wind.factoryautotest;

import android.hardware.Camera;
import android.content.Context;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.hardware.Camera.CameraInfo;
import android.os.SystemProperties;

public class FlashLightTest {

	private static FlashLightTest mInstance = null;
	private Context mContext = null;
	private CameraManager manager;
	private FlashLightTest(Context c) {
		mContext = c;
		manager = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);		
	}

	public static FlashLightTest getInstance(Context c) {
		if(mInstance == null) {
			mInstance = new FlashLightTest(c);
		}
		return mInstance;
	}

	public void test(String cameraId,String value) { //value:"1" open  "0" close
		if("1".equals(cameraId)||"0".equals(cameraId)){
			if("1".equals(value)) {
				openFlash(cameraId);
			} else {
				closeFlash(cameraId);
			}
		}

	}
	
	
    private void openFlash(String cameraId) {
        try{
            manager.setTorchMode(cameraId, true);
        }catch (CameraAccessException e){
            
        }
    }
	
    private void closeFlash(String cameraId) {
        try{
            manager.setTorchMode(cameraId, false);
        }catch (CameraAccessException e){
            
        }
    }
}
