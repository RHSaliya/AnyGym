package com.project.anygym.Adapters;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygym.DataHolder.GymDataHolder;
import com.project.anygym.DataHolder.myDataHolder;
import com.project.anygym.GymInfoActivity;
import com.project.anygym.R;

import java.util.List;

public class GymRecyclerAdapter extends RecyclerView.Adapter<GymRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<GymDataHolder> gymDataHolderList;
    public GymRecyclerAdapter(Context context, List<GymDataHolder> gymDataHolderList) {
        this.context = context;
        this.gymDataHolderList = gymDataHolderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_gyms_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder gymViewHolder, final int position) {
        gymViewHolder.gymAddress.setText(gymDataHolderList.get(position).getAddress().replace("#", ", "));
        gymViewHolder.gymName.setText(gymDataHolderList.get(position).getRelativeName().split("-")[3]+", "+gymDataHolderList.get(position).getRelativeName().split("-")[2]);
        gymViewHolder.charge.setText(gymDataHolderList.get(position).getCharge()+" Credits");
        gymViewHolder.gymTimings.setText(Html.fromHtml(gymDataHolderList.get(position).getTimings()));

        if(gymDataHolderList.get(position).getRating()>5)
        {
            gymViewHolder.rating.setText(" - ");
            gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Close));
        }
        else {
            gymViewHolder.rating.setText(gymDataHolderList.get(position).getRating()+"");
            if (gymDataHolderList.get(position).getRating() > 4) {
                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star5));
            } else if (gymDataHolderList.get(position).getRating() > 3) {
                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star4));
            } else if (gymDataHolderList.get(position).getRating() > 2) {
                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star3));
            } else if (gymDataHolderList.get(position).getRating() > 1) {
                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star2));
            } else {
                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star1));
            }
        }

        if (gymDataHolderList.get(position).getStatus().equals("O")) {
            gymViewHolder.status.setText("Open");
            gymViewHolder.status.setTextColor(context.getResources().getColor(R.color.Open));
        } else if (gymDataHolderList.get(position).getStatus().equals("C")) {
            gymViewHolder.status.setText("Close");
            gymViewHolder.status.setTextColor(context.getResources().getColor(R.color.Close));
        } else if (gymDataHolderList.get(position).getStatus().equals("F")) {
            gymViewHolder.status.setText("Full");
            gymViewHolder.status.setTextColor(context.getResources().getColor(R.color.Full));
        }

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("rating")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        gymDataHolderList.get(position).setRating(Double.parseDouble(dataSnapshot.getValue().toString()));
                        if(gymDataHolderList.get(position).getRating()>5)
                        {
                            gymViewHolder.rating.setText(" - ");
                            gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Close));
                        }
                        else {
                            gymViewHolder.rating.setText(gymDataHolderList.get(position).getRating()+"");
                            if (gymDataHolderList.get(position).getRating() > 4) {
                                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star5));
                            } else if (gymDataHolderList.get(position).getRating() > 3) {
                                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star4));
                            } else if (gymDataHolderList.get(position).getRating() > 2) {
                                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star3));
                            } else if (gymDataHolderList.get(position).getRating() > 1) {
                                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star2));
                            } else {
                                gymViewHolder.rating.setBackgroundColor(context.getResources().getColor(R.color.Star1));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymDataHolderList.get(position).getRelativeName()).child("status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        gymDataHolderList.get(position).setStatus(dataSnapshot.getValue().toString());
                        if (gymDataHolderList.get(position).getStatus().equals("O")) {
                            gymViewHolder.status.setText("Open");
                            gymViewHolder.status.setTextColor(context.getResources().getColor(R.color.Open));
                        } else if (gymDataHolderList.get(position).getStatus().equals("C")) {
                            gymViewHolder.status.setText("Close");
                            gymViewHolder.status.setTextColor(context.getResources().getColor(R.color.Close));
                        } else if (gymDataHolderList.get(position).getStatus().equals("F")) {
                            gymViewHolder.status.setText("Full");
                            gymViewHolder.status.setTextColor(context.getResources().getColor(R.color.Full));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //---------------------------Logo
        if (gymDataHolderList.get(position).isLogo()) {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolderList.get(position).getRelativeName() + "/logo.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(gymViewHolder.logoImageView);
                }
            });
        } else {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolderList.get(position).getRelativeName() + "/thumbnail/0.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(gymViewHolder.logoImageView);
                }
            });
        }

        //------------------------Click event
        gymViewHolder.gyms_recycler_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataHolder.gymDataHolder = gymDataHolderList.get(position);
                v.getContext().startActivity(new Intent(v.getContext(), GymInfoActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return gymDataHolderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView gymName, gymAddress,gymTimings,rating,status,charge;
        public ConstraintLayout gyms_recycler_layout;
        public ImageView logoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gymName = itemView.findViewById(R.id.gymTitle);
            gymAddress = itemView.findViewById(R.id.gymAddress);
            gymTimings = itemView.findViewById(R.id.gymTimings);
            rating = itemView.findViewById(R.id.rating);
            status = itemView.findViewById(R.id.status);
            charge = itemView.findViewById(R.id.recyclerCharge);
            logoImageView = itemView.findViewById(R.id.gym_logo);
            gyms_recycler_layout = itemView.findViewById(R.id.gyms_recycler_layout);
        }
    }
}
