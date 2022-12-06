package com.project.anygym.MainFragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygym.Adapters.GymRecyclerAdapter;
import com.project.anygym.CustomFunctions.GymScanningFunction;
import com.project.anygym.DataHolder.GymDataHolder;
import com.project.anygym.DataHolder.myDataHolder;
import com.project.anygym.R;

import java.util.ArrayList;
import java.util.List;


public class GymsFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private RecyclerView gymsRecyclerView, gymSearchRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager, mSearchLayoutManager;
    private GymRecyclerAdapter gymRecyclerAdapter, gymAdapter;
    private FloatingActionButton scanFAB;
    private List<GymDataHolder> gymDataHolderList, gymDataHolderListMain;

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

        //-----------------------------------------Gyms Recycler
        mLayoutManager = new LinearLayoutManager(getContext());
        mSearchLayoutManager = new LinearLayoutManager(getContext());

        gymsRecyclerView = view.findViewById(R.id.gyms_recycler_view);
        gymSearchRecyclerView = view.findViewById(R.id.gymSearch_recycler_view);

        scanFAB = view.findViewById(R.id.scanFAB);
        gymDataHolderList = new ArrayList<>();
        gymDataHolderListMain = new ArrayList<>();

        scanFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                } else {
                    final GymScanningFunction gymScanningFunction = new GymScanningFunction(getContext());
                    gymScanningFunction.scanGym();
                }
            }
        });

        //----------------------------------------RecyclerView Search
        gymSearchRecyclerView.setLayoutManager(mSearchLayoutManager);
        gymRecyclerAdapter = new GymRecyclerAdapter(getContext(), gymDataHolderList);
        gymSearchRecyclerView.setAdapter(gymRecyclerAdapter);
        //----------------------------------------RecyclerView Main
        gymsRecyclerView.setLayoutManager(mLayoutManager);
        gymAdapter = new GymRecyclerAdapter(getContext(), gymDataHolderListMain);
        gymsRecyclerView.setAdapter(gymAdapter);

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE).orderByKey()
                .startAt(getContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE).getString("ref",""))
                .endAt(getContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE).getString("ref","")+"\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    gymDataHolderListMain.add(snapshot.getValue(GymDataHolder.class));
                    gymAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       /* gymAdapter = new FirebaseRecyclerPagingAdapter<GymDataHolder, GymViewHolder>(options) {
            @NonNull
            @Override
            public GymViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new GymViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_gyms_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final GymViewHolder gymViewHolder, int i, @NonNull final GymDataHolder gymDataHolder) {
                gymViewHolder.itemView.setVisibility(View.VISIBLE);
                gymViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                gymViewHolder.gymAddress.setText(gymDataHolder.getAddress().replace("#", ", "));
                gymViewHolder.gymName.setText(gymDataHolder.getTitle());
                gymViewHolder.charge.setText(gymDataHolder.getCharge() + " Credits");
                gymViewHolder.gymTimings.setText(Html.fromHtml(gymDataHolder.getTimings()));

                if (gymDataHolder.getRating() > 5) {
                    gymViewHolder.rating.setText(" - ");
                    gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Close));
                } else {
                    gymViewHolder.rating.setText(gymDataHolder.getRating() + "");
                    if (gymDataHolder.getRating() > 4) {
                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star5));
                    } else if (gymDataHolder.getRating() > 3) {
                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star4));
                    } else if (gymDataHolder.getRating() > 2) {
                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star3));
                    } else if (gymDataHolder.getRating() > 1) {
                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star2));
                    } else {
                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star1));
                    }
                }

                if (gymDataHolder.getStatus().equals("O")) {
                    gymViewHolder.status.setText("Open");
                    gymViewHolder.status.setTextColor(getResources().getColor(R.color.Open));
                } else if (gymDataHolder.getStatus().equals("C")) {
                    gymViewHolder.status.setText("Close");
                    gymViewHolder.status.setTextColor(getResources().getColor(R.color.Close));
                } else if (gymDataHolder.getStatus().equals("F")) {
                    gymViewHolder.status.setText("Full");
                    gymViewHolder.status.setTextColor(getResources().getColor(R.color.Full));
                }

                FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + gymDataHolder.getTitle()).child("rating")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                gymDataHolder.setRating(Double.parseDouble(dataSnapshot.getValue().toString()));
                                if (gymDataHolder.getRating() > 5) {
                                    gymViewHolder.rating.setText(" - ");
                                    gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Close));
                                } else {
                                    gymViewHolder.rating.setText(gymDataHolder.getRating() + "");
                                    if (gymDataHolder.getRating() > 4) {
                                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star5));
                                    } else if (gymDataHolder.getRating() > 3) {
                                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star4));
                                    } else if (gymDataHolder.getRating() > 2) {
                                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star3));
                                    } else if (gymDataHolder.getRating() > 1) {
                                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star2));
                                    } else {
                                        gymViewHolder.rating.setBackgroundColor(getResources().getColor(R.color.Star1));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + gymDataHolder.getTitle()).child("status")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                gymDataHolder.setStatus(dataSnapshot.getValue().toString());
                                if (gymDataHolder.getStatus().equals("O")) {
                                    gymViewHolder.status.setText("Open");
                                    gymViewHolder.status.setTextColor(getResources().getColor(R.color.Open));
                                } else if (gymDataHolder.getStatus().equals("C")) {
                                    gymViewHolder.status.setText("Close");
                                    gymViewHolder.status.setTextColor(getResources().getColor(R.color.Close));
                                } else if (gymDataHolder.getStatus().equals("F")) {
                                    gymViewHolder.status.setText("Full");
                                    gymViewHolder.status.setTextColor(getResources().getColor(R.color.Full));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                //---------------------------Logo
                if (gymDataHolder.isLogo()) {
                    myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolder.getTitle() + "/logo.png");
                    myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext()).load(uri).into(gymViewHolder.logoImageView);
                        }
                    });
                } else {
                    myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + gymDataHolder.getTitle() + "/thumbnail/0.png");
                    myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext()).load(uri).into(gymViewHolder.logoImageView);
                        }
                    });
                }

                //------------------------Click event
                gymViewHolder.gyms_recycler_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDataHolder.gymDataHolder = gymDataHolder;
                        v.getContext().startActivity(new Intent(v.getContext(), GymInfoActivity.class));
                    }
                });
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                    case FINISHED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        retry();
                        break;
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                mSwipeRefreshLayout.setRefreshing(false);
                databaseError.toException().printStackTrace();
            }
        };
        gymsRecyclerView.setAdapter(gymAdapter);
*/
    }

    public void onSearch(final String query, int flag) {
        if (query.equals("") && gymsRecyclerView.getVisibility() == View.GONE) {
            gymsRecyclerView.setVisibility(View.VISIBLE);
            gymSearchRecyclerView.setVisibility(View.GONE);
        } else if (!query.equals("") && flag == 1) {
            gymDataHolderList.clear();
            gymsRecyclerView.setVisibility(View.GONE);
            gymSearchRecyclerView.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE).orderByKey().startAt(query).endAt(query+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            gymDataHolderList.add(snapshot.getValue(GymDataHolder.class));
                        }
                    }
                    gymRecyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
