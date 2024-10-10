package com.example.chats.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chats.R;
import com.example.chats.Utils.NetworkUtil;

public class SplashScreen extends AppCompatActivity {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String IS_FIRST_LAUNCH = "isFirstLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (NetworkUtil.isConnectedToInternet(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigateToNextScreen();
                }
            }, 3000);
        } else {
            showNoInternetDialog();
        }
    }

    private void navigateToNextScreen() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true);

        if (isFirstLaunch) {
            sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, false).apply();
            Intent intent = new Intent(SplashScreen.this, PhoneNumber.class);
            startActivity(intent);
        } else {
            if (isLoggedIn()) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SplashScreen.this, PhoneNumber.class);
                startActivity(intent);
            }
        }
        finish();
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Connect to a network")
                .setMessage("To use chats application, turn on mobile data or connect to Wi-Fi")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> {
                    if (NetworkUtil.isConnectedToInternet(this)) {
                        recreate();
                    } else {
                        showNoInternetDialog();
                    }
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .show();
    }
}
