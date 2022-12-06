package com.project.anygymadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GymRecyclerAdapter extends RecyclerView.Adapter<GymRecyclerAdapter.ViewHolder> {
    private List<GymDataHolder> gymDataHolderList;
    private Context context;


    public GymRecyclerAdapter(Context context, List<GymDataHolder> gymDataHolderList) {
        this.context = context;
        this.gymDataHolderList = gymDataHolderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_gyms_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.gymTitleTV.setText(String.format("%s, %s, %s",gymDataHolderList.get(position).getRelativeName().split("-")[3],gymDataHolderList.get(position).getRelativeName().split("-")[2], gymDataHolderList.get(position).getRelativeName().split("-")[1]));
        holder.gymCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GymInfoActivity.class);
                intent.putExtra("gymName", gymDataHolderList.get(position).getRelativeName());
                context.startActivity(intent);
            }
        });
        onVerified(holder,position);
    }
    @Override
    public int getItemCount() {
        return gymDataHolderList.size();
    }

    private void onVerified(final ViewHolder holder, final int position){
        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("rating")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(Double.parseDouble(dataSnapshot.getValue().toString())>5)
                        {
                            holder.rating.setText(" - ");
                            holder.rating.setBackgroundColor(context.getResources().getColor(R.color.Close));
                        }
                        else {
                            holder.rating.setText(dataSnapshot.getValue().toString()+"");
                            if (Double.parseDouble(dataSnapshot.getValue().toString()) > 4) {
                                holder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star5));
                            } else if (Double.parseDouble(dataSnapshot.getValue().toString()) > 3) {
                                holder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star4));
                            } else if (Double.parseDouble(dataSnapshot.getValue().toString()) > 2) {
                                holder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star3));
                            } else if (Double.parseDouble(dataSnapshot.getValue().toString()) > 1) {
                                holder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star2));
                            } else {
                                holder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star1));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.gymAddress.setText(dataSnapshot.getValue().toString().replace("#", ", "));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("timings")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.gymTimings.setText(Html.fromHtml(dataSnapshot.getValue().toString()));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue().toString().equals("O")) {
                            holder.status.setText("Open");
                            holder.status.setTextColor(context.getResources().getColor(R.color.Open));
                        } else if (dataSnapshot.getValue().toString().equals("C")) {
                            holder.status.setText("Close");
                            holder.status.setTextColor(context.getResources().getColor(R.color.Close));
                        } else if (dataSnapshot.getValue().toString().equals("F")) {
                            holder.status.setText("Full");
                            holder.status.setTextColor(context.getResources().getColor(R.color.Full));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("charge")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.charge.setText("Charge : "+dataSnapshot.getValue().toString()+" Credits");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("credits")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            holder.collectionTV.setText("Collection : 0 Credits");
                        }else {
                            holder.collectionTV.setText("Collection : "+dataSnapshot.getValue().toString()+" Credits");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+gymDataHolderList.get(position).getRelativeName()+"/visitations"+new SimpleDateFormat("/yyyy/MM", Locale.getDefault()).format(new Date())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            holder.dailyVisitsTV.setText("Monthly Visits : 0");
                        }else {
                            holder.dailyVisitsTV.setText("Monthly Visits : "+dataSnapshot.getChildrenCount());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("logo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //---------------------------Logo
                        if ((Boolean) dataSnapshot.getValue()) {
                            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolderList.get(position).getRelativeName() + "/logo.png");
                            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context).load(uri).into(holder.logoImageView);
                                }
                            });
                        } else {
                            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolderList.get(position).getRelativeName() + "/thumbnail/0.png");
                            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context).load(uri).into(holder.logoImageView);
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView gymTitleTV, dailyVisitsTV, collectionTV, gymAddress, gymTimings, rating, status, charge;
        private ImageView logoImageView;
        private CardView gymCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gymTitleTV = itemView.findViewById(R.id.gymTitleTV);
            dailyVisitsTV = itemView.findViewById(R.id.dailyVisitsTV);
            collectionTV = itemView.findViewById(R.id.collectionTV);
            gymAddress = itemView.findViewById(R.id.gymAddress);
            gymTimings = itemView.findViewById(R.id.gymTimings);
            rating = itemView.findViewById(R.id.rating);
            status = itemView.findViewById(R.id.status);
            charge = itemView.findViewById(R.id.recyclerCharge);
            logoImageView = itemView.findViewById(R.id.gym_logo);
            gymCard = itemView.findViewById(R.id.gymCard);
        }
    }
}
