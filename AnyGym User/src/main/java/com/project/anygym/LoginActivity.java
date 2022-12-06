package com.project.anygym;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygym.DataHolder.UserDataHolder;
import com.project.anygym.DataHolder.myDataHolder;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private Button btnSendOTP, btnVerifyOTP;
    private EditText otpET, mobileET;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String otp, mobile, verify;
    private ProgressDialog nDialog;
    private Intent intent;
    private TextView changeNoTV, resendOTPTV;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verify = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toasty.warning(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT, true).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        otpET = findViewById(R.id.otpET);
        mobileET = findViewById(R.id.mobileET);
        nDialog = new ProgressDialog(LoginActivity.this);
        resendOTPTV = findViewById(R.id.resendOTPTV);
        changeNoTV = findViewById(R.id.changeNoTV);


        //-----------------------------------------Validation
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = mobileET.getText().toString();
                if (mobile.length() != 10) {
                    Toasty.warning(getApplicationContext(), "Mobile Number should be 10 digits long.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                //-----------------------------------------ValidationOver
                sendVerificationCode("+91" + mobile);
                btnSendOTP.setVisibility(View.GONE);

                mobileET.setEnabled(false);
                mobileET.setBackground(getResources().getDrawable(R.drawable.textinput_disabled));
                mobileET.setTextColor(getResources().getColor(R.color.black5));


                btnVerifyOTP.setVisibility(View.VISIBLE);
                otpET.setBackground(getResources().getDrawable(R.drawable.textinput));
                otpET.setTextColor(getResources().getColor(R.color.black));
                otpET.setEnabled(true);
                otpET.requestFocus();
            }
        });

        resendOTPTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = mobileET.getText().toString();
                if (mobile.length() != 10) {
                    Toasty.warning(getApplicationContext(), "Mobile Number should be 10 digits long.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                //-----------------------------------------ValidationOver
                sendVerificationCode("+91" + mobile);
            }
        });

        changeNoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSendOTP.setVisibility(View.VISIBLE);
                otpET.setEnabled(false);
                otpET.setBackground(getResources().getDrawable(R.drawable.textinput_disabled));
                otpET.setTextColor(getResources().getColor(R.color.black5));


                btnVerifyOTP.setVisibility(View.GONE);
                mobileET.setBackground(getResources().getDrawable(R.drawable.textinput));
                mobileET.setTextColor(getResources().getColor(R.color.black));
                mobileET.setEnabled(true);
                mobileET.requestFocus();
            }
        });

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = otpET.getText().toString();

                //-----------------------------------------Verify OTP
                if (otp.equals("")) {
                    Toasty.error(getApplicationContext(), "Enter OTP.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                nDialog.setMessage("Please Wait...");
                nDialog.setTitle("Verifying ");
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(false);
                nDialog.show();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify, otp);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_SHORT, true).show();
                                    myDataHolder.mobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3);
                                    myDataHolder.profileStorageREF = FirebaseStorage.getInstance().getReference(myDataHolder.USER_PROFILE_IMAGES_PATH + myDataHolder.mobile + ".png");
                                    myDataHolder.userDBREF = FirebaseDatabase.getInstance().getReference(myDataHolder.USER_DATABASE_PATH + myDataHolder.mobile);

                                    myDataHolder.userDBREF.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            nDialog.dismiss();
                                            myDataHolder.userDataHolder = snapshot.getValue(UserDataHolder.class);
                                            if (myDataHolder.userDataHolder != null) {
                                                SharedPreferences pref = getApplicationContext().getSharedPreferences("UserPref", 0);
                                                if (pref.getString("ref","").equals("")){
                                                    intent = new Intent(getApplicationContext(), FirstTimeActivity.class);
                                                } else {
                                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                                }
                                            } else {
                                                intent = new Intent(getApplicationContext(), UserActivity.class);
                                            }
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    nDialog.dismiss();
                                    Toasty.error(getApplicationContext(), "Wrong OTP.", Toast.LENGTH_SHORT, true).show();
                                }
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT, true).show();
                            }
                        });
                //-----------------------------------------Verify OTP OVER
            }
        });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }
}
