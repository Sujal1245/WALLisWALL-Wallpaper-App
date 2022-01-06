package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class Wallpaper extends AppCompatActivity {

    private ImageView wall;
    private CircularProgressIndicator loadIn;
    private ExtendedFloatingActionButton applyWall;
    private Bitmap wallpaper;
    private WallpaperManager wallpaperManager;
    private int flag = 0;

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

        wall = findViewById(R.id.wall);

        loadIn = findViewById(R.id.loadingIndicator);
        loadIn.hide();

        StorageReference wallRef = FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("StorageRef"));

        GlideApp.with(getApplicationContext()).load(wallRef).into(wall);

        applyWall = findViewById(R.id.applyWall);
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

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); //create a new one
            layoutParams.weight = 1.0f;
            layoutParams.gravity = Gravity.CENTER; //this is layout_gravity
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(layoutParams);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setLayoutParams(layoutParams);


            applyWall.shrink();
            loadIn.show();
        });
    }

    public void doStuffInBackground()
    {
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
            case 123: {
                setOnHome();
                break;
            }
            case 456: {
                setOnLock();
                break;
            }
            case 789: {
                setOnBoth();
                break;
            }
            default: {
                setOnBoth();
            }
        }
    }
}