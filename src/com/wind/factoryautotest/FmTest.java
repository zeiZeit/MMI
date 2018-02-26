package com.wind.factoryautotest;
//add by zuozhuang begin 
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.RemoteException;
import android.os.IBinder;
import java.io.IOException;
import java.io.OutputStream;
import android.util.Log;

import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.Handler;

import qcom.fmradio.FmConfig;
import qcom.fmradio.FmReceiver;
import qcom.fmradio.FmRxEvCallbacksAdaptor;

public class FmTest {
	

    public static final int REGIONAL_BAND_CHINA = 8;
    
    private static final String FMRADIO_DEVICE_FD_STRING = "/dev/radio0";

    private FmReceiver mReceiver;
    private FmConfig mFMConfiguration;
	
    private final int FM_STATE = 0;
    private final String LOGTAG = "MMI_FmTest";
    private int mTunedFrequency = 98100;
    private int mBandMinFreq = 76000;
    private int mBandMaxFreq = 108000;
    private int mChanSpacing = 0;
    private int mFrequencyBand_Stepsize = 200;
    private static final boolean IS_AIDL = false;
	protected AudioManager mAudioManager;
	
    private static FmTest mInstance = null;
	Context mContext = null;
	Handler mHandler = null;
	
	static {
        Log.i("MMI_FM", "Loading FM-JNI Library");
        System.loadLibrary("qcomfm_jni");

    }
	
	private FmTest(Context c,Handler h){
		mContext = c;	
		mHandler = h;
	}
	
    public static FmTest getInstance(Context c,Handler h){
        if(mInstance == null){
            mInstance = new FmTest(c,h);
        }
        return mInstance;
    }

	private boolean isFmOn;
    private boolean mIsSSRInProgress;
    private boolean mIsFMDeviceLoopbackActive;
    private int mInitializeStation = 100100;
	
    public void test(float freq){

		if(freq == 0){
			disableReceiver();
		}else{
			mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		    // Register receiver monitor headSet status
			IntentFilter filterHeadset = new IntentFilter();
			filterHeadset.addAction(Intent.ACTION_HEADSET_PLUG);
			mContext.registerReceiver(mHeadsetReceiver, filterHeadset);
			Log.v(LOGTAG, "Register mHeadsetReceiver");
			tuneToStation((int)freq*1000);
		}

    }

    public void finishFM(Context context){
		
		
    }
	
	private boolean tuneToStation(int frequency) {
        if (mReceiver == null) {
            return false;
        }

        if (!isValidStation(frequency)) {
            Log.d(LOGTAG, "invalid frequency");
            return false;
        }

        return mReceiver != null && mReceiver.setStation(frequency);
    }
	
    private boolean isValidStation(int frequency) {
        return frequency >= mFMConfiguration.getLowerLimit()
                && frequency <= mFMConfiguration.getUpperLimit();
    }
	
	private Runnable initReceiverRunnable = new Runnable() {
        @Override
        public void run() {
            initReceiver();
        }
    };
	
