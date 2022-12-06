package com.project.anygymowner.DataHolder;

import android.graphics.drawable.Drawable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class myDataHolder {
    //---------Hold Data
    public static String mobile;
    public static final String PROFILE_PATH = "/sdcard/Android/data/com.project.anygymowner/files/Pictures/profile.jpg";
    public static final String IMAGE_PATH = "/sdcard/Android/data/com.project.anygymowner/files/Pictures/";
    public static final String LOGO_PATH = "/sdcard/Android/data/com.project.anygymowner/files/Pictures/logo.jpg";
    public static final String GYM_BASIC_DATABASE = "Gym/Gym Basic Database/";
    public static final String GYM_DETAILED_DATABASE = "Gym/Gym Detailed Database/";
    public static final String GYM_LOCATION_DATABASE = "Gym/Gym Location Database/";
    public static final String VERIFY_GYM_BASIC_DATABASE = "Verify/Gym/Gym Basic Database/";
    public static final String VERIFY_GYM_DETAILED_DATABASE = "Verify/Gym/Gym Detailed Database/";
    public static final String VERIFY_GYM_LOCATION_DATABASE = "Verify/Gym/Gym Location Database/";
    public static final String GYM_IMAGE_PATH = "Gym_Images/";
    public static final String REGISTERED_GYMS_PATH = "Gym/Registered Gyms/";
    public static final String USER_DATABASE_PATH = "User/User Database/";
    public static final String GYM_CREDITS_PATH = "Gym/Gym Credits Database";
    public static final String USER_PROFILE_IMAGES_PATH = "User_Profile_Images/";
    public static final String OWNER_PROFILE_IMAGE_PATH = "Owner_Profile_Images/";
    public static final String OWNER_DATABASE_PATH = "Owner/Owner Database/";
    public static String gymName, about;

    //----------Firebase
    public static FirebaseAuth myAUTH;
    public static FirebaseDatabase myDB;
    public static FirebaseStorage mySTORAGE;
    public static StorageReference profileStorageREF, logoStorageREF, gymImgsStorageRef;
    public static DatabaseReference gymDBREF, userDBREF, gymDetailDBREF, ownerDBREF,registeredGymsDBREF,gymVerifyDBREF,gymVerifyDetailedDBREF;
    //----------DataHolder
    public static GymDataHolder gymDataHolder;
    public static OwnerDataHolder ownerDataHolder;

    public static StorageReference gymLogoStorageRef;
}