package com.example.fitart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {

    private ArrayList<RVCard> mStatsList;

    public static class RVViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public RVViewHolder(View itemView)
        {
            super(itemView);
            mImageView = itemView.findViewById(R.id.card_image_view);
            mTextView1 = itemView.findViewById(R.id.card_line_one);
            mTextView2 = itemView.findViewById(R.id.card_line_two);
        }
    }

    public RVAdapter(ArrayList<RVCard> statsList)
    {
        mStatsList = statsList;
    }
    @NonNull
    @Override
    public RVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        RVViewHolder rvVH = new RVViewHolder(v);
        return rvVH;
    }

    @Override
    public void onBindViewHolder(@NonNull RVViewHolder holder, int position)
    {
        RVCard currentItem = mStatsList.get(position);
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
    }

    @Override
    public int getItemCount() {
        return mStatsList.size();
    }
}
