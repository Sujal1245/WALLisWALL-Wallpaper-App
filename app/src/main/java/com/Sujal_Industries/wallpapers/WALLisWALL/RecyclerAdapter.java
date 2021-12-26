package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private final ArrayList<StorageReference> images;
    private final OnImageListener mOnImageListener;

    public RecyclerAdapter(ArrayList<StorageReference> images, OnImageListener onImageListener) {
        this.images = images;
        this.mOnImageListener = onImageListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout, parent, false);
        return new ImageViewHolder(view, mOnImageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        holder.itemView.getLayoutParams().width = (displayMetrics.widthPixels / 2) - 28;
        StorageReference sr = images.get(position);
        GlideApp.with(holder.itemView.getContext()).load(sr).thumbnail(0.2f).placeholder(R.drawable.ic_baseline_collections_24).into(holder.Album);
        holder.AlbumTitle.setText(giveName(position));

        ViewCompat.setTransitionName(holder.itemView, "Wall" + (position + 1) + "_Transition");
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static String giveName(int position) {
        return "Wall " + (position + 1);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView Album;
        final TextView AlbumTitle;
        final OnImageListener onImageListener;

        public ImageViewHolder(@NonNull View itemView, OnImageListener onImageListener) {
            super(itemView);
            Album = itemView.findViewById(R.id.album);
            AlbumTitle = itemView.findViewById(R.id.album_title);
            this.onImageListener = onImageListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onImageListener.onImageClick(getBindingAdapterPosition());
        }
    }

    public interface OnImageListener {
        void onImageClick(int position);
    }
}
