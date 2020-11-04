package com.example.fitart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;




public class BackgroundGPSReceiver extends BroadcastReceiver{

        //accepts gps data from background service
        ArrayList<PolyLineData> receivedList;
        ArrayList<PolyLineData> activityList;
        GoogleMap mMap;

        public BackgroundGPSReceiver(ArrayList<PolyLineData> list, GoogleMap map){
            activityList = list;
            mMap = map;
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "GET_LOCATION_IN_BACKGROUND"){
                Bundle args = intent.getBundleExtra("BUNDLE");
                receivedList = (ArrayList<PolyLineData>) args.getSerializable("LOCATION");
                Toast.makeText(context, "entering onReceive",Toast.LENGTH_SHORT).show();
                if(receivedList.size() > 0){
                    Toast.makeText(context, "drawing lines",Toast.LENGTH_SHORT).show();
                    activityList.addAll(receivedList);
                    PolyLineData newline;
                    LatLng startLatLng;
                    LatLng endLatLng;
                    for (int i = 0; i < activityList.size(); i++) {
                        newline = activityList.get(i);
                        startLatLng = newline.getStartlocation();
                        endLatLng = newline.getEndlocation();
                        mMap.addPolyline(new PolylineOptions().add(endLatLng, startLatLng));
                    }
                }
            }
        }
    }
