package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.models.Building;
import com.sterbsociety.orarisapienza.models.Classroom;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.sterbsociety.orarisapienza.utils.AppUtils.CACHED_MAX_HOUR;
import static com.sterbsociety.orarisapienza.utils.AppUtils.CACHED_MIN_HOUR;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getCurrentWeekDayIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getRealBuilding;
import static com.sterbsociety.orarisapienza.utils.AppUtils.haversine;
import static com.sterbsociety.orarisapienza.utils.AppUtils.hourToString;
import static com.sterbsociety.orarisapienza.utils.AppUtils.timeToInt;

public class ClassListAdapter extends BaseClassListAdapter<ClassListAdapter.ViewHolder> {

    private ArrayList<Classroom> mDataList, backupList;
    private static Set<String> mClassFavourites;
    private static Drawable starImg;
    private int redColor, greenColor, blackColor;
    private int currentTimeIndex, currentDay;
    private Date now;

    public ClassListAdapter(Context context) {
        super(context);
        mClassFavourites = AppUtils.getFavouriteClassSet();
        starImg = context.getResources().getDrawable(R.drawable.ic_starred);
        redColor = context.getResources().getColor(R.color.red_normal);
        greenColor = context.getResources().getColor(R.color.green_normal);
        blackColor = context.getResources().getColor(R.color.coolBlack);
        mDataList = new ArrayList<>();
        currentTimeIndex = AppUtils.getCurrentTimeToInt();
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        now = new Date();
    }

    public void notifyDataSetChanged(List<Classroom> dataList) {
        this.mDataList = new ArrayList<>(dataList);
        super.notifyDataSetChanged();
    }

    private ArrayList<Classroom> filterClassroomListByQuery(ArrayList<Classroom> list, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final Iterator<Classroom> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Classroom classroom = iterator.next();
            final String buildingName = getRealBuilding(classroom).name.toLowerCase();
            if (!classroom.getName().toLowerCase().contains(lowerCaseQuery)
                    && !buildingName.contains(lowerCaseQuery) && classroom.getCode().contains(lowerCaseQuery)) {
                iterator.remove();
            }
        }
        return list;
    }

    public void filterClassroomListOnlyByQuery(String query) {
        mDataList.clear();
        final String lowerCaseQuery = query.toLowerCase();
        if (backupList == null) {
            // This happens only the first time, if the user hasn't completed a full research,
            // then it can only filter the whole list (which shows by default only free classroom in the current day)
            backupList = new ArrayList<>(AppUtils.getRealClassroomList());
            searchForAvailableClassrooms(backupList, getCurrentWeekDayIndex());
        }
        for (Classroom model : backupList) {
            final Building building = getRealBuilding(model);
            if (model.getName().toLowerCase().contains(lowerCaseQuery) || building.code.toLowerCase().contains(lowerCaseQuery)
                    || building.code.toLowerCase().contains(lowerCaseQuery)) {
                mDataList.add(model);
            }
        }
        notifyDataSetChanged();
    }

    public void filterClassroomList() {
        final boolean[] tmpArray = new boolean[6];
        tmpArray[getCurrentWeekDayIndex()] = true;
        filterClassroomList(tmpArray, 0, "", -1, null);
    }

    public void filterClassroomList(boolean[] dayIndexArray, int onlyAvailable, String query, int distanceRadius, @Nullable Location location) {
        mDataList.clear();
        for (int dayIndex = 0; dayIndex < dayIndexArray.length; dayIndex++) {
            if (dayIndexArray[dayIndex]) {
                // Foreach day request by the user
                ArrayList<Classroom> tmpList = new ArrayList<>(AppUtils.getRealClassroomList());

                if (onlyAvailable == 0) {
                    searchForAvailableClassrooms(tmpList, dayIndex);
                } else if (onlyAvailable == 1) {
                    searchForOccupiedClassrooms(tmpList, dayIndex);
                }

                tmpList = filterClassroomListByQuery(tmpList, query);

                if (distanceRadius != -1 && location != null) {
                    tmpList = filterClassroomListByDistance(tmpList, location, distanceRadius);
                }
                mDataList.addAll(tmpList);
            }
        }
        backupList = new ArrayList<>(mDataList);
        notifyDataSetChanged();
    }

    private void searchForAvailableClassrooms(List<Classroom> list, int dayIndex) {
        final Iterator<Classroom> iterator = list.iterator();
        final int minIndex = timeToInt(hourToString(CACHED_MIN_HOUR), dayIndex);
        final int maxIndex = timeToInt(hourToString(CACHED_MAX_HOUR), dayIndex);
        while (iterator.hasNext()) {
            int scrollIndex = minIndex;
            final Classroom classroom = iterator.next();
            final List<Integer> lessonList = AppUtils.MATRIX.get(classroom.getFullCode());
            if (scrollIndex > maxIndex || lessonList.get(scrollIndex) != 0) {
                iterator.remove();
            } else {
                boolean toBeRemoved = false;
                while (scrollIndex <= maxIndex) {
                    if (lessonList.get(scrollIndex) != 0) {
                        toBeRemoved = true;
                        break;
                    }
                    scrollIndex++;
                }
                if (toBeRemoved) {
                    iterator.remove();
                }
            }
        }
    }

    private void searchForOccupiedClassrooms(List<Classroom> list, int dayIndex) {
        final Iterator<Classroom> iterator = list.iterator();
        final int minIndex = timeToInt(hourToString(CACHED_MIN_HOUR), dayIndex);
        final int maxIndex = timeToInt(hourToString(CACHED_MAX_HOUR), dayIndex);
        while (iterator.hasNext()) {
            int scrollIndex = minIndex;
            final Classroom classroom = iterator.next();
            final List<Integer> lessonList = AppUtils.MATRIX.get(classroom.getFullCode());
            if (scrollIndex > maxIndex || lessonList.get(scrollIndex) == 0) {
                iterator.remove();
            } else {
                boolean toBeRemoved = false;
                while (scrollIndex <= maxIndex) {
                    if (lessonList.get(scrollIndex) == 0) {
                        toBeRemoved = true;
                        break;
                    }
                    scrollIndex++;
                }
                if (toBeRemoved) {
                    iterator.remove();
                }
            }
        }
    }

    private ArrayList<Classroom> filterClassroomListByDistance(ArrayList<Classroom> list, @NonNull Location currentLocation, int distanceRadius) {
        if (distanceRadius == 21) {
            return list;
        }
        final double currentLatitude = currentLocation.getLatitude();
        final double currentLongitude = currentLocation.getLongitude();
        final int realDistanceInMeters = distanceRadius * 100;
        final Iterator<Classroom> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Classroom classroom = iterator.next();
            final Building building = getRealBuilding(classroom);
            if (haversine(currentLatitude, currentLongitude, building.getLat(), building.getLong()) > realDistanceInMeters) {
                iterator.remove();
            }
        }
        return list;
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
        final Date minHour = AppUtils.getMinHour();
        final Date maxHour = AppUtils.getMaxHour();
        if (now.before(minHour) || now.after(maxHour) || currentDay   == Calendar.SUNDAY) {
            holder.background.setColor(blackColor);
        } else if (AppUtils.MATRIX.get(classroom.getFullCode()).get(currentTimeIndex) == 0) {
            // Classroom is now empty
            holder.background.setColor(greenColor);
        } else {
            // Classroom is now busy
            holder.background.setColor(redColor);
        }
        holder.classroom.setText(classroom.getName());
        holder.building.setText(getRealBuilding(classroom).name);
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