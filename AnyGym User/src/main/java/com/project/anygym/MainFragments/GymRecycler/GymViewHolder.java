package com.project.anygym.MainFragments.GymRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.project.anygym.R;

public class GymViewHolder extends RecyclerView.ViewHolder {
    public TextView gymName, gymAddress,gymTimings,rating,status,charge;
    public ConstraintLayout gyms_recycler_layout;
    public ImageView logoImageView;

    public GymViewHolder(@NonNull View itemView) {
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
