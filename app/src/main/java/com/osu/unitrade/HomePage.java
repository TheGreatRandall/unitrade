package com.osu.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.osu.unitrade.databinding.ActivityHomePageBinding;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private ActivityHomePageBinding binding;
    private TextView backFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backFront = (Button) findViewById(R.id.backFront);
        backFront.setOnClickListener(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_page);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backFront:
                Intent backActivity = new Intent(this, MainActivity.class);
                backActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backActivity);
                break;
        }
    }
}