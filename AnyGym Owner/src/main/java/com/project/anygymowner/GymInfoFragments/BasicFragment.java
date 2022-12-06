package com.project.anygymowner.GymInfoFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.chahinem.pageindicator.PageIndicator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymowner.Adapter.ImageAdapter;
import com.project.anygymowner.AddGymInfoActivity;
import com.project.anygymowner.DataHolder.myDataHolder;
import com.project.anygymowner.R;

import es.dmoral.toasty.Toasty;

public class BasicFragment extends Fragment{
    private ImageAdapter imageAdapter;
    private RecyclerView imageRV;
    private LinearLayoutManager layoutManager, fullScreenLayoutManager;
    private PageIndicator pageIndicator;
    private TextView gymTitleTV, gymAddressTV, gymDescriptionTV, gymTimingsTV, gymCreditsTV, statusTV, ratingTV, collectionTV;
    private ImageView gymLogoIV;
    private Toolbar gym_detail_toolbar;
    private Button btnCollect;
    private long collection;
    private RadioGroup statusRG;
    private RadioButton rbOpen, rbClose, rbFull;

    public BasicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gym_detail_toolbar = view.findViewById(R.id.gym_detail_toolbar);
        gym_detail_toolbar.setTitle(myDataHolder.gymDataHolder.getTitle());
        gym_detail_toolbar.setNavigationIcon(R.drawable.ic_back);
        gym_detail_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        gymTitleTV = view.findViewById(R.id.gymTitleTV);
        gymAddressTV = view.findViewById(R.id.gymAddressTV);
        gymDescriptionTV = view.findViewById(R.id.gymDescriptionTV);
        gymTimingsTV = view.findViewById(R.id.gymTimingsTV);
        gymCreditsTV = view.findViewById(R.id.gymCreditsTV);
        statusTV = view.findViewById(R.id.statusTV);
        ratingTV = view.findViewById(R.id.ratingTV);
        gymLogoIV = view.findViewById(R.id.gymLogoIV);
        collectionTV = view.findViewById(R.id.collectionGymInfoTV);
        btnCollect = view.findViewById(R.id.btnCollect);
        statusRG = view.findViewById(R.id.statusRG);
        rbClose = view.findViewById(R.id.rbClose);
        rbOpen = view.findViewById(R.id.rbOpen);
        rbFull = view.findViewById(R.id.rbFull);

        //------------------------------Image Recycler
        imageRV = view.findViewById(R.id.imageRV);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        imageRV.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(imageRV);

        pageIndicator = view.findViewById(R.id.pageIndicator);

        imageAdapter = new ImageAdapter(getContext(), Integer.parseInt(myDataHolder.about.split("#")[1]));
        imageRV.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        pageIndicator.attachTo(imageRV);
        gymDescriptionTV.setText(myDataHolder.about.split("#")[0]);

        if (myDataHolder.gymDataHolder.isLogo()) {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/logo.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext()).load(uri).into(gymLogoIV);
                }
            });
        } else {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/0.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getContext()).load(uri).centerCrop().into(gymLogoIV);
                }
            });
        }
        gymTitleTV.setText(myDataHolder.gymDataHolder.getTitle());
        gymAddressTV.setText(myDataHolder.gymDataHolder.getAddress().replace("#", ", "));
        gymTimingsTV.setText(Html.fromHtml(myDataHolder.gymDataHolder.getTimings()));
        gymCreditsTV.setText(myDataHolder.gymDataHolder.getCharge() + " Credits");

        if (myDataHolder.gymDataHolder.getRating() > 5) {
            ratingTV.setText(" - ");
            ratingTV.setBackgroundColor(getResources().getColor(R.color.Close));
        } else {
            ratingTV.setText(myDataHolder.gymDataHolder.getRating() + "");
            if (myDataHolder.gymDataHolder.getRating() > 4) {
                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star5));
            } else if (myDataHolder.gymDataHolder.getRating() > 3) {
                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star4));
            } else if (myDataHolder.gymDataHolder.getRating() > 2) {
                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star3));
            } else if (myDataHolder.gymDataHolder.getRating() > 1) {
                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star2));
            } else {
                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star1));
            }
        }

        if (myDataHolder.gymDataHolder.getStatus().equals("O")) {
            rbOpen.performClick();
            statusTV.setText("Open");
            statusTV.setTextColor(getResources().getColor(R.color.white));
            statusTV.setBackgroundColor(getResources().getColor(R.color.Open));
        } else if (myDataHolder.gymDataHolder.getStatus().equals("C")) {
            rbClose.performClick();
            statusTV.setText("Close");
            statusTV.setBackgroundColor(getResources().getColor(R.color.Close));
            statusTV.setTextColor(getResources().getColor(R.color.black4));
        } else if (myDataHolder.gymDataHolder.getStatus().equals("F")) {
            rbFull.performClick();
            statusTV.setText("Full");
            statusTV.setTextColor(getResources().getColor(R.color.white));
            statusTV.setBackgroundColor(getResources().getColor(R.color.Full));
        }
        RealtimeChange();

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()).child("credit").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()).child("credit").setValue((long)dataSnapshot.getValue()+collection);
                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE+myDataHolder.gymDataHolder.getRelativeName()).child("credits").setValue(0);
                        Toasty.success(getContext(),"Collected...",Toasty.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        statusRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbOpen:{
                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("status").setValue("O");
                        statusTV.setText("Open");
                        statusTV.setTextColor(getResources().getColor(R.color.white));
                        statusTV.setBackgroundColor(getResources().getColor(R.color.Open));
                        break;
                    }
                    case R.id.rbClose:{
                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("status").setValue("C");
                        statusTV.setText("Close");
                        statusTV.setBackgroundColor(getResources().getColor(R.color.Close));
                        statusTV.setTextColor(getResources().getColor(R.color.black4));
                        break;
                    }
                    case R.id.rbFull:{
                        statusTV.setText("Full");
                        statusTV.setTextColor(getResources().getColor(R.color.white));
                        statusTV.setBackgroundColor(getResources().getColor(R.color.Full));
                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("status").setValue("F");
                        break;
                    }
                }
            }
        });
    }

    private void RealtimeChange() {
        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("credits")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            collectionTV.setText(Html.fromHtml("<b>Collection : </b> 0"));
                            collection = 0;
                        }else {
                            collectionTV.setText(Html.fromHtml("<b>Collection : </b>"+dataSnapshot.getValue().toString()));
                            collection = (long)dataSnapshot.getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("rating")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myDataHolder.gymDataHolder.setRating(Float.parseFloat(dataSnapshot.getValue().toString()));
                        if (myDataHolder.gymDataHolder.getRating() > 5) {
                            ratingTV.setText(" - ");
                            ratingTV.setBackgroundColor(getResources().getColor(R.color.Close));
                        } else {
                            ratingTV.setText(myDataHolder.gymDataHolder.getRating() + "");
                            if (myDataHolder.gymDataHolder.getRating() > 4) {
                                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star5));
                            } else if (myDataHolder.gymDataHolder.getRating() > 3) {
                                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star4));
                            } else if (myDataHolder.gymDataHolder.getRating() > 2) {
                                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star3));
                            } else if (myDataHolder.gymDataHolder.getRating() > 1) {
                                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star2));
                            } else {
                                ratingTV.setBackgroundColor(getResources().getColor(R.color.Star1));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem membershipItem = menu.findItem(R.id.action_update);

        membershipItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), AddGymInfoActivity.class);
                startActivity(intent);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}