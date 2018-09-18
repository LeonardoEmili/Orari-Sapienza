package com.sterbsociety.orarisapienza.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.rxgps.RxGps;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import static com.sterbsociety.orarisapienza.utils.AppUtils.isGPSEnabled;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;

public class FilterActivity extends AppCompatActivity {

    private RangeSeekBar rangeSeekBar, rangeSeekBar2;
    private TextView leftTime, rightTime, distanceText, distanceFrom, upToText;
    private Button allowBtn, activeBtn;
    boolean isGPSEnabled, needToObfuscateView, needAllowBtn, needActiveBtn;
    private LinearLayout[] toggleArray;
    boolean[] activeToggleBtn, cachedDayIndex;
    private TextView[] dayButtonArray;
    private int cachedAvailabilityIndex, cachedMinHour, cachedMaxHour, cachedDistance;
    private Location lastLocation;
    private RxGps rxGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //applyThemeNoActionBar(FilterActivity.this);
        setLocale(FilterActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        boolean[] tempArray = AppUtils.getSelectedDayBtnIndex();
        cachedDayIndex = new boolean[tempArray.length];

        // Iterative for vs native System.arraycopy performances here:
        // https://stackoverflow.com/questions/2772152/why-is-system-arraycopy-native-in-java
        System.arraycopy(tempArray, 0, cachedDayIndex, 0, tempArray.length);

        cachedAvailabilityIndex = AppUtils.getSelectedClassBtnIndex();
        cachedMinHour = AppUtils.getMinHour();
        cachedMaxHour = AppUtils.getMaxHour();
        cachedDistance = AppUtils.getDistanceFromCurrentPosition();

        initGPS();
        initActivity();
    }


    /**
     * This method handles the GPS in this Activity.
     */
    @SuppressLint("CheckResult")
    private void initGPS() {

        // From Lollipop we have to check at runtime for Android's permissions.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // User has explicitly denied the permission.
            needToObfuscateView = true;
            needAllowBtn = true;
        }

