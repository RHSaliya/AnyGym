package com.project.anygym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygym.Adapters.MembershipAdapter;
import com.project.anygym.Adapters.TransactionsAdapter;
import com.project.anygym.DataHolder.myDataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MembershipActivity extends AppCompatActivity {

    private RecyclerView membershipRecycler;
    private List<String> membershipsList;
    private MembershipAdapter membershipAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        membershipRecycler = findViewById(R.id.membershipRecyclerView);
        membershipsList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        membershipRecycler.setLayoutManager(linearLayoutManager);
        membershipRecycler.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        membershipAdapter = new MembershipAdapter(MembershipActivity.this,membershipsList);
        membershipRecycler.setAdapter(membershipAdapter);


        FirebaseDatabase.getInstance().getReference(myDataHolder.USER_MEMBERSHIP_DATABASE_PATH+myDataHolder.userDataHolder.getMobile()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    membershipsList.add(snapshot.getKey()+"#"+snapshot.getValue());
                }
                membershipAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