    private BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            {
                if (!mAudioManager.isWiredHeadsetOn()) { // Un plug
                    Log.w(LOGTAG, "HeadsetConnectionReceiver:false");
                    if (isFmOn) {
                        disableReceiver();
                    }
                } else { // plug In
                    Log.w(LOGTAG, "HeadsetConnectionReceiver:true");
                    mHandler.post(initReceiverRunnable);
                }
            }
        }
    };
	
	private FmRxEvCallbacksAdaptor mCallback = new FmRxEvCallbacksAdaptor() {
        @Override
        public void FmRxEvEnableReceiver() {

            Log.d(LOGTAG, "onEvEnabled receiver ... ");
        }

        @Override
        public void FmRxEvDisableReceiver() {
            Log.d(LOGTAG, "FmRxEvDisableReceiver  ");
            isFmOn = false;
        }

        @Override
        public void FmRxEvRadioReset() {
            configureFMDeviceLoopback(false);
            isFmOn = false;
            if (mAudioManager.isWiredHeadsetOn()) {
                mIsSSRInProgress = true;
                try {
                    Thread.sleep(2000);
                } catch (Exception ex) {
                     Log.e(LOGTAG, "Turning on after SSR from service failed00000");
                }
                boolean bStatus = fmOn();
                if (bStatus) {
                    bStatus = tuneToStation(mInitializeStation);
                    if (!bStatus) Log.e(LOGTAG, "Tuning after SSR from service failed");
                } else {
                    Log.e(LOGTAG, "Turning on after SSR from service failed");
                }
                mIsSSRInProgress = false;
            }
        }

        @Override
        public void FmRxEvRadioTuneStatus(int frequency) {
            Log.d(LOGTAG, "FmRxEvRadioTuneStatus: " + frequency);
        }

        @Override
        public void FmRxEvSearchComplete(int freq) {
        }
    };
	

    private void disableReceiver() {
        if (mReceiver != null) {
            boolean status = mReceiver.disable(mContext);

            mReceiver = null;
            isFmOn = false;
            configureFMDeviceLoopback(false);
        }
    }

	private void configureFMDeviceLoopback(boolean enable) {
        int status;

        if (enable && !mIsFMDeviceLoopbackActive) {
            status = AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_FM,
                    AudioSystem.DEVICE_STATE_AVAILABLE, "", "");
            if (status != AudioSystem.SUCCESS) {

                AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_FM,
                        AudioSystem.DEVICE_STATE_UNAVAILABLE, "", "");
            } else {
                mIsFMDeviceLoopbackActive = true;
            }
        } else if (!enable && mIsFMDeviceLoopbackActive) {
            AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_FM,
                    AudioSystem.DEVICE_STATE_UNAVAILABLE, "", "");

            mIsFMDeviceLoopbackActive = false;
        }
    }
	
	
	private void initReceiver() {
		Log.d(LOGTAG,"initReceiver() start " );
        if (!mAudioManager.isWiredHeadsetOn()) {
            return;
        }
        if (isFmOn) {
            return;
        }
        loadConfig();
        Log.d(LOGTAG,"initReceiver() loadConfig ok " );
        if (fmOn()) {
            boolean status = tuneToStation(mInitializeStation);
            Log.d(LOGTAG,"initialize setStation for: " + mInitializeStation + "   status:" + status);
        }
    }
	
	private boolean setLowPowerMode(boolean bLowPower) {
        boolean bCommandSent = false;
        if (mReceiver != null) {
            
            if (bLowPower) {
                bCommandSent = mReceiver.setPowerMode(FmReceiver.FM_RX_LOW_POWER_MODE);
            } else {
                bCommandSent = mReceiver.setPowerMode(FmReceiver.FM_RX_NORMAL_POWER_MODE);
            }
        }
        return bCommandSent;
    }
	
	private boolean setAudioPath(boolean mode) {
        return mReceiver.setAnalogMode(mode);
    }
	
    public boolean enableAutoAF(boolean bEnable) {
        boolean bCommandSent = false;
        if (mReceiver != null) {
            
            // Make true this be called after enable true.
            bCommandSent = mReceiver.enableAFjump(bEnable);
        }
        return bCommandSent;
    }
	
    private boolean fmOn() {
        boolean status;
        if (mReceiver == null) {
            try {
                mReceiver = new FmReceiver(FMRADIO_DEVICE_FD_STRING, mCallback);
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        status = mReceiver.enable(mFMConfiguration, mContext);

        Log.e(LOGTAG, "fmOn enable receiver status:" + status);
        setAudioPath(false);
        if (status) {
            status = setLowPowerMode(false);

            // config audio device
            configureFMDeviceLoopback(true);

            status = mReceiver.registerRdsGroupProcessing(
                    FmReceiver.FM_RX_RDS_GRP_RT_EBL | FmReceiver.FM_RX_RDS_GRP_PS_EBL
                            | FmReceiver.FM_RX_RDS_GRP_AF_EBL
                            | FmReceiver.FM_RX_RDS_GRP_PS_SIMPLE_EBL
                            | FmReceiver.FM_RX_RDS_GRP_ECC_EBL | FmReceiver.FM_RX_RDS_GRP_PTYN_EBL
                            | FmReceiver.FM_RX_RDS_GRP_RT_PLUS_EBL);
            status = enableAutoAF(true);
            /* There is no internal Antenna*/
            status = mReceiver.setInternalAntenna(false);

            isFmOn = true;
        } else {
            // If enable failed, reset fm device and restart it in 2s.
            mReceiver.reset();
            mReceiver = null; // as enable failed no need to disable
            // failure of enable can be because handle
            // already open which gets effected if
            // we disable
            Log.e(LOGTAG, "enable Fm device failed. and reset , re enable again 2s");

            configureFMDeviceLoopback(false);
            mHandler.postDelayed(initReceiverRunnable, 2000);
        }
        return status;
    }
	
	
	private void loadConfig() {
        if (mFMConfiguration == null) {
            mFMConfiguration = new FmConfig();
        }
        // initialize FMconfig
        setCountry(REGIONAL_BAND_CHINA);
    }
	
	public void setCountry(int nCountryCode) {

        // Default: 87500  TO 10800 IN 100 KHZ STEPS
        mFMConfiguration.setRadioBand(FmReceiver.FM_USER_DEFINED_BAND);
        mFMConfiguration.setChSpacing(FmReceiver.FM_CHSPACE_100_KHZ);
        mFMConfiguration.setEmphasis(FmReceiver.FM_DE_EMP50);
        mFMConfiguration.setRdsStd(FmReceiver.FM_RDS_STD_RDS);

        mFMConfiguration.setUpperLimit(108000);
		mFMConfiguration.setLowerLimit(87000);
        mFrequencyBand_Stepsize = 100;
		
	}
}
//add by zuozhuang end