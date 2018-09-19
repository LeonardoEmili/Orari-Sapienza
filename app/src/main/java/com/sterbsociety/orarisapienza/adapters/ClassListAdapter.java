package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.models.Classroom;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClassListAdapter extends BaseClassListAdapter<ClassListAdapter.ViewHolder> {

    private List<Classroom> mDataList;
    private static Set<String> mClassFavourites;
    private static Drawable starImg;
    private int redColor, greenColor;

    public ClassListAdapter(Context context) {
        super(context);
        mClassFavourites = AppUtils.getFavouriteClassSet();
        starImg = context.getResources().getDrawable(R.drawable.ic_starred);
        redColor = context.getResources().getColor(R.color.red_normal);
        greenColor = context.getResources().getColor(R.color.green_normal);
    }

    public void notifyDataSetChanged(List<Classroom> dataList) {
        this.mDataList = new ArrayList<>(dataList);
        super.notifyDataSetChanged();
    }

    public void filterClassroomsByQuery(String query) {

        mDataList.clear();
        final String lowerCaseQuery = query.toLowerCase();
        final List<Classroom> dataList = AppUtils.getClassroomList();

        for (Classroom model : dataList) {
            final String className = model.getName().toLowerCase();
            final String buildingName = AppUtils.getRealBuilding(model).getName().toLowerCase();
            final String classId = model.getCode();
            if (className.contains(lowerCaseQuery) || buildingName.contains(lowerCaseQuery) || classId.contains(lowerCaseQuery)) {
                mDataList.add(model);
            }
        }
        notifyDataSetChanged();
    }

    public void applyOtherFilters() {
        final boolean[] dayIndexArray = AppUtils.getSelectedDayBtnIndex();
        final int startHour = AppUtils.getMinHour();
        final int endHour = AppUtils.getMaxHour();
        Iterator<Classroom> iterator = mDataList.iterator();
        while (iterator.hasNext()) {
            Classroom currentClassroom = iterator.next();
            if (!isClassroomAvailableForCertainRangeOfTime(dayIndexArray, startHour, endHour, currentClassroom)) {
                iterator.remove();
            }
        }
    }

    /**
     * @param indexArray is the array of days formed by the user request, each day requested is true
     * @param startHour  is the start hour from when the user wants to check classrooms
     * @param endHour    is the end hour from when the user wants to check classrooms
     * @return result of user's request, true if this classroom is available, else return false
     */
    private boolean isClassroomAvailableForCertainRangeOfTime(boolean[] indexArray, int startHour, int endHour, Classroom classroom) {
        for (int dayIndex = 0; dayIndex < indexArray.length; dayIndex++) {
            if (indexArray[dayIndex]) {
                // Inside here we have to check for the daily availability, startHour and endHour are simple ints (eg. 14:20 -> 14)
                int startHourIndex = dayIndex + startHour * 5;      // 5 is random
                final int endHourIndex = dayIndex + endHour * 5;    // 5 is random
                // Down here we get the array of ints related to the classroom we are checking for
                int[] classroomRow = new int[42];
                while (startHourIndex <= endHourIndex) {
                    if (classroomRow[startHourIndex] != -1) {
                        // This means that classroom is now occupied
                        return false;
                    }
                    // It goes for 5 minutes ahead
                    startHourIndex++;
                }
            }
        }
        return true;
    }

    /**
     * @param indexArray is the array of days formed by the user request, each day requested is true
     * @param startHour  is the start hour from when the user wants to check classrooms
     * @param endHour    is the end hour from when the user wants to check classrooms
     * @return result of user's request, true if this classroom is available, else return false
     */
    private boolean isClassroomOccupiedForCertainRangeOfTime(boolean[] indexArray, int startHour, int endHour, Classroom classroom) {
        for (int dayIndex = 0; dayIndex < indexArray.length; dayIndex++) {
            if (indexArray[dayIndex]) {
                // Inside here we have to check for the daily availability, startHour and endHour are simple ints (eg. 14:20 -> 14)
                int startHourIndex = dayIndex + startHour * 5;      // 5 is random
                final int endHourIndex = dayIndex + endHour * 5;    // 5 is random
                // Down here we get the array of ints related to the classroom we are checking for
                int[] classroomRow = new int[42];
                while (startHourIndex <= endHourIndex) {
                    if (classroomRow[startHourIndex] == -1) {
                        // This means that classroom is now available
                        return false;
                    }
                    // It goes for 5 minutes ahead
                    startHourIndex++;
                }
            }
        }
        return true;
    }

    public Classroom getClassroom(int index) {
        return mDataList.get(index);
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

        final Classroom classroom = mDataList.get(position);

        if (mClassFavourites.contains(classroom.getBuildingCode() + "-" + classroom.getCode())) {
            holder.classroom.setCompoundDrawablesWithIntrinsicBounds(null, null, starImg, null);
        } else {
            holder.classroom.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        if ((position % 2) == 0)
            holder.background.setColor(redColor);
        else
            holder.background.setColor(greenColor);
        holder.classroom.setText(classroom.getName());
        holder.building.setText(AppUtils.getRealBuilding(classroom).getName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView classroom, building;
        GradientDrawable background;

        ViewHolder(View itemView) {
            super(itemView);
            classroom = itemView.findViewById(R.id.tv_classroom);
            building = itemView.findViewById(R.id.tv_building);
            background = (GradientDrawable) (itemView.findViewById(R.id.circle_status)).getBackground();
        }
    }
}