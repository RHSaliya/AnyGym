package com.project.anygymadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymadmin.DataHolder.OwnerDataHolder;
import com.project.anygymadmin.DataHolder.UserDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.OwnerInfoActivity;
import com.project.anygymadmin.R;
import com.project.anygymadmin.UserInfoActivity;

import java.util.List;

public class OwnersAdapter extends RecyclerView.Adapter<OwnersAdapter.ViewHolder> {
    private Context context;
    private List<OwnerDataHolder> ownerDataHolderList;
    public OwnersAdapter(Context context, List<OwnerDataHolder> ownerDataHolderList) {
        this.context = context;
        this.ownerDataHolderList = ownerDataHolderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_owners_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        FirebaseStorage.getInstance().getReference(myDataHolder.USER_PROFILE_IMAGES_PATH + ownerDataHolderList.get(position).getMobile()+".png")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.ic_male_user_placeholder).into(holder.ownerProfile);
            }
        });
        holder.ownerNameTV.setText(ownerDataHolderList.get(position).getName());
        holder.ownerMobileTV.setText(ownerDataHolderList.get(position).getMobile());
        holder.ownerCreditsTV.setText(String.valueOf(ownerDataHolderList.get(position).getCredit())+"\nCredits");

        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+ownerDataHolderList.get(position).getMobile()).child("credit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.ownerCreditsTV.setText(dataSnapshot.getValue().toString()+"\nCredits");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+ownerDataHolderList.get(position).getMobile()).child("ownerGyms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.ownerTotalGymsTV.setText("Owns : "+String.valueOf(dataSnapshot.getChildrenCount())+" Gyms");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.ownerCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataHolder.ownerDataHolder = ownerDataHolderList.get(position);
                Intent intent = new Intent(context, OwnerInfoActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ownerDataHolderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ownerProfile;
        private TextView ownerNameTV,ownerMobileTV,ownerTotalGymsTV,ownerCreditsTV;
        private ConstraintLayout ownerCL;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerProfile = itemView.findViewById(R.id.ownerProfile);
            ownerNameTV = itemView.findViewById(R.id.ownerNameTV);
            ownerTotalGymsTV = itemView.findViewById(R.id.ownerTotalGymsTV);
            ownerMobileTV = itemView.findViewById(R.id.ownerMobileTV);
            ownerCreditsTV = itemView.findViewById(R.id.ownerCreditsTV);
            ownerCL = itemView.findViewById(R.id.ownersCL);
        }
    }
}
