package com.example.fitart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BackgroundGPSReceiver extends BroadcastReceiver{

    //accepts gps data from background service

        ArrayList<LatLng> lastReceivedLocation;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "GET_LOCATION_IN_BACKGROUND"){
               lastReceivedLocation = intent.getParcelableExtra("LOCATION");
            }
        }
    }
