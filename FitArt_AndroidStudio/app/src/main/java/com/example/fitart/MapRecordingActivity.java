package com.example.fitart;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MapRecordingActivity extends AppCompatActivity
{
    private boolean playPauseButtonClicked = false;
    BackgroundGPSReciever backgroundGPSReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_recording);

        backgroundGPSReciever = new BackgroundGPSReciever();
        IntentFilter intentFilter = new IntentFilter("GET_BACKGROUND_LOCATION");
        registerReceiver(backgroundGPSReciever,intentFilter);
        Button HijackedText4Testing = findViewById(R.id.button_play_pause);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }



    public void playPauseButtonClicked(){

        //starts/stops location gps tracking
        if (!playPauseButtonClicked) {
            playPauseButtonClicked = true;
            Intent service_intent = new Intent(this, GetLocationService.class);
            startService(service_intent);
        } else{
            playPauseButtonClicked = false;
            Intent service_intent = new Intent(this, GetLocationService.class);
            stopService(service_intent);
        }
    }

    //accepts gps data from background service
    class BackgroundGPSReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "GET_LOCATION_IN_BACKGROUND"){
                Location location = intent.getParcelableExtra("LOCATION");
                //HighJ



            }
        }
    }



}


