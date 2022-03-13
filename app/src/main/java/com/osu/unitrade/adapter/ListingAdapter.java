package com.osu.unitrade.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.osu.unitrade.R;
import com.osu.unitrade.entity.Listing;
import com.osu.unitrade.fragment.AddListingFragment;

import java.util.ArrayList;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> idList;
    ArrayList<Listing> list;
    DatabaseReference database;
    ProgressBar progressBar;

    private String userID;
    private FirebaseUser user;

    public ListingAdapter(Context context, ArrayList<String> idList,ArrayList<Listing> list) {
        this.context = context;
        this.idList = idList;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String listId = idList.get(position);
        Listing user = list.get(position);

        holder.firstName.setText(user.getTitle());
        holder.lastName.setText(user.getDescription());
        holder.list = user;
        holder.listID = listId;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView firstName, lastName;
        Button update, delete;
        Listing list;
        String listID;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.listingProgressBar);
            firstName = itemView.findViewById(R.id.listingTitle);
            lastName = itemView.findViewById(R.id.listingDescription);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);

            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();

            update.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("listID", listID);
                AddListingFragment fragment = new AddListingFragment();
                fragment.setArguments(bundle);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    database = FirebaseDatabase.getInstance().getReference("Listings/" + listID);
                    database.removeValue().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.d("Update listings"+listID,"Success");
                            Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(view.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    database = FirebaseDatabase.getInstance().getReference("User-Listings/" + userID + "/" + listID);
                    database.removeValue().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.d("Update user listings"+listID,"Success");
                            Toast.makeText(view.getContext(), "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(view.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                }
            });
        }
    }

}