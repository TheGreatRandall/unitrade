package com.osu.unitrade.fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private FirebaseDatabase db;
    private DatabaseReference database;
    private AllListingAdapter listingAdapter;
    private ArrayList<Listing> list;
    private View rootView;
    private Button nextPage, backPage;

    private SavedStateHandle saved;
    private String firstPostId, oldestPostId;
    private int pageIndex;

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
        if (savedInstanceState != null) {
            Log.d("saveState", "Get a saved InstanceState!");
            savedInstanceState.getLong("id", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rootView = inflater.inflate(R.layout.fragment_alllisting, container, false);
        } else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView = inflater.inflate(R.layout.fragment_alllisting_horizontal, container, false);
        }

        nextPage = rootView.findViewById(R.id.nextPage);
        backPage = rootView.findViewById(R.id.previousPage);
        progressBar = rootView.findViewById(R.id.loading_progressBar);
        recyclerView = rootView.findViewById(R.id.alllistingList);

        db = FirebaseDatabase.getInstance();
        database = db.getReference("Listings");
        database.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        listingAdapter = new AllListingAdapter(getActivity(), requireContext(), list);
        recyclerView.setAdapter(listingAdapter);

        pageIndex = 1;

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    database.limitToFirst(5).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            int count = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                oldestPostId = dataSnapshot.getKey();
                                if (count == 0) {
                                    firstPostId = oldestPostId;
                                }

                                Listing listing = dataSnapshot.getValue(Listing.class);
                                list.add(listing);
                                count++;
                            }

                            listingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (isAdded() && getActivity() != null) {
                        Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("nullActivity", "no activity attached");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), getString(R.string.failt_get_listing), Toast.LENGTH_SHORT).show();
            }
        });

        progressBar.setVisibility(View.GONE);

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextData();
                pageIndex++;
            }
        });

        if (pageIndex == 1) {
            backPage.setEnabled(false);
        } else {
            backPage.setEnabled(true);
            backPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getLastData();
                    pageIndex--;
                }
            });
        }

        return rootView;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        ConstraintLayout layout = (ConstraintLayout) getView();
        if (layout != null) {
            layout.removeAllViewsInLayout();
        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            rootView = layoutInflater.inflate(R.layout.fragment_alllisting, layout, true);
            layoutInflater.inflate(R.layout.fragment_alllisting, layout, false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView = layoutInflater.inflate(R.layout.fragment_alllisting_horizontal, layout, true);
        }

        progressBar = rootView.findViewById(R.id.loading_progressBar);
        recyclerView = rootView.findViewById(R.id.alllistingList);

        db = FirebaseDatabase.getInstance();
        database = db.getReference("Listings");
        database.keepSynced(true);

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
                if (connected) {
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
                } else {
                    if (isAdded() && getActivity() != null) {
                        Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("nullActivity", "no activity attached");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("cancel", "Listener was cancelled");
            }
        });

        progressBar.setVisibility(View.GONE);

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextData();
                pageIndex++;
            }
        });

        if (pageIndex == 1) {
            backPage.setEnabled(false);
        } else {
            backPage.setEnabled(true);
            backPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getLastData();
                    pageIndex--;
                }
            });
        }
    }

    public void getLastData() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    database.startAfter(oldestPostId).limitToFirst(5).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            int count = 0;
                            progressBar.setVisibility(View.GONE);
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                oldestPostId = dataSnapshot.getKey();
                                if (count == 0) {
                                    firstPostId = oldestPostId;
                                }
                                Listing listing = dataSnapshot.getValue(Listing.class);
                                list.add(listing);
                                count++;
                            }
                            progressBar.setVisibility(View.GONE);
                            listingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (isAdded() && getActivity() != null) {
                        Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("nullActivity", "no activity attached");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("cancel", "Listener was cancelled");
            }
        });
    }

    public void getNextData() {
        pageIndex ++;

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    database.startAfter(oldestPostId).limitToFirst(5).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressBar.setVisibility(View.VISIBLE);
                            list.clear();
                            int count = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                oldestPostId = dataSnapshot.getKey();
                                if (count == 0) {
                                    firstPostId = oldestPostId;
                                }
                                Listing listing = dataSnapshot.getValue(Listing.class);
                                list.add(listing);
                            }
                            progressBar.setVisibility(View.GONE);
                            listingAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (isAdded() && getActivity() != null) {
                        Toast.makeText(requireActivity(), "Unable to get the Internet connection", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("nullActivity", "no activity attached");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("cancel", "Listener was cancelled");
            }
        });
    }
}