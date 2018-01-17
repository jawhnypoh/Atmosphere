package com.example.poj.atmosphere;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity  {

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;
    private static final String TAG = "Location: ";

    public String theCityName;

    Double Lat, Long;
    String Latitude, Longitude;

    EditText cityInput;
    Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        enterButton = findViewById(R.id.enterButton);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to next activity, pass cityName
                theCityName = cityInput.getText().toString();

                Toast.makeText(MainActivity.this, theCityName, Toast.LENGTH_LONG).show();

                goToNextActivity();
            }
        });

        askPermission();

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

//    public void getLatLongFromPlace(String place) {
//        try {
//            Geocoder selected_pacakge_geocoder = new Geocoder(context);
//            List<Address> address;
//
//            address = selected_pacakge_geocoder.getFromLocation(place, 5);
//
//            if(address == null) {
//                d.dismiss;
//            }
//            else {
//                Address location = address.get(0);
//                    Lat = location.getLatitude();
//                    Long = location.getLongitude();
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private void goToNextActivity() {
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
    }

}
