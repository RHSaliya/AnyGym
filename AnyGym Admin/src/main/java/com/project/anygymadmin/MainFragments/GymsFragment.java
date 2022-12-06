package com.project.anygymadmin.MainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.project.anygymadmin.DataHolder.GymDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.R;

import java.util.ArrayList;
import java.util.List;


public class GymsFragment extends Fragment {
    private RecyclerView gymsRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GymRecyclerAdapter gymsAdapter;
    private List<GymDataHolder> gymDataHolderList;

    public GymsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gyms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gymDataHolderList = new ArrayList<>();
        //-----------------------------------------Gyms Recycler
        mLayoutManager = new LinearLayoutManager(getContext());
        gymsRecyclerView = view.findViewById(R.id.gyms_recycler_view);
        gymsRecyclerView.setLayoutManager(mLayoutManager);
        gymsRecyclerView.setHasFixedSize(true);
        gymsAdapter = new GymRecyclerAdapter(getContext(),gymDataHolderList);
        gymsRecyclerView.setAdapter(gymsAdapter);
        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gymDataHolderList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    gymDataHolderList.add(snapshot.getValue(GymDataHolder.class));
                }
                gymsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
