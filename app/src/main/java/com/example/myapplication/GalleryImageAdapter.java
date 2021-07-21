package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ViewHolder> {

    private final List<String> fileList;
    private final Activity activity;

    public GalleryImageAdapter(Activity activity, List<String> fileList) {
        this.activity = activity;
        this.fileList = fileList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected final ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.images);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(activity)
                .load(fileList.get(position))
                .override(200, 200)
                .centerCrop()
                .into(holder.image);
        final int itemPosition = holder.getAdapterPosition();
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, fileList.get(itemPosition),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
