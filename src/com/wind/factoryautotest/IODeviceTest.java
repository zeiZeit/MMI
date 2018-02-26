package com.wind.factoryautotest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

public class IODeviceTest {

    private static IODeviceTest mObject = null;
    private boolean mIsRecording = false;
    private boolean mIsPlaying = false;
    private MediaRecorder recorder = null;
    private MediaPlayer mMediaPlayer = null;

    public static IODeviceTest getInstance(){
        if(mObject == null){
            mObject = new IODeviceTest();
        }
        return mObject;
    }

    private int getStreamType(String type){
        //Log.i("yangjiajun", "play type is " + type);
        if("speaker".equals(type)){
            return AudioManager.STREAM_SYSTEM;
        }else if("earphone".equals(type)){
            return AudioManager.STREAM_MUSIC;
        }else if("receiver".equals(type)){
            return AudioManager.STREAM_VOICE_CALL;
        }else{
            return -1;
        }
    }

    private void startRecord(String path){
        new File(path).delete();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(path);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mIsRecording = true;
        Log.i("yangjiajun", "Start record ...");
    }

    private void startPlay(int streamType, String path){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(streamType);
        mMediaPlayer.setScreenOnWhilePlaying(true);
        try{
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        }catch(IOException e){
             e.printStackTrace();
        }
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.start();
        mIsPlaying = true;
        Log.i("yangjiajun", "Start play ...");
    }

    public void test(String[] params, OutputStream os){
        boolean isError = false;
        if(params.length == 4){
            if("record".equals(params[1])){
                if("start".equals(params[2])){
                    if(!mIsRecording){
                        startRecord(params[3]);
                    }else{
                        Log.i("yangjiajun", "Current was recording!");
                    }
                }else if("stop".equals(params[2])){
                    if(mIsRecording){
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        mIsRecording = false;
                        Log.i("yangjiajun", "Stop record end!");
                    }else{
                        Log.i("yangjiajun", "Current not recording!");
                    }
                }else{
                    Log.i("yangjiajun", "action name is error, must start or stop!");
                    isError = true;
                }
            }else{
                int streamType = getStreamType(params[1]);
                if(streamType >= 0){
                    if("start".equals(params[2])){
                        if(!mIsPlaying){
                            startPlay(streamType, params[3]);
                        }else{
                            Log.i("yangjiajun", "Current was playing!");
                        }
                    }else if("stop".equals(params[2])){
                        if(mIsPlaying){
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                            mIsPlaying = false;
                            Log.i("yangjiajun", "Stop play end!");
                        }else{
                            Log.i("yangjiajun", "Current not playing!");
                        }
                    }else{
                        Log.i("yangjiajun", "action name is error, must start or stop!");
                        isError = true;
                    }
                }else{
                    Log.i("yangjiajun", "output device name is error!");
                    isError = true;
                }
            }
        }else{
            Log.i("yangjiajun", "params length is error!");
            isError = true;
        }
        if(isError){
            try {
                os.write("fail".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                os.write("success".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
