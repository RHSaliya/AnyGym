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
import com.project.anygymadmin.VerifyActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OwnerInfoGymRecyclerAdapter extends RecyclerView.Adapter<OwnerInfoGymRecyclerAdapter.ViewHolder> {
    private List<String> gymsList,verifiedList;
    private Context context;
    private GymDataHolder gymDataHolder;


    public OwnerInfoGymRecyclerAdapter(Context context, List<String> gymsList, List<String> verifiedList) {
        this.context = context;
        this.gymsList = gymsList;
        this.verifiedList = verifiedList;
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
        holder.gymTitleTV.setText(String.format("%s, %s, %s",gymsList.get(position).split("-")[3],gymsList.get(position).split("-")[2], gymsList.get(position).split("-")[1]));
        if (verifiedList.get(position).equals("Y")){
            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymsList.get(position)).child("rating")
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

            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymsList.get(position)).child("address")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.gymAddress.setText(dataSnapshot.getValue().toString().replace("#", ", "));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymsList.get(position)).child("timings")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.gymTimings.setText(Html.fromHtml(dataSnapshot.getValue().toString()));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymsList.get(position)).child("status")
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

            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymsList.get(position)).child("charge")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.charge.setText("Charge : "+dataSnapshot.getValue().toString()+" Credits");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+gymsList.get(position)).child("credits")
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

            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+gymsList.get(position)+"/visitations"+new SimpleDateFormat("/yyyy/MM", Locale.getDefault()).format(new Date())).addValueEventListener(new ValueEventListener() {
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

            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+gymsList.get(position)).child("logo")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //---------------------------Logo
                            if ((Boolean) dataSnapshot.getValue()) {
                                myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymsList.get(position) + "/logo.png");
                                myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(context).load(uri).into(holder.logoImageView);
                                    }
                                });
                            } else {
                                myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymsList.get(position) + "/thumbnail/0.png");
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
            holder.gymCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GymInfoActivity.class);
                    intent.putExtra("gymName", gymsList.get(position));
                    context.startActivity(intent);
                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE+gymsList.get(position)).child("address")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.gymAddress.setText(dataSnapshot.getValue().toString().replace("#", ", "));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE+gymsList.get(position)).child("timings")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.gymTimings.setText(Html.fromHtml(dataSnapshot.getValue().toString()));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE+gymsList.get(position)).child("charge")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.charge.setText("Charge : "+dataSnapshot.getValue().toString()+" Credits");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE+gymsList.get(position)).child("logo")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //---------------------------Logo
                            if ((Boolean) dataSnapshot.getValue()) {
                                myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymsList.get(position) + "/logo.png");
                                myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(context).load(uri).into(holder.logoImageView);
                                    }
                                });
                            } else {
                                myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymsList.get(position) + "/thumbnail/0.png");
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
            holder.ratingCard.setVisibility(View.GONE);
            holder.collectionTV.setVisibility(View.GONE);
            if (verifiedList.get(position).equals("R")){
                holder.dailyVisitsTV.setText("Rejected");
            } else {
                holder.dailyVisitsTV.setText("Not Verified");
            }
            holder.gymCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE+gymsList.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            myDataHolder.gymDataHolder = dataSnapshot.getValue(GymDataHolder.class);
                            v.getContext().startActivity(new Intent(v.getContext(), VerifyActivity.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return gymsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView gymTitleTV, dailyVisitsTV, collectionTV, gymAddress, gymTimings, rating, status, charge;
        private ImageView logoImageView;
        private CardView gymCard, ratingCard;
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
            ratingCard = itemView.findViewById(R.id.ratingCard);
        }
    }
}
