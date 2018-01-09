package com.example.poj.atmosphere;

import android.app.Activity;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements OnConnectionFailedListener {

    TextView cityField, detailsField, currentTempField, humidityField, pressureField, weatherIcon, updatedField;

    Typeface weatherFont;

    private static final int PLACE_PICKER_REQUEST = 1;

    private TextView theName;
    private TextView theAddress;
    private TextView theAttribution;
    private GoogleApiClient mGoogleApiClient;


    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // Construct a GoogleApiClient
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }

        Button locButton = (Button)findViewById(R.id.locButton);

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    builder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Log.d("statusTag", "STATUS: Placepicker default location set ");
                    Intent intent = builder.build(MainActivity.this);

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

            asyncTask.execute("44.5646", "-123.2620"); // Latitude and Longitude
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("errorTag", "Location Connection Failed. ");
    }
}
