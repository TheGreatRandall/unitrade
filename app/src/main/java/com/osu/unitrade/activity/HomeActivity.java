package com.osu.unitrade.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osu.unitrade.fragment.AllListingFragment;
import com.osu.unitrade.fragment.MylistingFragment;
import com.osu.unitrade.R;
import com.osu.unitrade.fragment.NewSettingsFragment;
import com.osu.unitrade.model.User;

public class HomeActivity extends AppCompatActivity {


    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private String nickname;

    private Button allListing;
    private Button myListing;
    private Button setting;

    public FusedLocationProviderClient fusedLocationProviderClient;
    public Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(getString(R.string.app_name));
        setContentView(R.layout.activity_home);

        Fragment existingFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(existingFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, existingFragment).commit();
        }else{
            Bundle bundle = new Bundle();
            bundle.putString("nickname", HomeActivity.this.nickname);
            AllListingFragment fragment = new AllListingFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }

        updateGPS();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String nick = userProfile.nickname;
                    HomeActivity.this.nickname = nick;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "fail to get user", Toast.LENGTH_SHORT).show();
            }
        });

        allListing = (Button) findViewById(R.id.allListing);
        allListing.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("nickname", HomeActivity.this.nickname);
            AllListingFragment fragment = new AllListingFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        });

        myListing = (Button) findViewById(R.id.myListing);
        myListing.setOnClickListener(view -> {
            startLocationUpdates();
            Bundle bundle = new Bundle();
            if (currentLocation != null) {
                bundle.putDouble("currentLongitude", currentLocation.getLongitude());
                bundle.putDouble("currentLatitude", currentLocation.getLatitude());
            } else {
                bundle.putDouble("currentLongitude", 0);
                bundle.putDouble("currentLatitude", 0);
            }
            MylistingFragment fragment = new MylistingFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        });


        setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("nickname", HomeActivity.this.nickname);
            NewSettingsFragment fragment = new NewSettingsFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        });

        AllListingFragment fragment = new AllListingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGPS() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

}