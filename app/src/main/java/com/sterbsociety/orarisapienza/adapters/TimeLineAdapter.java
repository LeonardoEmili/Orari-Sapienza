package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.ClassDetailActivity;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.sterbsociety.orarisapienza.views.TimelineView;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.LineType;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Static vs non-static ViewHolder in RecyclerView.Adapter discussion here:
 * https://stackoverflow.com/questions/31302341/what-difference-between-static-and-non-static-viewholder-in-recyclerview-adapter
 */
public class TimeLineAdapter extends RecyclerView.Adapter<BaseTimeLineViewHolder> {

    private static List<TimeLineModel> mFeedList;
    private static int currentClassroomIndex;

    public TimeLineAdapter(List<TimeLineModel> feedList) {
        this(feedList, -1);
    }

    public TimeLineAdapter(List<TimeLineModel> feedList, int currentClassroomIndex) {
        mFeedList = feedList;
        TimeLineAdapter.currentClassroomIndex = currentClassroomIndex;
        SimpleViewHolder.index = 1;
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
        holder.startHour.setText(AppUtils.getSimpleDate(timeLineModel.getStartDate()));
        holder.endHour.setText(AppUtils.getSimpleDate(timeLineModel.getEndDate()));

        if (holder instanceof SpecialViewHolder) {
            // We want to display it as the current element.
            ((SpecialViewHolder) holder).buildingAddress.setText(AppUtils.getRealBuilding(timeLineModel.getClassroom()).getLocation());
        } else if (holder instanceof SimpleViewHolder) {
            // StudyPlanActivity visualization.
            ((SimpleViewHolder) holder).timeLineIndex.setText(String.format(Locale.getDefault(), "%d", SimpleViewHolder.index++));
        }
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    class DefaultViewHolder extends BaseTimeLineViewHolder implements View.OnClickListener {

        DefaultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            currentClassroomIndex = getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    static class SpecialViewHolder extends BaseTimeLineViewHolder {

        TextView buildingAddress;

        SpecialViewHolder(@NonNull View itemView) {
            super(itemView);
            buildingAddress = itemView.findViewById(R.id.timeline_address);
            final ImageView infoButton = itemView.findViewById(R.id.info_building_btn);
            infoButton.setOnClickListener(view -> {
                final Context context = infoButton.getContext();
                Intent i = new Intent(context, ClassDetailActivity.class);
                i.putExtra(AppUtils.DEFAULT_KEY, mFeedList.get(currentClassroomIndex).getClassroom());
                context.startActivity(i);
            });
        }
    }

    static class SimpleViewHolder extends BaseTimeLineViewHolder {

        TextView timeLineIndex;
        static int index;

        SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            timeLineIndex = itemView.findViewById(R.id.timeline_index);
            final ImageView infoButton = itemView.findViewById(R.id.info_building_btn);
            infoButton.setOnClickListener(view -> {
                final Context context = infoButton.getContext();
                Intent i = new Intent(context, ClassDetailActivity.class);
                i.putExtra(AppUtils.DEFAULT_KEY, mFeedList.get(getAdapterPosition()).getClassroom());
                context.startActivity(i);
            });
        }
    }
}