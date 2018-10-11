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
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.schedulers.Schedulers;
import xyz.sahildave.widget.SearchViewLayout;

import com.airbnb.android.airmapview.AirMapMarker;
import com.airbnb.android.airmapview.AirMapView;
import com.airbnb.android.airmapview.DefaultAirMapViewBuilder;
import com.airbnb.android.airmapview.listeners.OnMapInitializedListener;

import com.bumptech.glide.Glide;
import com.github.florent37.rxgps.RxGps;
import com.google.android.gms.maps.model.LatLng;
import com.sterbsociety.orarisapienza.views.MyDoubleDateAndTimePickerDialog;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.BuildingListViewAdapter;
import com.sterbsociety.orarisapienza.fragments.SearchStaticListSupportFragment;

import com.sterbsociety.orarisapienza.models.Building;
import com.sterbsociety.orarisapienza.models.StudyPlanPresenter;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.sterbsociety.orarisapienza.utils.NetworkStatus;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.sterbsociety.orarisapienza.utils.AppUtils.STUDY_PLAN;
import static com.sterbsociety.orarisapienza.utils.AppUtils.applyThemeNoActionBar;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getBestDates;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getFullDateFormatter;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getStringByLocal;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;


public class MapsActivity extends AppCompatActivity implements OnMapInitializedListener {

    public AirMapView mapView;
    public SearchViewLayout searchViewLayout;
    private MyDoubleDateAndTimePickerDialog.Builder mPickerDialog;
    private Date tab0date, tab1date;
    private Button firstButton, secondButton;
    private Location lastLocation;
    private AirMapMarker<?> lastAirMapMarker;
    private RxGps rxGps;
    private StudyPlanPresenter studyPlanPresenter;
    public boolean isPlaceSelected, errorDialogMustBeDisplayed;
    public BuildingListViewAdapter mAdapter;
    public ImageView clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeNoActionBar(MapsActivity.this);
        setLocale(MapsActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initActivity();
        initGps();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final Date[] bestDates = getBestDates();
        mPickerDialog = new MyDoubleDateAndTimePickerDialog.Builder(this)
                .curved()
                .minutesStep(15)
                .tab0Date(bestDates[0])
                .tab1Date(bestDates[1])
                .minDateRange(bestDates[0])
                .tab0Text(getString(R.string.from_))
                .tab1Text(getString(R.string.to_))
                .listener(dates -> {

                    tab0date = dates.get(0);
                    tab1date = dates.get(1);

                    // I don't think it's necessary, but it's better to be sure.
                    if (tab0date == null || tab1date == null) {
                        return;
                    }

                    if (tab0date.after(tab1date)
                            || (getFullDateFormatter().format(tab0date).equals(getFullDateFormatter().format(tab1date)))
                            || TimeUnit.HOURS.convert(tab1date.getTime() - tab0date.getTime(), TimeUnit.MILLISECONDS)  > 24) {
                        errorDialogMustBeDisplayed = true;
                    } else {
                        errorDialogMustBeDisplayed = false;
                        studyPlanPresenter.setDates(tab0date, tab1date, getFullDateFormatter());
                    }
                });

        studyPlanPresenter.setDates(bestDates[0], bestDates[1], getFullDateFormatter());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == STUDY_PLAN && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    private void initActivity() {

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        firstButton = findViewById(R.id.button1);
        secondButton = findViewById(R.id.button2);
        clearButton = findViewById(R.id.search_clear_search);
        mapView = findViewById(R.id.map_view);
        final LinearLayout alterNativeLayout = findViewById(R.id.dinosaur_wrapper);

        if (NetworkStatus.getInstance().isOnline(this)) {
            final DefaultAirMapViewBuilder mapViewBuilder = new DefaultAirMapViewBuilder(this);
            mapView.setOnMapInitializedListener(this);
            mapView.initialize(getSupportFragmentManager());
            alterNativeLayout.setVisibility(View.GONE);
        } else {
            ImageView imageView = alterNativeLayout.findViewById(R.id.img_view);
            Glide.with(this).asGif().load(R.drawable.pika).into(imageView);
        }

        studyPlanPresenter = new StudyPlanPresenter();
        initSearchViewLayout();
    }

    private void initSearchViewLayout() {
        searchViewLayout = findViewById(R.id.search_view_container);
        mAdapter = new BuildingListViewAdapter(this, AppUtils.getFavouriteBuildingList());
        searchViewLayout.setExpandedContentFragment(this, new SearchStaticListSupportFragment());
        searchViewLayout.setCollapsedHint(getString(R.string.where_to_study));
        searchViewLayout.setExpandedHint(getString(R.string.query_example));
        searchViewLayout.setOnToggleAnimationListener(new SearchViewLayout.OnToggleAnimationListener() {
            @Override
            public void onStart(boolean expanded) {
                if (!expanded) {
                    firstButton.setVisibility(View.VISIBLE);
                    secondButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish(boolean expanded) {
                if (expanded) {
                    firstButton.setVisibility(View.GONE);
                    secondButton.setVisibility(View.GONE);
                }
            }
        });

        searchViewLayout.setSearchListener(query -> {
            // Inside here we can do the same search
            searchViewLayout.collapse();
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
        if (!errorDialogMustBeDisplayed) {
            Intent i = new Intent(this, StudyPlanActivity.class);
            i.putExtra(AppUtils.DEFAULT_KEY, studyPlanPresenter);
            startActivityForResult(i, STUDY_PLAN);
        }
    }

    public void useGPSPosition() {
        isPlaceSelected = true;
        if (lastLocation == null) {
            getAccuratePosition();
        }
        if (lastLocation != null) {
            setMarker(lastLocation.getLatitude(), lastLocation.getLongitude(), lastLocation.hashCode(), getString(R.string.your_position));
            studyPlanPresenter.setGpsLocation(lastLocation);
            studyPlanPresenter.setBuilding(null);
        }
    }

    public void useBuildingPosition(Building building) {
        isPlaceSelected = true;
        setMarker(building.getLat(), building.getLong(), building.hashCode(), building.name);
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
    }

    public void createStudyPlan(View view) {
        if (isPlaceSelected && !errorDialogMustBeDisplayed) {
            launchStudyPlanActivity();
        } else {
            if (errorDialogMustBeDisplayed) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.dates_error))
                        .setMessage(getString(R.string.selected_dates_are_incompatibles))
                        .setCancelable(false)
                        .setNeutralButton(getString(R.string.ok), null)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.place_not_selected))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), (dialog, id) -> launchStudyPlanActivity())
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
            }
        }
    }

    public void clearSearchQuery(View view) {
        clearButton.setVisibility(View.GONE);
        searchViewLayout.setCollapsedHint(getString(R.string.where_to_study));
        isPlaceSelected = false;
        if (lastAirMapMarker != null) {
            mapView.removeMarker(lastAirMapMarker);
        }
    }
}