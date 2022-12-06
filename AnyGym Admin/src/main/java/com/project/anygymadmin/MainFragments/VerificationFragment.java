package com.project.anygymadmin.MainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.project.anygymadmin.Adapter.GymRecyclerAdapter;
import com.project.anygymadmin.Adapter.VerifyGymRecyclerAdapter;
import com.project.anygymadmin.DataHolder.GymDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.R;

import java.util.ArrayList;
import java.util.List;


public class VerificationFragment extends Fragment {
    private RecyclerView gymsRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private VerifyGymRecyclerAdapter gymRecyclerAdapter;
    private List<GymDataHolder> gymDataHolderList;
    private List<String> gymNameList;
    private TextView nothingTV;


    public VerificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nothingTV = view.findViewById(R.id.nothingTV);
        //-----------------------------------------Gyms Recycler
        mLayoutManager = new LinearLayoutManager(getContext());
        gymDataHolderList = new ArrayList<>();
        gymNameList = new ArrayList<>();
        gymsRecyclerView = view.findViewById(R.id.verify_gyms_recycler_view);

        gymsRecyclerView.setLayoutManager(mLayoutManager);
        gymsRecyclerView.setHasFixedSize(true);
        gymsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        myDataHolder.gymVerifyDBREF = FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE);
        gymRecyclerAdapter = new VerifyGymRecyclerAdapter(getContext(), gymDataHolderList);

        gymsRecyclerView.setAdapter(gymRecyclerAdapter);

        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0){
                    nothingTV.setVisibility(View.VISIBLE);
                }else{
                    nothingTV.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        gymDataHolderList.add(snapshot.getValue(GymDataHolder.class));
                    }
                    gymRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
