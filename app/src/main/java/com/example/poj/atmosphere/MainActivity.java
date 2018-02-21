package com.example.poj.atmosphere;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity  {

    // How long the screen will stay in milliseconds
    private int timeoutMillis = 1500;
    //private int timeoutMillis = 0;

    // Time when this link was created
    private long startTimeMillis = 0;

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private static final String TAG = "MA Location: ";
    private static final String M_TAG = "MainActivity: ";

    TextView logo;

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public Class getNextActivityClass() {
        return WeatherActivity.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        logo = findViewById(R.id.logo_icon);
        logo.setTypeface(fontAwesomeFont);

        startTimeMillis = System.currentTimeMillis();

        if(Build.VERSION.SDK_INT >= 23) {
            Log.d(M_TAG, "Build version SDK > 23, calling to ask permissions. ");
            askPermission();
        }
        else {
            goToNextActivity();
        }

    }

    public void askPermission() {
        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            Log.d(M_TAG, "Asking for permissions first..........");
        }
        else {
            Log.d(M_TAG, "Permissions already granted, moving on ");
            goToNextActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted for location, run the rest of the app
                    Log.d(M_TAG, "Permission already granted, calling goToNextActivity() ");
                    goToNextActivity();
                }
                else {
                    // Permission was denied, exit app
                    finish();
                }
                return;
            }
        }
    }

    /*
     *
     * This is thanks to http://pcessflight.com/smart-android-splash-screen-grabbing-permissions/
     *
     */
    private void goToNextActivity() {
        Log.d(M_TAG, "goToNextActivity() Called! ");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

        long delayMillis = getTimeoutMillis() - (System.currentTimeMillis() - startTimeMillis);
        if (delayMillis < 0) {
            delayMillis = 0;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, getNextActivityClass());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                finish();
            }
        }, delayMillis);

    }
}
