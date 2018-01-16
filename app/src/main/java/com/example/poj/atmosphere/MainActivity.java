package com.example.poj.atmosphere;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity  {

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private static final String TAG = "Location: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayShowTitleEnabled(false);

    }

    public void askPermission() {
        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            Log.d(TAG, "Asking for permissions first..........");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted for location, run the rest of the app
                    /* Run functions from WeatherActivity */
                    //getLocation();
                    //startLocationUpdates();
                }
                else {
                    // Permission was denied, exit app
                    finish();
                }
                return;
            }
        }
    }

}
