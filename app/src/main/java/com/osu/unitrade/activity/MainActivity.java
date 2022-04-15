package com.osu.unitrade.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.osu.unitrade.R;
import com.osu.unitrade.activity.LoginActivity;
import com.osu.unitrade.activity.RegisterActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Unitrade");

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_main);
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_main_horizontal);
        }

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_main);
        }else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_main_horizontal);
        }

        getSupportActionBar().setTitle("Unitrade");

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(this);
    }
}