package com.osu.unitrade;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private String userID;
    private String nickname;

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
    public static AddListingFragment newInstance(String nickname) {
        AddListingFragment fragment = new AddListingFragment();
        Bundle args = new Bundle();
        args.putString("nickname", nickname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        mDatabase.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    nickname = userProfile.nickname;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "fail to get user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Lifecycle","------------profile fragment is onCreateView----------");

        View rootView = inflater.inflate(R.layout.fragment_add_listing, container, false);
        submit = rootView.findViewById(R.id.addListingSubmit);
        title = rootView.findViewById(R.id.AddListingTitle);
        description = rootView.findViewById(R.id.addListingDescription);
        progressBar = rootView.findViewById(R.id.addListing_progressBar);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = mDatabase.child("Listings").push().getKey();
                Listing listing = new Listing(userID, title.getText().toString(), description.getText().toString());
                Map<String, Object> listingValues = listing.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Listings/" + key, listingValues);
                childUpdates.put("/User-Listings/" + userID + "/" + key, listingValues);
                mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(), "added", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(requireActivity(), "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return rootView;
    }
}