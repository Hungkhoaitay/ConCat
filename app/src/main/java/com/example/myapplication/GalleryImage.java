package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryImage extends RecyclerView.Adapter<GalleryImage.ViewHolder> {

    String data[];
    Context context;

    public GalleryImage(Context context, String[] data) {
        this.data = data;
        this.context = context;
    }
    @Override
    public GalleryImage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.access_gallery, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
        // create view holder
    }

    @Override
    public void onBindViewHolder(GalleryImage.ViewHolder holder, int position) {
        // insert data into the view

    }

    @Override
    public int getItemCount() {
        return data.length;
        // create the number of view
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
