package com.example.fitart;

import android.Manifest;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;

import mad.location.manager.lib.Commons.Utils;
import mad.location.manager.lib.Interfaces.ILogger;
import mad.location.manager.lib.Interfaces.SimpleTempCallback;
import yuku.ambilwarna.AmbilWarnaDialog;

import mad.location.manager.lib.Interfaces.LocationServiceInterface;
import mad.location.manager.lib.Loggers.GeohashRTFilter;
import mad.location.manager.lib.SensorAux.SensorCalibrator;
import mad.location.manager.lib.Services.KalmanLocationService;
import mad.location.manager.lib.Services.ServicesHelper;
import mad.location.manager.lib.Interfaces.ILogger;


public class MapRecordingActivity extends AppCompatActivity implements LocationServiceInterface, OnMapReadyCallback, ILogger  {

    public static final String EXTRA_MESSAGE = "com.example.MapRecordingActivity.MESSAGE";
    private GoogleMap mMap;
    private boolean playPauseButtonClicked = false;

    private ArrayList<PolyLineData> currentPolyList = new ArrayList<>();
    private double currentMilesTravled = 0;
    private Button playPauseButton;
    private Button doneButton;
    private LatLng lastLocation = null;
    private Marker lastLocationMarker = null;
    private long start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        start = System.nanoTime();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_recording);
        setupMapIfNeeded();


        playPauseButton = findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(playPauseOnClickListener);

        doneButton = findViewById(R.id.button_done);
        doneButton.setOnClickListener(doneButtonOnClickListener);


        //sets up listener for broadcasts. moves gps data from service to activity
        ////BackgroundGPSReceiver backgroundGPSReceiver = new BackgroundGPSReceiver(mMap, currentPolyList, lastLocationMarker, lastLocation, kalmanFilter);//swap dummy_list with sam's list
        //IntentFilter intentFilter = new IntentFilter("GET_LOCATION_IN_BACKGROUND");
        //this.registerReceiver(backgroundGPSReceiver, intentFilter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);


        ServicesHelper.addLocationServiceInterface(this);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.map_recording);
    }
        @Override
         public void onDestroy() {

        ServicesHelper.getLocationService(this, new SimpleTempCallback<KalmanLocationService>() {
                @Override
                public void onCall(KalmanLocationService value) {
                    if (value.IsRunning()) {
                        value.stop();
                        return;
                    }
                }
        });
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.addToPolyLineList(currentPolyList);
        mgr.saveMapState(mMap);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.addToPolyLineList(currentPolyList);
        mgr.saveMapState(mMap);


    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.addToPolyLineList(currentPolyList);
        mgr.saveMapState(mMap);

    }

    @Override
    public void onResume() {
        super.onResume();
        setupMapIfNeeded();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.loadPolyListFromState();
        currentPolyList = mgr.getPolyLineList();
        Intent service_intent = new Intent(this, GetLocationService.class);
        stopService(service_intent);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
            PolyLineData newline;
            LatLng startLatLng;
            LatLng endLatLng;
            Cap roundCap = new RoundCap();
            for (int i = 0; i < currentPolyList.size(); i++) {
                newline = currentPolyList.get(i);
                startLatLng = newline.getStartlocation();
                endLatLng = newline.getEndlocation();
                mMap.addPolyline(new PolylineOptions().add(endLatLng, startLatLng).jointType(2).startCap(roundCap).endCap(roundCap));
            }
        } else if (lastLocationMarker != null) {
            lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
        }

        mMap.setMapType(mgr.getSavedMapType());
    }


    private View.OnClickListener playPauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            playPauseButtonClicked(v);
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
        if (!playPauseButtonClicked) {
            //first guarantee permission to use gps
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //permission not granted
            } else {
                playPauseButtonClicked = true;
                Toast.makeText(this, "play button clicked", Toast.LENGTH_SHORT).show();

                ServicesHelper.getLocationService(this, new SimpleTempCallback<KalmanLocationService>() {
                    @Override
                    public void onCall(KalmanLocationService value) {
                        if (value.IsRunning()) {
                            return;
                        }
                        value.stop();
                        KalmanLocationService.Settings settings = KalmanLocationService.defaultSettings;
                        value.reset(settings);
                        value.start();
                    }
                });
            }
        } else {
            playPauseButtonClicked = false;
            // Intent service_intent = new Intent(this, GetLocationService.class); //old code - remove if/when irrelevant
            Toast.makeText(this, "pause button clicked?", Toast.LENGTH_SHORT).show();
            ServicesHelper.getLocationService(this, new SimpleTempCallback<KalmanLocationService>() {
                @Override
                public void onCall(KalmanLocationService value) {
                    if (value.IsRunning()) {
                        value.stop();
                        return;
                    }
                }
            });
            // stopService(service_intent); // !! important: have ondestroy broadcast any leftover data when service is stopped
        }
    }


    public void doneButtonClicked(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapRecordingActivity.this);
        builder.setMessage("Give it a name!").setTitle("Are you finished?").setCancelable(false);
        final EditText userInput = new EditText(MapRecordingActivity.this);
        builder.setView(userInput);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String usersName = userInput.getText().toString();
                MapStateManager currentState = new MapStateManager(MapRecordingActivity.this, usersName);

                long finish = System.nanoTime();
                long timeElapsed = finish - start;

                currentState.addTimeToSaveState(timeElapsed);
                currentState.addToPolyLineList(currentPolyList);
                currentState.addMilesToSaveState(currentMilesTravled);
                currentState.saveMapState(mMap);
                currentPolyList = new ArrayList<>();
                lastLocation = null;
                mMap.clear();
                Intent intent = new Intent(MapRecordingActivity.this, EditActivity.class);
                intent.putExtra(EXTRA_MESSAGE, usersName);
                currentMilesTravled = 0;
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


    public double CalculateDistance(double lat1, double lat2, double long1, double long2) {
        double theta = long1 - long2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    @Override
    public void locationChanged(Location location) {
        if (lastLocation == null && mMap != null) {
            lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 16));
            lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
        } else if (playPauseButtonClicked) {



            LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
            double temp = CalculateDistance(lastLocation.latitude, newLocation.latitude, lastLocation.longitude, newLocation.longitude);
            if(temp > 0.000947) {
                currentMilesTravled += temp;

                Cap roundCap = new RoundCap();
                mMap.addPolyline(new PolylineOptions().clickable(false).add(newLocation, lastLocation).jointType(2).startCap(roundCap).endCap(roundCap));
                PolyLineData lineData = new PolyLineData(lastLocation, newLocation);
                currentPolyList.add(lineData);
                lastLocation = newLocation;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                lastLocationMarker.remove();
                lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
            }
        } else
            ;

    }

    @Override
    public void log2file(String s, Object... objects) {

    }
}


