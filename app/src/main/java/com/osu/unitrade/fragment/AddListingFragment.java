package com.osu.unitrade.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osu.unitrade.R;
import com.osu.unitrade.model.Listing;
import com.osu.unitrade.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddListingFragment extends Fragment {

    private Button submit;
    private EditText title;
    private EditText description;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseUser user;
    private String userID, listID;
    private String key, nickname, email;
    private Double currentLongitude, currentLatitude;

    private ProgressBar progressBar;

    public AddListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddListingFragment newInstance(String listID, Double currentLongitude, Double currentLatitude) {
        AddListingFragment fragment = new AddListingFragment();
        Bundle args = new Bundle();
        args.putString("listID", listID);
        args.putDouble("currentLongitude", currentLongitude);
        args.putDouble("currentLatitude", currentLatitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            listID = bundle.getString("listID");
            currentLongitude = bundle.getDouble("currentLongitude");
            currentLatitude = bundle.getDouble("currentLatitude");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        mDatabase.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    nickname = userProfile.nickname;
                    email = userProfile.emailAddress;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), getString(R.string.fail_get_user), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Lifecycle", "------------profile fragment is onCreateView----------");

        View rootView = inflater.inflate(R.layout.fragment_addlisting, container, false);
        submit = rootView.findViewById(R.id.addListingSubmit);
        title = rootView.findViewById(R.id.AddListingTitle);
        description = rootView.findViewById(R.id.addListingDescription);

        if (listID != null) {
            key = listID;

            mDatabase.child("Listings/" + listID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Listing listing = snapshot.getValue(Listing.class);
                    if (listing != null) {
                        title.setText(listing.getTitle());
                        description.setText(listing.getDescription());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireActivity(),getString(R.string.failt_get_listing) , Toast.LENGTH_LONG).show();
                }
            });
        }

        progressBar = rootView.findViewById(R.id.addListing_progressBar);
        submit.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if (key == null) {
                key = mDatabase.child("Listings").push().getKey();
            }


            Listing listing = new Listing(nickname, email, title.getText().toString(), description.getText().toString(), String.valueOf(currentLongitude), String.valueOf(currentLatitude));
            Map<String, Object> listingValues = listing.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Listings/" + key, listingValues);
            childUpdates.put("/User-Listings/" + userID + "/" + key, listingValues);
            mDatabase.updateChildren(childUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    title.setText("");
                    description.setText("");
                    key = mDatabase.child("Listings").push().getKey();
                    Toast.makeText(requireActivity(), getString(R.string.add_success), Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("currentLongitude", currentLongitude);
                    bundle.putDouble("currentLatitude", currentLatitude);
                    MylistingFragment fragment = new MylistingFragment();
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.add_fail), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        });
        return rootView;
    }
    


}