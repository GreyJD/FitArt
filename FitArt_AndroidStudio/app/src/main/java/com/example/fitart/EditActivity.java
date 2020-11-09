package com.example.fitart;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.fitart.GetLocationService;
import com.example.fitart.MapStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String usersFileName;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_route);


        Intent intent = getIntent();
        usersFileName = intent.getStringExtra(MapRecordingActivity.EXTRA_MESSAGE);
        saveButton = findViewById(R.id.button_save);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        saveButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        SharedPreferences savedState = PreferenceManager.getDefaultSharedPreferences(this);
        MapStateManager mgr = new MapStateManager(this, usersFileName);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            Toast.makeText(this, "entering Resume State", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(update);

            mMap.setMapType(mgr.getSavedMapType());
            mgr.loadPolyListFromState();
            ArrayList<PolyLineData> newLines = mgr.getPolyLineList();
            PolyLineData newline;
            LatLng startLatLng;
            LatLng endLatLng;
            for(int i = 0; i < newLines.size(); i++){
                newline =  newLines.get(i);
                startLatLng = newline.getStartlocation();
                endLatLng = newline.getEndlocation();
                mMap.addPolyline(new PolylineOptions().add(endLatLng, startLatLng));
            }
        }

    }

}
