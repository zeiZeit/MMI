package com.wind.factoryautotest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ButtonLightTest {

    private void setButtonLightBrightness(String brightness){
        try{
            FileOutputStream os = new FileOutputStream("/sys/class/leds/button-backlight/brightness");
            os.write(brightness.getBytes());
            os.close();
        }catch(FileNotFoundException fe){
            fe.printStackTrace();
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }

    public void test(boolean enable){
        setButtonLightBrightness(enable?"255":"0");
    }
}
