package com.example.fitart;

import android.Manifest;

import android.app.ActivityManager;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;


import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.EditText;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;



public class MapRecordingActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_MESSAGE = "com.example.MapRecordingActivity.MESSAGE";
    private GoogleMap mMap;
    private boolean playPauseButtonClicked = false;
    private boolean isPaused;

    private ArrayList<PolyLineData> currentPolyList = new ArrayList<>();
    private Button playPauseButton;
    private Button doneButton;

    private Marker lastLocationMarker = null;
    private long start;
    private BackgroundGPSReceiver backgroundGPSReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        start = System.nanoTime();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_recording);
        setupMapIfNeeded();
        playPauseButton = findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(playPauseOnClickListener);
        isPaused = true;

        doneButton = findViewById(R.id.button_done);
        doneButton.setOnClickListener(doneButtonOnClickListener);





        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "need to request premission", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        playPauseButtonClicked = false;
        mgr.setPlaybutton(playPauseButtonClicked);
        mgr.saveMapState(mMap);
        Intent service_intent = new Intent(this, GetLocationService.class);
        stopService(service_intent);

    }

    @Override
         public void onDestroy() {
            super.onDestroy();
            MapStateManager mgr = new MapStateManager(this, "CurrentSession");
            mgr.setPlaybutton(playPauseButtonClicked);
            mgr.saveMapState(mMap);
    }

    @Override
    public void onStop() {
        super.onStop();

        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.setPlaybutton(playPauseButtonClicked);
        mgr.saveMapState(mMap);

    }


    @Override
    public void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.setPlaybutton(playPauseButtonClicked);
        Toast.makeText(this, "on pause", Toast.LENGTH_SHORT).show();
        mgr.saveMapState(mMap);

    }
    @Override
    public void onStart() {
        super.onStart();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        playPauseButtonClicked = mgr.getPlaybutton();
        if(playPauseButtonClicked == true){
            if(!(isMyServiceRunning(GetLocationService.class))) {
                Intent service_intent = new Intent(this, GetLocationService.class);
                startService(service_intent);
            }
        }
        else{
            if(isMyServiceRunning(GetLocationService.class)) {
                Intent service_intent = new Intent(this, GetLocationService.class);
                stopService(service_intent);
            }
        }
        if (!playPauseButtonClicked)
            playPauseButton.setText("Play");
        else
            playPauseButton.setText("Pause");
        setupMapIfNeeded();


    }

    @Override
    public void onResume() {
        super.onResume();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        playPauseButtonClicked = mgr.getPlaybutton();
        if(playPauseButtonClicked == true){
            if(!(isMyServiceRunning(GetLocationService.class))) {
                Intent service_intent = new Intent(this, GetLocationService.class);
                startService(service_intent);
            }
        }
        else{
            if(isMyServiceRunning(GetLocationService.class)) {
                Intent service_intent = new Intent(this, GetLocationService.class);
                stopService(service_intent);
            }
        }
        if (!playPauseButtonClicked)
            playPauseButton.setText("Play");
        else
            playPauseButton.setText("Pause");
        Toast.makeText(this, "on resume", Toast.LENGTH_SHORT).show();
        setupMapIfNeeded();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        drawLines();

    }


    private View.OnClickListener playPauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            playPauseButtonClicked(v);

            //Change play pause button text
            if (!playPauseButtonClicked)
                playPauseButton.setText("Play");
            else
                playPauseButton.setText("Pause");
        }
    };


    private View.OnClickListener doneButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            doneButtonClicked(v);

        }
    };

    private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }



    public void playPauseButtonClicked(View view) {

        //starts/stops location gps tracking


        //starts/stops location gps tracking
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        if (!playPauseButtonClicked) {
            //first guarantee permission to use gps
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //permission not granted
            }
            else {
                playPauseButtonClicked = true;
                if(backgroundGPSReceiver == null){
                    backgroundGPSReceiver = new BackgroundGPSReceiver(mMap, lastLocationMarker);//swap dummy_list with sam's list
                    IntentFilter intentFilter = new IntentFilter("GET_LOCATION_IN_BACKGROUND");
                    this.registerReceiver(backgroundGPSReceiver,intentFilter);
                }
                mgr.setPlaybutton(playPauseButtonClicked);
                mgr.saveMapState(mMap);
                //sets up listener for broadcasts. moves gps data from service to activity
                Toast.makeText(this, "play button clicked", Toast.LENGTH_SHORT).show();
                Intent service_intent = new Intent(this, GetLocationService.class);
                startService(service_intent);
            }
        } else {
            playPauseButtonClicked = false;
            mgr.setPlaybutton(playPauseButtonClicked);
            mgr.saveMapState(mMap);
            // Intent service_intent = new Intent(this, GetLocationService.class); //old code - remove if/when irrelevant
            Toast.makeText(this, "pause button clicked?", Toast.LENGTH_SHORT).show();
            //Intent service_intent = new Intent(this, GetLocationService.class);
            //stopService(service_intent);

            // stopService(service_intent); // !! important: have ondestroy broadcast any leftover data when service is stopped
        }
    }


    public void doneButtonClicked(View view) {

        Intent service_intent = new Intent(this, GetLocationService.class);
        stopService(service_intent);
        AlertDialog.Builder builder = new AlertDialog.Builder(MapRecordingActivity.this);
        builder.setMessage("Give it a name!").setTitle("Are you finished?").setCancelable(false);
        final EditText userInput = new EditText(MapRecordingActivity.this);
        builder.setView(userInput);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String usersName = userInput.getText().toString();
                MapStateManager currentState = new MapStateManager(MapRecordingActivity.this, usersName);
                MapStateManager mgr = new MapStateManager(MapRecordingActivity.this, "CurrentSession");
                mgr.loadPolyListFromState();
                PolyLineData newline;
                LatLng startLatLng;
                LatLng endLatLng;
                double distance = 0;
                for (int j = 0; j < currentPolyList.size(); j++) {
                    newline = currentPolyList.get(j);
                    startLatLng = newline.getStartlocation();
                    endLatLng = newline.getEndlocation();
                    distance += CalculateDistance(startLatLng.latitude, endLatLng.latitude, startLatLng.longitude, endLatLng.longitude);

                }
                currentPolyList = mgr.getPolyLineList();
                long finish = System.nanoTime();
                long timeElapsed = finish - start;
                currentState.addTimeToSaveState(timeElapsed);
                currentState.setPolylinesList(currentPolyList);
                currentState.addMilesToSaveState(distance);
                currentState.saveMapState(mMap);
                currentPolyList = new ArrayList<>();
                playPauseButtonClicked = false;

                mgr.deletePolylineData();
                mMap.clear();
                Intent intent = new Intent(MapRecordingActivity.this, EditActivity.class);
                intent.putExtra(EXTRA_MESSAGE, usersName);
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public double CalculateDistance(double lat1, double lat2, double long1, double long2) {
        double theta = long1 - long2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }



    public void drawLines(){
        if(mMap != null){
            mMap.clear();
            MapStateManager mgr = new MapStateManager(this, "CurrentSession");
            CameraPosition position = mgr.getSavedCameraPosition();
            if (position != null) {
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                mMap.moveCamera(update);

                mMap.setMapType(mgr.getSavedMapType());
                mgr.loadPolyListFromState();
                ArrayList<PolyLineData> newLines = mgr.getPolyLineList();
                PolyLineData newline;
                LatLng startLatLng;
                LatLng endLatLng;
                Cap roundCap = new RoundCap();
                for (int i = 0; i < newLines.size(); i++) {
                    newline = newLines.get(i);
                    startLatLng = newline.getStartlocation();
                    endLatLng = newline.getEndlocation();
                    mMap.addPolyline(new PolylineOptions().add(endLatLng, startLatLng).jointType(2).startCap(roundCap).endCap(roundCap));
                }
            }
            mMap.setMapType(mgr.getSavedMapType());
        }
    }
}


