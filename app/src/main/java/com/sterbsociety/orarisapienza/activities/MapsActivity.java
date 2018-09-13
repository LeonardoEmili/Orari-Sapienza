package com.sterbsociety.orarisapienza.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.schedulers.Schedulers;
import xyz.sahildave.widget.SearchViewLayout;

import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;

import com.github.florent37.rxgps.RxGps;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.sterbsociety.orarisapienza.views.MyDoubleDateAndTimePickerDialog;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.BuildingListViewAdapter;
import com.sterbsociety.orarisapienza.fragments.SearchStaticListSupportFragment;

import com.sterbsociety.orarisapienza.models.Building;
import com.sterbsociety.orarisapienza.models.StudyPlanPresenter;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.sterbsociety.orarisapienza.utils.NetworkStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sterbsociety.orarisapienza.utils.AppUtils.STUDY_PLAN;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isSameStudyPlanRequestAsBefore;


public class MapsActivity extends AppCompatActivity implements OnMapInitializedListener {

    public AirMapView mapView;
    public SearchViewLayout searchViewLayout;
    private MyDoubleDateAndTimePickerDialog.Builder mPickerDialog;
    private Date tab0date, tab1date;
    private Button myButton;
    private SimpleDateFormat simpleDateFormat;
    private Location lastLocation;
    private AirMapMarker<?> lastAirMapMarker;
    private RxGps rxGps;
    private StudyPlanPresenter studyPlanPresenter;
    public boolean isPlaceSelected;
    public BuildingListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(MapsActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initActivity();
        initGps();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final Calendar calendar = Calendar.getInstance();
        final Date startDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 4);
        final Date endDate = calendar.getTime();

        mPickerDialog = new MyDoubleDateAndTimePickerDialog.Builder(this)
                .curved()
                .minutesStep(15)
                .tab0Date(startDate)
                .tab1Date(endDate)
                .tab0Text(getString(R.string.from_))
                .tab1Text(getString(R.string.to_))
                .listener(dates -> {

                    tab0date = dates.get(0);
                    tab1date = dates.get(1);

                    studyPlanPresenter.setDates(tab0date, tab1date, simpleDateFormat);
                });

        studyPlanPresenter.setDates(startDate, endDate, simpleDateFormat);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSameStudyPlanRequestAsBefore(studyPlanPresenter)) {
            // todo alert the user if he wants to regenerate a study plan with same settings
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == STUDY_PLAN && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    // todo tested crash: phone with api level 22 crash with GIF
    private void initActivity() {

        AppUtils.setLocale(MapsActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        myButton = findViewById(R.id.button1);
        mapView = findViewById(R.id.map_view);
        RelativeLayout alterNativeLayout = findViewById(R.id.dinosaur_wrapper);

        if (NetworkStatus.getInstance().isOnline(this)) {
            DefaultAirMapViewBuilder mapViewBuilder = new DefaultAirMapViewBuilder(this);
            mapView.setOnMapInitializedListener(this);
            mapView.initialize(getSupportFragmentManager());
            alterNativeLayout.setVisibility(View.GONE);
        }

        simpleDateFormat = new SimpleDateFormat("E, d MMM, yyyy HH:mm", Locale.ENGLISH);
        studyPlanPresenter = new StudyPlanPresenter();

        initSearchViewLayout();
    }

    private void initSearchViewLayout() {

        searchViewLayout = findViewById(R.id.search_view_container);

        AppUtils.createFakeBuildingsList();
        mAdapter = new BuildingListViewAdapter(this, AppUtils.getFavouriteBuildingList());

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

        searchViewLayout.setSearchListener(query -> {
            searchViewLayout.collapse();
            // todo add check if query is correct
            Snackbar.make(searchViewLayout, "User clicked ENTER" + query, Snackbar.LENGTH_LONG).show();
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
                mAdapter.filterBuildingsByQuery(query.toString());
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
        if (tab0date != null && tab1date != null) {
            mPickerDialog.tab0Date(tab0date);
            mPickerDialog.tab1Date(tab1date);
        }
        mPickerDialog.display();
    }

    /**
     * Utility method which is responsible for correctly setting up the map markers.
     */
    public void setMarker(double latitude, double longitude, long markerId, String markerTitle) {

        if (lastAirMapMarker != null) {
            mapView.removeMarker(lastAirMapMarker);
        }
        // Updates the flag of MapsActivity here
        isPlaceSelected = true;
        LatLng latLng = new LatLng(latitude, longitude);

        // The last AirMapMarker is saved since we want to keep just one inside the view
        lastAirMapMarker = new AirMapMarker.Builder()
                .id(markerId)
                .position(latLng)
                .title(markerTitle)
                .build();
        mapView.addMarker(lastAirMapMarker);
        mapView.animateCenterZoom(latLng, 17);
    }

    @SuppressLint("CheckResult")
    public void getAccuratePosition() {
        rxGps.locationHight()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    if (location != null) {
                        lastLocation = location;
                    }
                }, throwable -> {
                    if (throwable instanceof RxGps.PermissionException) {
                        Log.e(MapsActivity.class.getSimpleName(), throwable.getMessage());
                    } else if (throwable instanceof RxGps.PlayServicesNotAvailableException) {
                        Log.e(MapsActivity.class.getSimpleName(), throwable.getMessage());
                    }
                });
    }

    private void launchStudyPlanActivity() {
        Intent i = new Intent(this, StudyPlanActivity.class);
        i.putExtra(AppUtils.DEFAULT_KEY, studyPlanPresenter);
        startActivityForResult(i, STUDY_PLAN);
    }

    public void useGPSPosition() {
        isPlaceSelected = true;
        if (lastLocation == null) {
            getAccuratePosition();
        }
        if (lastLocation != null) {
            setMarker(lastLocation.getLatitude(), lastLocation.getLongitude(), lastLocation.hashCode(), "");
            studyPlanPresenter.setGpsLocation(lastLocation);
            studyPlanPresenter.setBuilding(null);
        }
    }

    public void useBuildingPosition(Building building) {
        isPlaceSelected = true;
        setMarker(building.getLat(), building.getLong(), building.hashCode(), building.getName());
        studyPlanPresenter.setBuilding(building);
        studyPlanPresenter.setGpsLocation(null);
    }

    /**
     * Docs at:
     * https://github.com/florent37/RxGps
     * https://github.com/ReactiveX/RxJava
     * https://www.youtube.com/watch?v=k3D0cWyNno4
     */
    @SuppressLint("CheckResult")
    public void initGps() {

        rxGps = new RxGps(this);
        rxGps.lastLocation()

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    if (location != null && lastLocation == null) {
                        lastLocation = location;
                    }
                }, throwable -> {
                    if (throwable instanceof RxGps.PermissionException) {
                        // The user does not allow the permission
                        Log.e(MapsActivity.class.getSimpleName(), throwable.getMessage());
                    } else if (throwable instanceof RxGps.PlayServicesNotAvailableException) {
                        Log.e(MapsActivity.class.getSimpleName(), throwable.getMessage());
                    }
                });
        //getAccuratePosition();
    }

    public void createStudyPlan(View view) {
        if (isPlaceSelected) {
            launchStudyPlanActivity();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.place_not_selected))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                        launchStudyPlanActivity();
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        }
    }
}