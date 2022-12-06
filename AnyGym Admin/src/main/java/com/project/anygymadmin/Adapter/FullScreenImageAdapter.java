package com.project.anygymadmin.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.R;

public class FullScreenImageAdapter extends RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder> {
    private Context context;
    private int imgCount;

    public FullScreenImageAdapter(Context context, int imgCount) {
        this.context = context;
        this.imgCount = imgCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_full_screen_image_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        myDataHolder.gymImgsStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH+myDataHolder.gymDataHolder.getRelativeName()+"/"+position+".png");
        myDataHolder.gymImgsStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fullScreenImageView);
        }
    }
}
