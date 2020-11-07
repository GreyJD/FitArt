package com.example.fitart;


import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


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

public class MapRecordingActivity extends AppCompatActivity implements OnMapReadyCallback
{
    public static final String EXTRA_MESSAGE = "com.example.MapRecordingActivity.MESSAGE";
    private  ArrayList<PolyLineData> currentPolyList = new ArrayList<>() ;
    private GoogleMap mMap;
    private LatLng lastLocation = null;
    private Marker lastLocationMarker = null;
    private Button doneButton;
    private Button playPauseButton;
    final KalmanLatLong kalmanFilter = new KalmanLatLong(3);


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

        //merged in start
        setupMapIfNeeded();


        //playPauseButton = findViewById(R.id.button_play_pause);
        //playPauseButton.setOnClickListener(playPauseOnClickListener);

        //doneButton = findViewById(R.id.button_done);
        //doneButton.setOnClickListener(doneButtonOnClickListener);


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
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {

                    if (lastLocation == null) {
                        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
                        lastLocationMarker =  mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
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
                    }
                    else
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
        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {

                    if (lastLocation == null) {
                        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
                        lastLocationMarker =  mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
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
                    }
                    else
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
        }
        else
            Toast.makeText(this, "no network or gps provider?", Toast.LENGTH_SHORT).show();
        //merged in stop

        //sets up listener for broadcasts. moves gps data from service to activity

        //must add map fragment here (dynamic fragment allocation)
        //having the fragment declared in the .xml file is static fragment allocation

        // https://www.youtube.com/watch?v=li12Kmvk7BQ    this is the tutorial used for adding fragments dynamically

        //FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //MapsFragment mapsFragment = new MapsFragment();
        //fragmentTransaction.add(R.id.map_recording_fragment_container, mapsFragment);
        //fragmentTransaction.commit();
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

            mMap.setMapType(mgr.getSavedMapType());
            PolyLineData newline;
            LatLng startLatLng;
            LatLng endLatLng;
            for(int i = 0; i < currentPolyList.size(); i++){
                newline =  currentPolyList.get(i);
                startLatLng = newline.getStartlocation();
                endLatLng = newline.getEndlocation();
                mMap.addPolyline(new PolylineOptions().add(endLatLng, startLatLng));
            }
        }
    }

    private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onDestroy(){
        if(playPauseButtonClicked){
            Intent service_intent = new Intent(this, GetLocationService.class);
            startService(service_intent);

        }
        super.onDestroy();
    }

    @Override
    public void onStop(){
        super.onStop();


    }

    @Override
    public void onStart(){
        super.onStart();

    }


    @Override
    public void onPause(){
        super.onPause();

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
                if(backgroundGPSReceiver == null){
                    backgroundGPSReceiver = new BackgroundGPSReceiver(mMap, currentPolyList,lastLocationMarker,lastLocation,kalmanFilter);//swap dummy_list with sam's list
                    IntentFilter intentFilter = new IntentFilter("GET_LOCATION_IN_BACKGROUND");
                    this.registerReceiver(backgroundGPSReceiver,intentFilter);
                }
                //Intent service_intent = new Intent(this, GetLocationService.class); // old code - remove if/when irrelevant
                //startService(service_intent);
            }
        } else{
            playPauseButtonClicked = false;
           // Intent service_intent = new Intent(this, GetLocationService.class); //old code - remove if/when irrelevant
           // stopService(service_intent); // !! important: have ondestroy broadcast any leftover data when service is stopped
        }
    }


    public void mapArtButtonClicked(View view) {
        //there are 3 states involved here
        // 1 - map fragment only
        // 2 - openGL running with map fragment on backstack
        // 3 - map running with openGL on backstack


        // should add current map or art fragment to backstack and swap active fragments
        if (mapArtButtonClicked == 0) {
            // 1 - map fragment only
            // if this button has not been clicked replace map fragment with openGl fragment
            mapArtButtonClicked++;

         //   FragmentManager fragmentManager = getSupportFragmentManager();
           // FragmentTransaction transaction = fragmentManager.beginTransaction();
           // OpenGlFragment openGlFragment = new OpenGlFragment();
           // transaction.replace(R.id.map_recording_fragment_container, openGlFragment);
           // transaction.addToBackStack("map_frag");
           // transaction.commit();

            //OpenGlFragment openGlFragment = new OpenGlFragment();
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.replace(R.id.map_recording_fragment_container, openGlFragment);
            //transaction.addToBackStack("map_frag");
            //transaction.commit();

            int p = 100;

        } else if (mapArtButtonClicked == 1) {
            // 2 - openGL running with map fragment on backstack
            mapArtButtonClicked++;

        } else if (mapArtButtonClicked == 2) {
            // 3 - map running with openGL on backstack
            mapArtButtonClicked--;

        } else {
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
    }

    public void doneButtonClicked(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(MapRecordingActivity.this);
        builder.setMessage("Are you sure you're finished?").setTitle("test").setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String usersName = "TestName";
                MapStateManager currentState = new MapStateManager( MapRecordingActivity.this, usersName);
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
              //  Intent intent = new Intent(MapRecordingActivity.this, EditActivity.class);
               // intent.putExtra(EXTRA_MESSAGE, usersName);
               // startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

        //Find Variables
      //  colorButton = findViewById(R.id.button_color);
      //  colorSwatchImage = findViewById(R.id.image_colorSwatch);
       // seekbar();

        //Assign default color
      //  defaultColor = ContextCompat.getColor(MapRecordingActivity.this, R.color.colorPrimary);

        //On.click.listener for color palette
      //  colorButton.setOnClickListener(new View.OnClickListener() {
     //       @Override
     //       public void onClick(View v) {
      //          openColorPicker();
    //        }
     //   });
    //}

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


