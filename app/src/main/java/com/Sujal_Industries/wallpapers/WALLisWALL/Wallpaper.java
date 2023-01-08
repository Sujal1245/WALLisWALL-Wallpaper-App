package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class Wallpaper extends AppCompatActivity {

    private View endView;
    private ImageView wall;
    private CircularProgressIndicator loadIn;
    private ExtendedFloatingActionButton applyWall;
    private Bitmap wallpaper;
    private WallpaperManager wallpaperManager;
    private int flag = 0;

    private static final String spFileKey = "WallisWall.SECRET_FILE";
    private boolean fav;
    private FavouritesHelper helper;
    private String wall_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        int position = getIntent().getIntExtra("Position", 0);
        endView = findViewById(R.id.linearL);
        ViewCompat.setTransitionName(endView, "Wall" + (position + 1) + "_Transition");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementEnterTransition(new MaterialContainerTransform().addTarget(R.id.linearL).setDuration(600L));
        getWindow().setSharedElementReturnTransition(new MaterialContainerTransform().addTarget(R.id.linearL).setDuration(500L));

        wall = findViewById(R.id.wall);
        loadIn = findViewById(R.id.loadingIndicator);
        applyWall = findViewById(R.id.applyWall);

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        helper = new FavouritesHelper(getSharedPreferences(spFileKey, MODE_PRIVATE));

        loadIn.hide();

        StorageReference wallRef = FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("StorageRef"));

        GlideApp.with(this)
                .load(wallRef)
                .placeholder(R.drawable.background_splash)
                .into(wall);

        wall_name = wallRef.getName();

        applyWall.setOnClickListener(v -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.MyMaterialAlertDialog)
                    .setTitle("Set Wall to....")
                    .setPositiveButton("Home Screen", (dialogInterface, i) -> {
                        flag = 123;
                        doStuffInBackground();
                    })
                    .setPositiveButtonIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_home_8))
                    .setNeutralButton("Lock Screen", (dialogInterface, i) -> {
                        flag = 456;
                        doStuffInBackground();
                    })
                    .setNeutralButtonIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_lock_8))
                    .setNegativeButton("Both Screens", (dialogInterface, i) -> {
                        flag = 789;
                        doStuffInBackground();
                    })
                    .setNegativeButtonIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_add_to_home_screen_8))
                    .create();

            dialog.show();

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1.0f;
            layoutParams.gravity = Gravity.CENTER;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(layoutParams);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setLayoutParams(layoutParams);

            applyWall.shrink();
            loadIn.show();
        });
    }

    public void doStuffInBackground() {
        new Thread(() -> {
            // Run whatever background code you want here.
            wallpaper = ((BitmapDrawable) wall.getDrawable()).getBitmap();
            wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            setWall();
            Wallpaper.this.runOnUiThread(loadIn::hide);
            Wallpaper.this.runOnUiThread(applyWall::extend);
        }).start();
    }

    public void setOnBoth() {
        try {
            wallpaperManager.setBitmap(wallpaper);
            Snackbar.make(findViewById(R.id.linearL), "Wall set successfully!", Snackbar.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnHome() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(wallpaper, null, true, WallpaperManager.FLAG_SYSTEM);
                Snackbar.make(findViewById(R.id.linearL), "Wall set on home!", Snackbar.LENGTH_SHORT).show();
            } else {
                setOnBoth();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnLock() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(wallpaper, null, true, WallpaperManager.FLAG_LOCK);
                Snackbar.make(findViewById(R.id.linearL), "Wall set on lock!", Snackbar.LENGTH_SHORT).show();
            } else {
                setOnBoth();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWall() {
        switch (flag) {
            case 123 -> setOnHome();
            case 456 -> setOnLock();
            case 789 -> setOnBoth();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one, menu);

        fav = helper.isFav(wall_name);

        if (fav) {
            menu.findItem(R.id.fav_toggle).setIcon(R.drawable.ic_baseline_favorite_24);
        } else {
            menu.findItem(R.id.fav_toggle).setIcon(R.drawable.ic_baseline_favorite_border_24);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fav_toggle) {
            String msg;
            if (fav) {
                helper.removeFav(wall_name);
                msg = "Wall removed from Fav :(";
            } else {
                helper.addFavourite(wall_name);
                msg = "Wall added to Fav :)";
            }
            Snackbar.make(endView, msg, BaseTransientBottomBar.LENGTH_SHORT).show();
            fav = !fav;
            invalidateOptionsMenu();
        }
        return true;
    }
}