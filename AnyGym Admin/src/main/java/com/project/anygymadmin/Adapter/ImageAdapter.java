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
import com.project.anygymadmin.VerifyActivity;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private int imgCount;


    public ImageAdapter(Context context, int imgCount) {
        this.context = context;
        this.imgCount = imgCount;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_verify_image_item, parent, false);
        return new ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull final ImageAdapter.ViewHolder holder,final int position) {
        myDataHolder.gymImgsStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH+myDataHolder.gymDataHolder.getRelativeName()+"/thumbnail/"+position+".png");
        myDataHolder.gymImgsStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Glide.with(context).load(uri).into(holder.imageView);
                }catch (IllegalArgumentException exc){}
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyActivity.fullScreenImageRV.setVisibility(View.VISIBLE);
                VerifyActivity.backIV.setVisibility(View.VISIBLE);
                VerifyActivity.fullScreenImageRV.getLayoutManager().scrollToPosition(position);
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
            imageView = itemView.findViewById(R.id.imageItem);
        }
    }
}
