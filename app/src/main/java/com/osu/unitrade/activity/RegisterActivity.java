package com.osu.unitrade.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.osu.unitrade.R;
import com.osu.unitrade.model.User;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private final String regex = "[a-z]+\\.{1}\\d+@{1}osu.edu";
    private TextView registerSubmit, back, signIn;
    private EditText editNickname, editEmailAddress, editPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.register_title));

        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        back = (Button) findViewById(R.id.register_back);
        back.setOnClickListener(this);
        registerSubmit = (Button) findViewById(R.id.registerSubmit);
        registerSubmit.setOnClickListener(this);
        signIn = (TextView) findViewById(R.id.register_SignIn);
        signIn.setOnClickListener(this);

        editNickname = (EditText) findViewById(R.id.register_editTextNickname);
        editEmailAddress = (EditText) findViewById(R.id.register_editTextEmailAddress);
        editPassword = (EditText) findViewById(R.id.register_editTextPassword);

        progressBar = (ProgressBar) findViewById(R.id.register_progressBar);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_back:
                Intent backActivity = new Intent(this, MainActivity.class);
                backActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backActivity);
                break;
            case R.id.registerSubmit:
                submit();
                break;
            case R.id.register_SignIn:
                Intent loginActivity = new Intent(this, LoginActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivity);
        }
    }

    private void submit() {
        String nickname = editNickname.getText().toString().trim();
        String email = editEmailAddress.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (nickname.isEmpty()) {
            editNickname.setError(getString(R.string.enter_nickname_error));
            editNickname.requestFocus();
            return;
        }

        if (!Pattern.compile(regex).matcher(email).matches()) {
            editEmailAddress.setError(getString(R.string.email_wrong_error));
            editEmailAddress.requestFocus();
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
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Success", "createUserWithEmail:success");
                User user = new User(nickname, email);
                FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (!user.isEmailVerified()) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.send_verify), Toast.LENGTH_LONG).show();
                                user.sendEmailVerification();
                                progressBar.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(RegisterActivity.this, getString(R.string.fail_to_register_db), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Error", "createUserWithEmail:failure", task.getException());
                Toast.makeText(RegisterActivity.this, getString(R.string.fail_to_register),
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }

        });

    }


}