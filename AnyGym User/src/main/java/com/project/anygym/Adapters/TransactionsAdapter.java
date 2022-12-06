package com.project.anygym.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.anygym.R;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    private Context context;
    private List<String> transactionsList;

    public TransactionsAdapter(Context context, List<String> transactionsList) {
        this.context = context;
        this.transactionsList = transactionsList;
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_transactions_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.ViewHolder holder, int position) {
        if (transactionsList.get(position).contains("Visit"))
        {
            holder.titleTransactionTV.setText(transactionsList.get(position).split("#")[1]+" : "+transactionsList.get(position).split("#")[0].split("-")[3]+", "+transactionsList.get(position).split("#")[0].split("-")[2]);
            holder.dateTimeTransactionTV.setText("Date : " + transactionsList.get(position).split("#")[2] + "    Time : " + transactionsList.get(position).split("#")[3]);
            holder.chargeTransactionTV.setText("Charge : " + transactionsList.get(position).split("#")[4].substring(1) + " Credits");
            holder.totalTransactionTV.setText(Html.fromHtml("<b><font color='red'>" + transactionsList.get(position).split("#")[4] + " Credits</font></b>"));
        }
        else if (transactionsList.get(position).split("#").length == 5)
        {
            holder.titleTransactionTV.setText(transactionsList.get(position).split("#")[0].split("-")[3]+", "+transactionsList.get(position).split("#")[0].split("-")[2]);
            holder.dateTimeTransactionTV.setText("Date : " + transactionsList.get(position).split("#")[1] + "    Time : " + transactionsList.get(position).split("#")[2]);
            holder.chargeTransactionTV.setText("Charge : " + transactionsList.get(position).split("#")[3] + " Rs.");
            holder.totalTransactionTV.setText(Html.fromHtml("<b><font color='#008000'>" + transactionsList.get(position).split("#")[4] + " Credits</font></b>"));
        }
        else {
            holder.titleTransactionTV.setText(transactionsList.get(position).split("#")[1]+" : "+transactionsList.get(position).split("#")[0].split("-")[3]+", "+transactionsList.get(position).split("#")[0].split("-")[2]);
            holder.dateTimeTransactionTV.setText("Date : " + transactionsList.get(position).split("#")[2] + "    Time : " + transactionsList.get(position).split("#")[3]);
            holder.chargeTransactionTV.setText("Charge : " + transactionsList.get(position).split("#")[4] + " Credits");
            holder.totalTransactionTV.setText(Html.fromHtml("<b><font color='red'>" + transactionsList.get(position).split("#")[5] + " Credits</font><br><font color='#008000'>" + transactionsList.get(position).split("#")[6] + " Days</font></b>"));
        }
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTransactionTV,dateTimeTransactionTV,chargeTransactionTV,totalTransactionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTransactionTV = itemView.findViewById(R.id.titleTransactionTV);
            dateTimeTransactionTV = itemView.findViewById(R.id.dateTimeTransactionTV);
            chargeTransactionTV = itemView.findViewById(R.id.chargeTransactionTV);
            totalTransactionTV = itemView.findViewById(R.id.totalTransactionTV);
        }
    }
}