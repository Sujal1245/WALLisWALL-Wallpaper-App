package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private final ArrayList<StorageReference> images;
    private final OnImageListener mOnImageListener;
    private final int widthPixels;

    public RecyclerAdapter(ArrayList<StorageReference> images, OnImageListener onImageListener, int widthPixels) {
        this.images = images;
        this.mOnImageListener = onImageListener;
        this.widthPixels = widthPixels;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout, parent, false);
        return new ImageViewHolder(view, mOnImageListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.itemView.getLayoutParams().width = (widthPixels / 2) - 28;

        StorageReference sr = images.get(position);
        RequestBuilder<Drawable> requestBuilder= GlideApp.with(holder.itemView.getContext())
                .asDrawable().sizeMultiplier(0.1f);
        GlideApp.with(holder.itemView.getContext())
                .load(sr)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(requestBuilder)
                .placeholder(R.drawable.background_splash)
                .into(holder.album);
        holder.albumTitle.setText(giveName(sr.getName()));

        ViewCompat.setTransitionName(holder.itemView, "Wall" + (position + 1) + "_Transition");
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public String giveName(String file_name) {
        return file_name.substring(0, file_name.lastIndexOf('.'));
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView album;
        final TextView albumTitle;
        final OnImageListener onImageListener;

        public ImageViewHolder(@NonNull View itemView, OnImageListener onImageListener) {
            super(itemView);

            this.album = itemView.findViewById(R.id.album);
            this.albumTitle = itemView.findViewById(R.id.album_title);
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