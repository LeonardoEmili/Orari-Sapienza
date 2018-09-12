package com.sterbsociety.orarisapienza.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.views.TimelineView;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.LineType;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeLineAdapter extends RecyclerView.Adapter<BaseTimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private int currentClassroomIndex;

    public TimeLineAdapter(List<TimeLineModel> feedList) {
        this(feedList, -1);
    }

    public TimeLineAdapter(List<TimeLineModel> feedList, int currentClassroomIndex) {
        mFeedList = feedList;
        this.currentClassroomIndex = currentClassroomIndex;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount(), currentClassroomIndex);
    }

    @NonNull
    @Override
    public BaseTimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case LineType.SIMPLE:
                return new SimpleViewHolder(mLayoutInflater.inflate(R.layout.item_timeline_simple, parent, false));
            case LineType.SPECIAL:
                return new SpecialViewHolder(mLayoutInflater.inflate(R.layout.item_timeline_special, parent, false));
            default:
                return new DefaultViewHolder(mLayoutInflater.inflate(R.layout.item_timeline, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseTimeLineViewHolder holder, int position) {

        final TimeLineModel timeLineModel = mFeedList.get(position);

        holder.classroom.setText(timeLineModel.getClassroom().getName());
        holder.startHour.setText(timeLineModel.getStartDate());
        holder.endHour.setText(timeLineModel.getEndDate());

        if (holder instanceof SpecialViewHolder) {
            // We want to display it as the current element.
            ((SpecialViewHolder)holder).buildingAddress.setText(timeLineModel.getClassroom().getMainBuildingAddress());
        } else if (holder instanceof SimpleViewHolder) {
            // Normal visualization
            ((SimpleViewHolder)holder).timeLineIndex.setText(String.format(Locale.getDefault(), "%d", SimpleViewHolder.index++));
        }
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    static class DefaultViewHolder extends BaseTimeLineViewHolder {

        DefaultViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class SpecialViewHolder extends BaseTimeLineViewHolder {

        TextView buildingAddress;

        SpecialViewHolder(@NonNull View itemView) {
            super(itemView);
            buildingAddress = itemView.findViewById(R.id.timeline_address);
        }
    }

    static class SimpleViewHolder extends BaseTimeLineViewHolder {

        TextView timeLineIndex;
        static int index = 1;

        SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            timeLineIndex = itemView.findViewById(R.id.timeline_index);
        }
    }
}