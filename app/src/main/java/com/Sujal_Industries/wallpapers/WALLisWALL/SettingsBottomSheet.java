package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsBottomSheet extends BottomSheetDialogFragment {
    static final String TAG = "SettingsBottomSheet";
    private SharedPreferences sharedPreferences;
    private FavouritesHelper helper;
    private boolean isNight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_bottom_sheet, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(MainActivity.spFileKey, MainActivity.MODE_PRIVATE);
        helper = new FavouritesHelper(sharedPreferences);
        ChipGroup themeToggle = v.findViewById(R.id.themeToggle);
        Chip darkChip = v.findViewById(R.id.turnDark);
        Chip lightChip = v.findViewById(R.id.turnLight);
        MaterialButton clearFavsButton = v.findViewById(R.id.clear_fav_button);
        MaterialButtonToggleGroup shuffleToggle = v.findViewById(R.id.setting3_toggle);
        MaterialButton setting3_on = v.findViewById(R.id.setting3_on);
        MaterialButton setting3_off = v.findViewById(R.id.setting3_off);

        isNight = sharedPreferences.getBoolean("isNight", false);
        if (isNight) {
            darkChip.setChecked(true);
        } else {
            lightChip.setChecked(true);
        }
        //Setting(1) below
        themeToggle.setOnCheckedStateChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (!isNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isNight", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isNight", false);
            }
            editor.apply();
        });

        //Setting(2) below
        clearFavsButton.setOnClickListener(view -> {
            AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Clear Favourites?")
                    .setMessage("Note: You won't be able to undo this task.")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        helper.clearFavs();
                        Toast.makeText(requireActivity().getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                    })
                    .create();
            dialog.show();
        });

        //Setting(3) below

        boolean currentShuffle = sharedPreferences.getBoolean("Shuffle?", false);

        if (currentShuffle) {
            setting3_on.setChecked(true);
        } else {
            setting3_off.setChecked(true);
        }

        shuffleToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Shuffle?", checkedId == R.id.setting3_on);
                editor.apply();
            }
        });

        return v;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        ((MainActivity) requireActivity()).manageBottomNav();
    }
}
