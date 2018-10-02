package com.markvac.line.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.markvac.line.LineApplication;
import com.markvac.line.R;

import org.json.JSONException;
import org.json.JSONObject;

public class GetCoordinates extends Service implements LocationListener, GpsStatus.Listener {

    public static final String ACTION_GET_COORDINATES = GetCoordinates.class.getName() + "Coordinates";
    private JSONObject coordinatesJson;
    private LocationManager mLocationManager;
    private LineApplication app;

    @Override
    public void onCreate() {
        super.onCreate();

        app = (LineApplication) getApplicationContext();
        coordinatesJson = new JSONObject();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationManager != null) {
            mLocationManager.addGpsStatusListener(this);
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    7000,
                    0,
                    this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            coordinatesJson = new JSONObject();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            if (app.shaPref.getBoolean("allowRedrawLine", false)){
                try {
                    if (app.shaPref.getString("coordsTracing", null) != null) {
                        JSONObject jsonFromShared = new JSONObject(app.shaPref.getString("coordsTracing", null));
                        coordinatesJson = jsonFromShared;
                        String id = String.valueOf(coordinatesJson.length());
                        coordinatesJson.put(id, latitude + ", " + longitude);
                        String stringToShared = coordinatesJson.toString();
                        app.editor.putString("coordsTracing", stringToShared);
                        app.editor.commit();
                    } else {
                        coordinatesJson.put("0", latitude + ", " + longitude);
                        String stringToShared = coordinatesJson.toString();
                        app.editor.putString("coordsTracing", stringToShared);
                        app.editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Estas dos lineas quizás se podrían borrar y así omitir el "Else"
                app.editor.putString("coordsTracing", null);
                app.editor.commit();
            }

            Intent intent = new Intent(ACTION_GET_COORDINATES);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
    public void onDestroy() {
        super.onDestroy();
        Log.w("jjj", "Destroyed");
    }
}
