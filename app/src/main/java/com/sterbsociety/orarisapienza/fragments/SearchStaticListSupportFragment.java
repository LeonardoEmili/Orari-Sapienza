package com.sterbsociety.orarisapienza.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.MapsActivity;
import com.sterbsociety.orarisapienza.model.Building;

import androidx.cardview.widget.CardView;
import xyz.sahildave.widget.SearchViewLayout;

import static com.sterbsociety.orarisapienza.utils.AppUtils.addBuildingToFavourites;

/**
 * You can read about the weird behaviour of ListView with items with focusable or
 * clickable that does not call the method onclick here:
 * https://stackoverflow.com/questions/5551042/onitemclicklistener-not-working-in-listview
 */
public class SearchStaticListSupportFragment extends Fragment {

    public SearchStaticListSupportFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_search_static_list, container, false);
        final MapsActivity mapsActivity = ((MapsActivity)rootView.getContext());

        final ListView listView = rootView.findViewById(R.id.search_static_list);
        final SearchViewLayout searchViewLayout = mapsActivity.searchViewLayout;

        CardView gpsButton = rootView.findViewById(R.id.use_gps_button);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = rootView.findViewById(R.id._gps);
                searchViewLayout.setCollapsedHint(textView.getText().toString());
                searchViewLayout.collapse();
            }
        });

        listView.setAdapter(mapsActivity.listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Building building = mapsActivity.listViewAdapter.getItem(position);
                addBuildingToFavourites(mapsActivity, building, position);
                searchViewLayout.collapse();
            }
        });

        return rootView;
    }
}