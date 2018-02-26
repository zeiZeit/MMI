package com.wind.factoryautotest;

import java.util.Iterator;
import java.util.NoSuchElementException;

import android.content.Context;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class GpsTest {

    private LocationManager locationManager;
    private Handler mHandler = null;
    private Context mContext = null;
    //private boolean mExit = false;
    private boolean mIsTesting = false;
    private static GpsTest mInstance = null;
    public static GpsTest getInstance(){
        if(mInstance == null){
            mInstance = new GpsTest();
        }
        return mInstance;
    }
    private GpsStatus.Listener mSvStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            /*if(mExit){
                return;
            }*/
            GpsStatus gs = locationManager.getGpsStatus(null);
            Iterator<GpsSatellite> satellites = gs.getSatellites().iterator();
            int n = 0;
            Log.i("yangjiajun", "onGpsStatusChanged...");
            while(satellites.hasNext()){
                satellites.next();
                n++;
                    Log.i("yangjiajun", "gps n: " + n);
                if(n >= 3){
                    //mExit = true;
                    //mHandler.removeCallbacks(mThread);
                    locationManager.removeGpsStatusListener(mSvStatusListener);
                    locationManager.removeUpdates(mLocationlistener);
                    Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER, false);
                    mIsTesting = false;
                    Log.i("yangjiajun", "success");
                    //Utils.writeResultFile(mPath, "success");
                    android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_gps", 1);
                    break;
                }
            }
        }
    };
    private LocationListener mLocationlistener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.i("yangjiajun", "l: " + location.getLatitude() + "lg: " + location.getLongitude());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };
    /*private Thread mThread = new Thread() {
        public void run() {
            locationManager.removeGpsStatusListener(mSvStatusListener);
            locationManager.removeUpdates(mLocationlistener);
            Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER, false);
            Log.i("yangjiajun", "GPSTest fail");
            mIsTesting = false;
            //Utils.writeResultFile(mPath, "\ngps: fail");
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_gps", 2);
        }
    };*/

    public void test(Context context, Handler handler){
        Log.i("yangjiajun", "gps test...mIsTesting: " + mIsTesting);
        if(!mIsTesting){
            mIsTesting = true;
            mContext = context;
            mHandler = handler;
            android.provider.Settings.System.putInt(mContext.getContentResolver(), "emode_pac_flag_gps", 2);
            handler.post(new Thread(){
                public void run(){
                    locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                    Log.i("yangjiajun", "gps test...");
                    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER, true);
                        Log.i("yangjiajun", "set gps enable!");
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationlistener);
                    locationManager.addGpsStatusListener(mSvStatusListener);
                    //mHandler.postDelayed(mThread, 150000);
                }
            });
        }
    }
}
