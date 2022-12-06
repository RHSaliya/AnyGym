package com.project.anygymowner.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymowner.DataHolder.UserDataHolder;
import com.project.anygymowner.DataHolder.myDataHolder;
import com.project.anygymowner.R;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private Context context;
    private List<String> userMobileList;
    private List<String> userDaysList;
    public MembersAdapter(Context context,List<String> userMobileList, List<String> userDaysList) {
        this.context = context;
        this.userDaysList = userDaysList;
        this.userMobileList = userMobileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_members_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        FirebaseDatabase.getInstance().getReference(myDataHolder.USER_DATABASE_PATH+userMobileList.get(position)).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.userNameTV.setText(dataSnapshot.getValue().toString());
                holder.membershipDays.setText(userDaysList.get(position)+"\nDays");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseStorage.getInstance().getReference(myDataHolder.USER_PROFILE_IMAGES_PATH + userMobileList.get(position)+".png")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.ic_male_user_placeholder).into(holder.userProfile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userMobileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView userProfile;
        private TextView userNameTV, membershipDays;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            membershipDays = itemView.findViewById(R.id.membershipDays);
        }
    }
}
