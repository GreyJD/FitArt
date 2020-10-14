package com.example.fitart;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MapRecordingActivity extends AppCompatActivity
{
    private boolean playPauseButtonClicked = false;
    BackgroundGPSReceiver backgroundGPSReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_recording);

        backgroundGPSReceiver = new BackgroundGPSReceiver();
        IntentFilter intentFilter = new IntentFilter("GET_LOCATION_IN_BACKGROUND");
        this.registerReceiver(backgroundGPSReceiver,intentFilter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }



    public void playPauseButtonClicked(View view){

        // !! if the service is already running when this activity starts the service will be called multiple times
        // !!



        //starts/stops location gps tracking
        if (!playPauseButtonClicked) {

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
            //stopService(service_intent); // !! important: have ondestroy broadcast any leftover data when service is stopped
            startService(service_intent);
        }

    }
}


