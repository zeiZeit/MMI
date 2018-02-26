package com.wind.factoryautotest;

public class BreathLightTest {

    public void test(boolean enable){
        String lightPath = "/sys/class/leds";
        Utils.writeFile(lightPath, enable ? "255":"0");
    }
}
