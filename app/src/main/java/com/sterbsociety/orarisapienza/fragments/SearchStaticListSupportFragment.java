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

import com.airbnb.android.airmapview.AirMapMarker;
import com.google.android.gms.maps.model.LatLng;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.MapsActivity;
import com.sterbsociety.orarisapienza.adapter.BuildingListViewAdapter;
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

    private MapsActivity mapsActivity;
    private AirMapMarker<?> lastAirMapMarker;

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
        final BuildingListViewAdapter mAdapter = mapsActivity.buildingListViewAdapter;

        // Use current GPS position button here
        CardView gpsButton = rootView.findViewById(R.id.use_gps_button);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = rootView.findViewById(R.id._gps);
                searchViewLayout.setCollapsedHint(textView.getText().toString());
                searchViewLayout.collapse();
            }
        });

        // We set the adapter for our listView
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Building building = mAdapter.getItem(position);
                if (building != null) {
                    setMarker(building, id);
                    addBuildingToFavourites(mapsActivity, building, position);
                }
                searchViewLayout.collapse();
            }
        });

        return rootView;
    }

    /**
     * Utility method which is responsible for correctly setting up the map markers.
     */
    private void setMarker(Building building, long id) {

        if (lastAirMapMarker != null) {
            mapsActivity.mapView.removeMarker(lastAirMapMarker);
        }
        // Updates the flag of MapsActivity here
        mapsActivity.isPlaceSelected = true;
        LatLng latLng = new LatLng(building.getLat(), building.getLong());

        // The last AirMapMarker is saved since we want to keep just one inside the view
        lastAirMapMarker = new AirMapMarker.Builder()
                .id(id)
                .position(latLng)
                .title(building.getName())
                .build();
        mapsActivity.mapView.addMarker(lastAirMapMarker);
        mapsActivity.mapView.animateCenterZoom(latLng, 16);
    }
}