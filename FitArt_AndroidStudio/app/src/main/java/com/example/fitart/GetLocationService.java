package com.example.fitart;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class GetLocationService extends Service {


    // service code used from
    // https://stackoverflow.com/questions/34573109/how-to-make-an-android-app-to-always-run-in-background
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id"; // add once channel is in place

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //do location stuff here

        LocationManager locationManager;
        LocationListener locationListener;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //send location to whatever needs it, maybe a workManager?


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                // maybe add some sort of screen animation if we're feelin' spicy

            }

            @Override
            public void onProviderDisabled(String s) {
                // maybe add some sort of screen animation if we're feelin' spicy

            }
        };


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            //print permission denied IMPORTANT: the module that starts this service should make this check again
            // and if true then request permissions, this can only be done in an activity, not a service.
            //must use requestPermissions for access_fine, acess_coarse, and internet
        }

        locationManager.requestLocationUpdates("gps", 25000, 10, locationListener);


        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground(){
        Intent notificationIntent = new Intent(this, /**/MapsActivity.class/**/); //must alter if this moves to a new activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //NotificationCompat is for old versions, maybe update to Notification.builder? needs research
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,NOTIF_CHANNEL_ID)
                .setOngoing(true)
                //.setSmallIcon(R.drawable.ic_notification) //this is found in res/drawable... there is no icon so it gives an error
                .setContentTitle(getString(R.string.app_name))
                .setContentText("service running in background")
                .setContentIntent(pendingIntent)
                .build());
    }


}
