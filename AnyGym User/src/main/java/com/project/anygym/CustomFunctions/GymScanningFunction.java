package com.project.anygym.CustomFunctions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygym.DataHolder.myDataHolder;
import com.project.anygym.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GymScanningFunction {
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private BottomSheetDialog barcodeDialog;
    private View barcodeView;
    private Context context;
    private TextView gymName, otherInfo, warningTV;
    private LinearLayout linearScanningLayout, linearResultLayout;
    private Map updateMultiple;
    private long charge;
    private LottieAnimationView resultLAV;
    private LottieAnimationView loadingLAV;
    private Button btnStop;
    private Handler handler;
    private Vibrator vibrator;
    private String path, date, time;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    public GymScanningFunction(Context context) {
        this.context = context;
        barcodeView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_barcode, null);
        barcodeDialog = new BottomSheetDialog(context);
        barcodeDialog.setContentView(barcodeView);
        surfaceView = barcodeView.findViewById(R.id.barcodeSurface);
        gymName = barcodeView.findViewById(R.id.gymName);
        otherInfo = barcodeView.findViewById(R.id.otherInfo);
        warningTV = barcodeView.findViewById(R.id.warningTV);
        linearScanningLayout = barcodeView.findViewById(R.id.linearScanningLayout);
        linearResultLayout = barcodeView.findViewById(R.id.linearResultLayout);
        resultLAV = barcodeView.findViewById(R.id.resultLAV);
        btnStop = barcodeView.findViewById(R.id.btnStop);
        loadingLAV = barcodeView.findViewById(R.id.loadingLAV);
        updateMultiple = new HashMap();
        handler = new Handler();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        pref = context.getSharedPreferences("UserPref", 0);
        editor = pref.edit();

        barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setRequestedPreviewSize(640, 640)
                .setAutoFocusEnabled(true) //you should add this feature
                .setRequestedFps(1f)
                .build();
    }

    public void scanGym() {
        linearScanningLayout.setAlpha(1f);
        linearResultLayout.setAlpha(0f);
        btnStop.setAlpha(0f);
        loadingLAV.setAlpha(0f);
        btnStop.setEnabled(false);
        warningTV.setAlpha(0f);
        barcodeDialog.show();
        updateMultiple.clear();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        if (pref.getString("lastVisit","").equals( new SimpleDateFormat("dd", Locale.getDefault()).format(new Date())))
        {
            warningTV.setAlpha(1f);
        }

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                if (barcode.size() != 0) {
                    barcodeDetector.release();
                    loadingLAV.setAlpha(1f);
                    FirebaseDatabase.getInstance().getReference(myDataHolder.REGISTERED_GYMS_PATH).child(barcode.valueAt(0).displayValue.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //----------------------------------------------No Such Gym
                            if (dataSnapshot.getValue() == null) {
                                loadingLAV.setAlpha(0f);
                                gymName.setText("No Such GYM Found");
                                resultLAV.setAnimation(R.raw.reject);
                                resultLAV.playAnimation();
                                linearScanningLayout.setAlpha(0f);
                                linearResultLayout.setAlpha(1f);
                                otherInfo.setText("");
                                warningTV.setAlpha(0f);
                                vibrator.vibrate(100);
                                return;
                            } else {
                                //----------------------------------------------Days
                                FirebaseDatabase.getInstance().getReference("Gym/Gym Detailed Database/" + barcode.valueAt(0).displayValue + "/members/" + myDataHolder.userDataHolder.getMobile()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() == null || (long) dataSnapshot.getValue() < 1) {
                                            //----------------------------------------------Gym Charge
                                            FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_BASIC_DATABASE + barcode.valueAt(0).displayValue).child("charge").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    charge = (long) dataSnapshot.getValue();
                                                    //----------------------------------------------User Credits
                                                    FirebaseDatabase.getInstance().getReference(myDataHolder.USER_DATABASE_PATH + myDataHolder.userDataHolder.getMobile()).child("credit").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            //----------------------------------------------Insufficient Credits
                                                            if (dataSnapshot.getValue() == null || (long) dataSnapshot.getValue() < charge) {
                                                                otherInfo.setText(Html.fromHtml("<font color='red'>Insufficient Credits</font>"));
                                                                loadingLAV.setAlpha(0f);
                                                                gymName.setText(barcode.valueAt(0).displayValue.split("-")[3]+", "+barcode.valueAt(0).displayValue.split("-")[2]);
                                                                resultLAV.setAnimation(R.raw.reject);
                                                                resultLAV.playAnimation();
                                                                linearScanningLayout.setAlpha(0f);
                                                                linearResultLayout.setAlpha(1f);
                                                                warningTV.setAlpha(0f);
                                                                vibrator.vibrate(100);
                                                            } else {
                                                                //----------------------------------------------Deduct credits
                                                                updateMultiple.put(myDataHolder.USER_DATABASE_PATH + myDataHolder.userDataHolder.getMobile() + "/credit", (long) dataSnapshot.getValue() - charge);
                                                                otherInfo.setText(Html.fromHtml("<b>-" + charge + "</b> Credits<br><b>" + ((long) dataSnapshot.getValue() - charge) + "</b> Credits left"));

                                                                FirebaseDatabase.getInstance().getReference(myDataHolder.GYM_DETAILED_DATABASE + barcode.valueAt(0).displayValue + "/credits").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        resultLAV.setAnimation(R.raw.count);
                                                                        path = new SimpleDateFormat("/yyyy/MM/ddHHmmss", Locale.getDefault()).format(new Date());
                                                                        date = new SimpleDateFormat("dd,MMM,EEE", Locale.getDefault()).format(new Date());
                                                                        time = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());

                                                                        updateMultiple.put(myDataHolder.GYM_DETAILED_DATABASE + barcode.valueAt(0).displayValue + "/credits", (snapshot.getValue() == null) ? charge : (long) snapshot.getValue() + charge);
                                                                        updateMultiple.put("Gym/Gym Detailed Database/" + barcode.valueAt(0).displayValue + "/transactions/" + path + myDataHolder.userDataHolder.getMobile(), myDataHolder.userDataHolder.getMobile() + "#Visit#" + date + "#" + time + "#-" + charge);
                                                                        updateMultiple.put("User/User Transaction Database/" + myDataHolder.userDataHolder.getMobile() + "/" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()), barcode.valueAt(0).displayValue + "#Visit#" + date + "#" + time + "#-" + charge);
                                                                        updateMultiple.put(myDataHolder.USER_VISITATION_DATABASE + myDataHolder.userDataHolder.getMobile() + new SimpleDateFormat("/yyyy/MM/", Locale.getDefault()).format(new Date()) + new SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(new Date()), barcode.valueAt(0).displayValue + "#" + date.toUpperCase() + "#" + time + "#" + charge);
                                                                        updateMultiple.put("Gym/Gym Detailed Database/" + barcode.valueAt(0).displayValue + "/visitations/" + path + myDataHolder.userDataHolder.getMobile(), myDataHolder.userDataHolder.getMobile() + "#" + myDataHolder.userDataHolder.getName() + "#" + date + "#" + time + "#" + charge);

                                                                        handler.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                btnStop.setAlpha(0f);
                                                                                btnStop.setEnabled(false);
                                                                                resultLAV.setAnimation(R.raw.run);
                                                                                resultLAV.playAnimation();
                                                                                FirebaseDatabase.getInstance().getReference().updateChildren(updateMultiple);
                                                                                editor.putString("lastVisit",new SimpleDateFormat("dd", Locale.getDefault()).format(new Date())).commit();
                                                                                vibrator.vibrate(100);
                                                                            }
                                                                        }, 5000);

                                                                        btnStop.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                handler.removeCallbacksAndMessages(null);
                                                                                btnStop.setAlpha(0f);
                                                                                btnStop.setEnabled(false);
                                                                                otherInfo.setText(Html.fromHtml("<font color='red'><b>Canceled</b></font>"));
                                                                                resultLAV.setAnimation(R.raw.reject);
                                                                                resultLAV.playAnimation();
                                                                            }
                                                                        });

                                                                        btnStop.setAlpha(1f);
                                                                        btnStop.setEnabled(true);
                                                                        loadingLAV.setAlpha(0f);
                                                                        gymName.setText(barcode.valueAt(0).displayValue.split("-")[3]+", "+barcode.valueAt(0).displayValue.split("-")[2]);
                                                                        linearResultLayout.setAlpha(1f);
                                                                        warningTV.setAlpha(0f);
                                                                        linearScanningLayout.setAlpha(0f);
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
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } else {
                                            //----------------------------------------------Deduct Days
                                            path = new SimpleDateFormat("/yyyy/MM/ddHHmmss", Locale.getDefault()).format(new Date());
                                            date = new SimpleDateFormat("dd,MMM,EEE", Locale.getDefault()).format(new Date());
                                            time = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
                                            otherInfo.setText(Html.fromHtml("<b>" + ((long) dataSnapshot.getValue() - 1) + "</b> Days left"));
                                            updateMultiple.put("User/User Membership Database/" + myDataHolder.userDataHolder.getMobile() + "/" + barcode.valueAt(0).displayValue, (long) dataSnapshot.getValue() - 1);
                                            updateMultiple.put("Gym/Gym Detailed Database/" + barcode.valueAt(0).displayValue + "/members/" + myDataHolder.userDataHolder.getMobile(), (long) dataSnapshot.getValue() - 1);
                                            updateMultiple.put(myDataHolder.USER_VISITATION_DATABASE + myDataHolder.userDataHolder.getMobile() + new SimpleDateFormat("/yyyy/MM/", Locale.getDefault()).format(new Date()) + new SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(new Date()), barcode.valueAt(0).displayValue + "#" + date.toUpperCase() + "#" + time);
                                            updateMultiple.put("Gym/Gym Detailed Database/" + barcode.valueAt(0).displayValue + "/visitations/" + path + myDataHolder.userDataHolder.getMobile(), myDataHolder.userDataHolder.getMobile() + "#" + myDataHolder.userDataHolder.getName() + "#" + date + "#" + time);

                                            FirebaseDatabase.getInstance().getReference().updateChildren(updateMultiple);
                                            editor.putString("lastVisit",new SimpleDateFormat("dd", Locale.getDefault()).format(new Date())).commit();
                                            loadingLAV.setAlpha(0f);
                                            gymName.setText(barcode.valueAt(0).displayValue);
                                            resultLAV.setAnimation(R.raw.run);
                                            resultLAV.playAnimation();
                                            linearScanningLayout.setAlpha(0f);
                                            linearResultLayout.setAlpha(1f);
                                            warningTV.setAlpha(0f);
                                            vibrator.vibrate(100);
                                        }
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
                }
            }
        });
    }
}