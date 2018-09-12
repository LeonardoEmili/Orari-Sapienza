package com.sterbsociety.orarisapienza.adapters;

import android.view.View;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

abstract class BaseTimeLineViewHolder extends RecyclerView.ViewHolder {

    TextView startHour, endHour, classroom;

    BaseTimeLineViewHolder(@NonNull View itemView) {
        super(itemView);
        startHour = itemView.findViewById(R.id.start_hour);
        endHour = itemView.findViewById(R.id.end_hour);
        classroom = itemView.findViewById(R.id.timeline_class);
    }
}