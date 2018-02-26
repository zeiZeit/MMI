package com.wind.factoryautotest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class Utils {

    private String result = "";
    private static Utils mUtils = null;
    public static native boolean checkCompass(int x, int y, int z, int[] array);
    public static native boolean checkGsensor(float x, float y, float z, float[] array);
    public static native boolean checkGyro(float x, float y, float z, float[] array);

    static{
        System.loadLibrary("fat_tools");
    }

    public Utils(){
    }

    public static Utils getInstance(){
        if(mUtils == null){
            mUtils = new Utils();
        }
        return mUtils;
    }

    public synchronized void setResult(String ret){
        result = ret;
        this.notify();
    }

    public synchronized void writeResult(OutputStream os){
        try{
            this.wait();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        try{
            os.write(result.getBytes());
            result = "";
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void appendResult(String path, String value) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(path, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(os != null){
            try {
                os.write(value.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(String path, String value) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(os != null){
            try {
                os.write(value.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeResultFile(String path, String value) {
        /*File file = new File(path);
        if(!file.exists()){
            File dir = new File(path.substring(0, path.lastIndexOf('/')));
            if(!dir.exists()){
                dir.mkdir();
            }
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(os != null){
            try {
                os.write(value.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        getInstance().setResult(value);
    }

}
