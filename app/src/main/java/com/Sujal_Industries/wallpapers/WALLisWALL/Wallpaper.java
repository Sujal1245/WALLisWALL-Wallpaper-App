package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class Wallpaper extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        boolean isNight = getIntent().getBooleanExtra("isNight", false);
        if (isNight) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navColorDark));
        } else {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navColorLight));
        }

        int position = getIntent().getIntExtra("Position", 0);
        View endView = findViewById(R.id.linearL);
        ViewCompat.setTransitionName(endView, "Wall" + (position + 1) + "_Transition");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementEnterTransition(new MaterialContainerTransform().addTarget(R.id.linearL).setDuration(300L));
        getWindow().setSharedElementReturnTransition(new MaterialContainerTransform().addTarget(R.id.linearL).setDuration(250L));

        ImageView wall = findViewById(R.id.wall);

        CircularProgressIndicator loadIn = findViewById(R.id.loadingIndicator);
        loadIn.hide();

        StorageReference wallRef = FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("StorageRef"));

        GlideApp.with(getApplicationContext()).load(wallRef).into(wall);

        ExtendedFloatingActionButton applyWall = findViewById(R.id.applyWall);
        applyWall.setOnClickListener(v -> {
            applyWall.shrink();
            loadIn.show();
            new Thread(() -> {
                // Run whatever background code you want here.
                Bitmap wallpaper = ((BitmapDrawable) wall.getDrawable()).getBitmap();
                WallpaperManager m = WallpaperManager.getInstance(getApplicationContext());
                try {
                    m.setBitmap(wallpaper);
                    Snackbar.make(findViewById(R.id.linearL), "Wall set successfully!", Snackbar.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Wallpaper.this.runOnUiThread(loadIn::hide);
                Wallpaper.this.runOnUiThread(applyWall::extend);
            }).start();
        });
    }
}