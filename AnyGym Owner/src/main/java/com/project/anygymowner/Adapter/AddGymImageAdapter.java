package com.project.anygymowner.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.anygymowner.AddGymInfoActivity;
import com.project.anygymowner.R;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class AddGymImageAdapter extends RecyclerView.Adapter<AddGymImageAdapter.ViewHolder> {
    public static List<File> imageFileList;
    private Context context;
    private Intent intent;
    static int pos1=-1;
    public static final int REQ_GALLERY_IMAGE_RECYCLER = 4;


    public AddGymImageAdapter(List<File> imageFileList) {
        this.imageFileList = imageFileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (position == imageFileList.size() - 1 && imageFileList.size() < 9) {
            holder.imageView.setImageResource(R.drawable.ic_add_image);
        } else {
            ///holder.imageView.setImageBitmap(imageFileList.get(position));
            Glide.with(context).asBitmap().load(imageFileList.get(position)).centerCrop().override(400,400).into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == imageFileList.size() - 1 && imageFileList.size() < 9) {
                    //-----------------------------------------Open Gallery
                    intent = new Intent();
                    intent.setType("image/*");
                    //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((Activity) context).startActivityForResult(intent, REQ_GALLERY_IMAGE_RECYCLER);
                }else if(pos1!=-1){
                    Collections.swap(AddGymInfoActivity.imageFileList,pos1,position);
                    Collections.swap(AddGymInfoActivity.imageUriList,pos1,position);
                    AddGymInfoActivity.addGymImageAdapter.notifyDataSetChanged();
                    AddGymInfoActivity.btnCancelSwap.setVisibility(View.GONE);
                    pos1=-1;
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
                    builder.setTitle("Showcase image")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new File(AddGymInfoActivity.imageFileList.get(position).getAbsolutePath().replace(".jpg", "t.jpg")).delete();
                                    AddGymInfoActivity.imageFileList.get(position).delete();
                                    AddGymInfoActivity.imageFileList.remove(position);
                                    AddGymInfoActivity.imageUriList.remove(position);
                                    AddGymInfoActivity.addGymImageAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Swap", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (pos1==-1){
                                        pos1=position;
                                    }
                                    AddGymInfoActivity.btnCancelSwap.setVisibility(View.VISIBLE);
                                    AddGymInfoActivity.btnCancelSwap.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AddGymInfoActivity.btnCancelSwap.setVisibility(View.GONE);
                                            pos1=-1;
                                        }
                                    });
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return imageFileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerImage);
        }
    }
}
