package com.example.fitart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BackgroundGPSReceiver extends BroadcastReceiver{

        //accepts gps data from background service
        ArrayList<LatLng> receivedList;
        ArrayList<LatLng> activityList;

        public BackgroundGPSReceiver(ArrayList<LatLng> list){
            activityList = list;
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "GET_LOCATION_IN_BACKGROUND"){
                receivedList = intent.getParcelableArrayListExtra("LOCATION");
                if(receivedList.size() > 0){
                    activityList.addAll(receivedList);
                }
            }
        }
    }
