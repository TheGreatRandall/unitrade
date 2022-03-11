package com.osu.unitrade;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> idList;
    ArrayList<Listing> list;
    DatabaseReference database;

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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.tvfirstName);
            lastName = itemView.findViewById(R.id.tvlastName);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);

            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("listID", listID);
                    AddListingFragment fragment = new AddListingFragment();
                    fragment.setArguments(bundle);
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    database = FirebaseDatabase.getInstance().getReference("Listings/" + listID);
                    database.removeValue();

                    database = FirebaseDatabase.getInstance().getReference("User-Listings/" + userID + "/" + listID);
                    database.removeValue();
                }
            });
        }
    }

}