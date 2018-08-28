package com.sterbsociety.orarisapienza.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchViewAdapter extends BaseAdapter implements Filterable {

    private ArrayList<String> mData;
    private String[] mSuggestions;
    private LayoutInflater inflater;
    private Drawable searchImg, historyImg;

    public SearchViewAdapter(Context context, List<String> suggestions) {
        inflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        mSuggestions = suggestions.toArray(new String[0]);
        searchImg = context.getResources().getDrawable(R.drawable.ic_action_search);
        historyImg = context.getResources().getDrawable(R.drawable.ic_history);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence query) {

                FilterResults filterResults = new FilterResults();

                // Retrieve the autocomplete results.
                List<String> searchData;

                if (!TextUtils.isEmpty(query)) {

                    searchData = new ArrayList<>();

                    final String lowerCaseQuery = query.toString().toLowerCase();

                    for (String string : mSuggestions) {
                        if (string.toLowerCase().contains(lowerCaseQuery)) {
                            searchData.add(string);
                        }
                    }
                } else {

                    searchData = AppUtils.getFavouriteCourses();
                }
                // Assign the mData to the FilterResults
                filterResults.values = searchData;
                filterResults.count = searchData.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    mData = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * This adapter uses the ViewHolder pattern, followed this guide:
     * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#improving-performance-with-the-viewholder-pattern
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SuggestionsViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_suggestion_item, parent, false);
            viewHolder = new SuggestionsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SuggestionsViewHolder) convertView.getTag();
        }

        final String currentListData = (String) getItem(position);

        viewHolder.textView.setText(currentListData);

        if (AppUtils.hasBeenAlreadySearchedByUser(currentListData)) {
            viewHolder.imageView.setImageDrawable(historyImg);
        } else {
            viewHolder.imageView.setImageDrawable(searchImg);
        }

        return convertView;
    }

    private class SuggestionsViewHolder {

        TextView textView;
        ImageView imageView;

        SuggestionsViewHolder(View convertView) {
            textView = convertView.findViewById(R.id.suggestion_text);
            imageView = convertView.findViewById(R.id.suggestion_icon);
        }
    }
}