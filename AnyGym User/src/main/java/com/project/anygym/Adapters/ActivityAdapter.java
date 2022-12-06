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

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    private Context context;
    private List<String> activityList;

    public ActivityAdapter(Context context, List<String> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_activity_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, int position) {
        if (activityList.get(position).split("#").length == 3)
        {
            holder.titleActivityTV.setText(activityList.get(position).split("#")[0].split("-")[3]+", "+activityList.get(position).split("#")[0].split("-")[2]);
            holder.dateActivityTV.setText(activityList.get(position).split("#")[1].split(",")[0]);
            holder.monthActivityTV.setText(activityList.get(position).split("#")[1].split(",")[1]);
            holder.dayActivityTV.setText(activityList.get(position).split("#")[1].split(",")[2]);
            holder.timeActivityTV.setText(activityList.get(position).split("#")[2].replaceAll("^0", ""));
            holder.creditsActivityTV.setVisibility(View.GONE);
        }else {
            holder.titleActivityTV.setText(activityList.get(position).split("#")[0].split("-")[3]+", "+activityList.get(position).split("#")[0].split("-")[2]);
            holder.dateActivityTV.setText(activityList.get(position).split("#")[1].split(",")[0]);
            holder.monthActivityTV.setText(activityList.get(position).split("#")[1].split(",")[1]);
            holder.dayActivityTV.setText(activityList.get(position).split("#")[1].split(",")[2]);
            holder.timeActivityTV.setText(activityList.get(position).split("#")[2].replaceAll("^0", ""));
            holder.creditsActivityTV.setText("Charge : "+activityList.get(position).split("#")[3]+" Credits");
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleActivityTV, creditsActivityTV, monthActivityTV, dateActivityTV, dayActivityTV, timeActivityTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleActivityTV = itemView.findViewById(R.id.titleActivityTV);
            creditsActivityTV = itemView.findViewById(R.id.creditsActivityTV);
            monthActivityTV = itemView.findViewById(R.id.monthActivityTV);
            dateActivityTV = itemView.findViewById(R.id.dateActivityTV);
            dayActivityTV = itemView.findViewById(R.id.dayActivityTV);
            timeActivityTV = itemView.findViewById(R.id.timeActivityTV);
        }
    }
}