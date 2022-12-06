package com.project.anygymowner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymowner.DataHolder.myDataHolder;
import com.project.anygymowner.DataHolder.OwnerDataHolder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;

public class OwnerActivity extends AppCompatActivity {

    private static final int REQ_CAMERA_IMAGE = 1;
    private static final int REQ_GALLERY_IMAGE = 2;
    private EditText nameET, ageET, emailET, upiET, upiMatchET;
    private Button btnSubmit, btnLogout;
    private ImageView profileImage, editImageView;
    private RadioButton radioMale, radioFemale;
    private Bitmap bitmap;
    private ProgressDialog nDialog;
    private String name, gender, age, email, mode, upiID;
    private String positive = "Camera", negative = "Gallery";
    private boolean isProfile = false, isChanged = false;
    private File imgFile = null;
    private Intent intent;
    private int credit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        //-----------------------------------------Find Views
        nameET = findViewById(R.id.nameET);
        ageET = findViewById(R.id.ageET);
        emailET = findViewById(R.id.emailET);
        profileImage = findViewById(R.id.profileImage);
        editImageView = findViewById(R.id.editImageview);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        upiET = findViewById(R.id.upiET);
        upiMatchET = findViewById(R.id.upiMatchET);
        intent = getIntent();
        mode = (intent.getStringExtra("mode") == null) ? "null" : "update";


        //-----------------------------------------Update Profile
        if (mode.equals("update")) {
            if (myDataHolder.ownerDataHolder.isProfile()) {
                editImageView.setVisibility(View.VISIBLE);
                positive = "Update";
                negative = "Remove";
                isProfile = myDataHolder.ownerDataHolder.isProfile();

            }
            btnSubmit.setText("Update");
            btnLogout.setText("Cancel");
            setData();
        } else {
            credit = 0;
        }

        //-----------------------------------------On selecting image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileImage();
            }
        });


        //-----------------------------------------Submit Button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameET.getText().toString().toLowerCase().trim().replaceAll(" +", " ");
                age = ageET.getText().toString();
                email = emailET.getText().toString().trim().replaceAll(" +", " ");
                upiID = upiET.getText().toString().trim();
                //-----------------------------------------Spinner
                nDialog = new ProgressDialog(OwnerActivity.this);
                nDialog.setMessage("Please wait...");
                nDialog.setTitle("Uploading...");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(false);
                nDialog.show();
                //-----------------------------------------Validation
                if (!isProfile) {
                    if (mode.equals("update") && myDataHolder.ownerDataHolder.isProfile()) {
                        new File(myDataHolder.PROFILE_PATH).delete();
                        myDataHolder.profileStorageREF.delete();
                    }
                } else if (isChanged) {
                    //-----------------------------------------Store Image To Firebase
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile.jpg");
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                    try {
                        OutputStream fOut = new FileOutputStream(imgFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.close();
                    } catch (FileNotFoundException e) {
                    } catch (IOException e) {
                    }
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    myDataHolder.profileStorageREF.putBytes(byteArray);
                }

                if (name.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter name.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (!name.matches("[a-zA-Z]+\\s[a-zA-Z]+")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Name must contain Surname.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                name = name.substring(0, 1).toUpperCase() + name.substring(1, name.indexOf(" ")) + " " + name.substring(name.indexOf(" ") + 1, name.indexOf(" ") + 2).toUpperCase() + name.substring(name.indexOf(" ") + 2);

                if (age.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter age.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (!email.equals("") && !email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Invalid email format.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (radioMale.isChecked()) {
                    gender = "Male";
                } else {
                    gender = "Female";
                }

                if (upiID.equals("") || !upiID.equals(upiMatchET.getText().toString()) || !upiID.contains("@"))
                {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Enter UPI id", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                //-----------------------------------------Store Data To Firebase Storage
                myDataHolder.ownerDataHolder = new OwnerDataHolder(name, gender, age, email, myDataHolder.mobile, upiID, credit, isProfile);
                myDataHolder.ownerDBREF.setValue(myDataHolder.ownerDataHolder);

                //-----------------------------------------Wait 1 second and start Activity
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nDialog.dismiss();
                        Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_SHORT, true).show();
                        if (mode.equals("update")) {
                            return;
                        }
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }, 1000);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("update")) {
                    onBackPressed();
                    return;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OwnerActivity.this, R.style.AppTheme));
                builder.setTitle("Are you Sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myDataHolder.myAUTH.signOut();
                                intent = new Intent(getApplicationContext(), LoginActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    //-----------------------------------------Profile Image Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA_IMAGE) {
            // result of camera
            if (resultCode == RESULT_OK) {
                CropImage.activity(Uri.fromFile(imgFile))
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .setInitialRotation(90)
                        .setMinCropResultSize(640, 640)
                        .start(this);
            }
        } else if (requestCode == REQ_GALLERY_IMAGE) {
            // result of gallery
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .setMinCropResultSize(640, 640)
                        .start(this);

            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImage = result.getUri();
                Glide.with(getApplicationContext()).asBitmap().load(selectedImage).override(300, 300).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                        profileImage.setImageBitmap(bitmap);
                        isProfile = true;
                    }
                });
                isChanged = true;
                positive = "Update";
                negative = "Remove";
                editImageView.setVisibility(View.VISIBLE);
            }
            if (imgFile != null) {
                imgFile.delete();
            }
        }
    }

    private void profileImage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(OwnerActivity.this, R.style.AppTheme));
        builder.setTitle("Profile image")
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (positive.equals("Update")) {
                            positive = "Camera";
                            negative = "Gallery";
                            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    try {
                                        imgFile = File.createTempFile("profile_image", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                                    } catch (IOException ex) {

                                    }
                                    if (imgFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                                "com.project.anygymowner",
                                                imgFile);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(intent, REQ_CAMERA_IMAGE);
                                    }
                                }
                            })
                                    .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(intent, REQ_GALLERY_IMAGE);
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return;
                        }

                        //-----------------------------------------Open Camera
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            imgFile = File.createTempFile("profile_image", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                        } catch (IOException ex) {

                        }
                        if (imgFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                    "com.project.anygymowner",
                                    imgFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(intent, REQ_CAMERA_IMAGE);
                        }
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (negative.equals("Remove")) {
                            isProfile = false;
                            positive = "Camera";
                            negative = "Gallery";
                            profileImage.setImageDrawable(getDrawable(R.drawable.ic_male_user_profile_picture));
                            editImageView.setVisibility(View.GONE);
                            return;
                        }

                        //-----------------------------------------Open Gallery
                        intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, REQ_GALLERY_IMAGE);
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

    private void setData() {
        nameET.setText(myDataHolder.ownerDataHolder.getName());
        emailET.setText(myDataHolder.ownerDataHolder.getEmail());
        ageET.setText(myDataHolder.ownerDataHolder.getAge());
        if (new File(myDataHolder.PROFILE_PATH).exists()) {
            Glide.with(getApplicationContext()).load(myDataHolder.PROFILE_PATH).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profileImage);
            bitmap = BitmapFactory.decodeFile(myDataHolder.PROFILE_PATH);
        }
        if (myDataHolder.ownerDataHolder.getGender().equals("Male")) {
            radioMale.setChecked(true);
        } else {
            radioFemale.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}