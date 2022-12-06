package com.project.anygymowner;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygymowner.Adapter.AddGymImageAdapter;
import com.project.anygymowner.DataHolder.GymDataHolder;
import com.project.anygymowner.DataHolder.GymLocationDataHolder;
import com.project.anygymowner.DataHolder.myDataHolder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AddGymInfoActivity extends AppCompatActivity {
    private static final int REQ_CAMERA_IMAGE = 1;
    private static final int REQ_GALLERY_IMAGE = 2;
    public static AddGymImageAdapter addGymImageAdapter;
    boolean Accept = false;
    private EditText gymTitleET, gymShopNoET, buildingNameET, landmarkET, areaET, pinCodeET, cityET, stateET, descriptionET;
    private EditText chargeET;
    private TextView chargeTV;
    private int charge;
    private TextView openingTimeTV, closingTimeTV, openingTimeFemalesTV, closingTimeFemalesTV;
    private TextView spinnerClick;
    private LinearLayout timingSelector, femaleTimingSelector, animate;
    private ImageView logoImage, editImageview;
    private Button btnSubmit, btnLogout;
    public static Button btnCancelSwap;
    private Bitmap bitmap;
    private ProgressDialog nDialog;
    private String positive = "Camera", negative = "Gallery";
    private String gymTitle, gymShopNo, buildingName, landmark, area, pinCode, gymAddress, gymDescription, gymUserType, gymTimings, gymCity, gymState;
    private File imgFile = null;
    private Intent intent;
    private Spinner usersTypeSpinner;
    private RecyclerView imageRecycler;
    private boolean isLogo = false, isChanged = false;
    private TimePickerDialog timePickerDialog;
    private String min, daynight;
    public static List<File> imageFileList;
    private Uri selectedImage;
    public static List<Uri> imageUriList;
    private ByteArrayOutputStream stream;
    private int i, imageFlag = 0;
    private Geocoder coder;
    private List<Address> mapAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gym);
        //EditTexts
        gymTitleET = findViewById(R.id.gymTitleEditText);
        gymShopNoET = findViewById(R.id.shopNoEditText);
        buildingNameET = findViewById(R.id.buildingNameEditText);
        landmarkET = findViewById(R.id.landmarkEditText);
        areaET = findViewById(R.id.areaEditText);
        pinCodeET = findViewById(R.id.pinCodeEditText);
        cityET = findViewById(R.id.cityEditText);
        stateET = findViewById(R.id.stateEditText);
        descriptionET = findViewById(R.id.descriptionET);
        //ImageViews
        logoImage = findViewById(R.id.logoImage);
        editImageview = findViewById(R.id.editImageview);
        //RecyclerViews
        imageRecycler = findViewById(R.id.imageRecycler);
        //Spiners
        usersTypeSpinner = findViewById(R.id.usersTypeSpinner);
        //LinearLayouts
        animate = findViewById(R.id.animate);
        //Buttons
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);
        btnCancelSwap = findViewById(R.id.btnCancelSwap);
        //Lists
        imageUriList = new ArrayList<>();
        imageFileList = new ArrayList<>();

        coder = new Geocoder(this);
        myDataHolder.registeredGymsDBREF = myDataHolder.myDB.getReference(myDataHolder.REGISTERED_GYMS_PATH);

        Timings();
        image_recycler();
        wordCount();
        charges();
        Clear_GlideCaches();

        spinnerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersTypeSpinner.performClick();
            }
        });

        //-----------------------------------------Submit Button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //-----------------------------------------Spinner
                nDialog = new ProgressDialog(AddGymInfoActivity.this);
                nDialog.setMessage("Please wait...");
                nDialog.setTitle("Uploading...");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(false);
                nDialog.show();

                gymTitle = gymTitleET.getText().toString().trim().replaceAll(" +", " ");
                gymShopNo = gymShopNoET.getText().toString().trim();
                buildingName = buildingNameET.getText().toString().trim().replaceAll(" +", " ");
                landmark = landmarkET.getText().toString().trim().replaceAll(" +", " ");
                area = areaET.getText().toString().trim().replaceAll(" +", " ");
                pinCode = pinCodeET.getText().toString();
                gymCity = cityET.getText().toString().trim().replaceAll(" +", " ");
                gymState = stateET.getText().toString().trim().replaceAll(" +", " ");
                gymDescription = descriptionET.getText().toString().trim().replaceAll(" +", " ");
                gymUserType = usersTypeSpinner.getSelectedItem().toString();


                //-----------------------------------------Validation
                if (gymTitle.equals("")) {
                    nDialog.dismiss();
                    gymTitleET.requestFocus();
                    Toasty.warning(getApplicationContext(), "Please enter Gym Name.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (!isLogo) {
                } else if (isChanged) {
                    //-----------------------------------------Store Image To Firebase
                    myDataHolder.logoStorageREF = myDataHolder.mySTORAGE.getReference(myDataHolder.GYM_IMAGE_PATH + gymTitle + "/logo.png");
                    stream = new ByteArrayOutputStream();
                    imgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "logo.jpg");
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
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    byte[] byteArray = stream.toByteArray();
                    myDataHolder.logoStorageREF.putBytes(byteArray);
                }

                if (imageFileList.size() < 5) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Minimum 4 Images required.", Toast.LENGTH_SHORT, true).show();
                    return;
                }


                if (gymShopNo.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter Shop No.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (buildingName.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter Building name.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (landmark.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter Landmark.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (area.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter Area.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (pinCode.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Please enter pinCode.", Toast.LENGTH_SHORT, true).show();
                    return;
                } else if (pinCode.length() < 6) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Pincode should be 6 digits long.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (gymCity.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Enter City.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (gymState.equals("")) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Enter State.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (!Accept) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Accept charges T&C.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (charge == 0) {
                    nDialog.dismiss();
                    Toasty.warning(getApplicationContext(), "Enter Charge.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                //-----------------------------------------Store Data To Firebase Storage
                if (gymDescription.equals("")) {
                    gymDescription = "Welcome to " + gymTitle + ".";
                }
                gymAddress = gymShopNo + "#" + buildingName + "#" + landmark + "#" + area + "#" + pinCode + "#" + gymCity + "#" + gymState;

                if (gymUserType.equals("Male")) {
                    gymTimings = "<b>Male :</b> " + openingTimeTV.getText().toString() + " - " + closingTimeTV.getText().toString();
                } else if (gymUserType.equals("Female")) {
                    gymTimings = "<b>Female :</b> " + openingTimeFemalesTV.getText().toString() + " - " + closingTimeFemalesTV.getText().toString();
                } else if (gymUserType.equals("Male & Female")) {
                    gymTimings = "<b>Male/Female</b> : " + openingTimeTV.getText().toString() + " - " + closingTimeTV.getText().toString();
                } else {
                    gymTimings = "<b>Male :</b> " + openingTimeTV.getText().toString() + " - " + closingTimeTV.getText().toString() + "#<br><b>Female :</b> " + openingTimeFemalesTV.getText().toString() + " - " + closingTimeFemalesTV.getText().toString();
                }

                myDataHolder.registeredGymsDBREF.child(gymTitle.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            nDialog.dismiss();
                            Toasty.error(getApplicationContext(), "Gym with this name already exists", Toast.LENGTH_SHORT, true).show();
                            return;
                        } else {
                            writeDataToFireBase();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void charges() {
        chargeET = findViewById(R.id.chargeET);
        chargeTV = findViewById(R.id.chargeTV);

        chargeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charge = Integer.parseInt(0 + chargeET.getText().toString());
                chargeTV.setText(charge * 5 + " ₹");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void wordCount() {
        final EditText descriptionEdittext = findViewById(R.id.descriptionET);
        final TextView descriptionCharCount = findViewById(R.id.descriptionCharCount);

        descriptionEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descriptionCharCount.setText("" + descriptionEdittext.length() + "/2000");
                if (descriptionEdittext.getLineCount() > 7) {
                    descriptionEdittext.setLines(descriptionEdittext.getLineCount());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void image_recycler() {
        GridLayoutManager imageGridLayout = new GridLayoutManager(getApplicationContext(), 4);
        imageRecycler.setLayoutManager(imageGridLayout);
        addGymImageAdapter = new AddGymImageAdapter(imageFileList);
        imageFileList.add(null);
        addGymImageAdapter.notifyDataSetChanged();
        imageRecycler.setAdapter(addGymImageAdapter);
        addGymImageAdapter.notifyDataSetChanged();
    }

    private void Timings() {
        spinnerClick = findViewById(R.id.top_spinner);
        timingSelector = findViewById(R.id.timingsSelector);
        femaleTimingSelector = findViewById(R.id.femaleTimingsSelector);
        openingTimeTV = findViewById(R.id.openingTime);
        closingTimeTV = findViewById(R.id.closingTime);
        openingTimeFemalesTV = findViewById(R.id.openingTimeFemales);
        closingTimeFemalesTV = findViewById(R.id.closingTimeFemales);

        List<String> categories = new ArrayList<>();
        categories.add("Male");
        categories.add("Female");
        categories.add("Male & Female");
        categories.add("Male & Female (Different Time)");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usersTypeSpinner.setAdapter(dataAdapter);
        usersTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        spinnerClick.setText("Male");
                        femaleTimingSelector.setVisibility(View.GONE);
                        break;
                    }
                    case 1: {
                        spinnerClick.setText("Female");
                        femaleTimingSelector.setVisibility(View.GONE);
                        break;
                    }
                    case 2: {
                        spinnerClick.setText("Male & Female");
                        femaleTimingSelector.setVisibility(View.GONE);
                        break;
                    }
                    case 3: {
                        spinnerClick.setText("Male & Female (Different Time)");
                        femaleTimingSelector.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        openingTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str[] = openingTimeTV.getText().toString().split(":| ");
                int hour = str[2].equals("AM") ? Integer.parseInt(str[0]) : Integer.parseInt(str[0]) + 12;
                timePickerDialog = new TimePickerDialog(AddGymInfoActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                daynight = hourOfDay < 12 ? "AM" : "PM";
                                hourOfDay = hourOfDay == 0 ? 12 : hourOfDay;
                                hourOfDay = hourOfDay < 13 ? hourOfDay : hourOfDay - 12;
                                min = minute < 10 ? "0" + minute : "" + minute;

                                openingTimeTV.setText(hourOfDay + ":" + min + " " + daynight);
                            }
                        }, hour, Integer.parseInt(str[1]), false);
                timePickerDialog.show();
            }
        });

        closingTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str[] = closingTimeTV.getText().toString().split(":| ");
                int hour = str[2].equals("AM") ? Integer.parseInt(str[0]) : Integer.parseInt(str[0]) + 12;

                timePickerDialog = new TimePickerDialog(AddGymInfoActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                daynight = hourOfDay < 12 ? "AM" : "PM";
                                hourOfDay = hourOfDay == 0 ? 12 : hourOfDay;
                                hourOfDay = hourOfDay < 13 ? hourOfDay : hourOfDay - 12;
                                min = minute < 10 ? "0" + minute : "" + minute;

                                closingTimeTV.setText(hourOfDay + ":" + min + " " + daynight);
                            }
                        }, hour, Integer.parseInt(str[1]), false);
                timePickerDialog.show();
            }
        });

        openingTimeFemalesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str[] = openingTimeFemalesTV.getText().toString().split(":| ");
                int hour = str[2].equals("AM") ? Integer.parseInt(str[0]) : Integer.parseInt(str[0]) + 12;
                timePickerDialog = new TimePickerDialog(AddGymInfoActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                daynight = hourOfDay < 12 ? "AM" : "PM";
                                hourOfDay = hourOfDay == 0 ? 12 : hourOfDay;

                                hourOfDay = hourOfDay < 13 ? hourOfDay : hourOfDay - 12;
                                min = minute < 10 ? "0" + minute : "" + minute;

                                openingTimeFemalesTV.setText(hourOfDay + ":" + min + " " + daynight);
                            }
                        }, hour, Integer.parseInt(str[1]), false);
                timePickerDialog.show();
            }
        });

        closingTimeFemalesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str[] = closingTimeFemalesTV.getText().toString().split(":| ");
                int hour = str[2].equals("AM") ? Integer.parseInt(str[0]) : Integer.parseInt(str[0]) + 12;
                timePickerDialog = new TimePickerDialog(AddGymInfoActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                daynight = hourOfDay < 12 ? "AM" : "PM";
                                hourOfDay = hourOfDay == 0 ? 12 : hourOfDay;
                                hourOfDay = hourOfDay < 13 ? hourOfDay : hourOfDay - 12;
                                min = minute < 10 ? "0" + minute : "" + minute;

                                closingTimeFemalesTV.setText(hourOfDay + ":" + min + " " + daynight);
                            }
                        }, hour, Integer.parseInt(str[1]), false);
                timePickerDialog.show();
            }
        });
    }

    private void writeDataToFireBase() {
        myDataHolder.gymDBREF = myDataHolder.myDB.getReference("Verify/"+myDataHolder.GYM_BASIC_DATABASE + gymState +"-"+ gymCity +"-"+ area +"-"+gymTitle);
        myDataHolder.gymDataHolder = new GymDataHolder(gymTitle, gymState +"-"+ gymCity +"-"+ area +"-"+gymTitle, gymAddress, myDataHolder.ownerDataHolder.getName(), 6, myDataHolder.mobile, "C", gymTimings, charge, isLogo);
        myDataHolder.gymDBREF.setValue(myDataHolder.gymDataHolder);

        try {
            mapAddress = coder.getFromLocationName(gymTitle + ", " + gymAddress.replace("#", ", "), 1);
            if (mapAddress == null) {
                mapAddress = coder.getFromLocationName(gymTitle + ", " + landmark + ", " + area + ", " + pinCode, 1);
                if (mapAddress == null) {
                    mapAddress = coder.getFromLocationName(pinCode, 1);
                    if (mapAddress == null) {
                        FirebaseDatabase.getInstance().getReference("Verify/"+myDataHolder.GYM_LOCATION_DATABASE + gymState +"-"+ gymCity +"-"+ area +"-"+ myDataHolder.gymDataHolder.getTitle()).setValue(new GymLocationDataHolder(0, 0,  gymState +"-"+ gymCity +"-"+ area +"-"+gymTitle));
                    } else {
                        Address location = mapAddress.get(0);
                        FirebaseDatabase.getInstance().getReference("Verify/"+myDataHolder.GYM_LOCATION_DATABASE + gymState +"-"+ gymCity +"-"+ area +"-"+ myDataHolder.gymDataHolder.getTitle()).setValue(new GymLocationDataHolder(location.getLatitude(), location.getLongitude(),  gymState +"-"+ gymCity +"-"+ area +"-"+gymTitle));
                    }
                } else {
                    Address location = mapAddress.get(0);
                    FirebaseDatabase.getInstance().getReference("Verify/"+myDataHolder.GYM_LOCATION_DATABASE + gymState +"-"+ gymCity +"-"+ area +"-"+ myDataHolder.gymDataHolder.getTitle()).setValue(new GymLocationDataHolder(location.getLatitude(), location.getLongitude(),  gymState +"-"+ gymCity +"-"+ area +"-"+gymTitle));
                }
            } else {
                Address location = mapAddress.get(0);
                FirebaseDatabase.getInstance().getReference("Verify/"+myDataHolder.GYM_LOCATION_DATABASE + gymState +"-"+ gymCity +"-"+ area +"-"+ myDataHolder.gymDataHolder.getTitle()).setValue(new GymLocationDataHolder(location.getLatitude(), location.getLongitude(),  gymState +"-"+ gymCity +"-"+ area +"-"+gymTitle));
            }

        } catch (IOException ex) {

        }

        imageFileList.remove(imageFileList.size() - 1);
        myDataHolder.gymDetailDBREF = myDataHolder.myDB.getReference("Verify/"+myDataHolder.GYM_DETAILED_DATABASE + gymState +"-"+ gymCity +"-"+ area +"-"+ gymTitle);
        myDataHolder.gymDetailDBREF.child("about").setValue(gymDescription + "#" + imageFileList.size());

        for (i = 0; i < imageFileList.size(); i++) {
            myDataHolder.gymImgsStorageRef = myDataHolder.mySTORAGE.getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/" + i + ".png");
            myDataHolder.gymImgsStorageRef.putFile(Uri.fromFile(imageFileList.get(i)));

            myDataHolder.gymImgsStorageRef = myDataHolder.mySTORAGE.getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/thumbnail/" + i + ".png");
            imgFile = new File(imageFileList.get(i).getAbsolutePath().replace(".jpg", "t.jpg"));
            myDataHolder.gymImgsStorageRef.putFile(Uri.fromFile(imgFile));
        }

        //--------------------Add new Gym to owner Gyms List
        myDataHolder.ownerDataHolder.addGym(gymState+"-"+gymCity+"-"+area+"-"+gymTitle);
        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()+"/ownerGyms/"+gymState+"-"+gymCity+"-"+area+"-"+gymTitle).setValue("N");

        //-----------------------------------------Start Activity
        nDialog.dismiss();
        Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_SHORT, true).show();
        intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //-----------------------------------------Profile Image Result
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable Intent data) {
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
                Glide.with(this).asBitmap().load(selectedImage).override(300, 300).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap = resource;
                        logoImage.setImageBitmap(bitmap);
                        isLogo = true;
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
        } else if (requestCode == AddGymImageAdapter.REQ_GALLERY_IMAGE_RECYCLER) {
            // result of gallery
            if (resultCode == RESULT_OK) {
                selectedImage = data.getData();
                if (imageUriList.contains(selectedImage)) {
                    Toasty.warning(getApplicationContext(), "Ths Image Is Already Selected", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                Glide.with(this).asBitmap().load(selectedImage).override(2000, 960).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (resource.getWidth() < 640 || resource.getHeight() < 480) {
                            Toasty.warning(getApplicationContext(), "Please Select a Higher Resolution Image", Toast.LENGTH_SHORT, true).show();
                            return;
                        }
                        //------------------------------Store Image to Storage
                        imageUriList.add(selectedImage);
                        imageFileList.remove(imageFileList.size() - 1);
                        imgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), new SimpleDateFormat("hhmmss").format(new Date()) + ".jpg");
                        imageFileList.add(imgFile);
                        if (imageFileList.size() < 9) {
                            imageFileList.add(null);
                        }
                        addGymImageAdapter.notifyDataSetChanged();

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(imgFile);
                            resource.compress(Bitmap.CompressFormat.JPEG, 40, fos);
                            imgFile = new File(imgFile.getAbsolutePath().replace(".jpg", "t.jpg"));
                            fos = new FileOutputStream(imgFile);
                            Bitmap.createScaledBitmap(resource, (resource.getWidth() * 480) / resource.getHeight(), 480, false).compress(Bitmap.CompressFormat.JPEG, 50, fos);
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
            }
        }
    }

    public void onUsers(View view) {
        LinearLayout usersLinear = findViewById(R.id.usersLinear);
        TextView toggleUsers = findViewById(R.id.toggleUsers);
        if (usersLinear.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(200));
            usersLinear.setVisibility(View.VISIBLE);
            toggleUsers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
        } else {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(100));
            usersLinear.setVisibility(View.GONE);
            toggleUsers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        }
    }

    public void onAddress(View view) {
        final LinearLayout addressLinear = findViewById(R.id.addressLinear);
        TextView toggleAddres = findViewById(R.id.toggleAddress);
        if (addressLinear.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(200));
            addressLinear.setVisibility(View.VISIBLE);
            toggleAddres.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
        } else {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(100));
            addressLinear.setVisibility(View.GONE);
            toggleAddres.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        }
    }

    public void onImages(View view) {
        final RelativeLayout imagesLinear = findViewById(R.id.imagesLinear);
        TextView toggleImages = findViewById(R.id.toggleImages);
        if (imagesLinear.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(200));
            imagesLinear.setVisibility(View.VISIBLE);
            toggleImages.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
        } else {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(100));
            imagesLinear.setVisibility(View.GONE);
            toggleImages.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        }
    }

    public void onDescription(View view) {
        final ConstraintLayout descriptionLinear = findViewById(R.id.descriptionLinear);
        TextView toggleDescription = findViewById(R.id.toggleDescription);
        if (descriptionLinear.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(200));
            descriptionLinear.setVisibility(View.VISIBLE);
            toggleDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
        } else {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(100));
            descriptionLinear.setVisibility(View.GONE);
            toggleDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        }
    }

    public void onLogoImageClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddGymInfoActivity.this, R.style.AppTheme));
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
                            isLogo = false;
                            positive = "Camera";
                            negative = "Gallery";
                            logoImage.setImageDrawable(getDrawable(R.drawable.ic_gymlogo_back));
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

    public void onLogout(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddGymInfoActivity.this, R.style.AppTheme));
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

    public void onCharges(View view) {
        LinearLayout termsLayout = findViewById(R.id.termsLinear);
        TextView toggleCharges = findViewById(R.id.toggleCharges);
        if (termsLayout.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(200));
            termsLayout.setVisibility(View.VISIBLE);
            toggleCharges.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
        } else {
            TransitionManager.beginDelayedTransition(animate, new AutoTransition().setDuration(100));
            termsLayout.setVisibility(View.GONE);
            toggleCharges.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        }
    }

    public void onAccept(View view) {
        final Button btnAccept = findViewById(R.id.btnAccept);
        final LinearLayout chargesLinear = findViewById(R.id.chargesLinear);
        btnAccept.setVisibility(View.GONE);
        chargesLinear.setVisibility(View.VISIBLE);
        Accept = true;
    }

    public void Clear_GlideCaches() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.get(AddGymInfoActivity.this).clearMemory();
            }
        }, 0);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(AddGymInfoActivity.this).clearDiskCache();
            }
        });
    }
}