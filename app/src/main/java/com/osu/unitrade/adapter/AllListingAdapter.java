package com.osu.unitrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.osu.unitrade.R;
import com.osu.unitrade.model.Listing;

import java.util.ArrayList;

public class AllListingAdapter extends RecyclerView.Adapter<AllListingAdapter.MyViewHolder> {

    Context context;
    ArrayList<Listing> list;


    public AllListingAdapter(Context context, ArrayList<Listing> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.all_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Listing listing = list.get(position);
        holder.title.setText(listing.getTitle());
        holder.nickname.setText(listing.getNickname());
        holder.email.setText(listing.getEmail());
        holder.description.setText(listing.getDescription());
        holder.location.setText(listing.getLatitude() + listing.getLongitude());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, nickname, email, location;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listingTitle);
            nickname = itemView.findViewById(R.id.listingNickname);
            description = itemView.findViewById(R.id.listingDescription);
            location = itemView.findViewById(R.id.location);
            email = itemView.findViewById(R.id.listingEmail);
        }
    }

}