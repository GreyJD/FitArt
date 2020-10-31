package com.example.fitart;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class MapRecordingActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_MESSAGE = "com.example.MapRecordingActivity.MESSAGE";
    private GoogleMap mMap;
    private boolean playPauseButtonClicked = false;
    private  ArrayList<PolyLineData> currentPolyList = new ArrayList<>() ;
    private Button playPauseButton;
    private Button doneButton;
    private LatLng lastLocation = null;
    private Marker lastLocationMarker = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setupMapIfNeeded();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_recording);
        setupMapIfNeeded();


        playPauseButton = findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(playPauseOnClickListener);

        doneButton = findViewById(R.id.button_done);
        doneButton.setOnClickListener(doneButtonOnClickListener);

        final KalmanLatLong kalmanFilter = new KalmanLatLong(3);

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

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

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


    private View.OnClickListener playPauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            playPauseButtonClicked(v);
        }
    };

    private View.OnClickListener doneButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v){
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

    @Override
    protected void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this, "CurrentSession");
        mgr.saveMapState(mMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupMapIfNeeded();
    }



    public void playPauseButtonClicked(View view){

        //starts/stops location gps tracking


                if(playPauseButtonClicked == false){
                    playPauseButtonClicked = true;
                    Toast.makeText(this, "play button clicked", Toast.LENGTH_SHORT).show();

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                }
                else{
                    playPauseButtonClicked = false;
                    Toast.makeText(this, "pause button clicked?", Toast.LENGTH_SHORT).show();
                }

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

}



class PolyLineData implements Serializable{

    private double startlat;
    private double startlong;
    private double endlat;
    private double endlong;



    public PolyLineData() {
    }

    public PolyLineData(LatLng startlocation, LatLng endlocation) {
        this.startlat = startlocation.latitude;
        this.startlong = startlocation.longitude;
        this.endlat = endlocation.latitude;
        this.endlong = endlocation.longitude;    }

    public LatLng getStartlocation() {
        return  new LatLng(startlat,startlong);
    }

    public void setStartlocation(LatLng startlocation) {
        this.startlat = startlocation.latitude;
        this.startlong = startlocation.longitude;
    }

    public LatLng getEndlocation() {
        return  new LatLng(endlat,endlong);
    }

    public void setEndlocation(LatLng endlocation) {
        this.endlat = endlocation.latitude;
        this.endlong = endlocation.longitude;     }
}


class  MapStateManager {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";
    private static final String BEARING = "bearing";
    private static final String TILT = "tilt";
    private static final String MAPTYPE = "MAPTYPE";
    private static final String POLYLINES = "polylines";



    private ArrayList<PolyLineData> polyLineList;

    private SharedPreferences mapStatePrefs;

    public ArrayList<PolyLineData> getPolyLineList(){
        return polyLineList;
    }

    public void addToPolyLineList(ArrayList<PolyLineData> value){
        polyLineList = value;
    }

    public void loadPolyListFromState(){
        Gson gson = new Gson();
        String json = mapStatePrefs.getString(POLYLINES, null);
        Type type = new TypeToken<ArrayList<PolyLineData>>() {}.getType();
        polyLineList = gson.fromJson(json, type);
        if(polyLineList ==  null)
            polyLineList = new ArrayList<>();


    }

    public MapStateManager(Context context, String name) {
        mapStatePrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        polyLineList = new ArrayList<>();
    }

    public void saveMapState(GoogleMap mapMie) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        CameraPosition position = mapMie.getCameraPosition();

        Gson gson = new Gson();
        String json = gson.toJson(polyLineList);
        editor.putString(POLYLINES, json);

        editor.putFloat(LATITUDE, (float) position.target.latitude);
        editor.putFloat(LONGITUDE, (float) position.target.longitude);
        editor.putFloat(ZOOM, position.zoom);
        editor.putFloat(TILT, position.tilt);
        editor.putFloat(BEARING, position.bearing);
        editor.putInt(MAPTYPE, mapMie.getMapType());
        editor.commit();
    }

    public CameraPosition getSavedCameraPosition() {
        double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
        if (latitude == 0) {
            return null;
        }
        double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
        LatLng target = new LatLng(latitude, longitude);

        float zoom = mapStatePrefs.getFloat(ZOOM, 0);
        float bearing = mapStatePrefs.getFloat(BEARING, 0);
        float tilt = mapStatePrefs.getFloat(TILT, 0);

        CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
        return position;
    }

    public int getSavedMapType() {
        return mapStatePrefs.getInt(MAPTYPE, GoogleMap.MAP_TYPE_NORMAL);
    }
}

class KalmanLatLong {
    private final float MinAccuracy = 1;

    private float Q_metres_per_second;
    private long TimeStamp_milliseconds;
    private double lat;
    private double lng;
    private float variance; // P matrix.  Negative means object uninitialised.  NB: units irrelevant, as long as same units used throughout

    public KalmanLatLong(float Q_metres_per_second) { this.Q_metres_per_second = Q_metres_per_second; variance = -1; }

    public long get_TimeStamp() { return TimeStamp_milliseconds; }
    public double get_lat() { return lat; }
    public double get_lng() { return lng; }
    public float get_accuracy() { return (float)Math.sqrt(variance); }

    public void SetState(double lat, double lng, float accuracy, long TimeStamp_milliseconds) {
        this.lat=lat; this.lng=lng; variance = accuracy * accuracy; this.TimeStamp_milliseconds=TimeStamp_milliseconds;
    }

    /// <summary>
    /// Kalman filter processing for lattitude and longitude
    /// </summary>
    /// <param name="lat_measurement_degrees">new measurement of lattidude</param>
    /// <param name="lng_measurement">new measurement of longitude</param>
    /// <param name="accuracy">measurement of 1 standard deviation error in metres</param>
    /// <param name="TimeStamp_milliseconds">time of measurement</param>
    /// <returns>new state</returns>
    public void Process(double lat_measurement, double lng_measurement, float accuracy, long TimeStamp_milliseconds) {
        if (accuracy < MinAccuracy) accuracy = MinAccuracy;
        if (variance < 0) {
            // if variance < 0, object is unitialised, so initialise with current values
            this.TimeStamp_milliseconds = TimeStamp_milliseconds;
            lat=lat_measurement; lng = lng_measurement; variance = accuracy*accuracy;
        } else {
            // else apply Kalman filter methodology

            long TimeInc_milliseconds = TimeStamp_milliseconds - this.TimeStamp_milliseconds;
            if (TimeInc_milliseconds > 0) {
                // time has moved on, so the uncertainty in the current position increases
                variance += TimeInc_milliseconds * Q_metres_per_second * Q_metres_per_second / 1000;
                this.TimeStamp_milliseconds = TimeStamp_milliseconds;
                // TO DO: USE VELOCITY INFORMATION HERE TO GET A BETTER ESTIMATE OF CURRENT POSITION
            }

            // Kalman gain matrix K = Covarariance * Inverse(Covariance + MeasurementVariance)
            // NB: because K is dimensionless, it doesn't matter that variance has different units to lat and lng
            float K = variance / (variance + accuracy * accuracy);
            // apply K
            lat += K * (lat_measurement - lat);
            lng += K * (lng_measurement - lng);
            // new Covarariance  matrix is (IdentityMatrix - K) * Covarariance
            variance = (1 - K) * variance;
        }
    }
}
