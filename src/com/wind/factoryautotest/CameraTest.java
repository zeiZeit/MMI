package com.wind.factoryautotest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceView;

public class CameraTest {

    private static CameraTest[] mInstance = {null, null, null};
    private boolean isRunning = false;
    private int mId = 0;
    private String mPath = null;

    private CameraTest(int id){
        mId = id;
    }

    public static CameraTest getInstance(int id){
        if(mInstance[id] == null){
            mInstance[id] = new CameraTest(id);
        }
        return mInstance[id];
    }

    private void takePic(Camera cam){
        cam.takePicture(null, null, new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera cat) {
                cat.stopPreview();
                cat.release();
                isRunning = false;
                try {
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    android.util.Log.i("yangjiajun", "camera capture path: " + mPath);
                    File file = new File(mPath);
                    if(!file.exists()){
                        File dir = new File(mPath.substring(0, mPath.lastIndexOf('/')));
                        if(!dir.exists()){
                            dir.mkdir();
                        }
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(file));
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.write(data);
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void test(Context context, String path, SurfaceView surface, boolean isTestWhiteCard) {
        mPath = path;
        Camera cam = null;
        if(!isRunning){
            cam = Camera.open(mId);
            if(cam != null){
                isRunning = true;
            }else{
                android.util.Log.i("yangjiajun", "can not open camera!");
                return;
            }
        }else{
            android.util.Log.i("yangjiajun", "isRunning ...");
            return;
        }
        new File(path).delete();
        try {
            cam.setPreviewDisplay(surface.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = cam.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setJpegQuality(100);
        List<Size> list_size = parameters.getSupportedPictureSizes();
        Size size = list_size.get(list_size.size() - 1);
        parameters.setPictureSize(size.width, size.height);
        if(mId == Camera.CameraInfo.CAMERA_FACING_BACK){
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        //parameters.setZSDMode("off");
        cam.setParameters(parameters);
        cam.startPreview();
        if(mId == Camera.CameraInfo.CAMERA_FACING_BACK){
            if(isTestWhiteCard){
                takePic(cam);
            }else{
            cam.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean af, Camera caa) {
                    Log.i("yangjiajun", "focus ret: " + af);
                    if(af){
                        takePic(caa);
                    }else{
                        caa.stopPreview();
                        caa.release();
                        isRunning = false;
                    }                          
                }	
            });
            }	 
			
        }else{
            takePic(cam);
        }
    }

}
