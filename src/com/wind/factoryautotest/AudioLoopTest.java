package com.wind.factoryautotest;
import android.media.AudioSystem;


import android.util.Log;
import java.io.IOException;
import java.lang.Exception;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioSystem;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.os.Handler;

public class AudioLoopTest {
	protected static final String LOG_TAG = "MMI_audiolooptest";
    private static final int SHOW_TEST_STATUS_DIALOG = 0x6688;

    //IWindowManager wm;
    private AudioManager mAudioManager;
	private AudioManager audioManager;
    private final int TYPE_HEADMIC_TO_SPEAKER = 0;
    private final int TYPE_MIC_TO_HEADSET_EARPIECE = 1;
    private final int TYPE_CAMERAMIC_TO_HEADSET_EARPIECE = 2;

    /**
     * Index match with type.
     */
    private boolean[] mPassTypes = {false, false, false};

    private static final int ACOUSTIC_LOOPBACK_TYPE = 0;
    private static final int AFE_LOOPBACK_TYPE = 1;
    private int mLoopbackType = ACOUSTIC_LOOPBACK_TYPE;

    private boolean mIsDoubleMic;
    private BroadcastReceiver mReceiver;

    private boolean isTesting;

    // Audio record and track parameters.
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
	
	private static AudioLoopTest mInstance = null;
	Context mContext = null;
	Handler mHandler = null;
	
	private AudioLoopTest(Context c,Handler h){
		mContext = c;	
		mHandler = h;
	}
	
    public static AudioLoopTest getInstance(Context c,Handler h){
        if(mInstance == null){
            mInstance = new AudioLoopTest(c,h);
        }
        return mInstance;
    }


    private void init() {
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		audioManager = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE));
		mAudioManager.requestAudioFocus(null, AudioManager.STREAM_RING,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		if(mAudioManager==null) Log.d("zuozhuang", "mAudioManager is null");
		if(audioManager==null) Log.d("zuozhuang", "audioManager is null");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_HEADSET_PLUG)) {

                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mContext.registerReceiver(mReceiver, intentFilter);

    }


    public void test(String v) {
		
		init();
		
        switch (v) {
            case "HEADMIC_TO_SPEAKER":
                play(TYPE_HEADMIC_TO_SPEAKER);
                break;
            case "MIC_TO_HEADSET":
                play(TYPE_MIC_TO_HEADSET_EARPIECE);
                break;
            case "CAMERAMIC_TO_HEADSET_EARPIECE":
                play(TYPE_CAMERAMIC_TO_HEADSET_EARPIECE);
                break;
			case "0" :
				pauseTest();
            default:
				break;
        }
		
		
    }




    
    protected void pauseTest() {
        if (isTesting) {
            isTesting = false;
        }
        if (mReceiver != null) {
			mAudioManager.abandonAudioFocus(null);
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }


    private void restore() {
        // close speaker
        AudioSystem.setForceUse(AudioSystem.FOR_COMMUNICATION, AudioSystem.FORCE_NONE);
        int ret = AudioSystem.setDeviceConnectionState(AudioManager.DEVICE_OUT_WIRED_HEADSET,
                AudioSystem.DEVICE_STATE_AVAILABLE, "", "");
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    private void play(int type) {
        isTesting = true;
        int source = MediaRecorder.AudioSource.MIC;
        float ratio = 0.9F;
        switch (type) {
            case TYPE_HEADMIC_TO_SPEAKER:
                source = MediaRecorder.AudioSource.MIC;
                // set speaker as outdevice.
				if(mAudioManager==null) Log.d("zuozhuang", "mAudioManager is null in play()");
                mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                int ret = AudioSystem.setDeviceConnectionState(AudioManager.DEVICE_OUT_WIRED_HEADSET,
                        AudioSystem.DEVICE_STATE_UNAVAILABLE, "", "");

                break;
            case TYPE_MIC_TO_HEADSET_EARPIECE:

                source = MediaRecorder.AudioSource.MIC;
                AudioSystem.setForceUse(AudioSystem.FOR_COMMUNICATION,
                        AudioSystem.FORCE_WIRED_ACCESSORY);
                break;
            case TYPE_CAMERAMIC_TO_HEADSET_EARPIECE:

                // Ref mic to head_ear
                source = MediaRecorder.AudioSource.CAMCORDER;
                break;

        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (ratio * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                (int) (ratio * audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)),
                0);
        audioManager.setStreamVolume(AudioManager.STREAM_RING,
                (int) (ratio * audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                (int) (ratio * audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);

        new LoopTestThread(source).start();

    }

    private void stop() {
        isTesting = false;
    }

    private class LoopTestThread extends Thread {
        private final int SAMPLE_RATE = 16000;
        private int source;

        public LoopTestThread(int source) {
            this.source = source;
        }

        @Override
        public void run() {
            int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            bufferSize = Math.max(bufferSize, android.media.AudioTrack.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));

            if (audioRecord == null) {
                audioRecord = new AudioRecord(source, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            }
            if (audioTrack == null) {
                audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                        android.media.AudioTrack.MODE_STREAM);
            }

            audioTrack.setPlaybackRate(SAMPLE_RATE);

            audioRecord.startRecording();
            audioTrack.play();

            byte[] buffer = new byte[bufferSize];
            while (isTesting) {
                int readSize = audioRecord.read(buffer, 0, bufferSize);
                if (readSize > 0) audioTrack.write(buffer, 0, readSize);
            }

            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;

            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;

            //
            restore();
        }
	}
}
