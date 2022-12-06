package com.project.anygymadmin.MainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymadmin.Adapter.UsersAdapter;
import com.project.anygymadmin.DataHolder.UserDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.R;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {
    private RecyclerView usersRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserDataHolder> userDataHolderList;
    private UsersAdapter usersAdapter;
    public UsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //-----------------------------------------Gyms Recycler
        mLayoutManager = new LinearLayoutManager(getContext());
        usersRecyclerView = view.findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(mLayoutManager);
        userDataHolderList = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(),userDataHolderList);
        usersRecyclerView.setAdapter(usersAdapter);

        FirebaseDatabase.getInstance().getReference(myDataHolder.USER_DATABASE_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    userDataHolderList.add(snapshot.getValue(UserDataHolder.class));
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
