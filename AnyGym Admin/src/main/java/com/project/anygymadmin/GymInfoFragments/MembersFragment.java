package com.project.anygymadmin.GymInfoFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymadmin.Adapter.MembersAdapter;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.project.anygymadmin.R;

import java.util.ArrayList;
import java.util.List;

public class MembersFragment extends Fragment {

    private List<String> userMobileList;
    private List<String> userDaysList;
    private RecyclerView membersRV;
    private MembersAdapter membersAdapter;
    private Toolbar myToolbar;

    public MembersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userDaysList = new ArrayList<>();
        userMobileList = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myToolbar = view.findViewById(R.id.myToolbar);
        myToolbar.setTitle(myDataHolder.gymDataHolder.getTitle());
        myToolbar.setNavigationIcon(R.drawable.ic_back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        membersRV = view.findViewById(R.id.membersRV);
        membersAdapter = new MembersAdapter(getContext(),userMobileList,userDaysList);
        membersRV.setAdapter(membersAdapter);
        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+myDataHolder.gymDataHolder.getRelativeName()+"/members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDaysList.clear();
                userMobileList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    userDaysList.add(snapshot.getValue().toString());
                    userMobileList.add(snapshot.getKey());
                }
                membersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}