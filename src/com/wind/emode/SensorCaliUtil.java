package com.wind.emode;

import java.io.IOException;
import java.io.FileInputStream;
import android.util.Log;
import android.content.Context;
import android.os.SystemProperties;
import com.wind.factoryautotest.PartitionUtils;

public class SensorCaliUtil {

    public static final String FILE_CALI_PROXI = "/sys/bus/platform/drivers/als_ps/ps_enable";
    public static final String FILE_CALI_GSENSOR = "/sys/class/meizu/acc/acc_calibration";
    public static final String FILE_CALI_GSENSOR_FACTORY = "/sys/class/meizu/acc/acc_calibration_factory";

    static{
		    System.loadLibrary("emode_tools");
	  }

    private static String getPsValue(){
        FileInputStream is = null;
        try {
            is = new FileInputStream(FILE_CALI_PROXI);
            byte[] buff = new byte[8];
            int count = is.read(buff);
            is.close();
            return new String(buff, 0, count);
        } catch (Exception e) {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    private static boolean gsCali(){
        FileInputStream is = null;
        String cfile = null;
        if(SystemProperties.get("ro.build.product").equals("Y15")){
				    cfile = FILE_CALI_GSENSOR_FACTORY;
				}else{
				    cfile = FILE_CALI_GSENSOR;
				}
        try {
            is = new FileInputStream(cfile);
            byte[] buff = new byte[17];
            int count = is.read(buff);
            is.close();
            return true;
        } catch (Exception e) {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return false;
    }

    public static int byteArrayToInt(byte[] bytes, int index){
        int value = 0;
        int shift;
        for(int i = index; i < index + 4; i++){
            shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }

    private static boolean writeCaliValue(int value){
	   /*   byte[] buff = NvRAMAgentUtils.readFile();
        if(buff != null){
            WriteCaliValue(value, buff);
            int result = NvRAMAgentUtils.writeFile(buff);
            if(result != 1024){
                Log.e("Emode_Tools", "Emode- SensorCaliUtil - writeFile fail!");
                return false;
            }else{
                Log.e("Emode_Tools", "Emode- SensorCaliUtil - writeFile success!");
                return true;
            }
        }else{
            Log.e("Emode_Tools", "Emode- SensorCaliUtil - read nv fail!");
            return false;
        }*/
              return false;
	  }
    public static native int WriteCaliValue(int cvalue, byte[] out_buf);

    public static String test(Context context){
        boolean bResult = false;
        String fl = getPsValue();
        if(fl != null && fl.length() > 1){
            int value = -1;
            if(fl != null && !"".equals(fl.trim())){
                value = Integer.parseInt(fl.substring(0, fl.length() - 1));
            }
            if(value != -1){
                if(writeCaliValue(value)){
                    bResult = true;
                }
            }
        }
        if(bResult){
            bResult = gsCali();
        }
        android.provider.Settings.System.putInt(context.getContentResolver(), "emode_pac_flag_cali", bResult?1:2);
        return bResult?"success":"fail";
    }
}
