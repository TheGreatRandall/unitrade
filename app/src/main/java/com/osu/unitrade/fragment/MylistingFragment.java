package com.osu.unitrade.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osu.unitrade.adapter.ListingAdapter;
import com.osu.unitrade.R;
import com.osu.unitrade.model.Listing;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MylistingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MylistingFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference database;
    ListingAdapter listingAdapter;
    ArrayList<String> listingIdList;
    ArrayList<Listing> list;
    private String userID;
    private Double currentLongitude, currentLatitude;
    private FirebaseUser user;
    private Button addListingButton;


    public MylistingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MylistingFragment.
     */
    public static MylistingFragment newInstance(Double currentLongitude, Double currentLatitude) {
        MylistingFragment fragment = new MylistingFragment();
        Bundle args = new Bundle();
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
            currentLatitude = bundle.getDouble("currentLatitude");
            currentLongitude = bundle.getDouble("currentLongitude");
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mylisting, container, false);
        addListingButton = root.findViewById(R.id.add_listing_button);
        addListingButton.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("currentLongitude", currentLongitude);
                    bundle.putDouble("currentLatitude", currentLatitude);
                    AddListingFragment fragment = new AddListingFragment();
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                }
        );
        recyclerView = root.findViewById(R.id.mylistingList);
        database = FirebaseDatabase.getInstance().getReference("User-Listings/" + userID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        listingIdList = new ArrayList<>();
        listingAdapter = new ListingAdapter(requireContext(), listingIdList, list, currentLongitude, currentLatitude);
        recyclerView.setAdapter(listingAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listingIdList.clear();
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String listingID = dataSnapshot.getKey();
                                Listing listing = dataSnapshot.getValue(Listing.class);
                                listingIdList.add(listingID);
                                list.add(listing);
                            }
                            listingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("cancel", "Listener was cancelled");
            }
        });


        return root;
    }
}