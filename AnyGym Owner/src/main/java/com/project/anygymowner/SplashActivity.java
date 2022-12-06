package com.project.anygymowner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymowner.DataHolder.GymDataHolder;
import com.project.anygymowner.DataHolder.OwnerDataHolder;
import com.project.anygymowner.DataHolder.myDataHolder;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private Intent intent;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getPermissions();
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                    initialize();
        } else if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void initialize() {
        myDataHolder.myAUTH = FirebaseAuth.getInstance();
        myDataHolder.myDB = FirebaseDatabase.getInstance();
        myDataHolder.mySTORAGE = FirebaseStorage.getInstance();
        handler = new Handler();
        if (myDataHolder.myAUTH.getCurrentUser() != null) {
            myDataHolder.mobile = myDataHolder.myAUTH.getCurrentUser().getPhoneNumber().substring(3);
            myDataHolder.profileStorageREF = myDataHolder.mySTORAGE.getReference(myDataHolder.OWNER_PROFILE_IMAGE_PATH + myDataHolder.mobile + ".png");
            myDataHolder.ownerDBREF = myDataHolder.myDB.getReference(myDataHolder.OWNER_DATABASE_PATH + myDataHolder.mobile);

            myDataHolder.ownerDBREF.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot snapshot) {
                    myDataHolder.ownerDataHolder = snapshot.getValue(OwnerDataHolder.class);
                    if (myDataHolder.ownerDataHolder != null) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }, 1000);
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                intent = new Intent(getApplicationContext(), OwnerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }, 1000);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermissions();
                    break;
                } else {
                    finish();
                }
            }
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermissions();
                    break;
                } else {
                    finish();
                }
            }
        }
    }
}
