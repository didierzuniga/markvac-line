package com.markvac.line.login.view;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.markvac.line.LineApplication;
import com.markvac.line.R;
import com.markvac.line.login.presenter.SigninPresenter;
import com.markvac.line.login.presenter.SigninPresenterImpl;
import com.markvac.line.tracing.view.Tracing;

public class Signin extends AppCompatActivity implements SigninView {

    private TextInputEditText fieldUsername, fieldPassword;
    private Button buttonSignin;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private LineApplication app;
    private SigninPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        app = (LineApplication) getApplicationContext();
        presenter = new SigninPresenterImpl(this);
        firebaseAuth = FirebaseAuth.getInstance();

        fieldUsername = findViewById(R.id.idFieldUsername);
        fieldPassword = findViewById(R.id.idFieldPassword);
        buttonSignin = findViewById(R.id.idButtonSignin);
        progressBar = findViewById(R.id.idProgressbar);

        buttonSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldUsername.getText().toString().equals("") || fieldPassword.getText().toString().equals("")){
                    Toast.makeText(Signin.this, R.string.toast_required_fields, Toast.LENGTH_SHORT).show();
                } else {
                    showProgressBar();
                    disableInputs();
                    presenter.signin(fieldUsername.getText().toString(),
                                    fieldPassword.getText().toString(),
                                    Signin.this,
                                    firebaseAuth);
                }
            }
        });
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
    public void disableInputs() {
        fieldUsername.setEnabled(false);
        fieldPassword.setEnabled(false);
        buttonSignin.setEnabled(false);
    }

    @Override
    public void signinError(String error) {
        firebaseAuth.getInstance().signOut();
        enableInputs();
        hideProgressBar();
        if (error == "The password is invalid or the user does not have a password."){
            Toast.makeText(this, getString(R.string.toast_invalid_password), Toast.LENGTH_SHORT).show();
        } else if (error == "There is no user record corresponding to this identifier. The user may have been deleted."){
            Toast.makeText(this, getString(R.string.toast_user_not_registered), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void signinSuccess(String username, String email, String uid) {
        app.username = username;
        app.email = email;
        app.uid = uid;
        hideProgressBar();
        Intent intent = new Intent(this, Tracing.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void dniNotExist() {
        enableInputs();
        hideProgressBar();
        Toast.makeText(this, getString(R.string.toast_dni_not_exist), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void enableInputs() {
        fieldUsername.setEnabled(true);
        fieldPassword.setEnabled(true);
        buttonSignin.setEnabled(true);
    }
}
