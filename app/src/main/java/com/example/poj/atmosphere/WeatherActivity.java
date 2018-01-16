package com.example.poj.atmosphere;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

public class WeatherActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView cityField, detailsField, currentTempField, humidityField, pressureField, weatherIcon, updatedField;

    Double Lat, Long;
    String Latitude, Longitude;

    Typeface weatherFont;
    Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );

    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    private static final int PLACE_PICKER_REQUEST = 1;

    private TextView theName;
    private TextView theAddress;
    private TextView theAttribution;

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final String TAG = "Location: ";

    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_weather);

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Button locButton = (Button)findViewById(R.id.locButton);

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    builder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Log.d("statusTag", "STATUS: Placepicker default location set ");
                    Intent intent = builder.build(WeatherActivity.this);

                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    Log.d("statusTag", "STATUS: Placepicker activity started ");

                }
                catch
                        (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    Log.d("errorTag", "ERROR: Error has been found and caught");
                }
            }
        });

        //setWeatherStats();

    }

    protected void getLocation() {
        locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        // Try to get last known location
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if(location != null) {
            Log.d(TAG, "GPS is turned on! ");
            Lat = location.getLatitude();
            Long = location.getLongitude();
            Log.d(TAG, "Latitude and Longitude: " + Lat + " and " + Long);

        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Lat = location.getLatitude();
            Long = location.getLongitude();
        }

        Log.d(TAG, "Latitude and Longitude: " + Lat + " and " + Long);

        setWeatherStats();
    }

    protected void setWeatherStats() {
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTempField = (TextView)findViewById(R.id.current_temperature_field);
        humidityField = (TextView)findViewById(R.id.humidity_field);
        pressureField = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        Functions.placeIdTask asyncTask = new Functions.placeIdTask(new Functions.AsyncResponse() {
            public void processFinish(String weatherCity, String weatherDescription, String weatherTemp,
                                      String weatherHumidity, String weatherPressure, String weatherUpdatedOn,
                                      String weatherIconText, String sunRise) {

                cityField.setText(weatherCity);
                updatedField.setText(weatherUpdatedOn);
                detailsField.setText(weatherDescription);
                currentTempField.setText(weatherTemp);
                humidityField.setText("Humidity: " +weatherHumidity);
                pressureField.setText("Pressure: " +weatherPressure);
                weatherIcon.setText(Html.fromHtml(weatherIconText));
            }
        });

        //Latitude = String.valueOf(mCurrentLocation.getLatitude());
        //Longitude = String.valueOf(mCurrentLocation.getLongitude());
        Latitude = String.valueOf(Lat);
        Longitude = String.valueOf(Long);

        Log.d(TAG, "Latitude and Longitude: " + Latitude + Longitude);

        //asyncTask.execute("44.5646", "-123.2620"); // Latitude and Longitude
        asyncTask.execute(Latitude, Longitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted for location, run the rest of the app
                    getLocation();
                    startLocationUpdates();
                }
                else {
                    // Permission was denied, exit app
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart..........");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop............");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            Log.d(TAG, "Asking for permissions first..........");
        }

        Log.d(TAG, "onConnected - isConnected " + mGoogleApiClient.isConnected());
    }

    protected void startLocationUpdates() {

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location updating..........");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Starting onLocationChanged..........");
        mCurrentLocation = location;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped..........");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed..........");
        }
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == RESULT_OK) {

            final Place place = PlacePicker.getPlace(data, this);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getName();
            String theMsg = (String.format("Location: %s", place.getName()));
            if (attributions == null) {
                attributions = "";
            }

            theName.setText(name);
            theAddress.setText(address);
            theAttribution.setText(Html.fromHtml(attributions));

            Toast.makeText(this, theMsg, Toast.LENGTH_LONG).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}