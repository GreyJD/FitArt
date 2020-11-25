package com.example.fitart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;


import java.util.ArrayList;

import static java.security.AccessController.getContext;


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
        Bitmap bitmap = getBitmapFromVectorDrawable(context, R.drawable.ic_directions_run_24px);
        BitmapDescriptor b = BitmapDescriptorFactory.fromBitmap(bitmap);
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
                lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true).icon(b));

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
                    lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true).icon(b));
                    mgr.addToPolyLineList(lineData);
                    mgr.savePolylineData();
                }
            }
            else {
                if (lastLocationMarker != null) {
                    lastLocationMarker.remove();
                }
                lastLocationMarker = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current Location").flat(true).icon(b));
            }
        }
            //activityList.addAll(receivedList);
    }
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}

