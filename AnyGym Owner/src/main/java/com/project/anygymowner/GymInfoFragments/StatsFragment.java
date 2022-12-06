package com.project.anygymowner.GymInfoFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymowner.Adapter.ActivityAdapter;
import com.project.anygymowner.DataHolder.myDataHolder;
import com.project.anygymowner.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatsFragment extends Fragment implements View.OnClickListener {
    private TextView activeMonthTV, yearTV, noVisitsTV;
    private TextView month1, month2, month3, month4, month5, month6, month7, month8, month9, month10, month11, month12;
    private HorizontalScrollView monthScrollView;
    private Spinner yearSpinner;
    private List<Integer> categories;
    private List<String> gymActivityList;
    private RecyclerView recyclerViewActivity;
    private ActivityAdapter activityAdapter;
    private DataSnapshot originalData;
    private int i;
    private Toolbar myToolbar;

    public StatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myToolbar = view.findViewById(R.id.myToolbarStats);
        myToolbar.setTitle(myDataHolder.gymDataHolder.getTitle());
        myToolbar.setNavigationIcon(R.drawable.ic_back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        monthScrollView = view.findViewById(R.id.monthScrollView);
        yearSpinner = view.findViewById(R.id.yearTypeSpinner);
        recyclerViewActivity = view.findViewById(R.id.recyclerViewActivity);
        yearTV = view.findViewById(R.id.yearTV);
        noVisitsTV = view.findViewById(R.id.noVisitsTV);

        gymActivityList = new ArrayList<>();
        yearTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearSpinner.performClick();
            }
        });
        activityAdapter = new ActivityAdapter(getContext(), gymActivityList);
        recyclerViewActivity.setAdapter(activityAdapter);
        recyclerViewActivity.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        monthClick(view);
        spinner();
    }

    private void monthClick(View view) {
        month1 = view.findViewById(R.id.month1);
        month2 = view.findViewById(R.id.month2);
        month3 = view.findViewById(R.id.month3);
        month4 = view.findViewById(R.id.month4);
        month5 = view.findViewById(R.id.month5);
        month6 = view.findViewById(R.id.month6);
        month7 = view.findViewById(R.id.month7);
        month8 = view.findViewById(R.id.month8);
        month9 = view.findViewById(R.id.month9);
        month10 = view.findViewById(R.id.month10);
        month11 = view.findViewById(R.id.month11);
        month12 = view.findViewById(R.id.month12);
        month1.setOnClickListener(this);
        month2.setOnClickListener(this);
        month3.setOnClickListener(this);
        month4.setOnClickListener(this);
        month5.setOnClickListener(this);
        month6.setOnClickListener(this);
        month7.setOnClickListener(this);
        month8.setOnClickListener(this);
        month9.setOnClickListener(this);
        month10.setOnClickListener(this);
        month11.setOnClickListener(this);
        month12.setOnClickListener(this);

        switch (Calendar.getInstance().get(Calendar.MONTH)) {
            case 0: {
                activeMonthTV = month1;
                break;
            }
            case 1: {
                activeMonthTV = month2;
                break;
            }
            case 2: {
                activeMonthTV = month3;
                break;
            }
            case 3: {
                activeMonthTV = month4;
                break;
            }
            case 4: {
                activeMonthTV = month5;
                break;
            }
            case 5: {
                activeMonthTV = month6;
                break;
            }
            case 6: {
                activeMonthTV = month7;
                break;
            }
            case 7: {
                activeMonthTV = month8;
                break;
            }
            case 8: {
                activeMonthTV = month9;
                break;
            }
            case 9: {
                activeMonthTV = month10;
                break;
            }
            case 10: {
                activeMonthTV = month11;
                break;
            }
            case 11: {
                activeMonthTV = month12;
                break;
            }
        }

    }

    private void spinner() {
        categories = new ArrayList<>();
        categories.add(2019);
        categories.add(2020);
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.year_spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.year_spinner_item);
        yearSpinner.setAdapter(dataAdapter);
        yearSpinner.setSelection(categories.indexOf(Calendar.getInstance().get(Calendar.YEAR)));
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearTV.setText(String.valueOf(categories.get(position)));
                activeMonthTV.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (activeMonthTV == v && gymActivityList.size() != 0) {
            return;
        }
        if (activeMonthTV != null) {
            activeMonthTV.setBackgroundColor(Color.WHITE);
            activeMonthTV.setTextColor(Color.BLACK);
            gymActivityList.clear();
            activityAdapter.notifyDataSetChanged();
        }
        activeMonthTV = (TextView) v;
        activeMonthTV.setBackgroundResource(R.drawable.button);
        activeMonthTV.setTextColor(Color.WHITE);
        monthScrollView.smoothScrollTo(((activeMonthTV.getLeft() + activeMonthTV.getRight() - monthScrollView.getWidth()) / 2), 0);

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName() + "/visitations/" + yearSpinner.getSelectedItem().toString() + "/" + activeMonthTV.getHint().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gymActivityList.clear();
                originalData = dataSnapshot;
                if (dataSnapshot.getChildrenCount() == 0) {
                    recyclerViewActivity.setVisibility(View.GONE);
                    noVisitsTV.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewActivity.setVisibility(View.VISIBLE);
                    noVisitsTV.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        gymActivityList.add(snapshot.getValue().toString());
                    }
                    activityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onSearch(String query, int flag) {
        if (query.equals("")) {
            gymActivityList.clear();
            for (DataSnapshot snapshot : originalData.getChildren()) {
                gymActivityList.add(snapshot.getValue().toString());
            }
        } else if (!query.equals("") && flag == 1) {
            gymActivityList.clear();
            for (DataSnapshot snapshot : originalData.getChildren()) {
                if (snapshot.getValue().toString().toUpperCase().replace("#"," ").replace(","," ").contains(query.toUpperCase())) {
                    gymActivityList.add(snapshot.getValue().toString());
                }
            }
            if (gymActivityList.size() == 0) {
                recyclerViewActivity.setVisibility(View.GONE);
                noVisitsTV.setVisibility(View.VISIBLE);
            } else {
                recyclerViewActivity.setVisibility(View.VISIBLE);
                noVisitsTV.setVisibility(View.GONE);
            }
        }
        activityAdapter.notifyDataSetChanged();
    }
}
