package com.wind.factoryautotest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.SystemProperties;

import android.content.Context;

import android.util.Log;

public class LEDTest {

    private void writeColor(String path, String brightness){
        try {
		Log.i("weihongxi","path:"+path+",brightness:"+brightness);
            OutputStream os = new FileOutputStream(path);
            os.write(brightness.getBytes());
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void test(String ct){
/*         writeColor("/sys/class/leds/red/brightness", "0");
        writeColor("/sys/class/leds/green/brightness", "0");
        writeColor("/sys/class/leds/blue/brightness", "0");
        String path = "/sys/class/leds/red/brightness";
        if(ct == 2){
            path = "/sys/class/leds/green/brightness";
        }else if(ct == 3){
            path = "/sys/class/leds/blue/brightness";
        }
        if(status == 1){
            writeColor(path, (status == 1 ? "255":"0"));
        } */
		
		
		SystemProperties.set("sys.wind.ledtest", ct);
		
		
    }
}

