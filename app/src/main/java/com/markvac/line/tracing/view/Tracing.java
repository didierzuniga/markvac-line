package com.markvac.line.tracing.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.markvac.line.R;
import com.markvac.line.customizer.History;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Tracing extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, LocationListener, GpsStatus.Listener {

    private GoogleApiClient apiClient;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private ArrayList<LatLng> points; //added
    private Polyline line; //added
    private ArrayList<String> arrCoords;
    private Set<String> set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_view));

        set = new HashSet<String>();
        shaPref = getSharedPreferences("myShared", MODE_PRIVATE);
        editor = shaPref.edit();
        points = new ArrayList<LatLng>();
        arrCoords = new ArrayList<String>();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationManager != null) {
            mLocationManager.addGpsStatusListener(this);
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0,
                    this);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite)); //Change menu hamburguer color
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tracing) {
            onStart();
        } else if (id == R.id.nav_history) {
            goHistory();
        } else if (id == R.id.nav_logout) {
//            goLogout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHistory() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }

    private void redrawLine(){
        mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < arrCoords.size(); i++) {
            String[] latlong =  arrCoords.get(i).split(", ");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            LatLng point = new LatLng(latitude, longitude);
            options.add(point);
        }
//        for (int i = 0; i < points.size(); i++) {
//            LatLng point = points.get(i);
//            options.add(point);
//        }

        //addMarker(); //add Marker in current position
        line = mMap.addPolyline(options);
    }

//    private void addMarker() {
//        MarkerOptions options = new MarkerOptions();
//
//        // following four lines requires 'Google Maps Android API Utility Library'
//        // https://developers.google.com/maps/documentation/android/utility/
//        // I have used this to display the time as title for location markers
//        // you can safely comment the following four lines but for this info
////        IconGenerator iconFactory = new IconGenerator(this);
////        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
////        // options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime + requiredArea + city)));
////        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(requiredArea + ", " + city)));
////        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
//        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//        options.position(currentLatLng);
//        Marker mapMarker = mMap.addMarker(options);
//        long atTime = mCurrentLocation.getTime();
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
//        String title = mLastUpdateTime.concat(", " + requiredArea).concat(", " + city).concat(", " + country);
//        mapMarker.setTitle(title);
//
//
////        TextView mapTitle = (TextView) findViewById(R.id.textViewTitle);
////        mapTitle.setText(title);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
//                13));
//    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude); //you already have this
            points.add(latLng); //added
            arrCoords.add(latitude + ", " +longitude);
            set.addAll(arrCoords);
            editor.putStringSet("coords", set);
            editor.commit();

            redrawLine(); //added

            MarkerOptions mp = new MarkerOptions();
            mp.position(latLng);
            mMap.addMarker(mp);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
