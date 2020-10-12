package com.example.fitart;

import android.Manifest;
import android.app.Notification;
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

import java.util.Vector;

public class GetLocationService extends Service {

    // service code used from
    // https://stackoverflow.com/questions/34573109/how-to-make-an-android-app-to-always-run-in-background
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "GetLocationServiceChannel"; // add once channel is in place

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        LocationManager locationManager;
        LocationListener locationListener;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //broadcast location as LOCATION intent
                Intent sendLocation = new Intent();
                sendLocation.putExtra("LOCATION", location);
                sendLocation.setAction("GET_LOCATION_IN_BACKGROUND");

                //this should be updated to store data in a matrix and wait for the activity to be
                // running before broadcasting, but i'm aiming for proof of concept first

                sendBroadcast(sendLocation);

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

        locationManager.requestLocationUpdates("gps", 25000, 0, locationListener);




    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // !! location manager/listener needs to be deallocated here to avoid mem leak !!
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        // This function should be used to return location data.
        //

        startForeground();
        return START_NOT_STICKY;
    }

    private void startForeground(){
        Intent notificationIntent = new Intent(this, /**/MapsActivity.class/**/); //must alter if this moves to a new activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //NotificationCompat is for old versions, maybe update to Notification.builder? needs research
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,NOTIF_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.pototype_small_icon) //this is found in res/drawable... there is no icon so it gives an error
                .setContentTitle(getString(R.string.app_name))
                .setContentText("service running in background")
                .setContentIntent(pendingIntent)
                .build());
    }
}
