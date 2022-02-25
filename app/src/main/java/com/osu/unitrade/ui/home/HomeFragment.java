package com.osu.unitrade.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.osu.unitrade.MainActivity;
import com.osu.unitrade.R;
import com.osu.unitrade.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private static final String homeFragTag = "Home Fragment";
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        Log.d(homeFragTag, "Home Fragment onCreateView() called");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(homeFragTag, "Home Fragment onDestroy() called");
        binding = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(homeFragTag, "Home Fragment onResume() called");
        binding = null;
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(homeFragTag, "Home Fragment onPause() called");
        binding = null;
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(homeFragTag, "Home Fragment onStop() called");
        binding = null;
    }

}