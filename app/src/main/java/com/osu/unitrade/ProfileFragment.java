package com.osu.unitrade;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String nickname;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String nickname) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("nickname", nickname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nickname = getArguments().getString("nickname");

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Lifecycle","------------profile fragment is onStart----------");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Lifecycle","------------profile fragment is onResume----------");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Lifecycle","------------profile fragment is onPause----------");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Lifecycle","------------profile fragment is onStop----------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle","------------profile fragment is onDestroy----------");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Lifecycle","------------profile fragment is onCreateView----------");

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView greeting = (TextView) rootView.findViewById(R.id.texthello);
        greeting.setText(nickname);
        return rootView;
    }
}