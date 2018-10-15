package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.sterbsociety.orarisapienza.utils.AppUtils.WEEK_LENGTH;
import static com.sterbsociety.orarisapienza.utils.AppUtils.addClassroomToFavourites;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getHourByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getRealBuilding;

public class ClassDetailActivity extends AppCompatActivity implements OnMapInitializedListener {

    private AirMapView mapView;
    private Classroom classroom;
    private Building mainBuilding;
    private boolean isFavouriteButtonVisible;

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
        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-9817701892167034/7649462643");

        final TextView className = findViewById(R.id.class_name);
        final TextView buildingName = findViewById(R.id.building_name);
        final TextView buildingAddress = findViewById(R.id.building_address);
        final TextView classStatus = findViewById(R.id.current_status);
        final TextView currentLesson = findViewById(R.id.current_lesson);
        final TextView classTimetable = findViewById(R.id.class_timetable);
        final TextView lesson = findViewById(R.id.lesson);
        final TextView time = findViewById(R.id.time);
        final TextView professor = findViewById(R.id.professor);
        final TextView currentProfessor = findViewById(R.id.current_professor);

        Intent i = getIntent();
        classroom = i.getParcelableExtra(AppUtils.DEFAULT_KEY);

        // We are safe to use this method, since the index used in the method is relative
        // to a Building created at runtime.
        mainBuilding = getRealBuilding(classroom);

        className.setText(classroom.getName());
        buildingName.setText(mainBuilding.name);
        buildingAddress.setText(mainBuilding.address);

        // We retrieve the index of the current / most close in future lesson in this classroom
        int scrollIndex = AppUtils.getCurrentTimeToInt();
        final List<Integer> lessonList = AppUtils.MATRIX.get(classroom.getFullCode());  // List with integers
        final int lessonIndex = lessonList.get(scrollIndex);
        final Date now = new Date();
        if (lessonIndex == 0 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                || now.before(AppUtils.getMinHour()) || now.after(AppUtils.getMaxHour())) {
            // Then the classroom is available
            classStatus.setText(R.string.available);
            classStatus.setTextColor(getResources().getColor(R.color.green_normal));
            currentLesson.setVisibility(View.GONE);
            classTimetable.setVisibility(View.GONE);
            lesson.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
            professor.setVisibility(View.GONE);
            currentLesson.setVisibility(View.GONE);
        } else {
            final String[] lessonParts = AppUtils.LESSON_LIST.get(lessonIndex).split("_");
            classStatus.setText(R.string.occupied);
            currentLesson.setText(lessonParts[2].trim());
            currentProfessor.setText(lessonParts[4].trim());
            final String startHour = getHourByIndex(scrollIndex + 1);
            while (scrollIndex != WEEK_LENGTH && lessonList.get(scrollIndex) == lessonIndex) {
                scrollIndex++;
            }
            if (scrollIndex >= WEEK_LENGTH) {
                scrollIndex = WEEK_LENGTH - 1;
            }
            classTimetable.setText(getString(R.string.lesson_timetable, startHour, getHourByIndex(scrollIndex + 1)));
            // UX stuff - This is responsible for aligning lesson to its relative currentLesson
            ViewTreeObserver viewTreeObserver = currentLesson.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        currentLesson.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        lesson.setHeight(currentLesson.getHeight());
                    }
                });
            }
        }

        if (!AppUtils.getFavouriteClassSet().contains(classroom.getFullCode())) {
            isFavouriteButtonVisible = true;
        }

        // GUI stuff for improving the UX
        if (AppUtils.isDarkTheme()) {
            int white = getResources().getColor(android.R.color.white);
            className.setTextColor(white);
            buildingName.setTextColor(white);
            buildingAddress.setTextColor(white);
            currentLesson.setTextColor(white);
            classTimetable.setTextColor(white);
            ((TextView) findViewById(R.id.status)).setTextColor(white);
            ((TextView) findViewById(R.id.lesson)).setTextColor(white);
            ((TextView) findViewById(R.id.time)).setTextColor(white);
        }

        final DefaultAirMapViewBuilder mapViewBuilder = new DefaultAirMapViewBuilder(this);
        mapView = findViewById(R.id.map_view);
        mapView.setOnMapInitializedListener(this);
        mapView.initialize(getSupportFragmentManager(), mapViewBuilder.builder().withOptions(new AirGoogleMapOptions(new GoogleMapOptions().liteMode(true))).build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFavouriteButtonVisible) {
            getMenuInflater().inflate(R.menu.menu_class_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_button:
                addClassroomToFavourites(this, classroom);
                item.setVisible(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                .title(mainBuilding.name)
                .iconId(R.drawable.ic_location_pin)
                .build());
        mapView.animateCenterZoom(latLng, 16);
        mapView.setMyLocationEnabled(false);
    }
}