package com.flysolo.mistervapeshop.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.flysolo.mistervapeshop.MainActivity;
import com.flysolo.mistervapeshop.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            Intent intent  = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        },2000);
    }
}