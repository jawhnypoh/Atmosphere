//package com.example.poj.atmosphere;
//
///**
// * Created by poj on 1/1/18.
// */
//
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.location.places.Places;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//
//public class ClientActivity extends FragmentActivity
//        implements OnConnectionFailedListener {
//    private GoogleApiClient mGoogleApiClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//    }
//}
