package com.markvac.line.tracing.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.markvac.line.LineApplication;
import com.markvac.line.R;
import com.markvac.line.customizer.History;
import com.markvac.line.login.view.Signin;
import com.markvac.line.services.GetCoordinates;
import com.markvac.line.tracing.presenter.TracingPresenter;
import com.markvac.line.tracing.presenter.TracingPresenterImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Tracing extends AppCompatActivity implements TracingView, NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    private FirebaseAuth firebaseAuth;
    private GoogleMap mMap;
    private DotProgressBar dotProgressBar;
    private AlertDialog alert = null;
//    private LocationManager mLocationManager;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor editor;
    private Polyline line;
    private SupportMapFragment mapFragment;
    private JSONObject coordinatesJson;
    private FloatingActionButton btnPlayStop;
    private LineApplication app;
    private TracingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_view));

        presenter = new TracingPresenterImpl(this);
        app = (LineApplication) getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startService(new Intent(this, GetCoordinates.class));
        } else {
            startService(new Intent(this, GetCoordinates.class));
        }

        dotProgressBar = findViewById(R.id.idDotProgress);
        coordinatesJson = new JSONObject();

        shaPref = getSharedPreferences("sharedMarkvacLine", MODE_PRIVATE);
        editor = shaPref.edit();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnPlayStop = findViewById(R.id.fabPlayStop);
        int miColor = getResources().getColor(R.color.colorWhite);
        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{miColor});
        btnPlayStop.setBackgroundTintList(csl);

        if (shaPref.getBoolean("allowRedrawLine", false)){
            // If button was playing, change to Stop
            btnPlayStop.setImageResource(R.drawable.ic_stop);
        } else {
            // If button was stopped, change to playing
            btnPlayStop.setImageResource(R.drawable.ic_play);
        }

        btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shaPref.getBoolean("allowRedrawLine", false)){
                    btnPlayStop.setImageResource(R.drawable.ic_play);
                    editor.putBoolean("allowRedrawLine", false);
                    editor.commit();

                    // Coloco 5 puntos minimo para guardar como recorrido
                    if (coordinatesJson.length() > 2){
                        confirmStoreCoordinates();
                    }
                } else {
                    btnPlayStop.setImageResource(R.drawable.ic_stop);
                    editor.putBoolean("allowRedrawLine", true);
                    editor.commit();
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        double latitude = intent.getDoubleExtra("latitude", 0);
                        double longitude = intent.getDoubleExtra("longitude", 0);

                        processCoordinates(latitude, longitude);
                    }
                }, new IntentFilter(GetCoordinates.ACTION_GET_COORDINATES)
        );

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite)); //Change menu hamburguer color
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void showProgressBar() {
        dotProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        dotProgressBar.setVisibility(View.GONE);
    }

    public void disableButtons() {
        btnPlayStop.setEnabled(false);
    }

    public void enableButtons() {
        btnPlayStop.setEnabled(true);
    }

    public void confirmStoreCoordinates(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_successful_travel)
                .setCancelable(false)
                .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Response YES
                        //Antes de almacenar en DB calcular distancia y tiempo y fecha

                        View v = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                        v.setAlpha(0.5f); // Change this value to set the desired alpha

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgressBar();
                                disableButtons();
                            }
                        });

                        String coordinates = shaPref.getString("coordsTracing", null);
                        presenter.saveCoordinates(coordinates, app.dni, app.company);
                    }
                }).setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Response NO
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void successfulStore() {
        View v = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        v.setAlpha(1.0f); // Change this value to set the desired alpha

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                enableButtons();
            }
        });

        editor.remove("coordsTracing");
        editor.commit();
        coordinatesJson = new JSONObject();

        View parentLayout = findViewById(android.R.id.content);
        Snackbar snk = Snackbar.make(parentLayout, getString(R.string.toast_successful_store),
                Snackbar.LENGTH_SHORT);
        View snackBarView = snk.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.snackbar_text));
        textView.setTextColor(Color.WHITE);
        snk.setDuration(2000);
        snk.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stopService(new Intent(this, GetCoordinates.class));
            startService(new Intent(this, GetCoordinates.class));
        } else {
            stopService(new Intent(this, GetCoordinates.class));
            startService(new Intent(this, GetCoordinates.class));
        }
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
            goLogout();
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

    public void goLogout(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stopService(new Intent(this, GetCoordinates.class));
        } else {
            stopService(new Intent(this, GetCoordinates.class));
        }
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Signin.class);
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
        //mMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);

        try {
            JSONObject jsonFromShared = new JSONObject(shaPref.getString("coordsTracing", ""));
            coordinatesJson = jsonFromShared;

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public void processCoordinates(double latitude, double longitude) {
        mMap.clear();

        LatLng latLng = new LatLng(latitude, longitude); //you already have this

        // BEGIN - Traido aquí para que la imagen del mapa añadido no se cambie
        LatLng prueba = new LatLng(7.944498, -72.503353);
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.queen))
                .position(prueba, 600f, 900f);
        mMap.addGroundOverlay(groundOverlayOptions);
        // END - Traido aquí para que la imagen del mapa añadido no se cambie

        if(shaPref.getBoolean("allowRedrawLine", false)){
//            try {
//                String id = String.valueOf(coordinatesJson.length());
//                coordinatesJson.put(id, latitude + ", " +longitude);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            String stringToShared = coordinatesJson.toString();
//            editor.putString("coordsTracing", stringToShared);
//            editor.commit();

            redrawLine();
        }

        MarkerOptions mp = new MarkerOptions();
        mp.position(latLng);
        mMap.addMarker(mp);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
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
