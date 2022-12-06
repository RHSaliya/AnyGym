package com.project.anygym.MainFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.project.anygym.CustomFunctions.TurnGPSOn;
import com.project.anygym.DataHolder.GymDataHolder;
import com.project.anygym.DataHolder.GymLocationDataHolder;
import com.project.anygym.DataHolder.myDataHolder;
import com.project.anygym.GymInfoActivity;
import com.project.anygym.MainActivity;
import com.project.anygym.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Context context;
    private MapView mapView;
    private GoogleMap gMap;
    private LatLng myLocation;
    private Marker marker, active;
    private TextView markerTitle;
    private View markerView, nestedScrollView;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private List<String> gymList;
    private List<Marker> markerList;
    private LocationRequest mLocationRequest;
    private Handler handler;
    private View view;
    private int i;
    private DataSnapshot lastSnapShot;

    //----------------------------Gym Card Data
    private ImageView mapGymLogo;
    private TextView mapGymStatusTV, mapGymTitleTV, mapGymRatingTV, mapGymAddressTV, mapGymTimingsTV, mapGymCreditsTV;
    private ConstraintLayout mapGymDataCardView;
    private int height = -1;
    private ShimmerFrameLayout logoShimmer;
    private SharedPreferences sharedPref;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        //--------------------------Marker
        markerView = inflater.inflate(R.layout.marker, container, false);
        markerTitle = markerView.findViewById(R.id.markerTitle);
        //--------------------------Map
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        //--------------------------Gym Info Card
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        mapGymLogo = view.findViewById(R.id.mapGymLogo);
        mapGymTitleTV = view.findViewById(R.id.mapGymTitleTV);
        mapGymAddressTV = view.findViewById(R.id.mapGymAddressTV);
        mapGymCreditsTV = view.findViewById(R.id.mapGymCreditsTV);
        mapGymRatingTV = view.findViewById(R.id.mapGymRatingTV);
        mapGymStatusTV = view.findViewById(R.id.mapGymStatusTV);
        mapGymTimingsTV = view.findViewById(R.id.mapGymTimingsTV);
        mapGymDataCardView = view.findViewById(R.id.mapGymDataCardView);
        logoShimmer = view.findViewById(R.id.logoShimmer);
        markerList = new ArrayList<>();
        gymList = new ArrayList<>();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //-----------------------------------------Map
        mapView.getMapAsync(this);
        handler = new Handler();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        if (sharedPref.getBoolean("dayNight", false)) {
            gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.mapnight));
        };
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_LOCATION_DATABASE).orderByKey()
                .startAt(getContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE).getString("ref",""))
                .endAt(getContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE).getString("ref","")+"\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastSnapShot = dataSnapshot;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GymLocationDataHolder gymLocationDataHolder = snapshot.getValue(GymLocationDataHolder.class);
                    markerTitle.setText(gymLocationDataHolder.getGymName().split("-")[3]);
                    marker = gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(gymLocationDataHolder.getLatitude(), gymLocationDataHolder.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromView(markerView))));
                    markerList.add(marker);
                    gymList.add(gymLocationDataHolder.getGymName());
                }
                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(markerList.get(0).getPosition())      // Sets the center of the map to Mountain View
                        .zoom(13)                   // Sets the zoom
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(context, "" + sharedPref.getBoolean("dayNight", false), Toast.LENGTH_SHORT).show();
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                mapGymLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_gymlogo_back));
                logoShimmer.setVisibility(View.VISIBLE);
                logoShimmer.startShimmerAnimation();
                if (height == -1) {
                    height = nestedScrollView.getHeight();
                    View nevigationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("4"));
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) nevigationButton.getLayoutParams();
                    rlp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
                    rlp.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
                    rlp.setMargins(0, 0, 0, height);
                }

                View zoomButtons = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("1"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) zoomButtons.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
                rlp.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 0, height);

                active = marker;
                FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + gymList.get(markerList.indexOf(marker))).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            myDataHolder.gymDataHolder = dataSnapshot.getValue(GymDataHolder.class);
                            mapGymTitleTV.setText(myDataHolder.gymDataHolder.getRelativeName().split("-")[3]+", "+myDataHolder.gymDataHolder.getRelativeName().split("-")[2]);
                            mapGymAddressTV.setText(myDataHolder.gymDataHolder.getAddress().replace("#", ", "));
                            mapGymCreditsTV.setText(myDataHolder.gymDataHolder.getCharge() + " Credits");
                            mapGymTimingsTV.setText(Html.fromHtml(myDataHolder.gymDataHolder.getTimings()));

                            if (myDataHolder.gymDataHolder.getRating() > 5) {
                                mapGymRatingTV.setText(" - ");
                                mapGymRatingTV.setBackgroundColor(getResources().getColor(R.color.Close));
                            } else {
                                mapGymRatingTV.setText(myDataHolder.gymDataHolder.getRating() + "");
                                if (myDataHolder.gymDataHolder.getRating() > 4) {
                                    mapGymRatingTV.setBackgroundColor(getResources().getColor(R.color.Star5));
                                } else if (myDataHolder.gymDataHolder.getRating() > 3) {
                                    mapGymRatingTV.setBackgroundColor(getResources().getColor(R.color.Star4));
                                } else if (myDataHolder.gymDataHolder.getRating() > 2) {
                                    mapGymRatingTV.setBackgroundColor(getResources().getColor(R.color.Star3));
                                } else if (myDataHolder.gymDataHolder.getRating() > 1) {
                                    mapGymRatingTV.setBackgroundColor(getResources().getColor(R.color.Star2));
                                } else {
                                    mapGymRatingTV.setBackgroundColor(getResources().getColor(R.color.Star1));
                                }
                            }

                            myDataHolder.gymDataHolder.setStatus(dataSnapshot.getValue().toString());
                            if (myDataHolder.gymDataHolder.getStatus().equals("O")) {
                                mapGymStatusTV.setText("Open");
                                mapGymStatusTV.setTextColor(getResources().getColor(R.color.white));
                                mapGymStatusTV.setBackgroundColor(getResources().getColor(R.color.Open));
                            } else if (myDataHolder.gymDataHolder.getStatus().equals("C")) {
                                mapGymStatusTV.setText("Close");
                                mapGymStatusTV.setBackgroundColor(getResources().getColor(R.color.Close));
                                mapGymStatusTV.setTextColor(getResources().getColor(R.color.black4));
                            } else if (myDataHolder.gymDataHolder.getStatus().equals("F")) {
                                mapGymStatusTV.setText("Full");
                                mapGymStatusTV.setTextColor(getResources().getColor(R.color.white));
                                mapGymStatusTV.setBackgroundColor(getResources().getColor(R.color.Full));
                            }

                            if (myDataHolder.gymDataHolder.isLogo()) {
                                myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/logo.png");
                                myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(context).load(uri).into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                logoShimmer.stopShimmerAnimation();
                                                logoShimmer.setVisibility(View.GONE);
                                                mapGymLogo.setImageDrawable(resource);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    }
                                });
                            } else {
                                myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/0.png");
                                myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(context).load(uri).into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                logoShimmer.stopShimmerAnimation();
                                                logoShimmer.setVisibility(View.GONE);
                                                mapGymLogo.setImageDrawable(resource);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    }
                                });
                            }

                            mapGymDataCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    v.getContext().startActivity(new Intent(v.getContext(), GymInfoActivity.class));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return false;
            }
        });


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                View zoomButtons = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("1"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) zoomButtons.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
                rlp.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 0, 20);
            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                new TurnGPSOn(context).turnGPSOn(new TurnGPSOn.onGpsListener() {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    public Bitmap createBitmapFromView(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void onSearch(String query, int flag) {
        if (query.equals("")) {
            gMap.clear();
            markerList.clear();
            gymList.clear();
            for (DataSnapshot snapshot : lastSnapShot.getChildren()) {
                GymLocationDataHolder gymLocationDataHolder = snapshot.getValue(GymLocationDataHolder.class);
                markerTitle.setText(gymLocationDataHolder.getGymName());
                marker = gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(gymLocationDataHolder.getLatitude(), gymLocationDataHolder.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromView(markerView))));
                markerList.add(marker);
                gymList.add(gymLocationDataHolder.getGymName());
            }
        } else if (!query.equals("") && flag == 1) {
            gMap.clear();
            markerList.clear();
            gymList.clear();
            for (DataSnapshot snapshot : lastSnapShot.getChildren()) {
                GymLocationDataHolder gymLocationDataHolder = snapshot.getValue(GymLocationDataHolder.class);
                if (gymLocationDataHolder.getGymName().toUpperCase().contains(query.toUpperCase())) {
                    markerTitle.setText(gymLocationDataHolder.getGymName());
                    marker = gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(gymLocationDataHolder.getLatitude(), gymLocationDataHolder.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromView(markerView))));
                    markerList.add(marker);
                    gymList.add(gymLocationDataHolder.getGymName());
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPref.getBoolean("dayNight", false) && gMap != null) {
            gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.mapnight));
        } else if (gMap != null) {
            gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.mapnormal));
        }
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        marker = gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        Toast.makeText(context, "Changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
