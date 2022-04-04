package com.osu.unitrade.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osu.unitrade.fragment.AddListingFragment;
import com.osu.unitrade.fragment.AllListingFragment;
import com.osu.unitrade.fragment.MylistingFragment;
import com.osu.unitrade.R;
import com.osu.unitrade.fragment.NewSettingsFragment;
import com.osu.unitrade.fragment.SettingFragment;
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
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Unitrade");
        allListing = (Button) findViewById(R.id.allListing);

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

        allListing.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("nickname", HomeActivity.this.nickname);
            AllListingFragment fragment = new AllListingFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        });

        myListing = (Button) findViewById(R.id.myListing);
        myListing.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putDouble("currentLongitude", currentLocation.getLongitude());
            bundle.putDouble("currentLatitude", currentLocation.getLatitude());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGPS() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                }
            });
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

}