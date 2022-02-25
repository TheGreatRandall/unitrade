package com.osu.unitrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, login, toHome;
    private static final String mainTag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(mainTag, "Main Activity onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(this);

        toHome = (TextView) findViewById(R.id.toHome);
        toHome.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
       switch (v.getId()) {
           case R.id.register:
               startActivity(new Intent(this, Register.class));
               break;
           case R.id.login:
               startActivity(new Intent(this, Login.class));
               break;
           case R.id.toHome:
               startActivity(new Intent(this, HomePage.class));
               break;
       }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(mainTag, "Main Activity onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(mainTag, "Main Activity onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(mainTag, "Main Activity onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(mainTag, "Main Activity onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(mainTag, "Main Activity onDestroy() called");
    }
}