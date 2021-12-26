package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnImageListener {

    private RecyclerView recyclerView;
    private ArrayList<StorageReference> images;
    private RecyclerAdapter adapter;
    private ShimmerFrameLayout container;
    private static final String spFileKey = "WallisWall.SECRET_FILE";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementsUseOverlay(false);

        super.onCreate(savedInstanceState);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        setContentView(R.layout.activity_main);

        images = new ArrayList<>();
        container = findViewById(R.id.shimmerContainer);
        recyclerView = findViewById(R.id.recyclerView);

        SharedPreferences sharedPreferences = getSharedPreferences(spFileKey, MODE_PRIVATE);
        boolean isNight = sharedPreferences.getBoolean("isNight", false);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(images, this);
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.INVISIBLE);
        container.startShimmer();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("Wallpapers");

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    // All the items under listRef.
                    images.addAll(listResult.getItems());
                    container.stopShimmer();
                    container.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Uh-oh, an error occurred!
                    container.stopShimmer();
                    container.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public void onImageClick(int position) {
        StorageReference sr = images.get(position);
        Intent i = new Intent(getApplicationContext(), Wallpaper.class);
        i.putExtra("StorageRef", sr.getPath());
        i.putExtra("Position", position);
        View startView = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position)).itemView;
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, startView, ViewCompat.getTransitionName(startView));
        startActivity(i, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.darkToggle) {
            SharedPreferences sharedPreferences = getSharedPreferences(spFileKey, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean isNight = sharedPreferences.getBoolean("isNight", false);
            if (isNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isNight", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isNight", true);
            }
            editor.apply();
        }
        return true;
    }
}