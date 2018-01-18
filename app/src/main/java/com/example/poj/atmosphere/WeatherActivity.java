package com.example.poj.atmosphere;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
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

public class WeatherActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView cityField, detailsField, currentTempField, humidityField, pressureField, weatherIcon, updatedField;

    Double Lat, Long;
    String Latitude, Longitude;

    Typeface weatherFont;

    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    private static final int PLACE_PICKER_REQUEST = 1;

    private TextView theName;
    private TextView theAddress;
    private TextView theAttribution;

    private static final int PERMISSIONS_REQUEST_LOCATION = 100;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final String TAG = "Location: ";
    private static final String PPTAG = "PlacePicker: ";

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

        setContentView(R.layout.activity_weather);

        createLocationRequest();

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        Button locButton = findViewById(R.id.locButton);
        Button aboutButton = findViewById(R.id.aboutButton);

        locButton.setTypeface(fontAwesomeFont);
        aboutButton.setTypeface(fontAwesomeFont);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Go to About activity
                goToAboutActivity();
            }
        });

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // By default, PlacePicker starts at device's current location
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Log.d(PPTAG,    "Place Picker Started...Device location chosen");
                    Intent intent = builder.build(WeatherActivity.this);

                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                }
                catch
                        (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    Log.d(PPTAG, "Error caught ");
                }
            }
        });

        getLocation();
        setWeatherStats();

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
            Log.d(TAG, "getLastKnownLocation - Latitude and Longitude: " + Lat + " and " + Long);

        }
        else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Lat = location.getLatitude();
            Long = location.getLongitude();
            Log.d(TAG, "requestLocationUpdates - Latitude and Longitude: " + Lat + " and " + Long);

        }

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

        Latitude = String.valueOf(Lat);
        Longitude = String.valueOf(Long);

        Log.d(TAG, "setWeatherStats - Latitude and Longitude: " + Latitude + Longitude);

        asyncTask.execute(Latitude, Longitude);
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

        Log.d(PPTAG, "requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == RESULT_OK) {

            final Place place = PlacePicker.getPlace(data, this);

            if(place == null) {
                Log.d(PPTAG, "PlacePicker returned null! ");
            }

            final LatLng placeLatLng = place.getLatLng();
            Lat = placeLatLng.latitude;
            Long = placeLatLng.longitude;

            Latitude = String.valueOf(Lat);
            Longitude = String.valueOf(Long);

            Log.d(PPTAG, "PlacePicker LatLng: " + placeLatLng);

            setWeatherStats();

            //Toast.makeText(this, Latitude + ", " + Longitude, Toast.LENGTH_LONG).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void goToAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
