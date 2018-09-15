package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.android.airmapview.AirGoogleMapOptions;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.models.Building;
import com.sterbsociety.orarisapienza.models.Classroom;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import static com.sterbsociety.orarisapienza.utils.AppUtils.addClassroomToFavourites;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getRealBuilding;

public class ClassDetailActivity extends AppCompatActivity implements OnMapInitializedListener {

    private AirMapView mapView;
    private Classroom classroom;
    private Button favouritesButton;
    private Building mainBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyTheme(ClassDetailActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);
        initActivity();
    }

    private void initActivity() {

        AppUtils.setLocale(ClassDetailActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.name_class_detail));
        }

        LinearLayout mAdsContainer = findViewById(R.id.ad_container);
        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-3940256099942544/6300978111");

        final TextView className = findViewById(R.id.class_name);
        final TextView buildingName = findViewById(R.id.building_name);
        final TextView buildingAddress = findViewById(R.id.building_address);
        final TextView classStatus = findViewById(R.id.current_status);
        final TextView currentLesson = findViewById(R.id.current_lesson);
        final TextView classTimetable = findViewById(R.id.class_timetable);
        favouritesButton = findViewById(R.id.add_to_fav);

        Intent i = getIntent();
        classroom = i.getParcelableExtra(AppUtils.DEFAULT_KEY);

        // We are safe to use this method, since the index used in the method is relative
        // to a Building created at runtime.
        mainBuilding = getRealBuilding(classroom);

        className.setText(classroom.getName());
        buildingName.setText(mainBuilding.getName());
        buildingAddress.setText(mainBuilding.getLocation());
        classStatus.setText(classroom.getStatus());
        currentLesson.setText(classroom.getCurrentClass());
        classTimetable.setText(classroom.getCurrentClassTime());

        if (AppUtils.getFavouriteClassSet().contains(classroom.getCode()))
            favouritesButton.setVisibility(View.GONE);

        // GUI stuff for improving the UX
        if (AppUtils.isDarkTheme()) {
            int white = getResources().getColor(android.R.color.white);
            className.setTextColor(white);
            buildingName.setTextColor(white);
            buildingAddress.setTextColor(white);
            currentLesson.setTextColor(white);
            classTimetable.setTextColor(white);
            ((TextView)findViewById(R.id.status)).setTextColor(white);
            ((TextView)findViewById(R.id.lesson)).setTextColor(white);
            ((TextView)findViewById(R.id.time)).setTextColor(white);
        }

        final DefaultAirMapViewBuilder mapViewBuilder = new DefaultAirMapViewBuilder(this);
        mapView = findViewById(R.id.map_view);
        mapView.setOnMapInitializedListener(this);
        mapView.initialize(getSupportFragmentManager(), mapViewBuilder.builder().withOptions(new AirGoogleMapOptions(new GoogleMapOptions().liteMode(true))).build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onMapInitialized() {

        final LatLng latLng = new LatLng(mainBuilding.getLat(), mainBuilding.getLong());
        mapView.addMarker(new AirMapMarker.Builder()
                .id(mainBuilding.hashCode())
                .position(latLng)
                .title(mainBuilding.getName())
                .iconId(R.drawable.ic_location_pin)
                .build());
        mapView.animateCenterZoom(latLng, 16);
        mapView.setMyLocationEnabled(false);
    }

    public void addToFavourites(View view) {
        addClassroomToFavourites(this, classroom);
        favouritesButton.setVisibility(View.GONE);
    }
}