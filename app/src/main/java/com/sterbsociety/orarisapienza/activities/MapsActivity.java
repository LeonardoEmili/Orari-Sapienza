package com.sterbsociety.orarisapienza.activities;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import xyz.sahildave.widget.SearchViewLayout;

import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.sterbsociety.orarisapienza.MyDoubleDateAndTimePickerDialog;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapter.ListViewAdapter;
import com.sterbsociety.orarisapienza.fragments.SearchStaticListSupportFragment;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.sterbsociety.orarisapienza.utils.NetworkStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapInitializedListener{

    public AirMapView mapView;
    public SearchViewLayout searchViewLayout;
    private MyDoubleDateAndTimePickerDialog.Builder mPickerDialog;
    private Date tab0date, tab1date;
    private Button myButton;
    public boolean isPlaceSelected;
    private SimpleDateFormat simpleDateFormat;
    public ListViewAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(MapsActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initActivity();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mPickerDialog = new MyDoubleDateAndTimePickerDialog.Builder(this)
                .curved()
                .minutesStep(15)
                .tab0Text(getString(R.string.from_))
                .tab1Text(getString(R.string.to_))
                .listener(new MyDoubleDateAndTimePickerDialog.Listener() {

                    @Override
                    public void onDateSelected(List<Date> dates) {

                        tab0date = dates.get(0);
                        tab1date = dates.get(1);

                        // todo may be useful to remember how to pass to string format
                        //fromDate = simpleDateFormat.format(dates.get(0));

                        // if (!isPlaceSelected) {
                        //     show an alert box saying that it will just search in any building inside Sapienza.
                        // }
                        myButton.setText("SEARCH IT NOW");

                        // todo build PDS and retrieve Dates from tabXDate here above
                    }
                });
    }

    private void initActivity() {

        AppUtils.setLocale(MapsActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        myButton = findViewById(R.id.my_button);
        mapView = findViewById(R.id.map_view);
        RelativeLayout alterNativeLayout = findViewById(R.id.dinosaur_wrapper);

        if (NetworkStatus.getInstance().isOnline(this)) {
            DefaultAirMapViewBuilder mapViewBuilder = new DefaultAirMapViewBuilder(this);
            mapView.setOnMapInitializedListener(this);
            mapView.initialize(getSupportFragmentManager());
            alterNativeLayout.setVisibility(View.GONE);
        }

        simpleDateFormat = new SimpleDateFormat("EEE d MM HH:mm", Locale.ENGLISH);

        initSearchViewLayout();
    }

    private void initSearchViewLayout() {

        searchViewLayout = findViewById(R.id.search_view_container);

        // Retrieve data
        AppUtils.createFakeBuildingsList();
        listViewAdapter = new ListViewAdapter(this, AppUtils.getFavouriteBuildingList());

        searchViewLayout.setExpandedContentFragment(this, new SearchStaticListSupportFragment());
        searchViewLayout.setHint(getString(R.string.where_to_study));

        searchViewLayout.setOnToggleAnimationListener(new SearchViewLayout.OnToggleAnimationListener() {
            @Override
            public void onStart(boolean expanded) {
                if (!expanded) {
                    myButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish(boolean expanded) {
                if (expanded) {
                    myButton.setVisibility(View.GONE);
                }
            }
        });

        searchViewLayout.setSearchListener(new SearchViewLayout.SearchListener() {
            @Override
            public void onFinished(String query) {
                searchViewLayout.collapse();
                // todo add check if query is correct
                Snackbar.make(searchViewLayout, "User clicked ENTER" + query, Snackbar.LENGTH_LONG).show();
            }
        });

        searchViewLayout.setSearchBoxListener(new SearchViewLayout.SearchBoxListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do here
            }

            @Override
            public void afterTextChanged(Editable query) {
                listViewAdapter.filterBuildingsByQuery(query.toString());
            }
        });
    }


    @Override
    public void onMapInitialized() {

        final LatLng latLng = new LatLng(41.904130, 12.515297);
        mapView.animateCenterZoom(latLng, 14);
        mapView.setMyLocationEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (mPickerDialog != null && mPickerDialog.isPickerDialogOpen()) {
            mPickerDialog.close();
        } else {
            super.onBackPressed();
        }
    }

    public void showPickerDialog(View view) {
        if (tab0date != null && tab1date!= null) {
            mPickerDialog.tab0Date(tab0date);
            mPickerDialog.tab1Date(tab1date);
        }
        mPickerDialog.display();
    }
}
