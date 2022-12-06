package com.project.anygymadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymadmin.Adapter.FullScreenImageAdapter;
import com.project.anygymadmin.Adapter.ImageAdapter;
import com.project.anygymadmin.DataHolder.GymDataHolder;
import com.project.anygymadmin.DataHolder.GymLocationDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class VerifyActivity extends AppCompatActivity {

    private ImageView logoImage;
    public static ImageView backIV;
    private TextView gymTitleTV;

    private RecyclerView imageRecyclerView;
    public static RecyclerView fullScreenImageRV;
    private FullScreenImageAdapter fullScreenImageAdapter;
    private ImageAdapter imageAdapter;
    private LinearLayoutManager fullScreenImageLayoutManager;

    private TextView userTypeTV;
    private EditText descriptionET, commentsET;
    private EditText shopNoET, buildingNameET, landmarkET, areaET, pinCodeET, cityET, stateET, chargeET;
    private Button btnVerify;
    private Intent intent;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        logoImage = findViewById(R.id.logoImage);
        backIV = findViewById(R.id.backIV);
        gymTitleTV = findViewById(R.id.gymTitleTV);
        userTypeTV = findViewById(R.id.userTypeTV);
        descriptionET = findViewById(R.id.descriptionET);
        commentsET = findViewById(R.id.commentsET);
        shopNoET = findViewById(R.id.shopNoET);
        buildingNameET = findViewById(R.id.buildingNameET);
        landmarkET = findViewById(R.id.landmarkET);
        areaET = findViewById(R.id.areaET);
        pinCodeET = findViewById(R.id.pinCodeET);
        cityET = findViewById(R.id.cityET);
        stateET = findViewById(R.id.stateET);
        chargeET = findViewById(R.id.chargeET);
        btnVerify = findViewById(R.id.btnVerify);

        gymTitleTV.setText(myDataHolder.gymDataHolder.getTitle());

        //-----------------------Image RecyclerView
        fullScreenImageRV = findViewById(R.id.verifyFullScreenImageRV);
        fullScreenImageLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        fullScreenImageRV.setLayoutManager(fullScreenImageLayoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(fullScreenImageRV);

        imageRecyclerView = findViewById(R.id.verifyImageRecycler);
        GridLayoutManager imageGridLayout = new GridLayoutManager(getApplicationContext(), 4);
        imageRecyclerView.setLayoutManager(imageGridLayout);

        if (myDataHolder.gymDataHolder.isLogo()) {
            myDataHolder.gymImgsStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH+myDataHolder.gymDataHolder.getRelativeName()+"/logo.png");
            myDataHolder.gymImgsStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        Glide.with(getApplicationContext()).load(uri).into(logoImage);
                    }catch (IllegalArgumentException exc){}
                }
            });
        }

        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("about").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fullScreenImageAdapter = new FullScreenImageAdapter(VerifyActivity.this, Integer.parseInt(dataSnapshot.getValue().toString().split("#")[1]));
                fullScreenImageRV.setAdapter(fullScreenImageAdapter);

                imageAdapter = new ImageAdapter(VerifyActivity.this, Integer.parseInt(dataSnapshot.getValue().toString().split("#")[1]));
                imageRecyclerView.setAdapter(imageAdapter);
                imageAdapter.notifyDataSetChanged();
                descriptionET.setText(dataSnapshot.getValue().toString().split("#")[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-------------------------Timings Data
        userTypeTV.setText(Html.fromHtml(myDataHolder.gymDataHolder.getTimings().replace("#","\n")));

        shopNoET.setText(myDataHolder.gymDataHolder.getAddress().split("#")[0]);
        buildingNameET.setText(myDataHolder.gymDataHolder.getAddress().split("#")[1]);
        landmarkET.setText(myDataHolder.gymDataHolder.getAddress().split("#")[2]);
        areaET.setText(myDataHolder.gymDataHolder.getAddress().split("#")[3]);
        pinCodeET.setText(myDataHolder.gymDataHolder.getAddress().split("#")[4]);
        cityET.setText(myDataHolder.gymDataHolder.getAddress().split("#")[5]);
        stateET.setText(myDataHolder.gymDataHolder.getAddress().split("#")[6]);
        chargeET.setText(myDataHolder.gymDataHolder.getCharge() + "");

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreenImageRV.setVisibility(View.GONE);
                backIV.setVisibility(View.GONE);
            }
        });


        //----------------------------char count
        final TextView descriptionCharCount = findViewById(R.id.descriptionCharCount);

        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descriptionCharCount.setText("" + descriptionET.length() + "/2000");
                if (descriptionET.getLineCount() > 7) {
                    descriptionET.setLines(descriptionET.getLineCount());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final TextView commentCharCount = findViewById(R.id.commentCharCount);

        commentsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                commentCharCount.setText("" + commentsET.length() + "/2000");
                if (commentsET.getLineCount() > 7) {
                    commentsET.setLines(commentsET.getLineCount());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).child("about").setValue(dataSnapshot.getValue().toString().substring(7,dataSnapshot.getValue().toString().lastIndexOf('}')));
                        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).setValue(dataSnapshot.getValue(GymDataHolder.class));
                        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_LOCATION_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_LOCATION_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).setValue(dataSnapshot.getValue(GymLocationDataHolder.class));
                        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_LOCATION_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3)).child("ownerGyms").child(myDataHolder.gymDataHolder.getRelativeName()).setValue("Y");
                databaseReference = FirebaseDatabase.getInstance().getReference("Availability/States").child(myDataHolder.gymDataHolder.getRelativeName().split("-")[0]).child(myDataHolder.gymDataHolder.getRelativeName().split("-")[1]);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            databaseReference.setValue(1);
                        } else {
                            databaseReference.setValue((long) dataSnapshot.getValue() + 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH + myDataHolder.gymDataHolder.getMobile()).child("ownerGyms").child(myDataHolder.gymDataHolder.getRelativeName()).setValue("Y");
                FirebaseDatabase.getInstance().getReference(myDataHolder.REGISTERED_GYMS_PATH).child(myDataHolder.gymDataHolder.getRelativeName().toLowerCase()).setValue(1);

                comments();
                Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_SHORT, true).show();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fullScreenImageRV.getVisibility() == View.VISIBLE) {
            fullScreenImageRV.setVisibility(View.GONE);
            backIV.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void onReject(View view) {
        comments();
        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH + myDataHolder.gymDataHolder.getMobile()).child("ownerGyms").child(myDataHolder.gymDataHolder.getRelativeName()).setValue("R");
        onBackPressed();
    }

    public void comments(){
        if (commentsET.getText().length()>0){
            FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH + myDataHolder.gymDataHolder.getMobile()).child("comments").child(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date())).setValue(myDataHolder.gymDataHolder.getRelativeName()+"#"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date())+"#"+commentsET.getText().toString());
        }
    }

    public void onDelete(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(VerifyActivity.this, R.style.AppTheme));
        builder.setTitle("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_BASIC_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).removeValue();
                        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).removeValue();
                        FirebaseDatabase.getInstance().getReference(myDataHolder.VERIFY_GYM_LOCATION_DATABASE + myDataHolder.gymDataHolder.getRelativeName()).removeValue();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
