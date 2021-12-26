package com.Sujal_Industries.wallpapers.WALLisWALL;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Hahahahaha");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
