package com.example.fitart;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MapRecordingActivity extends AppCompatActivity
{
    private boolean playPauseButtonClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_recording);

      /*  final Button play_pause_button = findViewById(R.id.button_play_pause);
        play_pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean playing = false;
                Intent service_intent = new Intent(this, GetLocationService.class);

                //service_intent.setAction("com.example.fitart.service.GetLocationService");
                startService(service_intent);
            }
        }); */
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
}
