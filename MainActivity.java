package com.scarletsteel.ruthereyet;

import java.sql.Timestamp;
import java.util.Date;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private LocationListener lListen;
    private Location mLastLocation;
    private LocationManager locationManager;
    private Location iDestination;
    private double iLat = 74;//iDestination.getLatitude();
    private double iLong = 74;//iDestination.getLongitude();
    private double limit = 1;
    private boolean trigger;

    TextView myText;
    Button myButton;
    ImageView myImage;
    public final static String EXTRA_MESSAGE = "this is the message";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            System.out.println("GoogleApiClient built.");
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myText = (TextView) findViewById(R.id.textView);
        myButton = (Button) findViewById(R.id.myButton);
        myImage = (ImageView) findViewById(R.id.logo);
    }

    @Override
    public void onStart() {
        System.out.println("Starting...");
        System.out.println("Connecting...");
        mGoogleApiClient.connect();
        super.onStart();

        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("Permissions Invalid.");
            return;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("Connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void createLocationRequest() {
        //Create location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setMaxWaitTime(10000);
        mLocationRequest.setInterval(5000 /* (long)checkDistance()*10 */);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

    }

//    public void onConnect(LocationListener ConnectionHint) {
//        System.out.println("cnnected");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (trigger) {
//            startLocationUpdates();
//        }
//    }

    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            System.out.println("Requesting Location Update.");

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setFastestInterval(3000);
            mLocationRequest.setMaxWaitTime(10000);
            mLocationRequest.setInterval(5000 /* (long)checkDistance()*10 */);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            System.out.println(mLocationRequest);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if (checkDistance() < limit)
    }

    public void onLocationChanged(Location location)
    {
        System.out.println("Location Updated.");
        java.util.Date date = new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));
        mLastLocation = location;
        if (checkDistance() <= limit)
        {
            //do something
        }
    }

    public double checkDistance() {
        double mLat = 0, mLong = 0;

        System.out.println("Calculating Distance...");
        if (mLastLocation != null) {
            mLat = mLastLocation.getLatitude();
            mLong = mLastLocation.getLongitude();
        }
        int R = 6371; // Radius of the earth in km
        double dLat = (mLat - iLat) * (Math.PI / 180);  // deg2rad below
        double dLon = (mLong - iLong) * (Math.PI / 180);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(iLat * (Math.PI / 180)) * Math.cos(mLat * (Math.PI / 180)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d / 1000;
    }

    @Override
    public void onStop()
    {
        System.out.println("Stopping");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        System.out.println("Connection Suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        System.out.println("Connection Failed.");
        System.out.println(connectionResult);
    }
}
