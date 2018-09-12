package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.views.TimelineView;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.LineType;
import com.sterbsociety.orarisapienza.utils.VectorDrawableUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private List<TimeLineModel> mFeedList;
    private int currentClassroomIndex;
    private Drawable inactiveDrawable, activeDrawable, defaultDrawable;

    public TimeLineAdapter(Context context, List<TimeLineModel> feedList) {
        this(context, feedList, -1);
    }

    public TimeLineAdapter(Context context, List<TimeLineModel> feedList, int currentClassroomIndex) {
        mFeedList = feedList;
        this.currentClassroomIndex = currentClassroomIndex;
        inactiveDrawable = VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_inactive, android.R.color.darker_gray);
        activeDrawable = VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_active, R.color.colorPrimary);
        defaultDrawable = VectorDrawableUtils.getDrawable(context, R.drawable.marker_color_primary, R.color.colorPrimary);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount(), currentClassroomIndex);
    }

    @NonNull
    @Override
    public TimeLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case LineType.SIMPLE:
                return new ViewHolder(mLayoutInflater.inflate(R.layout.item_timeline_simple, parent, false), viewType);
            case LineType.SPECIAL:
                return new ViewHolder(mLayoutInflater.inflate(R.layout.item_timeline_special, parent, false), viewType);
            default:
                return new ViewHolder(mLayoutInflater.inflate(R.layout.item_timeline, parent, false), viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineAdapter.ViewHolder holder, int position) {

        final TimeLineModel timeLineModel = mFeedList.get(position);

        if (holder.getItemViewType() == LineType.SPECIAL) {
            // We want to display it as the current element.
            //holder.mMessage.setText("sterbini");
        } else {
            // Normal visualization
        }

        /*
        if (timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(inactiveDrawable);
        } else if (timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(activeDrawable);
        } else {
            holder.mTimelineView.setMarker(defaultDrawable);
        } */

        /*
        if (!timeLineModel.getDate().isEmpty()) {
            holder.mDate.setVisibility(View.VISIBLE);
            holder.mDate.setText(timeLineModel.getDate());
        } else
            holder.mDate.setVisibility(View.GONE);

        holder.mMessage.setText(timeLineModel.getMessage());
        */
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    static class ViewHolder extends BaseTimeLineViewHolder {

        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}