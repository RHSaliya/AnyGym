package com.project.anygym;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.anygym.Adapters.TransactionsAdapter;
import com.project.anygym.DataHolder.myDataHolder;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class BuyCreditActivity extends AppCompatActivity implements PaymentStatusListener {
    private int credit = 0, amount = 0;
    private RecyclerView transactionRV;
    private List<String> transactionsList;
    private Map updateMultiple;
    private TransactionsAdapter transactionsAdapter;
    private EditText creditET;
    private TextView rupeeTV,noTransactionTV;
    private long currentCredits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_credit);
        transactionRV = findViewById(R.id.transactionRV);
        creditET = findViewById(R.id.creditET);
        rupeeTV = findViewById(R.id.rupeeTV);
        noTransactionTV = findViewById(R.id.noTransactionTV);
        Button btnAdd = findViewById(R.id.btnAdd);

        transactionsList = new ArrayList<>();
        transactionsAdapter = new TransactionsAdapter(BuyCreditActivity.this, transactionsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transactionRV.setLayoutManager(linearLayoutManager);
        transactionRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        transactionRV.setAdapter(transactionsAdapter);

        updateMultiple = new HashMap();
        creditET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rupeeTV.setText(Integer.parseInt("0" + creditET.getText().toString()) * 5 + " Rs.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credit = Integer.parseInt("0"+creditET.getText().toString());
                amount = credit * 5;

                if (credit == 0) {
                    Toast.makeText(BuyCreditActivity.this, "Enter Credits", Toast.LENGTH_SHORT).show();
                } else {
                    BuyCredit();
                }

            }
        });

        FirebaseDatabase.getInstance().getReference("User/User Transaction Database/" + myDataHolder.userDataHolder.getMobile()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    noTransactionTV.setVisibility(View.VISIBLE);
                    return;
                }
                noTransactionTV.setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    transactionsList.add(snapshot.getValue().toString());
                }
                transactionsAdapter.notifyDataSetChanged();
                transactionRV.getLayoutManager().scrollToPosition(transactionsList.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void BuyCredit() {

        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa("abc@upi")
                .setPayeeName("AnyGym")
                .setTransactionId(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + myDataHolder.userDataHolder.getMobile())
                .setTransactionRefId(myDataHolder.userDataHolder.getMobile() + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()))
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
        myDataHolder.userDBREF.child("credit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentCredits = (long) dataSnapshot.getValue();

                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                String time = new SimpleDateFormat("hh:mm:ss aa").format(new Date());

                updateMultiple.put(myDataHolder.USER_DATABASE_PATH+myDataHolder.userDataHolder.getMobile()+"/credit",currentCredits + credit);
                updateMultiple.put("User/User Transaction Database/" + myDataHolder.userDataHolder.getMobile() + "/" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()),"Purchase#" + date + "#" + time + "#" + amount + "#" +credit );
                updateMultiple.put("Transaction Database/" + myDataHolder.userDataHolder.getMobile() + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()), myDataHolder.userDataHolder.getName() +"#"+myDataHolder.userDataHolder.getMobile() +"#" + date + "#" + time + "#" + amount + "#" +credit );
                FirebaseDatabase.getInstance().getReference().updateChildren(updateMultiple);

                transactionsList.clear();
                FirebaseDatabase.getInstance().getReference("User/User Transaction Database/" + myDataHolder.userDataHolder.getMobile()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            return;
                        }
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            transactionsList.add(snapshot.getValue().toString());
                        }
                        transactionsAdapter.notifyDataSetChanged();
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
}