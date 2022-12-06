package com.project.anygym;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.chahinem.pageindicator.PageIndicator;
import com.ekalips.fancybuttonproj.FancyButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygym.Adapters.FullScreenImageAdapter;
import com.project.anygym.Adapters.ImageAdapter;
import com.project.anygym.CustomFunctions.BuyMembershipFunction;
import com.project.anygym.CustomFunctions.GymScanningFunction;
import com.project.anygym.CustomFunctions.TurnGPSOn;
import com.project.anygym.DataHolder.GymLocationDataHolder;
import com.project.anygym.DataHolder.myDataHolder;

import java.util.HashMap;
import java.util.Map;

public class GymInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    ImageAdapter imageAdapter;
    FullScreenImageAdapter fullScreenImageAdapter;
    RecyclerView imageRV;
    public static RecyclerView fullScreenImageRV;
    public static TextView indicatorTV;
    LinearLayoutManager layoutManager, fullScreenLayoutManager;
    PageIndicator pageIndicator;
    TextView gymTitleTV, gymAddressTV, gymDescriptionTV, gymTimingsTV, gymCreditsTV, statusTV, ratingTV;
    ImageView gymLogoIV;
    Toolbar gym_detail_toolbar;
    GymScanningFunction gymScanningFunction;
    BuyMembershipFunction buyMembershipFunction;
    String about;
    FancyButton btnRate;
    RatingBar ratingBar;
    Map updateMultiple;
    long gymRatingCount;
    float myRating;
    private GoogleMap mMap;
    private MapView mapGymInfo;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_info);

        gym_detail_toolbar = findViewById(R.id.gym_detail_toolbar);
        setSupportActionBar(gym_detail_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //------------------Map
        mapGymInfo = findViewById(R.id.mapGymInfo);
        mapGymInfo.onCreate(savedInstanceState);
        mapGymInfo.getMapAsync(this);


        gymTitleTV = findViewById(R.id.gymTitleTV);
        gymAddressTV = findViewById(R.id.gymAddressTV);
        gymDescriptionTV = findViewById(R.id.gymDescriptionTV);
        gymTimingsTV = findViewById(R.id.gymTimingsTV);
        gymCreditsTV = findViewById(R.id.gymCreditsTV);
        statusTV = findViewById(R.id.statusTV);
        ratingTV = findViewById(R.id.ratingTV);
        gymLogoIV = findViewById(R.id.gymLogoIV);
        indicatorTV = findViewById(R.id.indicatorTV);
        ratingBar = findViewById(R.id.ratingBar);
        btnRate = findViewById(R.id.btnRate);


        updateMultiple = new HashMap();

        gymScanningFunction = new GymScanningFunction(GymInfoActivity.this);
        buyMembershipFunction = new BuyMembershipFunction(GymInfoActivity.this);

        //------------------------------FullScreen Image Recycler
        fullScreenImageRV = findViewById(R.id.fullScreenImageRV);
        fullScreenLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        fullScreenImageRV.setLayoutManager(fullScreenLayoutManager);
        SnapHelper fullSnapHelper = new PagerSnapHelper();
        fullSnapHelper.attachToRecyclerView(fullScreenImageRV);


        //------------------------------Image Recycler
        imageRV = findViewById(R.id.imageRV);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        imageRV.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(imageRV);

        pageIndicator = findViewById(R.id.pageIndicator);
        myDataHolder.gymDetailDBREF = FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName());

        myDataHolder.gymDetailDBREF.child("about").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                about = dataSnapshot.getValue().toString();
                fullScreenImageAdapter = new FullScreenImageAdapter(GymInfoActivity.this, Integer.parseInt(about.split("#")[1]));
                fullScreenImageRV.setAdapter(fullScreenImageAdapter);
                imageAdapter = new ImageAdapter(GymInfoActivity.this, Integer.parseInt(about.split("#")[1]));
                imageRV.setAdapter(imageAdapter);
                imageAdapter.notifyDataSetChanged();
                pageIndicator.attachTo(imageRV);
                gymDescriptionTV.setText(about.split("#")[0]);
                indicatorTV.setText("1/"+about.split("#")[1]);
                fullScreenImageRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        indicatorTV.setText(fullScreenLayoutManager.findFirstVisibleItemPosition() + 1 + "/" + Integer.parseInt(about.split("#")[1]));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (myDataHolder.gymDataHolder.isLogo()) {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/logo.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).into(gymLogoIV);
                }
            });
        } else {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/0.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).centerCrop().into(gymLogoIV);
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
            statusTV.setText("Open");
            statusTV.setTextColor(getResources().getColor(R.color.white));
            statusTV.setBackgroundColor(getResources().getColor(R.color.Open));
        } else if (myDataHolder.gymDataHolder.getStatus().equals("C")) {
            statusTV.setText("Close");
            statusTV.setBackgroundColor(getResources().getColor(R.color.Close));
            statusTV.setTextColor(getResources().getColor(R.color.black4));
        } else if (myDataHolder.gymDataHolder.getStatus().equals("F")) {
            statusTV.setText("Full");
            statusTV.setTextColor(getResources().getColor(R.color.white));
            statusTV.setBackgroundColor(getResources().getColor(R.color.Full));
        }
        RealtimeChange();
        rating();
    }

    private void RealtimeChange() {
        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myDataHolder.gymDataHolder.setStatus(dataSnapshot.getValue().toString());
                        if (myDataHolder.gymDataHolder.getStatus().equals("O")) {
                            statusTV.setText("Open");
                            statusTV.setTextColor(getResources().getColor(R.color.white));
                            statusTV.setBackgroundColor(getResources().getColor(R.color.Open));
                        } else if (myDataHolder.gymDataHolder.getStatus().equals("C")) {
                            statusTV.setText("Close");
                            statusTV.setBackgroundColor(getResources().getColor(R.color.Close));
                            statusTV.setTextColor(getResources().getColor(R.color.black4));
                        } else if (myDataHolder.gymDataHolder.getStatus().equals("F")) {
                            statusTV.setText("Full");
                            statusTV.setTextColor(getResources().getColor(R.color.white));
                            statusTV.setBackgroundColor(getResources().getColor(R.color.Full));
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

    private void rating() {
        FirebaseDatabase.getInstance().getReference("User/User Ratings Database/" + myDataHolder.userDataHolder.getMobile()).child(myDataHolder.gymDataHolder.getRelativeName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    btnRate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnRate.collapse();
                            btnRate.setEnabled(false);
                            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("ratingCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null) {
                                        updateMultiple.put(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName() + "/ratingCount", 1);
                                        updateMultiple.put("User/User Ratings Database/" + myDataHolder.userDataHolder.getMobile() + "/" + myDataHolder.gymDataHolder.getRelativeName(), ratingBar.getRating());
                                        updateMultiple.put(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName() + "/rating", ratingBar.getRating());
                                        FirebaseDatabase.getInstance().getReference().updateChildren(updateMultiple);
                                        rating();
                                    } else {
                                        gymRatingCount = (long) dataSnapshot.getValue() + 1;
                                        updateMultiple.put(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName() + "/ratingCount", gymRatingCount);

                                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                updateMultiple.put(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName() + "/rating", Math.round(((Float.parseFloat(dataSnapshot.getValue().toString()) * (gymRatingCount - 1) + ratingBar.getRating()) / gymRatingCount) * 10.0) / 10.0);
                                                updateMultiple.put("User/User Ratings Database/" + myDataHolder.userDataHolder.getMobile() + "/" + myDataHolder.gymDataHolder.getRelativeName(), ratingBar.getRating());
                                                FirebaseDatabase.getInstance().getReference().updateChildren(updateMultiple);
                                                rating();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnRate.expand();
                                            btnRate.setVisibility(View.GONE);
                                        }
                                    }, 1000);
                                    btnRate.setEnabled(true);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                } else {
                    myRating = Float.parseFloat(dataSnapshot.getValue().toString());
                    ratingBar.setRating(myRating);
                    btnRate.setText("Change");
                    btnRate.setVisibility(View.GONE);
                    btnRate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnRate.setEnabled(false);
                            btnRate.collapse();
                            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("ratingCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    gymRatingCount = (long) dataSnapshot.getValue();

                                    FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            updateMultiple.put(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName() + "/rating", Math.round(((Float.parseFloat(dataSnapshot.getValue().toString()) * gymRatingCount - myRating + ratingBar.getRating()) / gymRatingCount) * 10.0) / 10.0);
                                            updateMultiple.put("User/User Ratings Database/" + myDataHolder.userDataHolder.getMobile() + "/" + myDataHolder.gymDataHolder.getRelativeName(), ratingBar.getRating());
                                            FirebaseDatabase.getInstance().getReference().updateChildren(updateMultiple);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    rating();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnRate.expand();
                                            btnRate.setVisibility(View.GONE);
                                        }
                                    }, 1000);
                                    btnRate.setEnabled(true);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            btnRate.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fullScreenImageRV.getVisibility() == View.VISIBLE) {
            fullScreenImageRV.setVisibility(View.GONE);
            indicatorTV.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gym_info_menu, menu);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
    }

    public void onScan(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        } else {
            gymScanningFunction.scanGym();
        }
    }

    public void onMembershipClick(View view) {
        buyMembershipFunction.buyMembership();
    }

    public void onMap(View view) {
        if (mapGymInfo.getVisibility() == View.VISIBLE)
        {
            mapGymInfo.setVisibility(View.GONE);
        }else {
            mapGymInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_LOCATION_DATABASE).child(myDataHolder.gymDataHolder.getRelativeName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    GymLocationDataHolder gymLocationDataHolder = dataSnapshot.getValue(GymLocationDataHolder.class);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(gymLocationDataHolder.getLatitude(), gymLocationDataHolder.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                .title(myDataHolder.gymDataHolder.getTitle()));

                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(marker.getPosition())      // Sets the center of the map to Mountain View
                        .zoom(13)                   // Sets the zoom
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                new TurnGPSOn(GymInfoActivity.this).turnGPSOn(new TurnGPSOn.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        MainActivity.isGPS = isGPSEnable;
                    }
                });
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapGymInfo.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapGymInfo.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapGymInfo.onStop();
    }
    @Override
    protected void onPause() {
        mapGymInfo.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapGymInfo.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapGymInfo.onLowMemory();
    }
}