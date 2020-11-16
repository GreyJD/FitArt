package com.example.fitart;

import android.Manifest;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;

import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MapRecordingActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_MESSAGE = "com.example.MapRecordingActivity.MESSAGE";
    private GoogleMap mMap;
    private boolean playPauseButtonClicked = false;
    private boolean isPaused;

    private ArrayList<PolyLineData> currentPolyList = new ArrayList<>();
    private Button playPauseButton;
    private Button doneButton;
    private LatLng lastLocation = null;
    private Marker lastLocationMarker = null;

    private static SeekBar seek_bar;
    ImageButton colorButton;
    ImageView colorSwatchImage;
    int defaultColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_recording);
        setupMapIfNeeded();


        playPauseButton = findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(playPauseOnClickListener);
        isPaused = false;

        doneButton = findViewById(R.id.button_done);
        doneButton.setOnClickListener(doneButtonOnClickListener);

        final KalmanLatLong kalmanFilter = new KalmanLatLong(3);

        //sets up listener for broadcasts. moves gps data from service to activity
        BackgroundGPSReceiver backgroundGPSReceiver = new BackgroundGPSReceiver( mMap, currentPolyList,  lastLocationMarker, lastLocation, kalmanFilter);//swap dummy_list with sam's list
        IntentFilter intentFilter = new IntentFilter("GET_LOCATION_IN_BACKGROUND");
        this.registerReceiver(backgroundGPSReceiver, intentFilter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        //check if the network provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {


                    if (lastLocation == null) {
                        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
                        lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
                    } else if (playPauseButtonClicked) {

                        long timeStamp = System.currentTimeMillis();

                        kalmanFilter.Process(location.getLatitude(), location.getLongitude(), location.getAccuracy(), timeStamp);
                        LatLng newLocation = new LatLng(kalmanFilter.get_lat(), kalmanFilter.get_lng());

                        mMap.addPolyline(new PolylineOptions().clickable(false).add(newLocation, lastLocation).jointType(2));
                        PolyLineData lineData = new PolyLineData(lastLocation, newLocation);
                        currentPolyList.add(lineData);


                        lastLocation = newLocation;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                        lastLocationMarker.remove();
                        lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));

                    } else
                        ;

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
            });
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {


                    if (lastLocation == null) {
                        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
                        lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
                    } else if (playPauseButtonClicked) {
                        long timeStamp = System.currentTimeMillis();

                        kalmanFilter.Process(location.getLatitude(), location.getLongitude(), location.getAccuracy(), timeStamp);
                        LatLng newLocation = new LatLng(kalmanFilter.get_lat(), kalmanFilter.get_lng());

                        mMap.addPolyline(new PolylineOptions().clickable(false).add(newLocation, lastLocation).jointType(2));
                        PolyLineData lineData = new PolyLineData(lastLocation, newLocation);
                        currentPolyList.add(lineData);

                        lastLocation = newLocation;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                        lastLocationMarker.remove();
                        lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
                    } else
                        ;

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
            });
        } else
            Toast.makeText(this, "no network or gps provider?", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy(){
        if(playPauseButtonClicked){
            Intent service_intent = new Intent(this, GetLocationService.class);
            startService(service_intent);

        }
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.addToPolyLineList(currentPolyList);
        mgr.saveMapState(mMap);
        super.onDestroy();
    }

    @Override
    public void onStop(){
        super.onStop();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.addToPolyLineList(currentPolyList);
        mgr.saveMapState(mMap);


    }

    @Override
    public void onStart(){
        super.onStart();

    }


    @Override
    public void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.addToPolyLineList(currentPolyList);
        mgr.saveMapState(mMap);

        //FIX: Attempting to change play_pause button text
        playPauseButton.setText("Play");
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

        //FIX: Attempting to change play pause button text
        playPauseButton.setText("Pause");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            Toast.makeText(this, "entering Resume State", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(update);
            PolyLineData newline;
            LatLng startLatLng;
            LatLng endLatLng;
            for (int i = 0; i < currentPolyList.size(); i++) {
                newline = currentPolyList.get(i);
                startLatLng = newline.getStartlocation();
                endLatLng = newline.getEndlocation();
                mMap.addPolyline(new PolylineOptions().add(endLatLng, startLatLng));
            }
        }
        else if ( lastLocationMarker != null)
            {
                lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
            }

            mMap.setMapType(mgr.getSavedMapType());
        }






    private View.OnClickListener playPauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            playPauseButtonClicked(v);
            isPaused = !isPaused;
            if (isPaused)
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
                //Intent service_intent = new Intent(this, GetLocationService.class); // old code - remove if/when irrelevant
                //startService(service_intent);
            }
        } else {
            playPauseButtonClicked = false;
            // Intent service_intent = new Intent(this, GetLocationService.class); //old code - remove if/when irrelevant
            Toast.makeText(this, "pause button clicked?", Toast.LENGTH_SHORT).show();
            // stopService(service_intent); // !! important: have ondestroy broadcast any leftover data when service is stopped
        }
    }


    public void doneButtonClicked(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapRecordingActivity.this);
        builder.setMessage("Are you sure you're finished?").setTitle("test").setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String usersName = "TestName";
                MapStateManager currentState = new MapStateManager(MapRecordingActivity.this, usersName);
                 /*
                 PolyLineData temp = new PolyLineData( new LatLng(-35.016, 143.321), new LatLng(-34.747, 145.592));
                 currentPolyList.add(temp);
                 temp = new PolyLineData(new LatLng(-34.364, 147.891), new LatLng(-33.501, 150.217));
                 currentPolyList.add(temp);
                 temp = new PolyLineData(new LatLng(-32.306, 149.248), new LatLng(-32.491, 147.309));
                 currentPolyList.add(temp);
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));

                  */
                currentState.addToPolyLineList(currentPolyList);
                currentState.saveMapState(mMap);
                currentPolyList = new ArrayList<>();
                lastLocation = null;
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


    public void openColorPicker() {
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



