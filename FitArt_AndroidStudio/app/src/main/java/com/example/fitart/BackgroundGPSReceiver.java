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
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;




public class BackgroundGPSReceiver extends BroadcastReceiver{

    private GoogleMap mMap;
    Marker lastLocationMarker;
    //accepts gps data from background service
    private LatLng lastLocation = null;
    private LatLng newLocation = null;
    private LatLng nullLatLng = new LatLng(0.0,0.0);




    public BackgroundGPSReceiver(GoogleMap MapRecordingActivitymMap, Marker MapRecordingActivityLastLocationMarker){
        mMap = MapRecordingActivitymMap;
        lastLocationMarker = MapRecordingActivityLastLocationMarker;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        MapStateManager mgr = new MapStateManager(context, "CurrentSession");

        boolean play = mgr.getPlaybutton();
        if (intent.getAction() == "GET_LOCATION_IN_BACKGROUND"){
            ArrayList<LatLng> receivedList;
            receivedList = intent.getParcelableArrayListExtra("LOCATION");
            lastLocation = receivedList.get(0);
            newLocation = receivedList.get(1);



            if(lastLocation  != null && newLocation.equals(new LatLng(0.0,0.0))) {
                if (lastLocationMarker != null) {
                    lastLocationMarker.remove();
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));

            }
            if (play) {
                if(!(newLocation.equals(nullLatLng))) {
                    RoundCap roundCap = new RoundCap();
                    mMap.addPolyline(new PolylineOptions().clickable(false).add(newLocation, lastLocation).jointType(2).startCap(roundCap).endCap(roundCap));
                    PolyLineData lineData = new PolyLineData(lastLocation, newLocation);
                    lastLocation = newLocation;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
                    if (lastLocationMarker != null) {
                        lastLocationMarker.remove();
                    }
                    lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
                    mgr.addToPolyLineList(lineData);
                    mgr.savePolylineData();
                }
            }
            else {
                if (lastLocationMarker != null) {
                    lastLocationMarker.remove();
                }
                lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true));
            }
        }
            //activityList.addAll(receivedList);
    }


}

