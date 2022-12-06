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
import com.project.anygymadmin.DataHolder.UserDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.MainActivity;
import com.project.anygymadmin.R;
import com.project.anygymadmin.UserInfoActivity;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<UserDataHolder> userDataHolderList;
    public UsersAdapter(Context context, List<UserDataHolder> userDataHolderList) {
        this.context = context;
        this.userDataHolderList = userDataHolderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_users_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.userNameTV.setText(userDataHolderList.get(position).getName());
        FirebaseStorage.getInstance().getReference(myDataHolder.USER_PROFILE_IMAGES_PATH + userDataHolderList.get(position).getMobile()+".png")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.ic_male_user_placeholder).into(holder.userProfile);
            }
        });

        holder.usersCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataHolder.userDataHolder = userDataHolderList.get(position);
                Intent intent = new Intent(context, UserInfoActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDataHolderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView userProfile;
        private TextView userNameTV;
        private ConstraintLayout usersCL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            usersCL = itemView.findViewById(R.id.usersCL);
        }
    }
}
