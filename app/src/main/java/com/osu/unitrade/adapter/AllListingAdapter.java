package com.osu.unitrade.adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.osu.unitrade.R;
import com.osu.unitrade.model.Listing;

import java.util.ArrayList;
import java.util.List;

public class AllListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    Activity currentActivity;
    Context context;
    ArrayList<Listing> list;


    public AllListingAdapter(Activity current, Context context, ArrayList<Listing> list) {
        this.currentActivity = current;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View v = LayoutInflater.from(context).inflate(R.layout.all_item, parent, false);
            return new MyViewHolder(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.all_item_loading, parent, false);
            return new LoadingViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            populateItemRows((MyViewHolder) holder, position);
        } else if( holder instanceof LoadingViewHolder){
            showLoadingView((LoadingViewHolder) holder, position);
        }

    }

    @Override
    public int getItemCount() {
        return list == null? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position){
        return list.get(position) == null? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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

    public class LoadingViewHolder extends  RecyclerView.ViewHolder{

        ProgressBar progressBar;

        public  LoadingViewHolder(@NonNull View itemView){
            super(itemView);
            progressBar = itemView.findViewById(R.id.loading_progress_bar);
        }
    }

    public void showLoadingView(LoadingViewHolder holder, int position){

    }

    public void populateItemRows(MyViewHolder holder, int position){
        Listing listing = list.get(position);
        holder.title.setText(listing.getTitle());
        holder.nickname.setText(listing.getNickname());
        holder.email.setText(listing.getEmail());
        holder.description.setText(listing.getDescription());

        Geocoder geocoder = new Geocoder(currentActivity);
        try{
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(listing.getLatitude()), Double.parseDouble(listing.getLongitude()), 1);
            holder.location.setText(addresses.get(0).getAddressLine(0));
        }catch (Exception E){
            holder.location.setText("Unable to get this listing's location");
        }
    }



}