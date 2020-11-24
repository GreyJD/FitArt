package com.example.fitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //next page
    private Button press_to_start;
    private Button gallery_button;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public final String savedMapsfile = "SAVED_ART";
    private SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        press_to_start = findViewById(R.id.button_start);
        gallery_button = findViewById(R.id.button_archive);

        prefs = getSharedPreferences("SAVED_ART", Context.MODE_PRIVATE);

        //Get time and distance from shared prefs
        Set<String> set = prefs.getStringSet("FILE_NAMES", null);
        String[] savedFileNames = set.toArray(new String[set.size()]);
        double totalDist = 0.0;
        long totalTime = 0;

        for(int i = 0; i < savedFileNames.length; i++ ) {
            MapStateManager state = new MapStateManager(this, savedFileNames[i]);
            totalDist += state.getMilesTravled();
            totalTime += state.getTimeTravled();
        }
        DecimalFormat df = new DecimalFormat("#.###");
        String stringDist = df.format(totalDist);
        long totalSeconds = totalTime/1000000000;
        long seconds = totalSeconds % 60;
        long hours = totalSeconds / 60;
        long mins = hours % 60;
        hours = hours / 60;
        String stringTime = ( hours + " hrs, " + mins + " mins, " + seconds + " secs");

        ///Populate Recycler View///
        ArrayList<RVCard> statsList = new ArrayList<>();
        statsList.add(new RVCard(R.drawable.ic_runner, "Total Distance", stringDist)); //Distance card
        statsList.add(new RVCard(R.drawable.ic_clock, "Total Time", stringTime)); //Time in app card
        ///Recycler View Setup ///
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RVAdapter(statsList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //Navigate to new page upon clicking Start
        press_to_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, MapRecordingActivity.class);
                startActivity(intent);
            }
        });
        gallery_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                //startSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}