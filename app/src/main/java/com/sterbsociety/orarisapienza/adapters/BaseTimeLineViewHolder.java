package com.sterbsociety.orarisapienza.adapters;

import android.view.View;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.sterbsociety.orarisapienza.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

abstract class BaseTimeLineViewHolder extends RecyclerView.ViewHolder {

    //TimelineView mTimelineView;
    TextView mDate, mMessage;

    BaseTimeLineViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        //mTimelineView = itemView.findViewById(R.id.time_marker);
        mDate = itemView.findViewById(R.id.text_timeline_date);
        mMessage = itemView.findViewById(R.id.text_timeline_title);
        //mTimelineView.initLine(viewType);
    }
}