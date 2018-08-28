package com.markvac.line.login.view;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.markvac.line.R;
import com.markvac.line.login.presenter.SigninPresenter;
import com.markvac.line.login.presenter.SigninPresenterImpl;

public class Signin extends AppCompatActivity implements SigninView {

    private TextInputEditText username, password;
    private SigninPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        presenter = new SigninPresenterImpl(this);

        username = findViewById(R.id.idUsername);
        password = findViewById(R.id.idPassword);
    }

}
