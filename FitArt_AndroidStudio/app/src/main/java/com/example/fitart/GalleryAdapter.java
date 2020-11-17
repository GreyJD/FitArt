package com.example.fitart;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Set;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyMapViewHolder> {
    private String[] prefList;
    private double[] distanceList;


    public static class MyMapViewHolder extends RecyclerView.ViewHolder {

        public TextView prefName;

        public TextView distanceNumView;
        public View mView;

        public MyMapViewHolder(View v) {
            super(v);
            prefName = v.findViewById(R.id.card_name);
            distanceNumView = v.findViewById(R.id.card_distance_num);
            mView = v;

        }

    }
    public GalleryAdapter(String[] list, double[] dist){
            prefList = list;
            distanceList = dist;
    }

    @Override
    public GalleryAdapter.MyMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_cardview, parent, false);
        MyMapViewHolder vh = new MyMapViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull MyMapViewHolder holder, final int position) {
        holder.mView.setLongClickable(true);
        String doubleString = Double.toString(distanceList[position]);
        holder.distanceNumView.setText(doubleString);
        holder.prefName.setText(prefList[position]);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra(MapRecordingActivity.EXTRA_MESSAGE, prefList[position]);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return prefList.length;
    }

}

