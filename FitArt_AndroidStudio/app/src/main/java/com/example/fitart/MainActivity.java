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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a shared preference file to store saved runs
       // Set<String> testSet = new HashSet<String>();
      //  testSet.add("test1");
      //  testSet.add("test2");

        SharedPreferences pref = getSharedPreferences(savedMapsfile, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = pref.edit();
        //editor.putStringSet("FILE_NAMES", testSet);
       // editor.commit();
        //Connect variables to layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        press_to_start = findViewById(R.id.button_start);
        gallery_button = findViewById(R.id.button_archive);

        ///Populate Recycler View///
        ArrayList<RVCard> statsList = new ArrayList<>();
        statsList.add(new RVCard(R.drawable.ic_runner, "Total Distance", "0.0 miles")); //Distance card
        statsList.add(new RVCard(R.drawable.ic_clock, "Total Time", "0 hrs, 0 mins, 0 secs")); //Time in app card
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