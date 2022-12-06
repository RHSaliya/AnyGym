package com.project.anygym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class FirstTimeActivity extends AppCompatActivity {

    private List<String> stateList, cityList;
    private Spinner stateSpinner,citySpinner;
    private String state;
    private ArrayAdapter<String> stateAdapter, cityAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        stateList = new ArrayList<>();
        cityList = new ArrayList<>();

        stateAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, stateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        cityAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        FirebaseDatabase.getInstance().getReference("Availability/States").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    stateList.add(snapshot.getKey());
                    stateAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = stateList.get(position);
                cityList.clear();
                cityList.add("Loading...");
                cityAdapter.notifyDataSetChanged();
                FirebaseDatabase.getInstance().getReference("Availability/States/"+state).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cityList.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            cityList.add(snapshot.getKey());
                        }
                        cityAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("ApplySharedPref")
    public void onApply(View view) {
        if (!citySpinner.getSelectedItem().equals("") && !state.equals("")){
            getSharedPreferences("UserPref", MODE_PRIVATE).edit().putString("ref",state+"-"+citySpinner.getSelectedItem()).commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toasty.warning(getApplicationContext(), "Select Both", Toast.LENGTH_SHORT, true).show();
        }
    }
}
