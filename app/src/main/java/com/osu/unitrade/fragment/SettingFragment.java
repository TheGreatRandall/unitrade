package com.osu.unitrade.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.osu.unitrade.R;
import com.osu.unitrade.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Button logout;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Lifecycle","------------setting fragment is onStart----------");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Lifecycle","------------setting fragment is onResume----------");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Lifecycle","------------setting fragment is onPause----------");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Lifecycle","------------setting fragment is onStop----------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle","------------setting fragment is onDestroy----------");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Lifecycle","------------setting fragment is onCreateView----------");
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        Button logout = (Button) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                Toast.makeText(getActivity(), "User logged out.", Toast.LENGTH_LONG).show();            }
        });
        return rootView;
    }
}