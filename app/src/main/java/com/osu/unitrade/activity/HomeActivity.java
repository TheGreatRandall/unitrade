package com.osu.unitrade.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private String nickname;

    private Button allListing;
    private Button myListing;
    private Button setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Unitrade");
        allListing = (Button) findViewById(R.id.allListing);

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
            bundle.putString("nickname", HomeActivity.this.nickname);
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
}