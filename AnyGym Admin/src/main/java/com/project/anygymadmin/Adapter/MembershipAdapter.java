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
import com.project.anygymadmin.DataHolder.GymDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.GymInfoActivity;
import com.project.anygymadmin.R;

import java.util.List;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.ViewHolder> {
    private Context context;
    private List<String> stringList;
    private String status;

    public MembershipAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.stringList = stringList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_membership_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.gymMembershipTitle.setText(stringList.get(position).split("#")[0].split("-")[3]+", "+stringList.get(position).split("#")[0].split("-")[2]);
        holder.membershipDays.setText(stringList.get(position).split("#")[1] + "\nDays");


        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + stringList.get(position).split("#")[0]).child("logo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((boolean) dataSnapshot.getValue()) {
                    myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + stringList.get(position).split("#")[0] + "/logo.png");
                    myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try {
                                Glide.with(context).load(uri).into(holder.gymMembershipLogo);
                            } catch (IllegalArgumentException e) {
                            }
                        }
                    });
                } else {
                    myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + stringList.get(position).split("#")[0] + "/thumbnail/0.png");
                    myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try {
                                Glide.with(context).load(uri).into(holder.gymMembershipLogo);
                            } catch (IllegalArgumentException e) {
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + stringList.get(position).split("#")[0]).child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.gymMembershipAddress.setText(dataSnapshot.getValue().toString().replace("#", ", "));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + stringList.get(position).split("#")[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myDataHolder.gymDataHolder = dataSnapshot.getValue(GymDataHolder.class);
                holder.membershipRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Intent intent = new Intent(context, GymInfoActivity.class);
                        intent.putExtra("gymName", myDataHolder.gymDataHolder.getRelativeName());
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + stringList.get(position).split("#")[0]).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue().toString();
                if (status.equals("O")) {
                    holder.membershipStatus.setText("Open");
                    holder.membershipStatus.setTextColor(context.getResources().getColor(R.color.Open));
                } else if (status.equals("C")) {
                    holder.membershipStatus.setText("Close");
                    holder.membershipStatus.setTextColor(context.getResources().getColor(R.color.Close));
                } else if (status.equals("F")) {
                    holder.membershipStatus.setText("Full");
                    holder.membershipStatus.setTextColor(context.getResources().getColor(R.color.Full));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView gymMembershipLogo;
        private TextView gymMembershipTitle, gymMembershipAddress, membershipStatus, membershipDays;
        private ConstraintLayout membershipRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gymMembershipLogo = itemView.findViewById(R.id.gymMembershipLogo);
            gymMembershipTitle = itemView.findViewById(R.id.gymMembershipTitle);
            gymMembershipAddress = itemView.findViewById(R.id.gymMembershipAddress);
            membershipStatus = itemView.findViewById(R.id.membershipStatus);
            membershipDays = itemView.findViewById(R.id.membershipDays);
            membershipRecyclerView = itemView.findViewById(R.id.membership_recycler_layout);
        }
    }
}
