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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.osu.unitrade.R;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private final String regex = "[a-z]+\\.{1}\\d+@{1}osu.edu";
    private TextView back, login, signUp, forgetPassword;
    private EditText editEmailAddress, editPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "------------login activity is on create----------");
        super.onCreate(savedInstanceState);



        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_login);
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_login_horizontal);
        }

        getSupportActionBar().setTitle(getString(R.string.login_title));

        mAuth = FirebaseAuth.getInstance();

        back = (Button) findViewById(R.id.login_back);
        back.setOnClickListener(this);
        login = (Button) findViewById(R.id.login_loginButton);
        login.setOnClickListener(this);

        signUp = (TextView) findViewById(R.id.login_SignUp);
        signUp.setOnClickListener(this);
        forgetPassword = (TextView) findViewById(R.id.login_ForgetPassword);
        forgetPassword.setOnClickListener(this);

        editEmailAddress = (EditText) findViewById(R.id.login_editTextEmailAddress);
        editEmailAddress.setText("chen.8095@osu.edu");
        editPassword = (EditText) findViewById(R.id.login_editTextPassword);
        editPassword.setText("123456");

        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_back:
                Intent backActivity = new Intent(this, MainActivity.class);
                backActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backActivity);
                break;
            case R.id.login_SignUp:
                Intent registerActivity = new Intent(this, RegisterActivity.class);
                registerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerActivity);
                break;
            case R.id.login_ForgetPassword:
                Intent resetActivity = new Intent(this, ResetPasswordActivity.class);
                resetActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(resetActivity);
                break;
            case R.id.login_loginButton:
                userLogin();
                break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_login);
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_login_horizontal);
        }

        mAuth = FirebaseAuth.getInstance();

        back = (Button) findViewById(R.id.login_back);
        back.setOnClickListener(this);
        login = (Button) findViewById(R.id.login_loginButton);
        login.setOnClickListener(this);

        signUp = (TextView) findViewById(R.id.login_SignUp);
        signUp.setOnClickListener(this);
        forgetPassword = (TextView) findViewById(R.id.login_ForgetPassword);
        forgetPassword.setOnClickListener(this);

        editEmailAddress = (EditText) findViewById(R.id.login_editTextEmailAddress);
        editEmailAddress.setText("chen.8095@osu.edu");
        editPassword = (EditText) findViewById(R.id.login_editTextPassword);
        editPassword.setText("123456");

        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
    }

    private void userLogin() {
        String email = editEmailAddress.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editEmailAddress.setError(getString(R.string.enter_email_error));
            editEmailAddress.requestFocus();
            return;
        }

        if (!Pattern.compile(regex).matcher(email).matches()) {
            editEmailAddress.setError(getString(R.string.email_wrong_error));
            editEmailAddress.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError(getString(R.string.enter_password_error));
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError(getString(R.string.password_len_less));
            editPassword.requestFocus();
            return;
        }

        if (password.length() > 16) {
            editPassword.setError(getString(R.string.password_len_more));
            editPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user.isEmailVerified()) {
                    Log.d("Success", "signInWithEmailAndPassword:success");
                    Toast.makeText(LoginActivity.this,getString(R.string.user_logged_in) , Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.not_verified), Toast.LENGTH_LONG).show();
                    user.sendEmailVerification();
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                Log.w("Error", "signInWithEmailAndPassword:failure", task.getException());
                Toast.makeText(LoginActivity.this, getString(R.string.wrong_password) , Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}