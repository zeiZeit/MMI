package com.wind.factoryautotest;

import java.io.File;
import java.io.FileOutputStream;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.wind.factoryautotest.R;

public class CameraTakePicActivity extends Activity {

	//private View butlayout;
	private SurfaceView surfaceView;
	private Camera mCamera;
	private File jpgFile;
	private Camera.Parameters mParameters;

	private static final String TAG = "MainActivity";

	private String mPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/autotest";

	private int mCameraId;

	private String mCameraFileName;

	private String BACK_FILENAME = mPath + "/backcamera.jpg";
	private String FONT_FILENAME = mPath + "/fontcamera.jpg";

	private int mDelayTime = 0;
	
	private static final int TAKE_PIC = 1;
	private static final int CLOSE_ACTIVITY = 2;

	private Thread mThread = new Thread() {

		public void run() {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;

				Log.i("bigbing", "mThread");
				//Utils.writeResultFile("","fail");
				finish();
			}
                        Utils.writeResultFile("","fail");
		}

	};
	
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TAKE_PIC:
				mCamera.takePicture(null, null, new MyPictureCallback());
				break;
				
			case CLOSE_ACTIVITY:
				if (mCamera != null) {
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
				finish();
				break;
			default:
				break;
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera_test);

		mPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/autotest";

		File file = new File(mPath);
		if (!file.exists()) {
			file.mkdir();
		}

		Intent intent = getIntent();
		if (intent != null) {
			mCameraId = intent.getIntExtra("camera_id", 0);
			
			/*if (Camera.CameraInfo.CAMERA_FACING_BACK == mCameraId) {
				mCameraFileName = BACK_FILENAME;
			} else {
				mCameraFileName = FONT_FILENAME;
			}*/
			
			mDelayTime = intent.getIntExtra("delay_time", 20);
			mCameraFileName = intent.getStringExtra("file_path");
			
		} else {
			mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		}

		surfaceView = (SurfaceView) findViewById(R.id.surface);
		surfaceView.getHolder().setSizeFromLayout();
		surfaceView.getHolder().setKeepScreenOn(true);
		surfaceView.getHolder().addCallback(new SurfaceCallback());

		//butlayout = findViewById(R.id.buttonlayout);
	}

	private class SurfaceCallback implements Callback {

		@SuppressLint("InlinedApi")
		public void surfaceCreated(SurfaceHolder holder) {

			if (mCamera == null) {
                                Log.i("zuozhuang", "camera:"+mCameraId+"  is openning...");
				mCamera = Camera.open(mCameraId);
				Log.i("zuozhuang", "camera:"+mCameraId+"  is openned");
				try {
					mCamera.setPreviewDisplay(holder);
					initCamera();
					mCamera.startPreview();
					
					mHandler.postDelayed(mThread, mDelayTime * 1000);
					mHandler.sendEmptyMessageDelayed(TAKE_PIC, 1000);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			mCamera.autoFocus(new AutoFocusCallback() {

				public void onAutoFocus(boolean success, Camera camera) {
					if (success) {
						initCamera();
						//camera.cancelAutoFocus();
					}
				}

			});
		}

		public void surfaceDestroyed(SurfaceHolder holder) {

			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		}

	}

	public void takepicture(View view) {
		switch (view.getId()) {//R.id.but_takepic
		case 10:
			mCamera.takePicture(null, null, new MyPictureCallback());
			break;

		default:
			break;
		}

	}

	private class MyPictureCallback implements PictureCallback {
		public void onPictureTaken(byte[] data, Camera camera) {
                        mHandler.removeCallbacks(mThread);
                        Utils.writeResultFile("","success");
			new SavePictureTask().execute(data);
			//camera.startPreview();
		}
	}

	class SavePictureTask extends AsyncTask<byte[], String, String> {
		protected String doInBackground(byte[]... params) {
			jpgFile = new File(mCameraFileName);
			try {
				FileOutputStream fos = new FileOutputStream(jpgFile.getPath());
				fos.write(params[0]);
				fos.close();
				//mHandler.removeCallbacks(mThread);
				mHandler.sendEmptyMessageDelayed(CLOSE_ACTIVITY, 1000);
				//Utils.writeResultFile("","success");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SuppressLint("InlinedApi")
	private void initCamera() {
		Log.i("zuozhuang", "initCamera begin");
		mParameters = mCamera.getParameters();
		mParameters.setPictureFormat(PixelFormat.JPEG);
		mParameters.setJpegThumbnailQuality(100);
		if(mCameraId == 0){ 
			mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);		
		}
		Log.i("zuozhuang", "initCamera start display");
		setDispaly(mParameters, mCamera);
		mCamera.setParameters(mParameters);
		mCamera.startPreview();
		Log.i("zuozhuang", "initCamera end");
	}

	@SuppressWarnings("deprecation")
	private void setDispaly(Camera.Parameters parameters, Camera camera) {
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			camera.setDisplayOrientation(90);
		} else {
			parameters.setRotation(90);
		}
	}
}
