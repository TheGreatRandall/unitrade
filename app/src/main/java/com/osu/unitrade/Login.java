package com.osu.unitrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private final String regex = "[a-z]+\\.{1}\\d+@{1}osu.edu";
    private TextView back, signUp;
    private EditText editEmailAddress, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        back = (Button) findViewById(R.id.login_back);
        back.setOnClickListener(this);

        signUp = (TextView) findViewById(R.id.login_SignUp);
        signUp.setOnClickListener(this);

        editEmailAddress = (EditText) findViewById(R.id.login_editTextEmailAddress);
        editPassword = (EditText) findViewById(R.id.login_editTextPassword);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_back:
                Intent backActivity = new Intent(this, MainActivity.class);
                backActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backActivity);
                break;
            case R.id.login_SignUp:
                Intent registerActivity = new Intent(this, Register.class);
                registerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerActivity);
        }
    }
}