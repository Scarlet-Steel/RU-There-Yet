package hru.myapplication;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public abstract class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
        , LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {//FragmentActivity

    //private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationListener ll;
    private Location mLastLocation;
    private LocationManager locationManager;
    private Location iDestination;
    private double iLat = iDestination.getLatitude();
    private double iLong = iDestination.getLongitude();
    private double limit;
    private boolean trigger;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    protected void onCreate() {
        //super.onCreate(savedInstanceState);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /*setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    //.addConnectionCallbacks(this)
                    //.addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        onCreate();
        mGoogleApiClient.connect();
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        trigger = true;
        createLocationRequest();
        onConnect(ll);
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled((true));//display user location
        }
        else
        {

        }

        // Add a marker in Sydney and move the camera
        LatLng rutgers = new LatLng(40.50, -74.45);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rutgers));
    }*/
    protected void createLocationRequest() {
        //Create location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setMaxWaitTime(100000);
        mLocationRequest.setInterval(5000 /* (long)checkDistance()*10 */);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
    }

    public void onConnect(LocationListener ConnectionHint) {
        if (trigger) {
            startLocationUpdates();
        }
        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, ll);
        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if (checkDistance() < limit)
        {
            //vibrate or something idk
        }
    }

    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (checkDistance() < limit) {
            //do something
        }
        createLocationRequest();
        onConnect(ll);
    }

    public double checkDistance() {
        double mLat = 0, mLong = 0;
        if (mLastLocation != null) {
            mLat = mLastLocation.getLatitude();
            mLong = mLastLocation.getLongitude();
        }
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(mLat - iLat);  // deg2rad below
        double dLon = deg2rad(mLong - iLong);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(iLat)) * Math.cos(deg2rad(mLat)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d / 1000;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
