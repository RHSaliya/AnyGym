package com.project.anygym.CustomFunctions;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygym.DataHolder.myDataHolder;
import com.project.anygym.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BuyMembershipFunction {
    BottomSheetDialog membershipDialog;
    View membershipView;
    Context context;
    ImageView gymLogo;
    TextView gymTitle, gymAddress, gymCharge, total, daysMembershipInfo, chargeMembershipInfo, gymMembershipName;
    EditText days;
    Button cancel, confirm;
    Map updateMultiple;
    LottieAnimationView lottieAnimationView;
    int totalCharge, totalDays;
    LinearLayout layoutMembership,layoutMembershipResult;

    public BuyMembershipFunction(Context context) {
        this.context = context;
        membershipView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_membership, null);
        membershipDialog = new BottomSheetDialog(context);
        membershipDialog.setContentView(membershipView);
        gymLogo = membershipView.findViewById(R.id.gym_logo);
        gymTitle = membershipView.findViewById(R.id.gymTitle);
        gymAddress = membershipView.findViewById(R.id.gymAddress);
        gymCharge = membershipView.findViewById(R.id.gymCharge);
        total = membershipView.findViewById(R.id.total);
        days = membershipView.findViewById(R.id.days);
        cancel = membershipView.findViewById(R.id.cancel);
        confirm = membershipView.findViewById(R.id.confirm);
        daysMembershipInfo = membershipView.findViewById(R.id.daysMembershipInfo);
        chargeMembershipInfo = membershipView.findViewById(R.id.chargeMembershipInfo);
        gymMembershipName = membershipView.findViewById(R.id.gymMembershipName);
        lottieAnimationView = membershipView.findViewById(R.id.lottieMembershipAnimation);
        layoutMembership = membershipView.findViewById(R.id.layoutMembership);
        layoutMembershipResult = membershipView.findViewById(R.id.membershipResultLayout);
        updateMultiple = new HashMap();
    }

    public void  buyMembership()
    {
        layoutMembershipResult.setAlpha(0);
        layoutMembership.setAlpha(1);
        totalDays = Integer.parseInt("0" + days.getText().toString());
        totalCharge = Integer.parseInt("0" + days.getText().toString()) * myDataHolder.gymDataHolder.getCharge();
        membershipDialog.show();

        gymTitle.setText(myDataHolder.gymDataHolder.getTitle());
        gymAddress.setText(myDataHolder.gymDataHolder.getAddress().replace("#", ", "));
        gymCharge.setText("x" + myDataHolder.gymDataHolder.getCharge());
        total.setText(totalCharge + " Credits");

        if (myDataHolder.gymDataHolder.isLogo()) {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/logo.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(gymLogo);
                }
            });
        } else {
            myDataHolder.gymLogoStorageRef = FirebaseStorage.getInstance().getReference(myDataHolder.GYM_IMAGE_PATH + myDataHolder.gymDataHolder.getRelativeName() + "/0.png");
            myDataHolder.gymLogoStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).centerCrop().into(gymLogo);
                }
            });
        }

        days.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                totalDays = Integer.parseInt("0" + days.getText().toString());
                totalCharge = totalDays * myDataHolder.gymDataHolder.getCharge();
                total.setText(totalCharge + " Credits");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membershipDialog.cancel();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setEnabled(false);
                cancel.setEnabled(false);
                if (totalCharge == 0) {
                    Toast.makeText(context, "Enter Days", Toast.LENGTH_SHORT).show();
                    return;
                }

                myDataHolder.userDBREF.child("credit").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((long) dataSnapshot.getValue() < totalCharge) {
                            daysMembershipInfo.setText(Html.fromHtml("<font color='red'>Insufficient Credits <b>"+(totalCharge-(long) dataSnapshot.getValue())+"</b> more Credits Required</font>"));
                            chargeMembershipInfo.setVisibility(View.GONE);
                            gymMembershipName.setText(myDataHolder.gymDataHolder.getTitle());
                            lottieAnimationView.setAnimation(R.raw.reject);
                            lottieAnimationView.playAnimation();
                            layoutMembership.setAlpha(0f);
                            layoutMembershipResult.setAlpha(1f);
                        } else {
                            chargeMembershipInfo.setVisibility(View.VISIBLE);
                            updateMultiple.put(myDataHolder.USER_DATABASE_PATH + myDataHolder.userDataHolder.getMobile() + "/credit", (long) dataSnapshot.getValue() - totalCharge);
                            chargeMembershipInfo.setText(Html.fromHtml("<font color='red'><b>-"+totalCharge+" Credits</b></font><br>" + ((long) dataSnapshot.getValue() - totalCharge) + " Credits left"));


                            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()+"/credits").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    updateMultiple.put(myDataHolder.GYM_DETAILED_DATABASE + myDataHolder.gymDataHolder.getRelativeName()+"/credits", (dataSnapshot.getValue() == null ? totalCharge : (long) dataSnapshot.getValue() + totalCharge));

                                    FirebaseDatabase.getInstance().getReference("User/User Membership Database/"+myDataHolder.userDataHolder.getMobile()).child(myDataHolder.gymDataHolder.getRelativeName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String path = new SimpleDateFormat("/yyyy/MM/ddHHmmss", Locale.getDefault()).format(new Date());
                                            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                                            String time = new SimpleDateFormat("hh:mm:ss aa").format(new Date());

                                            updateMultiple.put("User/User Membership Database/"+myDataHolder.userDataHolder.getMobile()+"/"+ myDataHolder.gymDataHolder.getRelativeName(), (dataSnapshot.getValue() == null) ? totalDays : (long) dataSnapshot.getValue() + totalDays);
                                            updateMultiple.put("Gym/Gym Detailed Database/"+myDataHolder.gymDataHolder.getRelativeName()+"/members/"+myDataHolder.userDataHolder.getMobile(), (dataSnapshot.getValue() == null) ? totalDays : (long) dataSnapshot.getValue() + totalDays);
                                            updateMultiple.put("Gym/Gym Detailed Database/"+myDataHolder.gymDataHolder.getRelativeName()+"/transactions/"+path+myDataHolder.userDataHolder.getMobile(),myDataHolder.userDataHolder.getMobile()+"#Membership#"+date+"#"+time+"#"+myDataHolder.gymDataHolder.getCharge()+"#-"+totalCharge+"#+"+totalDays);
                                            updateMultiple.put("User/User Transaction Database/"+myDataHolder.userDataHolder.getMobile()+"/"+new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()),myDataHolder.gymDataHolder.getRelativeName()+"#Membership#"+date+"#"+time+"#"+myDataHolder.gymDataHolder.getCharge()+"#-"+totalCharge+"#+"+totalDays);
                                            FirebaseDatabase.getInstance().getReference().updateChildren(updateMultiple);

                                            daysMembershipInfo.setText(Html.fromHtml("<font color='#008000'><b>+"+totalDays+" Days</b></font><br>" + ((dataSnapshot.getValue() == null) ? totalDays : (long) dataSnapshot.getValue() + totalDays )+ "</b> Days total"));
                                            gymMembershipName.setText(myDataHolder.gymDataHolder.getTitle());
                                            lottieAnimationView.setAnimation(R.raw.success);
                                            lottieAnimationView.playAnimation();
                                            layoutMembership.setAlpha(0f);
                                            layoutMembershipResult.setAlpha(1f);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                cancel.setEnabled(true);
                confirm.setEnabled(true);
            }
        });
    }
}
