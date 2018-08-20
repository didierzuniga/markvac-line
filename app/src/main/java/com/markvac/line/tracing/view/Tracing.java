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
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.markvac.line.R;
import com.markvac.line.customizer.History;

import org.json.JSONException;
import org.json.JSONObject;

public class Tracing extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, LocationListener, GpsStatus.Listener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private Polyline line;
    private SupportMapFragment mapFragment;
    private JSONObject coordinatesJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_view));

        coordinatesJson = new JSONObject();
        shaPref = getSharedPreferences("myShared", MODE_PRIVATE);
        editor = shaPref.edit();

        try {
            if (shaPref.getString("coordenadas", null) != null){
                JSONObject jsonFromShared = new JSONObject(shaPref.getString("coordenadas", ""));
                coordinatesJson = jsonFromShared;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


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

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
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
        LatLng prueba = new LatLng(7.944498, -72.503353);
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.queen))
                .position(prueba, 600f, 900f);
        mMap.addGroundOverlay(groundOverlayOptions);
        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);

        for (int i = 0; i < coordinatesJson.length(); i++) {
            try {
                String toSplit = coordinatesJson.getString(String.valueOf(i));
                String[] latlong = toSplit.split(", ");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng point = new LatLng(latitude, longitude);
                options.add(point);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        line = mMap.addPolyline(options);
    }

//    private void addMarker() {
//        MarkerOptions options = new MarkerOptions();
//
//        // following four lines requires 'Google Maps Android API Utility Library'
//        // https://developers.google.com/maps/documentation/android/utility/
//        // I have used this to display the time as title for location markers
//        // you can safely comment the following four lines but for this info
//        IconGenerator iconFactory = new IconGenerator(this);
//        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
//        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime + requiredArea + city)));
//        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(requiredArea + ", " + city)));
//        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
//        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//        options.position(currentLatLng);
//        Marker mapMarker = mMap.addMarker(options);
//        long atTime = mCurrentLocation.getTime();
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
//        String title = mLastUpdateTime.concat(", " + requiredArea).concat(", " + city).concat(", " + country);
//        mapMarker.setTitle(title);
//
//
//        TextView mapTitle = (TextView) findViewById(R.id.textViewTitle);
//        mapTitle.setText(title);
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

            try {
                String id = String.valueOf(coordinatesJson.length());
                coordinatesJson.put(id, latitude + ", " +longitude);
                Log.w("jjj", "coordinates length: "+coordinatesJson.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String stringToShared = coordinatesJson.toString();
            editor.putString("coordenadas", stringToShared);
            editor.commit();

            redrawLine();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
