package com.project.anygymowner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.project.anygymowner.DataHolder.myDataHolder;
import com.project.anygymowner.GymInfoActivity;
import com.project.anygymowner.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GymsAdapter extends RecyclerView.Adapter<GymsAdapter.ViewHolder> {
    private List<String> gymNamesList;
    private List<String> gymVerifyList;
    private Context context;
    private Intent intent;


    public GymsAdapter(List<String> gymNamesList, List<String> gymVerifyList) {
        this.gymNamesList = gymNamesList;
        this.gymVerifyList = gymVerifyList;
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
        if (gymVerifyList.get(position).equals("Y")){
            onVerified(holder,position);
        }else if (gymVerifyList.get(position).equals("R")){
            holder.rejectFL.setVisibility(View.VISIBLE);
            holder.gymCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Rejected...", Toast.LENGTH_SHORT).show();
                }
            });
            ((ViewGroup) holder.reviewFL.getParent()).removeView(holder.reviewFL);
        } else {
            holder.gymCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Reviewing...", Toast.LENGTH_SHORT).show();
                }
            });
            FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()).child("ownerGyms").child(gymNamesList.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue().toString().equals("Y")){
                        onVerified(holder,position);
                    } else if (dataSnapshot.getValue().toString().equals("R")){
                        holder.rejectFL.setVisibility(View.VISIBLE);
                        ((ViewGroup) holder.reviewFL.getParent()).removeView(holder.reviewFL);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        holder.gymTitleTV.setText(String.format("%s, %s, %s",gymNamesList.get(position).split("-")[3],gymNamesList.get(position).split("-")[2], gymNamesList.get(position).split("-")[1]));
    }
    @Override
    public int getItemCount() {
        return gymNamesList.size();
    }

    private void onVerified(final ViewHolder holder, final int position){
        holder.gymCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GymInfoActivity.class);
                intent.putExtra("gymName", gymNamesList.get(position));
                context.startActivity(intent);
            }
        });
        ((ViewGroup) holder.reviewFL.getParent()).removeView(holder.reviewFL);
        ((ViewGroup) holder.rejectFL.getParent()).removeView(holder.rejectFL);
        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymNamesList.get(position)).child("rating")
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

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymNamesList.get(position)).child("address")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.gymAddress.setText(dataSnapshot.getValue().toString().replace("#", ", "));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymNamesList.get(position)).child("timings")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.gymTimings.setText(Html.fromHtml(dataSnapshot.getValue().toString()));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymNamesList.get(position)).child("status")
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

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymNamesList.get(position)).child("charge")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.charge.setText("Charge : "+dataSnapshot.getValue().toString()+" Credits");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+gymNamesList.get(position)).child("credits")
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

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+gymNamesList.get(position)+"/visitations"+new SimpleDateFormat("/yyyy/MM", Locale.getDefault()).format(new Date())).addValueEventListener(new ValueEventListener() {
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

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymNamesList.get(position)).child("logo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //---------------------------Logo
                        if ((Boolean) dataSnapshot.getValue()) {
                            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymNamesList.get(position) + "/logo.png");
                            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context).load(uri).into(holder.logoImageView);
                                }
                            });
                        } else {
                            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymNamesList.get(position) + "/thumbnail/0.png");
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
        private FrameLayout reviewFL, rejectFL;
        private ImageView logoImageView;
        private CardView gymCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gymTitleTV = itemView.findViewById(R.id.gymTitleTV);
            dailyVisitsTV = itemView.findViewById(R.id.dailyVisitsTV);
            collectionTV = itemView.findViewById(R.id.collectionTV);
            reviewFL = itemView.findViewById(R.id.reviewFL);
            rejectFL = itemView.findViewById(R.id.rejectFL);
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
