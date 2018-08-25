package com.sterbsociety.orarisapienza.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.List;

import androidx.annotation.NonNull;

import static com.sterbsociety.orarisapienza.utils.AppUtils.hasBeenAlreadySearchedByUser;

/**
 * This adapter uses the ViewHolder pattern, followed this guide:
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern
 */
public class ListViewAdapter extends ArrayAdapter<String> {

    private List<String> mCourses;
    private Drawable searchImg, historyImg;

    public ListViewAdapter(Context context, List<String> courses) {
        super(context, R.layout.search_static_list_item, courses);
        mCourses = courses;
        searchImg = context.getResources().getDrawable(R.drawable.ic_action_search);
        historyImg = context.getResources().getDrawable(R.drawable.ic_history);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        String course = mCourses.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_static_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.

        viewHolder.title.setText(course);
        if (hasBeenAlreadySearchedByUser(course)) {
            viewHolder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(historyImg, null, null, null);
        } else {
            viewHolder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(searchImg, null, null, null);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void filterCoursesByQuery(String query) {
        mCourses.clear();

        if (query.trim().equals("")) {
            // If the SearchView is empty then only elements from history are shown
            mCourses.addAll(AppUtils.getFavouriteCourses());
        } else {

            final String lowerCaseQuery = query.toLowerCase();
            final List<String> dataList = AppUtils.getCoursesList();

            for (String course : dataList) {
                final String courseName = course.toLowerCase();
                if (courseName.contains(lowerCaseQuery)) {
                    mCourses.add(course);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {

        TextView title;

        ViewHolder(View itemView) {
            title = itemView.findViewById(R.id.card_title);
        }
    }
}
