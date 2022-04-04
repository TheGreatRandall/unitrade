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

    private int test = 0;

    private FirebaseUser user;
    private String userID, listID;
    private String key, nickname;
    private String currentLongitude, currentLatitude;
    private Location currentLocation;

    private ProgressBar progressBar;

    FusedLocationProviderClient fusedLocationProviderClient;

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
    public static AddListingFragment newInstance(String listID) {
        AddListingFragment fragment = new AddListingFragment();
        Bundle args = new Bundle();
        args.putString("listID", listID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            listID = bundle.getString("listID");
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
                Toast.makeText(requireActivity(), "fail to get user", Toast.LENGTH_SHORT).show();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else{
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
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
                    Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                }
            });
        }

        progressBar = rootView.findViewById(R.id.addListing_progressBar);
        submit.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if (key == null) {
                key = mDatabase.child("Listings").push().getKey();
            }

            getCurrentLocation();

            Listing listing = new Listing(userID, title.getText().toString(), description.getText().toString(), currentLongitude, currentLatitude);
            Map<String, Object> listingValues = listing.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Listings/" + key, listingValues);
            childUpdates.put("/User-Listings/" + userID + "/" + key, listingValues);
            mDatabase.updateChildren(childUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    title.setText("");
                    description.setText("");
                    key = mDatabase.child("Listings").push().getKey();
                    Toast.makeText(requireActivity(), "success", Toast.LENGTH_SHORT).show();
                    MylistingFragment fragment = new MylistingFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                } else {
                    Toast.makeText(requireActivity(), "failed", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        });
        return rootView;
    }
    

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationPermissionRequest.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION});
            }

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    currentLocation = task.getResult();

                    if (currentLocation != null) {
                        currentLongitude = String.valueOf(currentLocation.getLongitude());
                        currentLatitude = String.valueOf(currentLocation.getLatitude());
                        Toast.makeText(requireActivity(),"get location success", Toast.LENGTH_SHORT).show();
                    } else {
                        LocationRequest locationRequest = LocationRequest.
                                create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                currentLocation = locationResult.getLastLocation();

                                currentLongitude = String.valueOf(currentLocation.getLongitude());
                                currentLatitude = String.valueOf(currentLocation.getLatitude());

                                test = 1;
                            }
                        };


                       /* if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionRequest.launch(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION});
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());*/

                    }
                }
            });
        }


    }

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result ->{
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if(fineLocationGranted != null && fineLocationGranted){
                        Toast.makeText(getActivity(), "Precise location access granted", Toast.LENGTH_SHORT).show();
                    }else if(coarseLocationGranted != null && coarseLocationGranted){
                        Toast.makeText(getActivity(), "Precise location access granted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "Precise location access granted", Toast.LENGTH_SHORT).show();
                    }
                }

            });
}