package com.project.anygymadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private Button btnSendOTP, btnVerifyOTP;
    private EditText otpEditText, passwordEditText, mobileEditText;
    private String otp, mobile, verify, password;
    private final String MATCH_PASSWORD="z";
    private Intent intent;
    private ProgressDialog nDialog;
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
        mobileEditText = findViewById(R.id.mobileEditText);
        otpEditText = findViewById(R.id.otpEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nDialog = new ProgressDialog(LoginActivity.this);

        //-----------------------------------------Validation
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = mobileEditText.getText().toString();

                    password = passwordEditText.getText().toString();
                    if (!password.equals(MATCH_PASSWORD))
                    {
                        passwordEditText.setText(password);
                        Toasty.error(getApplicationContext(), "Wrong Password.", Toast.LENGTH_SHORT, true).show();
                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },300);*/
                        return;
                    }

                if (mobile.equals("")) {
                    Toasty.warning(getApplicationContext(), "Enter Mobile Number.", Toast.LENGTH_SHORT, true).show();
                    return;
                } else if (mobile.length() != 10) {
                    Toasty.warning(getApplicationContext(), "Mobile Number should be 10 digits long.", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                //-----------------------------------------ValidationOver
                sendVerificationCode("+91" + mobile);
                btnSendOTP.setVisibility(View.GONE);

                mobileEditText.setEnabled(false);
                mobileEditText.setBackground(getResources().getDrawable(R.drawable.textinput_disabled));
                mobileEditText.setTextColor(getResources().getColor(R.color.black5));
                //mobileEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mobile, 0, R.drawable.ic_pencil, 0);

                btnVerifyOTP.setVisibility(View.VISIBLE);
                otpEditText.setBackground(getResources().getDrawable(R.drawable.textinput));
                otpEditText.setTextColor(getResources().getColor(R.color.black));
                otpEditText.setEnabled(true);
                otpEditText.requestFocus();
            }
        });


        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = otpEditText.getText().toString();
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
                                nDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_SHORT, true).show();
                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
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
