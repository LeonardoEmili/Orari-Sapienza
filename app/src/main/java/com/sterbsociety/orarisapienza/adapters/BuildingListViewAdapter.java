package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.models.Building;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Custom ArrayAdapter which has been optimized thanks to this tutorial:
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern
 */
public class BuildingListViewAdapter extends ArrayAdapter<Building> {

    private List<Building> mDataList;
    private Drawable searchImg, historyImg;

    public BuildingListViewAdapter(Context context, List<Building> dataList) {
        super(context, R.layout.search_static_list_item, dataList);
        mDataList = dataList;
        searchImg = context.getResources().getDrawable(R.drawable.ic_search_cool_black_24dp);
        historyImg = context.getResources().getDrawable(R.drawable.ic_history);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Get the data for this view
        final Building building = mDataList.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_static_list_item, parent, false);
            viewHolder.buildingName = convertView.findViewById(R.id.building_name);
            viewHolder.buildingDetails = convertView.findViewById(R.id.building_details);
            viewHolder.imageView = convertView.findViewById(R.id.search_icon);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // this has to be changed to building code, since we check favourites with codes
        // we have to retrieve also the building code.
        if (AppUtils.isFavouriteBuilding(building.code)) {
            viewHolder.imageView.setImageDrawable(historyImg);
        } else {
            viewHolder.imageView.setImageDrawable(searchImg);
        }

        viewHolder.buildingName.setText(building.code);
        viewHolder.buildingDetails.setText(building.address);
        return convertView;
    }

    public void filterBuildingsByQuery(String query) {

        mDataList.clear();
        final String lowerCaseQuery = query.toLowerCase();

        if (lowerCaseQuery.trim().equals("")) {
            mDataList.addAll(AppUtils.getFavouriteBuildingList());
        } else {
            final List<Building> dataList = AppUtils.getBuildingList();
            for (Building building : dataList) {
                // here we could add filter by location (street)
                if (building.name.toLowerCase().contains(lowerCaseQuery)) {
                    mDataList.add(building);
                }
            }
        }
        notifyDataSetChanged();
    }

    // View lookup cache
    private static class ViewHolder {
        private TextView buildingName;
        private TextView buildingDetails;
        private ImageView imageView;
    }
}