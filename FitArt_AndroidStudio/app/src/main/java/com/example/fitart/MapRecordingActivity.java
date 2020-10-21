package com.example.fitart;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.MapFragment;

public class MapRecordingActivity extends AppCompatActivity
{
    private boolean playPauseButtonClicked = false;
    BackgroundGPSReceiver backgroundGPSReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_recording);

        //sets up listener for broadcasts. moves gps data from service to activity
        backgroundGPSReceiver = new BackgroundGPSReceiver();
        IntentFilter intentFilter = new IntentFilter("GET_LOCATION_IN_BACKGROUND");
        this.registerReceiver(backgroundGPSReceiver,intentFilter);

        //must add map fragment here (dynamic fragment allocation)
        //having the fragment declared in the .xml file is static fragment allocation

        //https://www.youtube.com/watch?v=li12Kmvk7BQ    this is the tutorial used for adding fragments dynamically

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapsFragment mapsFragment = new MapsFragment();
        fragmentTransaction.add(R.id.map_recording_fragment_container, mapsFragment);
        fragmentTransaction.commit();


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }


    public void playPauseButtonClicked(View view){

        //starts/stops location gps tracking
        if (!playPauseButtonClicked) {
            //first guarantee permission to use gps
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //permission not granted
            } else {
                playPauseButtonClicked = true;
                Intent service_intent = new Intent(this, GetLocationService.class);
                startService(service_intent);
            }
        } else{
            playPauseButtonClicked = false;
            Intent service_intent = new Intent(this, GetLocationService.class);
            stopService(service_intent); // !! important: have ondestroy broadcast any leftover data when service is stopped
        }
    }


    public void mapArtButtonClicked(View view) {

        // should add current map or art fragment to backstack and swap active fragments


/* starter xml code for opengl fragment (causes crashing if inserted)
     <fragment
        android:id="@+id/OpenGL_location_art"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/toolbar"
        android:layout_marginTop="-56dp"
        tools:context=".MapsActivity"
        />

  */
    }
}


