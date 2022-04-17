package com.osu.unitrade.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.osu.unitrade.R;

import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mauth;
    private final String regex = "[a-z]+\\.{1}\\d+@{1}osu.edu";
    private EditText emailEditText;
    private Button back, resetPasswordButton;
    private ProgressBar progressBar;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("emailEditText", emailEditText.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        emailEditText.setText(savedInstanceState.getString("emailEditText"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.forget_password));
        setContentView(R.layout.activity_reset_password);

        mauth = FirebaseAuth.getInstance();

        back = (Button) findViewById(R.id.reset_back);
        back.setOnClickListener(this);
        resetPasswordButton = (Button) findViewById(R.id.reset_resetPassword);
        resetPasswordButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.reset_editTextEmailAddress);

        if(savedInstanceState != null){
            emailEditText.setText(savedInstanceState.getString("emailEditText"));
        }

        progressBar = (ProgressBar) findViewById(R.id.reset_progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_resetPassword:
                resetPassword();
                break;
            case R.id.reset_back:
                Intent loginActivity = new Intent(this, LoginActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivity);
                break;
        }


    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError(getString(R.string.enter_email_error));
            emailEditText.requestFocus();
            return;
        }

        if (!Pattern.compile(regex).matcher(email).matches()) {
            emailEditText.setError(getString(R.string.email_wrong_error));
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mauth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.send_reset_email), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

                Intent loginActivity = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivity);
            } else {
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.fail_to_send_reset), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}