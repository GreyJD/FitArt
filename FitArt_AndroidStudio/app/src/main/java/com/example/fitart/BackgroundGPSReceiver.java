package com.example.fitart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class BackgroundGPSReceiver extends BroadcastReceiver{

    private GoogleMap mMap;
    ArrayList<PolyLineData> currentPolyList;
    Marker lastLocationMarker;
    //ArrayList<Long> timestampArray;
    //ArrayList<Float> accuracyArray;
    long[] timeStampObjects;
    float[] accurracyObjects;
    KalmanLatLong kalmanFilter;
    //accepts gps data from background service

    private LatLng lastLocation = null;



    ArrayList<LatLng> receivedList;
    //ArrayList<LatLng> activityList;



    public BackgroundGPSReceiver(GoogleMap MapRecordingActivitymMap,
                                 ArrayList<PolyLineData> MapRecordingActivityCurrentPolyList,
                                 Marker MapRecordingActivityLastLocationMarker,
                                 LatLng MapRecordingActivityLastLocation,
                                 KalmanLatLong MapRecordingActivityKalmanFilter){
        mMap = MapRecordingActivitymMap;
        this.currentPolyList = MapRecordingActivityCurrentPolyList;
        lastLocationMarker = MapRecordingActivityLastLocationMarker;
        lastLocation = MapRecordingActivityLastLocation;
        kalmanFilter = MapRecordingActivityKalmanFilter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == "GET_LOCATION_IN_BACKGROUND"){
            receivedList = intent.getParcelableArrayListExtra("LOCATION");
            long[] timeStampObjects = intent.getLongArrayExtra("TIMESTAMP");
            float[] accurracyObjects = intent.getFloatArrayExtra("ACCURACY");
            //add timestamp array to intent in service
            //add accuracy array to intent in service
            if(receivedList == null || receivedList.size() < 1 ) {
                //do nothing
            }else{
                //received latLngs from service, do stuff with em
                int i =0;
                if(lastLocation == null){
                    if(currentPolyList != null && !currentPolyList.isEmpty()){
                        //if polylist not empty and last location is null for some reason
                        // set last location from polyList
                        lastLocation = new LatLng(currentPolyList.get(currentPolyList.size()-1).getStartlocation().latitude,
                                currentPolyList.get(currentPolyList.size()-1).getStartlocation().longitude);
                    }else {
                        //otherwise last location set from received list
                        lastLocation = new LatLng(receivedList.get(0).latitude,receivedList.get(0).longitude);
                        i++;
                    }
                }
                for(; i < receivedList.size(); i++){
                    kalmanFilter.Process(receivedList.get(i).latitude,
                            receivedList.get(i).longitude,
                            accurracyObjects[i],
                            timeStampObjects[i]);
                    LatLng newLocation = new LatLng(kalmanFilter.get_lat(),kalmanFilter.get_lng());
                    mMap.addPolyline(new PolylineOptions().clickable(false).add(newLocation, lastLocation).jointType(2));
                    PolyLineData lineData = new PolyLineData(lastLocation, newLocation);
                    currentPolyList.add(lineData);
                    lastLocation = newLocation;
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                lastLocationMarker.remove();
                lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));

            }
            //activityList.addAll(receivedList);

        }
    }
}
