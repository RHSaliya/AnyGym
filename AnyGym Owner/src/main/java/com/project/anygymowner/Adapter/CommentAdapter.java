package com.project.anygymowner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.project.anygymowner.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<String> commentList;

    public CommentAdapter(Context context, List<String> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_comments_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        if (commentList.get(position).split("#").length==2){
            holder.gymCommentTV.setText("For You");
            holder.gymCommentTV.setText(commentList.get(position).split("#")[1]);
            holder.gymCommentTimeTV.setText(commentList.get(position).split("#")[0]);
        } else if(commentList.get(position).split("#").length==3) {
            holder.gymNameCommentTV.setText(commentList.get(position).split("#")[0].split("-")[3]+", "+commentList.get(position).split("#")[0].split("-")[2]+", "+commentList.get(position).split("#")[0].split("-")[1]+", "+commentList.get(position).split("#")[0].split("-")[0]);
            holder.gymCommentTV.setText(commentList.get(position).split("#")[2]);
            holder.gymCommentTimeTV.setText(commentList.get(position).split("#")[1]);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView gymNameCommentTV, gymCommentTV, gymCommentTimeTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gymNameCommentTV = itemView.findViewById(R.id.gymNameCommentTV);
            gymCommentTV = itemView.findViewById(R.id.gymCommentTV);
            gymCommentTimeTV = itemView.findViewById(R.id.gymCommentTimeTV);
        }
    }
}