package com.project.anygymadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymadmin.Adapter.MembershipAdapter;
import com.project.anygymadmin.DataHolder.myDataHolder;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    private ImageView userImageIV;
    private TextView userNameTV,userMobileTV,userInfoTV,userCreditsTV;

    private RecyclerView membershipRecycler;
    private List<String> membershipsList;
    private MembershipAdapter membershipAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar myToolbarUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        userImageIV = findViewById(R.id.userImageIV);
        userNameTV = findViewById(R.id.userNameTV);
        userMobileTV = findViewById(R.id.userMobileTV);
        userInfoTV = findViewById(R.id.userInfoTV);
        userCreditsTV = findViewById(R.id.userCreditsTV);

        myToolbarUserInfo = findViewById(R.id.myToolbarUserInfo);
        setSupportActionBar(myToolbarUserInfo);
        myToolbarUserInfo.setTitle("User Info");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseStorage.getInstance().getReference(myDataHolder.USER_PROFILE_IMAGES_PATH + myDataHolder.userDataHolder.getMobile()+".png")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).placeholder(R.drawable.ic_male_user_placeholder).into(userImageIV);
            }
        });

        userNameTV.setText(myDataHolder.userDataHolder.getName());
        userMobileTV.setText(myDataHolder.userDataHolder.getMobile());
        userNameTV.setText(myDataHolder.userDataHolder.getName());
        userInfoTV.setText("Age : "+myDataHolder.userDataHolder.getAge()+", Gender : "+myDataHolder.userDataHolder.getGender()
        +"\nHeight : "+myDataHolder.userDataHolder.getHeight()+", Weight : "+myDataHolder.userDataHolder.getWeight()+"Kg");
        userCreditsTV.setText("Balance : "+myDataHolder.userDataHolder.getCredit()+" Credits");

        FirebaseDatabase.getInstance().getReference(myDataHolder.USER_DATABASE_PATH+myDataHolder.userDataHolder.getMobile()).child("credit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCreditsTV.setText("Balance : "+dataSnapshot.getValue().toString()+" Credits");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        membershipRecycler = findViewById(R.id.membershipRecyclerView);
        membershipsList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        membershipRecycler.setLayoutManager(linearLayoutManager);
        membershipRecycler.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        membershipAdapter = new MembershipAdapter(UserInfoActivity.this,membershipsList);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}