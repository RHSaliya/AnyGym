package com.project.anygym.DataHolder;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class myDataHolder {
    //----------Maps
    public static double latitude;
    public static double longitude;
    //---------Hold Data
    public static String mobile;
    public static String PROFILE_PATH = "/sdcard/Android/data/com.project.anygym/files/Pictures/profile.jpg";
    public static String GYM_INTERNAL_IMAGE_PATH = "/sdcard/Android/data/com.project.anygym/files/Pictures/Gym Images/";
    public static final String GYM_BASIC_DATABASE = "Gym/Gym Basic Database/";
    public static final String GYM_DETAILED_DATABASE = "Gym/Gym Detailed Database/";
    public static final String GYM_IMAGE_PATH = "Gym_Images/";
    public static final String GYM_LOCATION_DATABASE = "Gym/Gym Location Database/";
    public static final String REGISTERED_GYMS_PATH = "Gym/Registered Gyms/";
    public static final String USER_VISITATION_DATABASE = "User/User Visitation Database/";
    public static final String USER_DATABASE_PATH = "User/User Database/";
    public static final String USER_MEMBERSHIP_DATABASE_PATH = "User/User Membership Database/";
    public static final String USER_PROFILE_IMAGES_PATH = "User_Profile_Images/";
    //----------Firebase
    public static StorageReference profileStorageREF,gymImgsStorageRef,gymLogoStorageRef;
    public static DatabaseReference userDBREF, gymDBREF, gymDetailDBREF;
    //----------DataHolder Objects
    public static UserDataHolder userDataHolder;
    public static GymDataHolder gymDataHolder;
}
