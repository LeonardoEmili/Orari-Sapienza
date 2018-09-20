package com.sterbsociety.orarisapienza.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.MapsActivity;
import com.sterbsociety.orarisapienza.adapters.BuildingListViewAdapter;
import com.sterbsociety.orarisapienza.models.Building;

import androidx.cardview.widget.CardView;
import xyz.sahildave.widget.SearchViewLayout;

import static com.sterbsociety.orarisapienza.utils.AppUtils.addBuildingToFavourites;

/**
 * You can read about the weird behaviour of ListView with items with focusable or
 * clickable that does not call the method onclick here:
 * https://stackoverflow.com/questions/5551042/onitemclicklistener-not-working-in-listview
 */
public class SearchStaticListSupportFragment extends Fragment {

    private MapsActivity mapsActivity;

    public SearchStaticListSupportFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_search_static_list, container, false);

        // Just references initialization here
        mapsActivity = ((MapsActivity) rootView.getContext());
        final ListView listView = rootView.findViewById(R.id.search_static_list);
        final SearchViewLayout searchViewLayout = mapsActivity.searchViewLayout;
        final BuildingListViewAdapter mAdapter = mapsActivity.mAdapter;

        // Use current GPS position button here
        CardView gpsButton = rootView.findViewById(R.id.use_gps_button);
        gpsButton.setOnClickListener(view -> {
            mapsActivity.useGPSPosition();
            searchViewLayout.setCollapsedHint(getString(R.string.current_postion));
            mapsActivity.clearButton.setVisibility(View.VISIBLE);
            searchViewLayout.collapse();
        });

        // We set the adapter for our listView
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            final Building building = mAdapter.getItem(position);
            if (building != null) {
                mapsActivity.useBuildingPosition(building);
                searchViewLayout.setCollapsedHint(building.getName());
                mapsActivity.clearButton.setVisibility(View.VISIBLE);
                addBuildingToFavourites(mapsActivity, building, position);
            }
            searchViewLayout.collapse();
        });

        return rootView;
    }
}