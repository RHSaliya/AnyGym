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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymadmin.DataHolder.GymDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.R;
import com.project.anygymadmin.VerifyActivity;

import java.util.List;

public class VerifyGymRecyclerAdapter extends RecyclerView.Adapter<VerifyGymRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<GymDataHolder> gymDataHolderList;

    public VerifyGymRecyclerAdapter(Context context, List<GymDataHolder> gymDataHolderList) {
        this.context = context;
        this.gymDataHolderList = gymDataHolderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_verify_gyms_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder gymViewHolder, final int position) {
        gymViewHolder.gymAddress.setText(gymDataHolderList.get(position).getAddress().replace("#", ", "));
        gymViewHolder.gymName.setText(gymDataHolderList.get(position).getTitle());
        gymViewHolder.charge.setText(gymDataHolderList.get(position).getCharge()+" Credits");
        gymViewHolder.gymTimings.setText(Html.fromHtml(gymDataHolderList.get(position).getTimings().replace("#","\n")));

        //---------------------------Logo
        if (gymDataHolderList.get(position).isLogo()) {
            myDataHolder.logoStorageREF = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolderList.get(position).getRelativeName() + "/logo.png");
            myDataHolder.logoStorageREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(gymViewHolder.logoImageView);
                }
            });
        } else {
            myDataHolder.logoStorageREF = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolderList.get(position).getRelativeName() + "/thumbnail/0.png");
            myDataHolder.logoStorageREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                //myDataHolder.logoDrawable = gymViewHolder.logoImageView.getDrawable();
                v.getContext().startActivity(new Intent(v.getContext(), VerifyActivity.class));
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
