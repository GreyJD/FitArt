package com.example.fitart;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        IntentFilter intentFilter = new IntentFilter("GET_BACKGROUND_LOCATION");
        registerReceiver(backgroundGPSReceiver,intentFilter);
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
            stopService(service_intent);
        }

    }



    //accepts gps data from background service
    class BackgroundGPSReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "GET_LOCATION_IN_BACKGROUND"){
                Location location = intent.getParcelableExtra("LOCATION");
                Button hijackedText4Testing = findViewById(R.id.button_play_pause);
                hijackedText4Testing.setText(location.toString());


            }
        }
    }



}


