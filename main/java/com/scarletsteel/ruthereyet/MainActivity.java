package com.scarletsteel.ruthereyet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
        , LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationListener ll;
    private Location mLastLocation;
    private LocationManager locationManager;
    private Location iDestination;
    private double iLat = 74;//iDestination.getLatitude();
    private double iLong = 74;//iDestination.getLongitude();
    private double limit;
    private boolean trigger;
    public static final String PREFS = "MyPrefsFile";


    private GoogleApiClient client;


    TextView myText;
    Button myButton;
    ImageView myImage;
    TextView routeText;
    TextView stopText;
    Context mContext;
    public final static String EXTRA_MESSAGE = "this is the message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        myText = (TextView) findViewById(R.id.textView);
        myButton = (Button) findViewById(R.id.myButton);
        routeText = (TextView) findViewById(R.id.route);
        stopText = (TextView) findViewById(R.id.stop);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    //.addConnectionCallbacks(this)
                    //.addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        String message = routeText.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putString("route", message);
        editor.commit();


        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public ArrayList<String> getRoutes() {
        //Query database
        ArrayList<String> routes = new ArrayList<String>();
        routes.add("A");
        routes.add("B");
        routes.add("C");
        return routes;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        String route = prefs.getString("route", "No route defined");//"No name defined" is the default value.
        routeText.setText(route);
        System.out.println("Set routeText to " + route);
        String stop = prefs.getString("stop", "No stop defined!"); //0 is the default value.
        stopText.setText(stop);
        int titleID = myText.getId();

        final ArrayList<String> routeList = getRoutes();
        final ArrayList<Button> routeButtons = new ArrayList<Button>();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_main);

        for (int i = 0; i < routeList.size(); i++) {
            Button temp = new Button(this);
            temp.setText(routeList.get(i));
            temp.setId(i+1);

            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                params.addRule(RelativeLayout.BELOW, titleID);
            } else {
                params.addRule(RelativeLayout.BELOW, i);
            }
            temp.setLayoutParams(params);
            routeButtons.add(temp);

            temp.setOnClickListener(new myOnClickListener(i, routeButtons, routeText, mContext) {

            });

            layout.addView(temp);
        }



        mGoogleApiClient.connect();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        trigger = true;
        checkDistance();
        createLocationRequest();
        onConnect(ll);



    }

    protected void createLocationRequest() {
        //Create location request
        System.out.println("It 5ran");
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

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
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
        System.out.println("It ran");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public double checkDistance() {
        double mLat = 0, mLong = 0;
        System.out.println("It4 ran");
        if (mLastLocation != null) {
            System.out.println("It 3ran");
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

    public double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }


    @Override
    public void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
