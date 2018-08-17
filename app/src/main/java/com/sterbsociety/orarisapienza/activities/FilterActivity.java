package com.sterbsociety.orarisapienza.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.MyLocationListener;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

public class FilterActivity extends AppCompatActivity {

    private RangeSeekBar rangeSeekBar, rangeSeekBar2;
    private TextView leftTime, rightTime, distanceText, distanceFrom, locationTitle, upToText;
    private Button allowBtn, activeBtn;
    private LocationListener mLocationListener = null;
    private LocationManager mLocationManager = null;
    boolean isGPSEnabled, needToObfuscateView, needAllowBtn, needActiveBtn;
    private LinearLayout vacantToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(FilterActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initGPS();
        initActivity();
    }

    private void initGPS() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // User has explicitly denied the permission.
            // todo obfuscate the view and show allow gps
            needToObfuscateView = true;
            needAllowBtn = true;
            return;
        }

        try {

            // If you are here then GPS permission has been granted
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            }

            // mLocationManager may return NullPointerException , but here we are inside a try-catch block.
            if (isGPSEnabled = (AppUtils.isGPSEnabled(this, mLocationManager))) {
                mLocationListener = new MyLocationListener(mLocationManager);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener);
            } else {
                needToObfuscateView = true;
                needActiveBtn = true;
            }


        } catch (Exception ex) {
            StyleableToast.makeText(this, "Some error occurred.",
                    Toast.LENGTH_LONG, R.style.errorToast).show();
            // todo send a report with the exception, probably a SecurityException.
            finish();
        }
    }

    private void initActivity() {

        AppUtils.setLocale(FilterActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        leftTime = findViewById(R.id.left_time);
        rightTime = findViewById(R.id.right_time);
        distanceText = findViewById(R.id.distance_text);
        upToText = findViewById(R.id.up_to_text);
        distanceFrom = findViewById(R.id.distance_from);
        rangeSeekBar = findViewById(R.id.range_seek_bar);
        rangeSeekBar2 = findViewById(R.id.range_seek_bar_2);
        locationTitle = findViewById(R.id.location_title);
        allowBtn = findViewById(R.id.allow_btn);
        activeBtn = findViewById(R.id.active_btn);
        vacantToggle = findViewById(R.id.vacant);

        initSeekBars();

        // Set as default
        // todo implements flags for toggling 'em all
        vacantToggle.setBackgroundColor(Color.parseColor("#AAf44336"));

        if (needToObfuscateView) {

            obfuscateView();

            if (needAllowBtn) {
                allowBtn.setVisibility(View.VISIBLE);
                locationTitle.setText("LOCATION (not allowed)");
            } else {
                activeBtn.setVisibility(View.VISIBLE);
                locationTitle.setText("LOCATION (not active)");
            }
        }
    }

    private void obfuscateView() {
        int inactiveColor = getResources().getColor(android.R.color.darker_gray);
        distanceText.setTextColor(inactiveColor);
        upToText.setVisibility(View.GONE);
        distanceFrom.setVisibility(View.GONE);
        rangeSeekBar2.setProgressColor(inactiveColor);
        rangeSeekBar2.setEnabled(false);
    }

    private void clearView() {
        distanceText.setTextColor(Color.parseColor("#c8000000"));
        upToText.setVisibility(View.VISIBLE);
        distanceFrom.setVisibility(View.VISIBLE);
        rangeSeekBar2.setProgressColor(getResources().getColor(R.color.colorSecondary));
        rangeSeekBar2.setEnabled(true);
        locationTitle.setText("LOCATION");
    }


    private void initSeekBars() {

        rangeSeekBar.setValue(0, 24);
        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                String lValue = (String.valueOf((int) leftValue).length() > 1) ? String.valueOf((int) leftValue) : "0" + (int) leftValue;
                String rValue = (String.valueOf((int) rightValue).length() > 1) ? String.valueOf((int) rightValue) : "0" + (int) rightValue;
                leftTime.setText(lValue);
                rightTime.setText(rValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });

        rangeSeekBar2.setValue(21);
        rangeSeekBar2.setOnRangeChangedListener(new OnRangeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                float currentValue = (float) (Math.round(leftValue * 10)) / 100;

                if (currentValue == (float) 2.1)
                    distanceFrom.setText(getResources().getString(R.string.infinity));
                else
                    distanceFrom.setText(currentValue + " km");
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


    private void showAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("GPS service is not active");
        dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);

                // Updates the value
                isGPSEnabled = AppUtils.isGPSEnabled(FilterActivity.this, mLocationManager);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // Nothing to do here
            }
        });
        dialog.show();
    }

    public void askForGPSPermission(View view) {
        AppUtils.askForGPSPermission(this);
    }

    public void activeGPS(View view) {

        if ((isGPSEnabled=AppUtils.isGPSEnabled(this, mLocationManager))) {
            initGPS();
            activeBtn.setVisibility(View.GONE);
            clearView();
        } else {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), AppUtils.GPS_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppUtils.GPS_RESULT && ((isGPSEnabled = AppUtils.isGPSEnabled(this, mLocationManager)))) {
            initGPS();
            activeBtn.setVisibility(View.GONE);
            clearView();
        }
    }

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
                    locationTitle.setText("LOCATION (not active)");
                }
            }
        }
    }

    public void killAll(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "wow");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}