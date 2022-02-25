package com.osu.unitrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private final String regex = "[a-z]+\\.{1}\\d+@{1}osu.edu";
    private TextView back, login, signUp;
    private EditText editEmailAddress, editPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        back = (Button) findViewById(R.id.login_back);
        back.setOnClickListener(this);
        login = (Button) findViewById(R.id.login_loginButton);
        login.setOnClickListener(this);

        signUp = (TextView) findViewById(R.id.login_SignUp);
        signUp.setOnClickListener(this);

        editEmailAddress = (EditText) findViewById(R.id.login_editTextEmailAddress);
        editPassword = (EditText) findViewById(R.id.login_editTextPassword);

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
                Intent registerActivity = new Intent(this, Register.class);
                registerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerActivity);
                break;
            case R.id.login_loginButton:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editEmailAddress.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editEmailAddress.setError("Enter your email.");
            editEmailAddress.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmailAddress.setError("Please enter a valid email!");
            editEmailAddress.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Enter your password.");
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError("The minimum length of the password should be 6 characters.");
            editPassword.requestFocus();
            return;
        }

        if (password.length() > 16) {
            editPassword.setError("The maximum length of the password should be 16 characters.");
            editPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Direct to main page
                    Log.d("Success", "signInWithEmailAndPassword:success");
                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.w("Warning", "signInWithEmailAndPassword:failure", task.getException());
                    Toast.makeText(Login.this, "Invalid email address or password entered to login!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}