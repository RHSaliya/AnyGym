package com.project.anygymowner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.project.anygymowner.Adapter.FullScreenImageAdapter;
import com.project.anygymowner.DataHolder.GymDataHolder;
import com.project.anygymowner.DataHolder.GymLocationDataHolder;
import com.project.anygymowner.DataHolder.myDataHolder;
import com.project.anygymowner.GymInfoFragments.BasicFragment;
import com.project.anygymowner.GymInfoFragments.MembersFragment;
import com.project.anygymowner.GymInfoFragments.StatsFragment;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;

import java.util.Base64;

public class GymInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static BottomNavigationView bottomNavigationView;
    //Fragments
    private final BasicFragment basicFragment = new BasicFragment();
    private final MembersFragment membersFragment = new MembersFragment();
    private final StatsFragment statsFragment = new StatsFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = basicFragment;

    public static RecyclerView fullScreenImageRV;
    public static TextView indicatorTV;
    private FullScreenImageAdapter fullScreenImageAdapter;
    private LinearLayoutManager fullScreenLayoutManager;

    private MapView mapGymInfo;
    private Marker marker;
    private ImageView qrCodeIV;
    private FloatingActionButtonExpandable fabMap,fabBarcode;
    private Animation slideUp;
    private LinearLayout progressLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_info);

        indicatorTV = findViewById(R.id.indicatorTV);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        qrCodeIV = findViewById(R.id.qrCodeIV);
        progressLL = findViewById(R.id.progressLL);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slideup);

        fabMap = findViewById(R.id.fabMap);
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMap.toggle(true);
                qrCodeIV.setVisibility(View.GONE);
                if (mapGymInfo.getVisibility() == View.GONE){
                    mapGymInfo.setVisibility(View.VISIBLE);
                    mapGymInfo.startAnimation(slideUp);
                } else {
                    mapGymInfo.setVisibility(View.GONE);
                }
            }
        });

        fabBarcode = findViewById(R.id.fabBarcode);
        fabBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabBarcode.toggle(true);
                mapGymInfo.setVisibility(View.GONE);
                if (qrCodeIV.getVisibility() == View.GONE){
                    qrCodeIV.setVisibility(View.VISIBLE);
                    qrCodeIV.startAnimation(slideUp);
                } else {
                    qrCodeIV.setVisibility(View.GONE);
                }
            }
        });

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(getIntent().getStringExtra("gymName"), BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        //------------------------------FullScreen Image Recycler
        fullScreenImageRV = findViewById(R.id.fullScreenImageRV);
        fullScreenLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        fullScreenImageRV.setLayoutManager(fullScreenLayoutManager);
        SnapHelper fullSnapHelper = new PagerSnapHelper();
        fullSnapHelper.attachToRecyclerView(fullScreenImageRV);
        myDataHolder.gymDetailDBREF = FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + getIntent().getStringExtra("gymName"));

        myDataHolder.gymDetailDBREF.child("about").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE+getIntent().getStringExtra("gymName")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myDataHolder.gymDataHolder = dataSnapshot.getValue(GymDataHolder.class);
                        ((ViewGroup)progressLL.getParent()).removeView(progressLL);
                        fragmentManager.beginTransaction().add(R.id.mainFrame, basicFragment).commit();
                        fragmentManager.beginTransaction().add(R.id.mainFrame, membersFragment).hide(membersFragment).commit();
                        fragmentManager.beginTransaction().add(R.id.mainFrame, statsFragment).hide(statsFragment).commit();
                        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.action_basic:
                                        fragmentManager.beginTransaction().hide(active).show(basicFragment).commit();
                                        fabMap.setVisibility(View.VISIBLE);
                                        fabBarcode.setVisibility(View.VISIBLE);
                                        active = basicFragment;
                                        return true;

                                    case R.id.action_members:
                                        fragmentManager.beginTransaction().hide(active).show(membersFragment).commit();
                                        fabMap.setVisibility(View.GONE);
                                        fabBarcode.setVisibility(View.GONE);
                                        mapGymInfo.setVisibility(View.GONE);
                                        qrCodeIV.setVisibility(View.GONE);
                                        fabBarcode.collapse(false);
                                        fabMap.collapse(false);
                                        active = membersFragment;
                                        return true;

                                    case R.id.action_stats:
                                        fragmentManager.beginTransaction().hide(active).show(statsFragment).commit();
                                        fabMap.setVisibility(View.GONE);
                                        fabBarcode.setVisibility(View.GONE);
                                        mapGymInfo.setVisibility(View.GONE);
                                        qrCodeIV.setVisibility(View.GONE);
                                        active = statsFragment;
                                        fabBarcode.collapse(false);
                                        fabMap.collapse(false);
                                        return true;
                                }
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                myDataHolder.about = dataSnapshot.getValue().toString();
                fullScreenImageAdapter = new FullScreenImageAdapter(GymInfoActivity.this, Integer.parseInt(myDataHolder.about.split("#")[1]));
                fullScreenImageRV.setAdapter(fullScreenImageAdapter);
                indicatorTV.setText("1/"+myDataHolder.about.split("#")[1]);
                fullScreenImageRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        indicatorTV.setText(fullScreenLayoutManager.findFirstVisibleItemPosition() + 1 + "/" + Integer.parseInt(myDataHolder.about.split("#")[1]));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //------------------Map
        mapGymInfo = findViewById(R.id.mapGymInfo);
        mapGymInfo.onCreate(savedInstanceState);
        mapGymInfo.getMapAsync(this);
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
        mapGymInfo.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_LOCATION_DATABASE).child(getIntent().getStringExtra("gymName")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GymLocationDataHolder gymLocationDataHolder = dataSnapshot.getValue(GymLocationDataHolder.class);
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(gymLocationDataHolder.getLatitude(), gymLocationDataHolder.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                        .title(myDataHolder.gymDataHolder.getTitle()));

                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(marker.getPosition())      // Sets the center of the map to Mountain View
                        .zoom(13)                   // Sets the zoom
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}