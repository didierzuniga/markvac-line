package com.markvac.line.login.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.markvac.line.LineApplication;
import com.markvac.line.R;
import com.markvac.line.login.presenter.SplashPresenter;
import com.markvac.line.login.presenter.SplashPresenterImpl;
import com.markvac.line.tracing.view.Tracing;

import java.util.Timer;

public class Splash extends AppCompatActivity implements SplashView {

    private AlertDialog alert = null;
    private ProgressBar progressBar;
    private LineApplication app;
    private SplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        app = (LineApplication) getApplicationContext();
        presenter = new SplashPresenterImpl(this);

        progressBar = findViewById(R.id.prgBarSplash);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void goSignin() {
        hideProgressBar();
        Intent intent = new Intent(this, Signin.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goTracing() {
        hideProgressBar();
        Intent intent = new Intent(this, Tracing.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void alertNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_gps_deactivate)
                .setCancelable(false)
                .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Splash.super.finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    goSignin();
                                }
                            }, 2000);
                        }
                    });

                } else {
                    hideProgressBar();
                    Toast.makeText(this, "Sin tu permiso no podemos seguir", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(Splash.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            showProgressBar();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.verifyNetworkAndInternet(Splash.this, app.isOnline(), app.firebaseUser, app.uid);
                        }
                    }, 1500);
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
