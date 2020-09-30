package com.example.fitart;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /*service code, this can be moved later, I am just assuming this will be the main activity
    * */

    // service code used from
    // https://stackoverflow.com/questions/34573109/how-to-make-an-android-app-to-always-run-in-background
    public class getLocationService extends Service {

        private static final int NOTIF_ID = 1;
        private static final String NOTIF_CHANNEL_ID = "CHANNEL_ID";

        @Nullable
        @Override
        public IBinder onBind(Intent intent){ return null; }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId){
            //do work here
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


    /*end service code*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
