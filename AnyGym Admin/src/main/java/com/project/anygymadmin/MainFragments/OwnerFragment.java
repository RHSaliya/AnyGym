package com.project.anygymadmin.MainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymadmin.Adapter.OwnersAdapter;
import com.project.anygymadmin.Adapter.UsersAdapter;
import com.project.anygymadmin.DataHolder.OwnerDataHolder;
import com.project.anygymadmin.DataHolder.UserDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.R;

import java.util.ArrayList;
import java.util.List;

public class OwnerFragment extends Fragment {
    private RecyclerView ownersRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<OwnerDataHolder> ownerDataHolderList;
    private OwnersAdapter ownersAdapter;
    public OwnerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_owner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getContext());
        ownersRecyclerView = view.findViewById(R.id.owners_recycler_view);
        ownersRecyclerView.setLayoutManager(mLayoutManager);
        ownerDataHolderList = new ArrayList<>();
        ownersAdapter = new OwnersAdapter(getContext(),ownerDataHolderList);
        ownersRecyclerView.setAdapter(ownersAdapter);
        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ownerDataHolderList.add(snapshot.getValue(OwnerDataHolder.class));
                }
                ownersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}