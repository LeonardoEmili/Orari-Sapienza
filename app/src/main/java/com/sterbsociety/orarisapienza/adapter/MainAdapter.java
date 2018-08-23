package com.sterbsociety.orarisapienza.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.model.Classroom;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends BaseAdapter<MainAdapter.ViewHolder> {

    private List<Classroom> mDataList;
    private static Set<String> mClassFavourites;
    private static Drawable starImg;


    public MainAdapter(Context context) {
        super(context);
        mClassFavourites = AppUtils.getFavouriteClassSet();
        starImg = context.getResources().getDrawable(R.drawable.ic_starred);
    }

    public void notifyDataSetChanged(List<Classroom> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    public void filterClassroomsByQuery(String query) {

        mDataList.clear();
        final String lowerCaseQuery = query.toLowerCase();
        final List<Classroom> dataList = AppUtils.getDataList();

        for (Classroom model : dataList) {
            final String className = model.getName().toLowerCase();
            final String buildingName = model.getMainBuilding().toLowerCase();
            final String classId = model.getCode();
            if (className.contains(lowerCaseQuery) || buildingName.contains(lowerCaseQuery) || classId.contains(lowerCaseQuery)) {
                mDataList.add(model);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_menu_main, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDataList.get(position), position);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        GradientDrawable background;
        Context mContext;

        ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            tvTitle = itemView.findViewById(R.id.tv_title);
            background = (GradientDrawable) (itemView.findViewById(R.id.circle_status)).getBackground();
        }

        void setData(Classroom classroom, int position) {
            
            if (mClassFavourites.contains(classroom.getCode())) {
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, starImg, null);
            } else {
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            if ((position%2)==0)
                background.setColor(ContextCompat.getColor(mContext, R.color.red_normal));
            else
                background.setColor(ContextCompat.getColor(mContext, R.color.green_normal));
            this.tvTitle.setText(classroom.getName());
        }
    }
}