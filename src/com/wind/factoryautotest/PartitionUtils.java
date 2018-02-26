package com.wind.factoryautotest;

import android.util.Log;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.SystemProperties;
import android.preference.PreferenceManager;


public class PartitionUtils {
	final static String TAG = "MMI_PartitionUtils";
    final static String FILE_PATH="/factory/proinfo";//"/dev/block/platform/soc/7824900.sdhci/by-name/proinfo";
    private static final int PAC_MMI_FLAG_INDEX = 77;
	
    public static boolean writeFile(boolean value) {

		return setAutoMMIResult( value );

    }
	
	private static boolean setAutoMMIResult(boolean val) {
        byte[] buff = readWholeProinfo();
        if (buff != null) {
            buff[PAC_MMI_FLAG_INDEX] = (val ? "P" : "F").getBytes()[0];
        }
        
		return writeWholeProinfo(buff);   
    }
	
	public static byte[] readWholeProinfo() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];
        int count;
        try {
            fis = new FileInputStream(FILE_PATH);
            count = fis.read(buffer);
            Log.i(TAG, "readWholeProinfo, read count=" + count);
            if (count != -1) {
                Log.i(TAG, "readWholeProinfo, info:" + new String(buffer, 0, count));
            }
        } catch (IOException e) {
            Log.e(TAG, "readWholeProinfo", e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "readWholeProinfo", e);
            }
        }
        return buffer;
    }
	
	public static boolean writeWholeProinfo(byte[] buf) {
        FileOutputStream fos = null;
		boolean ret =false;
        try {
            fos = new FileOutputStream(FILE_PATH);
            Log.i(TAG, "writeWholeProinfo, write: " + new String(buf));
            fos.write(buf);
			ret = true;
        } catch (IOException e) {
            Log.e(TAG, "writeWholeProinfo", e);
			ret = true;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "writeWholeProinfo", e);
            }
        }
		
		return ret;
    }
	
	
}
