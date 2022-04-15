package com.osu.unitrade.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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


    RecyclerView recyclerView;
    DatabaseReference database;
    AllListingAdapter listingAdapter;
    ArrayList<Listing> list;

    boolean isLoading = false;
    private String oldestPostId;
    private int recyclerVisiblePosition;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alllisting, container, false);

        recyclerView = root.findViewById(R.id.alllistingList);
        database = FirebaseDatabase.getInstance().getReference("Listings");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        listingAdapter = new AllListingAdapter(getActivity(), requireContext(), list);
        recyclerView.setAdapter(listingAdapter);

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if(!isLoading){
                    if(linearLayoutManager != null &&
                    linearLayoutManager.findLastCompletelyVisibleItemPosition() == list.size() - 1){
                        list.add(null);
                        listingAdapter.notifyItemInserted(list.size() - 1);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(list.size() - 1);
                                int scrollPosition = list.size();
                                listingAdapter.notifyItemRemoved(scrollPosition);
                                int currentSize = scrollPosition;
                                int nextLimit = currentSize + 5;

                                while(currentSize  < nextLimit){
                                    Log.d("loadMore", "I load a new list once");
                                    database.orderByKey().startAfter(oldestPostId).limitToFirst(5).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                oldestPostId = dataSnapshot.getKey();
                                                Listing listing = dataSnapshot.getValue(Listing.class);
                                                list.add(listing);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(requireActivity(), "fail to get listings", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    currentSize += 5;
                                }
                                isLoading = false;
                            }
                        }, 2000);
                        isLoading = true;
                    }
                }
            }
        });

        return root;
    }

}