package com.android.bsb.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.android.bsb.util.AppLogger;

import java.util.List;


public class LocationService extends Service {

    private String TAG = getClass().getSimpleName();

    private LocationManager localtionManager;

    private Context mContext;

    private boolean hasProvider;

    private String provider;

    private double[] locations = new double[2];


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        init();
    }


    private void init() {
        localtionManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = localtionManager.getProviders(true);
        hasProvider = providers != null && providers.size() > 0;
        if (!hasProvider) {
            AppLogger.LOGD(TAG, "the device no location provider return !");
            return;
        }


        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = localtionManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            provider = LocationManager.PASSIVE_PROVIDER;
        }

        AppLogger.LOGD(TAG, "location provider is " + provider);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (provider == null) {
            AppLogger.LOGD(TAG, "the device no location provider return !");
            return;
        }
        Location location = localtionManager.getLastKnownLocation(provider);

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            locations[0] = latitude;
            locations[1] = longitude;
        }


    }


    public void updateLocation() {
        if (localtionManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = localtionManager.getLastKnownLocation(provider);
            if(location != null){
                locations[0] = location.getLatitude();
                locations[1] = location.getLongitude();
            }

        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        localtionManager.requestLocationUpdates(provider,1000 * 60 * 5,
                5f,listener);
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(localtionManager != null){
            localtionManager.removeUpdates(listener);
        }
        return super.onUnbind(intent);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(localtionManager != null){
            localtionManager.removeUpdates(listener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }


    public boolean hasProvider(){
        return hasProvider;
    }

    public double[] getLocation(){
        return locations;
    }


    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            AppLogger.LOGD(TAG,"onLocationChanged ");
            locations[0] = location.getLatitude();
            locations[1] = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            AppLogger.LOGD(TAG,"onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            AppLogger.LOGD(TAG,"onProviderEnabled provider:"+provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            AppLogger.LOGD(TAG,"onProviderDisabled provider:"+provider);
        }
    };


    public class LocalBinder extends Binder {
        public LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }



}
