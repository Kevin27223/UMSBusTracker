package com.example.cheeseng.umsbustracker;

import com.example.cheeseng.umsbustracker.WebConnector.Bus;
import com.example.cheeseng.umsbustracker.WebConnector.BusStop;
import com.example.cheeseng.umsbustracker.WebConnector.Route;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnMyLocationButtonClickListener,
        OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener,
        Bus.AsyncResponse,AdapterView.OnItemSelectedListener{

    private static final String key = "AIzaSyB_yYif1SN20yrJhNOuVEX62aiaLarvWOs";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    GoogleApiClient mGoogleApiClient;
    Marker[] mCurrLocationMarker = new Marker[10];
    private Bus bus;
    private double latitude, longitude;
    private TextView eta;
    int currentRouteId = 1;
    
    public MainActivity(){}

    @Override
    public void setLatLang(double x, double y){
        latitude = x;
        longitude = y;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eta = (TextView) findViewById(R.id.eta_value);

        // Drop-down menu
        Spinner spinner = (Spinner) findViewById(R.id.route);
        spinner.setOnItemSelectedListener(this);

        //Populate spinner menu
        Route route = new Route(this, spinner);
        route.execute();

        //Navigation Menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Button button = (Button) findViewById(R.id.track_bus);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                LatLng busLatLng = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(busLatLng)
                        .zoom(18)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

                String msg = "Latitude:" + latitude + "; Longitude:" + longitude;
                Log.i("Pressed", msg);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     *  Spinner menu (drop down menu)
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        //String item = parent.getItemAtPosition(position).toString();

        // TODO: changing route and buses based on selection
        switch(position){
            case 0:
                currentRouteId = 1;
                //Toast.makeText(parent.getContext(), "Current route_id: " + currentRouteId, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                currentRouteId = 2;
                break;
            case 2:
                currentRouteId = 3;
                break;
            case 3:
                currentRouteId = 4;
                break;
        }
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     *  Navigation menu
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_route) {
            Intent intent = new Intent(MainActivity.this, RouteActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_favourite){

        }
        else if (id == R.id.nav_schedule){
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_announcement) {
            Intent intent = new Intent(MainActivity.this, AnnouncementActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setPadding(0,0,0,300);
        mMap.setOnMyLocationButtonClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        enableMyLocation();

        //Populate bus stop location
        BusStop busStop = new BusStop(mMap);
        busStop.execute();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = null;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LatLng userLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18.0f));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            LatLng initialLatLng = new LatLng(6.041061, 116.128084);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(initialLatLng);
            markerOptions.title("Bus");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus));
            for(int i=0; i<mCurrLocationMarker.length; i++) {
                mCurrLocationMarker[i] = mMap.addMarker(markerOptions);
            }
        }

        if (mCurrentLocation != null) {
            // Print current location if not null
            Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        long UPDATE_INTERVAL = 60 * 1000;  /* 60 secs */
        long FASTEST_INTERVAL = 30 * 1000; /* 30 sec */

        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        // Request location updates
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        bus = new Bus(this, mCurrLocationMarker, currentRouteId, eta, location.getLatitude(), location.getLongitude(), key);
        bus.delegate = this;
        bus.execute();

        /*String msg = "Latitude:" + latitude + "; Longitude:" + longitude;
        Log.i("Change", msg);*/
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    protected void onDestroy() {
        // Disconnecting the client invalidates it.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    /*protected void onStop() {
        // Disconnecting the client invalidates it.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    protected void onRestart(){
        buildGoogleApiClient();
        super.onRestart();
    }*/
}