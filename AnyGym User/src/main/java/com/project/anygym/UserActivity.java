package com.project.anygym;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.project.anygym.DataHolder.UserDataHolder;
import com.project.anygym.DataHolder.myDataHolder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;

public class UserActivity extends AppCompatActivity {

    private static final int REQ_CAMERA_IMAGE = 1;
    private static final int REQ_GALLERY_IMAGE = 2;
    private EditText nameEditText, ageEditText, emailEditText, heightEditText, weightEditText;
    private Button btnSubmit, btnLogout;
    private ImageView profileImage, editImageview;
    private RadioButton radioMale, radioFemale;
    private Bitmap bitmap;
    private ProgressDialog nDialog;
    private String name, gender, email, mode;
    private double weight,height;
    private String positive = "Camera", negative = "Gallery";
    private boolean isProfile = false, isChanged = false;
    private File imgFile = null;
    private Intent intent;
    private long credit;
    private int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //-----------------------------------------Find Views
        nameEditText = findViewById(R.id.nameEditText);
        profileImage = findViewById(R.id.profileImage);
        editImageview = findViewById(R.id.editImageview);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        ageEditText = findViewById(R.id.ageEditText);
        emailEditText = findViewById(R.id.emailEditText);
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
        intent = getIntent();
        mode = (intent.getStringExtra("mode") == null) ? "null" : "update";

        //-----------------------------------------Update Profile
        if (mode.equals("update")) {
            if (myDataHolder.userDataHolder.getIsProfile()) {
                editImageview.setVisibility(View.VISIBLE);
                positive = "Update";
                negative = "Remove";
                isProfile = myDataHolder.userDataHolder.getIsProfile();

            }
            btnSubmit.setText("Update");
            btnLogout.setText("Cancel");
            setData();
        } else {
            credit = 5;
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
                name = nameEditText.getText().toString().toLowerCase().trim().replaceAll(" +", " ");
                age = Integer.parseInt("0"+ageEditText.getText().toString());
                email = emailEditText.getText().toString().trim().replaceAll(" +", " ");
                height = Double.parseDouble("0"+heightEditText.getText().toString());
                weight = Double.parseDouble("0"+weightEditText.getText().toString().replace("Kg",""));

                //-----------------------------------------Spinner
                nDialog = new ProgressDialog(UserActivity.this);
                nDialog.setMessage("Please wait...");
                nDialog.setTitle("Uploading...");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(false);
                nDialog.show();
                //-----------------------------------------Validation
                if (!isProfile) {
                    if (mode.equals("update") && myDataHolder.userDataHolder.getIsProfile()) {
                        Toast.makeText(getApplicationContext(), "Working", Toast.LENGTH_LONG).show();
                        new File(myDataHolder.PROFILE_PATH).delete();
                        myDataHolder.profileStorageREF.delete();
                    }
                } else if (isChanged) {
                    //-----------------------------------------Store Image To Firebase
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, false);
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
                    Toasty.warning(getApplicationContext(), "Name format should be: Name Surname", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                name = name.substring(0, 1).toUpperCase() + name.substring(1, name.indexOf(" ")) + " " + name.substring(name.indexOf(" ") + 1, name.indexOf(" ") + 2).toUpperCase() + name.substring(name.indexOf(" ") + 2);

                if (!email.equals("") && !email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Invalid email format.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (age == 0) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter age.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (height == 0 || height < 3 || height > 10) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter height.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (weight == 0 || weight < 3 || weight > 200) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter weight.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (radioMale.isChecked()) {
                    gender = "Male";
                } else {
                    gender = "Female";
                }
                //-----------------------------------------Store Data To Firebase Storage
                writeDataToFireBase();

                //-----------------------------------------Wait 1 second and start Activity

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nDialog.dismiss();
                        Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_SHORT, true).show();
                        if (mode.equals("update")) {
                            View mainView = getLayoutInflater().inflate(R.layout.activity_main, null);
                            ImageView navHeaderImage = mainView.findViewById(R.id.navHeaderImage);
                            if (myDataHolder.userDataHolder.getIsProfile()) {
                                Glide.with(getApplicationContext()).load(myDataHolder.PROFILE_PATH).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(navHeaderImage);
                            } else {
                                navHeaderImage.setImageDrawable(getDrawable(R.drawable.ic_male_user_profile_picture));
                            }
                            return;
                        }
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserPref", 0);
                        if (pref.getString("ref","").equals("")){
                            intent = new Intent(getApplicationContext(), FirstTimeActivity.class);
                        } else {
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(UserActivity.this, R.style.AppTheme));
                builder.setTitle("Are you Sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
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
                Glide.with(this).asBitmap().load(selectedImage).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                        profileImage.setImageBitmap(bitmap);
                        isProfile = true;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
                isChanged = true;
                positive = "Update";
                negative = "Remove";
                editImageview.setVisibility(View.VISIBLE);
            }
            if (imgFile != null) {
                imgFile.delete();
            }
        }
    }

    private void writeDataToFireBase() {
        myDataHolder.userDataHolder = new UserDataHolder(name, gender, age, email, myDataHolder.mobile, credit, Math.round(height*100.0)/100.0, Math.round(weight*10.0)/10.0, isProfile);
        myDataHolder.userDBREF.setValue(myDataHolder.userDataHolder);
    }

    private void profileImage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(UserActivity.this, R.style.AppTheme));
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
                                                "com.project.anygym",
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
                                    "com.project.anygym",
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
                            editImageview.setVisibility(View.GONE);
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
        nameEditText.setText(myDataHolder.userDataHolder.getName());
        emailEditText.setText(myDataHolder.userDataHolder.getEmail());
        ageEditText.setText(""+myDataHolder.userDataHolder.getAge());
        heightEditText.setText(""+myDataHolder.userDataHolder.getHeight());
        weightEditText.setText(""+myDataHolder.userDataHolder.getWeight()+"Kg");
        if (new File(myDataHolder.PROFILE_PATH).exists()) {
            Glide.with(getApplicationContext()).load(myDataHolder.PROFILE_PATH).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profileImage);
            bitmap = BitmapFactory.decodeFile(myDataHolder.PROFILE_PATH);
        }
        if (myDataHolder.userDataHolder.getGender().equals("Male")) {
            radioMale.setChecked(true);
        } else {
            radioFemale.setChecked(true);
        }
        credit = myDataHolder.userDataHolder.getCredit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}