        rxGps = new RxGps(this);
        rxGps.lastLocation()

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    if (location != null) {
                        lastLocation = location;
                    }
                }, throwable -> {
                    needToObfuscateView = true;
                    needAllowBtn = true;
                });

        if (!(isGPSEnabled = isGPSEnabled(this))) {
            needActiveBtn = true;
            needToObfuscateView = true;
        }

        getAccuratePosition();
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

    private void initActivity() {

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        leftTime = findViewById(R.id.left_time);
        rightTime = findViewById(R.id.right_time);
        distanceText = findViewById(R.id.distance_text);
        upToText = findViewById(R.id.up_to_text);
        distanceFrom = findViewById(R.id.distance_from);
        rangeSeekBar = findViewById(R.id.range_seek_bar);
        rangeSeekBar2 = findViewById(R.id.range_seek_bar_2);
        allowBtn = findViewById(R.id.allow_btn);
        activeBtn = findViewById(R.id.active_btn);
        toggleArray = new LinearLayout[]{findViewById(R.id.vacant), findViewById(R.id.busy), findViewById(R.id.all)};
        dayButtonArray = new TextView[]{findViewById(R.id.mon), findViewById(R.id.tue), findViewById(R.id.wed)
                , findViewById(R.id.thu), findViewById(R.id.fri)};

        initSeekBars();
        setToggleButtons();
        setDayButtons();


        if (needToObfuscateView) {

            obfuscateView();

            if (needAllowBtn) {
                allowBtn.setVisibility(View.VISIBLE);
                upToText.setText(R.string.location_not_allowed);
            } else {
                activeBtn.setVisibility(View.VISIBLE);
                upToText.setText(R.string.location_not_active);
            }
        }
    }

    /**
     * This is an utility method to block the user from unchecking all the days.
     *
     * @param index is the current clicked day
     * @return is the relative outcome
     */
    private boolean isTheOnlyActiveLeft(int index) {

        // If the current button is going to be activated then no problems here.
        if (!cachedDayIndex[index])
            return false;

        int cnt = 0;
        for (boolean anActiveDayBtn : cachedDayIndex) {
            if (anActiveDayBtn)
                cnt++;
        }

        return cnt == 1 && cachedDayIndex[index];
        // todo maybe add some alert
    }

    /**
     * Another utility method for easily setting up the "day's buttons"
     */
    private void setDayButtons() {

        // Here we mark the chosen days as active
        for (int i = 0; i < cachedDayIndex.length; i++) {
            if (cachedDayIndex[i]) {
                TextView currentDefaultDay = dayButtonArray[i];
                currentDefaultDay.setBackgroundResource(R.drawable.back_secondary_col_rounded);
                currentDefaultDay.setTextColor(getResources().getColor(android.R.color.white));
            }
        }

        // Here we set up the behaviour for these buttons
        for (int i = 0; i < dayButtonArray.length; i++) {
            final int index = i;
            dayButtonArray[i].setOnClickListener(view -> {

                if (isTheOnlyActiveLeft(index)) {
                    return;
                }

                if (cachedDayIndex[index]) {
                    view.setBackgroundResource(R.drawable.back_gray_rounded);
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
                } else {
                    view.setBackgroundResource(R.drawable.back_secondary_col_rounded);
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.white));
                }

                // If we arrive here then the button has been toggled
                cachedDayIndex[index] = !cachedDayIndex[index];
            });
        }
    }

    /**
     * Another utility method for easily setting up the "availability's buttons"
     */
    private void setToggleButtons() {

        activeToggleBtn = new boolean[3];

        // Here we mark as active the selected button
        activeToggleBtn[cachedAvailabilityIndex] = true;
        ImageView mDefaultImage = (ImageView) toggleArray[cachedAvailabilityIndex].getChildAt(0);
        mDefaultImage.setImageDrawable(getDrawable(R.drawable.animated_check));
        ((Animatable) mDefaultImage.getDrawable()).start();

        for (int i = 0; i < toggleArray.length; i++) {
            final int index = i;
            toggleArray[i].setOnClickListener(view -> {
                for (int j = 0; j < toggleArray.length; j++) {
                    if (j != index) {
                        ((ImageView) toggleArray[j].getChildAt(0)).setImageDrawable(getDrawable(R.drawable.circle_to_check_static));
                        activeToggleBtn[j] = false;
                    }
                }

                // For each toggleButton we work on LinearLayout(father) and on it's ImageView(child) which is always the first child.
                final ImageView mImageView = ((ImageView) ((LinearLayout) view).getChildAt(0));

                if (!activeToggleBtn[index]) {
                    //view.setBackgroundColor(getResources().getColor(R.color.colorSecondaryFaded));
                    mImageView.setImageDrawable(getDrawable(R.drawable.animated_check));
                    ((Animatable) mImageView.getDrawable()).start();
                    activeToggleBtn[index] = true;
                }
                // We update the current availability button.
                cachedAvailabilityIndex = index;
            });
        }
    }

    /**
     * Utility method that marks as inactive the GPS SeekBar.
     */
    private void obfuscateView() {
        int inactiveColor = getResources().getColor(android.R.color.darker_gray);
        distanceText.setTextColor(inactiveColor);
        distanceFrom.setVisibility(View.GONE);
        rangeSeekBar2.setProgressColor(inactiveColor);
        rangeSeekBar2.setEnabled(false);
    }

    /**
     * Utility method that marks as active the GPS SeekBar.
     */
    private void clearView() {
        distanceText.setTextColor(getResources().getColor(R.color.coolBlack));
        upToText.setText(R.string.up_to);
        Log.e(FilterActivity.class.getSimpleName(), "clearView");
        distanceFrom.setVisibility(View.VISIBLE);
        rangeSeekBar2.setProgressColor(getResources().getColor(R.color.colorSecondary));
        rangeSeekBar2.setEnabled(true);
    }

    /**
     * @param leftValue  is the minHour
     * @param rightValue is the MaxHour
     */
    private void updateTime(float leftValue, float rightValue) {
        String lValue = (String.valueOf((int) leftValue).length() > 1) ? String.valueOf((int) leftValue) : "0" + (int) leftValue;
        String rValue = (String.valueOf((int) rightValue).length() > 1) ? String.valueOf((int) rightValue) : "0" + (int) rightValue;
        leftTime.setText(lValue);
        rightTime.setText(rValue);
        cachedMinHour = (int) leftValue;
        cachedMaxHour = (int) rightValue;
    }

    /**
     * @param leftValue is the current distance multiplied by 10 (UI stuff)
     */
    private void updateDistance(float leftValue) {
        float currentValue = (float) (Math.round(leftValue * 10)) / 100;
        String stringValue = currentValue + " km";
        cachedDistance = (int) leftValue;
        if (currentValue == (float) 2.1)
            distanceFrom.setText(getResources().getString(R.string.infinity));
        else
            distanceFrom.setText(stringValue);
    }


    /**
     * Utility method for properly setting up both the SeekBars.
     */
    private void initSeekBars() {

        rangeSeekBar.setValue(cachedMinHour, cachedMaxHour);
        updateTime(cachedMinHour, cachedMaxHour);
        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                updateTime(leftValue, rightValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });

        rangeSeekBar2.setValue(cachedDistance);
        updateDistance(cachedDistance);
        rangeSeekBar2.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                updateDistance(leftValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });
    }

    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_down);
    }

    @Override
    public void onBackPressed() {
        killAll(null);
    }


    /**
     * We let the user know if they've allowed to use GPS but it's currently not active.
     */
    private void showAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.gps_service_inactive));
        dialog.setPositiveButton(getString(R.string.open_settings), (paramDialogInterface, paramInt) -> {
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);

            // Updates the value
            isGPSEnabled = isGPSEnabled(FilterActivity.this);
        });
        dialog.setNegativeButton(R.string.cancel, (paramDialogInterface, paramInt) -> {
            // Nothing to do here
        });
        dialog.show();
    }

    public void askForGPSPermission(View view) {
        AppUtils.askForGPSPermission(this);
    }

    public void activeGPS(View view) {

        if ((isGPSEnabled = isGPSEnabled(this))) {
            initGPS();
            activeBtn.setVisibility(View.GONE);
            clearView();
        } else {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), AppUtils.GPS_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppUtils.GPS_RESULT && ((isGPSEnabled = isGPSEnabled(this)))) {
            initGPS();
            activeBtn.setVisibility(View.GONE);
            clearView();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppUtils.GPS_ACCESS) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                // "Oak's words echoed... There's a time and place for everything, but not now."
            } else {
                // User has allowed to use GPS service inside the app.
                allowBtn.setVisibility(View.GONE);
                showAlertDialog();
                if (isGPSEnabled) {
                    initGPS();
                    clearView();
                } else {
                    activeBtn.setVisibility(View.VISIBLE);
                    upToText.setText(R.string.location_not_active);
                }
            }
        }
    }

    public void killAll(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void applyFilters(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppUtils.KEY_FILTER_DAY, cachedDayIndex);
        returnIntent.putExtra(AppUtils.KEY_FILTER_AVAILABILITY, cachedAvailabilityIndex);
        returnIntent.putExtra(AppUtils.KEY_FILTER_MIN_HOUR, cachedMinHour);
        returnIntent.putExtra(AppUtils.KEY_FILTER_MAX_HOUR, cachedMaxHour);
        returnIntent.putExtra(AppUtils.KEY_FILTER_DISTANCE, cachedDistance);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}