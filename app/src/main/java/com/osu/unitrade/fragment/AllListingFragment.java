package com.osu.unitrade.fragment;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osu.unitrade.R;
import com.osu.unitrade.adapter.AllListingAdapter;
import com.osu.unitrade.model.Listing;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllListingFragment extends Fragment {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    DatabaseReference database;
    AllListingAdapter listingAdapter;
    ArrayList<Listing> list;
    View root;

    private SavedStateHandle saved;
    private String oldestPostId;
    private int visiableThread = 1;
    private int lastVisibleItem, totalItemCount;

    public AllListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllListingFragment newInstance(String param1, String param2) {
        AllListingFragment fragment = new AllListingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            Log.d("saveState", "Get a saved InstanceState!");
            savedInstanceState.getLong("id", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            root = inflater.inflate(R.layout.fragment_alllisting, container, false);
        }else if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            root = inflater.inflate(R.layout.fragment_alllisting_horizontal, container, false);
        }

        progressBar = root.findViewById(R.id.loading_progressBar);
        recyclerView = root.findViewById(R.id.alllistingList);
        database = FirebaseDatabase.getInstance().getReference("Listings");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        listingAdapter = new AllListingAdapter(getActivity(), requireContext(), list);
        recyclerView.setAdapter(listingAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if(connected){
                    database.limitToFirst(5).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                oldestPostId = dataSnapshot.getKey();
                                Listing listing = dataSnapshot.getValue(Listing.class);
                                list.add(listing);
                            }
                            listingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("cancel", "Listener was cancelled");
            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                progressBar.setVisibility(View.GONE);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + visiableThread)) {
                    progressBar.setVisibility(View.VISIBLE);

                    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                    connectedRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean connected = snapshot.getValue(Boolean.class);
                            if(connected){
                                database.orderByKey().startAfter(oldestPostId).limitToFirst(5).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            oldestPostId = dataSnapshot.getKey();
                                            Listing listing = dataSnapshot.getValue(Listing.class);
                                            list.add(listing);
                                        }
                                        listingAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("cancel", "Listener was cancelled");
                        }
                    });

                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        return root;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        ConstraintLayout layout = (ConstraintLayout) getView();
        if(layout != null){
            layout.removeAllViewsInLayout();
        }

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            root = layoutInflater.inflate(R.layout.fragment_alllisting, layout, true);
            layoutInflater.inflate(R.layout.fragment_alllisting, layout, false);
        }else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            root = layoutInflater.inflate(R.layout.fragment_alllisting_horizontal, layout, true);
        }

        progressBar = root.findViewById(R.id.loading_progressBar);
        recyclerView = root.findViewById(R.id.alllistingList);
        database = FirebaseDatabase.getInstance().getReference("Listings");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        listingAdapter = new AllListingAdapter(getActivity(), requireContext(), list);
        recyclerView.setAdapter(listingAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if(connected){
                    database.limitToFirst(5).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                oldestPostId = dataSnapshot.getKey();
                                Listing listing = dataSnapshot.getValue(Listing.class);
                                list.add(listing);
                            }
                            listingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("cancel", "Listener was cancelled");
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                progressBar.setVisibility(View.GONE);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + visiableThread)) {
                    progressBar.setVisibility(View.VISIBLE);

                    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                    connectedRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean connected = snapshot.getValue(Boolean.class);
                            if(connected){
                                database.orderByKey().startAfter(oldestPostId).limitToFirst(5).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            oldestPostId = dataSnapshot.getKey();
                                            Listing listing = dataSnapshot.getValue(Listing.class);
                                            list.add(listing);
                                        }
                                        listingAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("cancel", "Listener was cancelled");
                        }
                    });

                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

}