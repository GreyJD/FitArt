package com.example.fitart;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyMapViewHolder> {
    private String[] prefList;

    public static class MyMapViewHolder extends RecyclerView.ViewHolder {

        public TextView prefName;
        public TextView distance;
        public TextView distanceNum;

        public MyMapViewHolder(View v) {
            super(v);
            prefName = v.findViewById(R.id.card_name);
        }
    }
    public GalleryAdapter(String[] list){
            prefList = list;
    }

    @Override
    public GalleryAdapter.MyMapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_cardview, parent, false);
        MyMapViewHolder vh = new MyMapViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyMapViewHolder holder, int position) {
        holder.prefName.setText(prefList[position]);
    }

    @Override
    public int getItemCount() {
        return prefList.length;
    }

}

