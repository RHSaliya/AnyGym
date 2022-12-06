package com.project.anygymowner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymowner.Adapter.CommentAdapter;
import com.project.anygymowner.DataHolder.myDataHolder;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentsRV;
    private CommentAdapter commentAdapter;
    private LinearLayoutManager commentLinearLayoutManager;
    private List<String> commentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentsRV = findViewById(R.id.commentsRV);
        commentList = new ArrayList<>();
        commentLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentsRV.setLayoutManager(commentLinearLayoutManager);
        commentAdapter = new CommentAdapter(CommentActivity.this,commentList);
        commentsRV.setAdapter(commentAdapter);
        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    commentList.add(snapshot.getValue().toString());
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}