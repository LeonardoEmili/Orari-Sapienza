package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.android.airmapview.AirGoogleMapOptions;
import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.listeners.OnMapClickListener;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.model.Classroom;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.Iterator;

public class ClassDetailActivity extends AppCompatActivity implements OnMapInitializedListener, OnMapClickListener {

    private AirMapView mapView;
    private DefaultAirMapViewBuilder mapViewBuilder;
    private LinearLayout mAdsContainer;
    private AdView mAdView;
    private AdRequest mAdRequest;
    private Classroom classroom;
    private Button favouritesButton;

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

        mapViewBuilder = new DefaultAirMapViewBuilder(this);
        mapView = findViewById(R.id.map_view);
        mapView.setOnMapInitializedListener(this);
        mapView.setOnMapClickListener(this);
        mapView.initialize(getSupportFragmentManager(), mapViewBuilder.builder().withOptions(new AirGoogleMapOptions(new GoogleMapOptions().liteMode(true))).build());

        // AdMob App ID: ca-app-pub-9817701892167034~2496155654
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = AppUtils.hash(androidId).toUpperCase();

        mAdsContainer = findViewById(R.id.ad_container);
        mAdView = new AdView(getApplicationContext());
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(deviceId)
                .build();
        mAdView.loadAd(mAdRequest);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mAdsContainer.addView(mAdView, params);

        TextView className = findViewById(R.id.class_name);
        TextView buildingName = findViewById(R.id.building_name);
        TextView buildingAddress = findViewById(R.id.building_address);
        TextView classStatus = findViewById(R.id.current_status);
        TextView currentLesson = findViewById(R.id.current_lesson);
        TextView classTimetable = findViewById(R.id.class_timetable);
        favouritesButton = findViewById(R.id.add_to_fav);

        Intent i = getIntent();
        classroom = i.getParcelableExtra("KEY");

        className.setText(classroom.getName());
        buildingName.setText(classroom.getMainBuilding());
        buildingAddress.setText(classroom.getMainBuildingAddress());
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
            ((TextView)findViewById(R.id.course)).setTextColor(white);
            ((TextView)findViewById(R.id.time)).setTextColor(white);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onMapInitialized() {

        final LatLng latLng = new LatLng(41.904130, 12.515297);
        mapView.addMarker(new AirMapMarker.Builder()
                .id(1)
                .position(latLng)
                .title("Sapienza")
                .iconId(R.drawable.ic_location_pin)
                .build());
        mapView.animateCenterZoom(latLng, 16);
        mapView.setMyLocationEnabled(false);
        new AirGoogleMapOptions(new GoogleMapOptions().liteMode(true));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this, "click!", Toast.LENGTH_SHORT).show();
        System.out.println("clcccci");
    }

    public void addToFavourites(View view) {
        AppUtils.addClassToFavourites(this, classroom.getCode());
        favouritesButton.setVisibility(View.GONE);
    }
}