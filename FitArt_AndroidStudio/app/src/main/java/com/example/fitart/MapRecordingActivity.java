package com.example.fitart;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.IccOpenLogicalChannelResponse;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MapRecordingActivity extends AppCompatActivity
{
    private boolean playPauseButtonClicked = false;
    private int mapArtButtonClicked = 0;
    BackgroundGPSReceiver backgroundGPSReceiver;

    private static SeekBar seek_bar;
    ImageButton colorButton;
    ImageView colorSwatchImage;
    int defaultColor;


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
    public void onPause(){
        super.onPause();
        if(playPauseButtonClicked){
            Intent service_intent = new Intent(this, GetLocationService.class);
            startService(service_intent);

        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Intent service_intent = new Intent(this, GetLocationService.class);
        stopService(service_intent);

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
                //Intent service_intent = new Intent(this, GetLocationService.class);
                //startService(service_intent);
            }
        } else{
            playPauseButtonClicked = false;
           // Intent service_intent = new Intent(this, GetLocationService.class);
           // stopService(service_intent); // !! important: have ondestroy broadcast any leftover data when service is stopped
        }
    }


    public void mapArtButtonClicked(View view) {
        //there are 3 states involved here
        // 1 - map fragment only
        // 2 - openGL running with map fragment on backstack
        // 3 - map running with openGL on backstack




        // should add current map or art fragment to backstack and swap active fragments
        if (mapArtButtonClicked == 0){
            // 1 - map fragment only
            // if this button has not been clicked replace map fragment with openGl fragment
            mapArtButtonClicked ++;
            OpenGlFragment openGlFragment = new OpenGlFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.map_recording_fragment_container, openGlFragment);
            transaction.addToBackStack("map_frag");
            transaction.commit();

            int p = 100;

        }else if(mapArtButtonClicked == 1){
            // 2 - openGL running with map fragment on backstack
            //



            mapArtButtonClicked ++;

        }else if(mapArtButtonClicked == 2){
            // 3 - map running with openGL on backstack

            mapArtButtonClicked --;

        }else{
            //error state reached
        }



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

        //Find Variables
        colorButton = findViewById(R.id.button_color);
        colorSwatchImage = findViewById(R.id.image_colorSwatch);
        seekbar();

        //Assign default color
        defaultColor = ContextCompat.getColor(MapRecordingActivity.this, R.color.colorPrimary);

        //On.click.listener for color palette
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }

    //Brush Size Controls
    public void seekbar()
    {
        seek_bar = (SeekBar)findViewById(R.id.seekBar_BrushSize);
        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int brush_Size;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        brush_Size = progress / 10;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Debug purposes
                        Toast.makeText(MapRecordingActivity.this, "size:" + brush_Size, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    //Color choice popup
    public void openColorPicker()
    {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;

                //Attempt to update Color Swatch...Failed
                PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
                colorSwatchImage.setBackgroundColor(defaultColor);

                //Debug purposes
                Toast.makeText(MapRecordingActivity.this, "color:" + defaultColor, Toast.LENGTH_SHORT).show();

            }
        });
        ambilWarnaDialog.show();

    }


}


