package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnImageListener {

    private RecyclerView recyclerView;
    private ArrayList<StorageReference> images;
    private RecyclerAdapter adapter;
    private ShimmerFrameLayout container;
    private NavigationBarView bottomNavBar;
    static final String spFileKey = "WallisWall.SECRET_FILE";
    private FavouritesHelper helper;
    private SharedPreferences sharedPreferences;
    private int lastPage;
    private boolean needToAnimate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementsUseOverlay(false);

        super.onCreate(savedInstanceState);

        SplashScreen.installSplashScreen(this);

        setContentView(R.layout.activity_main);

        manageConnectivity();

        images = new ArrayList<>();
        container = findViewById(R.id.shimmerContainer);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavBar = findViewById(R.id.bottom_navigation);

        sharedPreferences = getSharedPreferences(spFileKey, MODE_PRIVATE);
        helper = new FavouritesHelper(sharedPreferences);
        boolean isNight = sharedPreferences.getBoolean("isNight", false);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(images, this, getScreenWidthPixels(MainActivity.this));
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.INVISIBLE);
        container.startShimmer();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("Wallpapers");

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    // All the items under listRef.
                    container.stopShimmer();
                    completeLoading(listResult);

                    bottomNavBar.setOnItemSelectedListener(item -> {
                        int itemId = item.getItemId();
                        if (itemId == R.id.main_page) {
                            setUpMainPage(listResult);
                        } else if (itemId == R.id.fav_page) {
                            setUpFavPage();
                        } else if (itemId == R.id.set_page) {
                            setUpSettingsPage();
                        } else {
                            return false;
                        }
                        return true;
                    });
                })
                .addOnFailureListener(e -> {
                    // Uh-oh, an error occurred!
                    container.stopShimmer();
                    container.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshRecyclerView() {
        if (sharedPreferences.getBoolean("Shuffle?", false)) {
            Collections.shuffle(images);
        }
        adapter.notifyDataSetChanged();
        if (needToAnimate) {
            animateRecyclerView();
        }
    }


    public void completeLoading(ListResult listResult) {
        images.addAll(listResult.getItems());

        container.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        bottomNavBar.setVisibility(View.VISIBLE);

        refreshRecyclerView();
    }

    public void setUpMainPage(ListResult listResult) {
        lastPage = 0;
        images.clear();
        images.addAll(listResult.getItems());
        refreshRecyclerView();
    }

    public void setUpFavPage() {
        lastPage = 1;
        int i = 0;
        HashMap<String, Boolean> favs = helper.getFavourites();
        ArrayList<StorageReference> nonFavs = new ArrayList<>();
        for (StorageReference image : images) {
            String wall_name = image.getName();
            //noinspection ConstantConditions
            boolean fav = favs.containsKey(wall_name) ? favs.get(wall_name) : false;
            if (!fav) {
                nonFavs.add(images.get(i));
            }
            i++;
        }
        images.removeAll(nonFavs);
        refreshRecyclerView();
        if (images.isEmpty()) {
            Toast.makeText(this, "Nothing here yet :)", Toast.LENGTH_SHORT).show();
        }
    }

    public void setUpSettingsPage() {
        SettingsBottomSheet settingsBottomSheet = new SettingsBottomSheet();
        settingsBottomSheet.show(getSupportFragmentManager(), SettingsBottomSheet.TAG);
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

    public void animateRecyclerView() {
        recyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            View v = recyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .setStartDelay(i * 101L)
                                    .start();
                        }

                        return true;
                    }
                });
    }

    public static int getScreenWidthPixels(AppCompatActivity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.getDisplay().getRealMetrics(displayMetrics);
        } else {
            context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;
    }

    public static boolean isConnectingToInternet(Context mContext) {
        if (mContext == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    final NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(network);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            } else {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                for (NetworkInfo tempNetworkInfo : networkInfos) {
                    if (tempNetworkInfo.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void manageConnectivity() {
        if (!isConnectingToInternet(this)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Unable to connect :(")
                    .setMessage("Looks like you aren't connected to internet. Want to retry?")
                    .setPositiveButton("RETRY", (dialogInterface, i) -> manageConnectivity())
                    .setCancelable(false)
                    .show();
        }
    }

    public void manageBottomNav() {
        int id = 123;
        switch (lastPage) {
            case 0 -> id = R.id.main_page;
            case 1 -> id = R.id.fav_page;
        }
        needToAnimate = false;
        bottomNavBar.setSelectedItemId(id);
        needToAnimate = true;
    }
}