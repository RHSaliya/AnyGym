package com.project.anygymadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.anygymadmin.Adapter.CommentAdapter;
import com.project.anygymadmin.Adapter.GymRecyclerAdapter;
import com.project.anygymadmin.Adapter.MembershipAdapter;
import com.project.anygymadmin.Adapter.OwnerInfoGymRecyclerAdapter;
import com.project.anygymadmin.DataHolder.GymDataHolder;
import com.project.anygymadmin.DataHolder.myDataHolder;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class OwnerInfoActivity extends AppCompatActivity implements PaymentStatusListener {
    private ImageView ownerImageIV;
    private TextView ownerNameTV,ownerMobileTV,ownerInfoTV,ownerCreditsTV;

    private RecyclerView gymsRecycler,commentsRV;
    private OwnerInfoGymRecyclerAdapter ownerInfoGymRecyclerAdapter;
    private CommentAdapter commentAdapter;
    private LinearLayoutManager linearLayoutManager, commentLinearLayoutManager;
    private long credit, amount;
    private String id;
    private List<String> commentList;

    private Toolbar myToolbarOwnerInfo;
    private EditText commentsET;
    private LinearLayout commentsLL;
    private Animation slideUp, slideDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_info);
        ownerImageIV = findViewById(R.id.ownerImageIV);
        ownerNameTV = findViewById(R.id.ownerNameTV);
        ownerMobileTV = findViewById(R.id.ownerMobileTV);
        ownerInfoTV = findViewById(R.id.ownerInfoTV);
        ownerCreditsTV = findViewById(R.id.ownerCreditsTV);
        commentsRV = findViewById(R.id.commentsRV);
        commentsET = findViewById(R.id.commentsET);
        commentsLL = findViewById(R.id.commentsLL);

        slideUp = AnimationUtils.loadAnimation(this, R.anim.slideup);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slidedown);

        myToolbarOwnerInfo = findViewById(R.id.myToolbarOwnerInfo);
        setSupportActionBar(myToolbarOwnerInfo);
        myToolbarOwnerInfo.setTitle("Owner Info");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseStorage.getInstance().getReference(myDataHolder.OWNER_PROFILE_IMAGE_PATH + myDataHolder.ownerDataHolder.getMobile()+".png")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).placeholder(R.drawable.ic_male_user_placeholder).into(ownerImageIV);
            }
        });

        ownerNameTV.setText(myDataHolder.ownerDataHolder.getName());
        ownerMobileTV.setText(myDataHolder.ownerDataHolder.getMobile());
        ownerNameTV.setText(myDataHolder.ownerDataHolder.getName());
        ownerInfoTV.setText("Age : "+myDataHolder.ownerDataHolder.getAge()+", Gender : "+myDataHolder.ownerDataHolder.getGender());
        ownerCreditsTV.setText("Balance : "+myDataHolder.ownerDataHolder.getCredit()+" Credits");

        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()).child("credit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ownerCreditsTV.setText("Balance : "+dataSnapshot.getValue().toString()+" Credits");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        commentList = new ArrayList<>();
        gymsRecycler = findViewById(R.id.gymsRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentsRV.setLayoutManager(commentLinearLayoutManager);
        gymsRecycler.setLayoutManager(linearLayoutManager);
        commentAdapter = new CommentAdapter(OwnerInfoActivity.this,commentList);
        commentsRV.setAdapter(commentAdapter);
        ownerInfoGymRecyclerAdapter = new OwnerInfoGymRecyclerAdapter(OwnerInfoActivity.this, new ArrayList<>(myDataHolder.ownerDataHolder.getOwnerGyms().keySet()),new ArrayList<>(myDataHolder.ownerDataHolder.getOwnerGyms().values()));
        gymsRecycler.setAdapter(ownerInfoGymRecyclerAdapter);

        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    commentList.add(snapshot.getValue().toString());
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        wordCount();
    }

    private void wordCount() {
        final TextView commentCharCount = findViewById(R.id.commentCharCount);

        commentsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                commentCharCount.setText("" + commentsET.length() + "/2000");
                if (commentsET.getLineCount() > 7) {
                    commentsET.setLines(commentsET.getLineCount());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void onPayClick(View view) {
        FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile()).child("credit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                credit = (long)dataSnapshot.getValue();
                amount = credit*5;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        id = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(myDataHolder.ownerDataHolder.getUpiID())
                .setPayeeName(myDataHolder.ownerDataHolder.getName())
                .setTransactionId(id + myDataHolder.ownerDataHolder.getMobile())
                .setTransactionRefId(myDataHolder.ownerDataHolder.getMobile()+id)
                .setDescription("For " + credit + " Credits")
                .setAmount(amount+".0")
                .build();
        easyUpiPayment.startPayment();
        easyUpiPayment.setPaymentStatusListener(this);
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        Toast.makeText(this, transactionDetails.getStatus(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionSuccess() {
        FirebaseDatabase.getInstance()
                .getReference(myDataHolder.OWNER_DATABASE_PATH+myDataHolder.ownerDataHolder.getMobile())
                .child("credit")
                .setValue(0);
        FirebaseDatabase.getInstance()
                .getReference("Payments/"+myDataHolder.ownerDataHolder.getMobile())
                .child(id + myDataHolder.ownerDataHolder.getMobile())
                .setValue(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date()) +"#"+ myDataHolder.userDataHolder.getMobile() +"#"+ credit +"#"+ amount);
        }

    @Override
    public void onTransactionSubmitted() {

    }

    @Override
    public void onTransactionFailed() {

    }

    @Override
    public void onTransactionCancelled() {

    }

    @Override
    public void onAppNotFound() {
        Toasty.warning(getApplicationContext(), "No UPI app found", Toasty.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onComment(View view) {
        if (commentsET.getText().length()>0){
            FirebaseDatabase.getInstance().getReference(myDataHolder.OWNER_DATABASE_PATH + myDataHolder.ownerDataHolder.getMobile()).child("comments").child(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date())).setValue(new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy", Locale.getDefault()).format(new Date())+"#"+commentsET.getText().toString());
            Toasty.success(getApplicationContext(),"Success...",Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (commentsLL.getVisibility()==View.VISIBLE){
            commentsLL.startAnimation(slideDown);
            commentsLL.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void onCommentShow(View view) {
        commentsLL.setVisibility(View.VISIBLE);
        commentsLL.startAnimation(slideUp);
    }
}