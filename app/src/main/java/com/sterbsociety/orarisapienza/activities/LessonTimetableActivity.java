package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapter.SearchViewAdapter;
import com.sterbsociety.orarisapienza.adapter.WeekDayFragmentPagerAdapter;
import com.sterbsociety.orarisapienza.fragments.WeekDayFragment;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.Objects;

public class LessonTimetableActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private LinearLayout mAdsContainer;
    private AdView mAdView;
    private AdRequest mAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(LessonTimetableActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_timetable);
        initActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lesson_timetable, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    private void initActivity() {

        AppUtils.setLocale(LessonTimetableActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.timetables);
            AppUtils.setToolbarColor(((ColorDrawable)toolbar.getBackground()).getColor());
        }

        final ViewPager viewPager = findViewById(R.id.viewpager);
        WeekDayFragmentPagerAdapter mPagerAdapter = new WeekDayFragmentPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(AppUtils.getCurrentWeekDayIndex());

        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        initSearchView();

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
    }

    private void initSearchView() {

        AppUtils.createCoursesList();
        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setAdapter(new SearchViewAdapter(this, AppUtils.getCoursesList()));
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String courseName = ((TextView)view.findViewById(R.id.suggestion_text)).getText().toString();
                AppUtils.addCourseToFavourites(view.getContext(), courseName);

                // This below shows the TableView and hides the welcome text.
                ((WeekDayFragment)getSupportFragmentManager().getFragments().get(0)).displayTableView(courseName);

                searchView.closeSearch();
                Objects.requireNonNull(getSupportActionBar()).setSubtitle(getString(R.string.course_code) + ": " + courseName.split("-")[0]);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
