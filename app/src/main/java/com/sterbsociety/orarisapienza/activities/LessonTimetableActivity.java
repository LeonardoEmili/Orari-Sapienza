package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapter.SearchViewAdapter;
import com.sterbsociety.orarisapienza.adapter.WeekDayFragmentPagerAdapter;
import com.sterbsociety.orarisapienza.fragments.WeekDayFragment;
import com.sterbsociety.orarisapienza.model.Lesson;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sterbsociety.orarisapienza.utils.AppUtils.setToolbarColor;

public class LessonTimetableActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private static boolean isTableVisible;
    private static List<List<Lesson>> scheduledLessons;


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

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.timetables);
            setToolbarColor(toolbar.getBackground());
        }

        final ViewPager viewPager = findViewById(R.id.viewpager);
        WeekDayFragmentPagerAdapter mPagerAdapter = new WeekDayFragmentPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(AppUtils.getCurrentWeekDayIndex());

        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        initSearchView();

        LinearLayout mAdsContainer = findViewById(R.id.ad_container);
        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-3940256099942544/6300978111");
    }

    private void initSearchView() {

        AppUtils.createCoursesList();
        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        final SearchViewAdapter searchViewAdapter = new SearchViewAdapter(this, AppUtils.getCoursesList());
        searchView.setAdapter(searchViewAdapter);
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                final TextView textView = view.findViewById(R.id.suggestion_text);
                final String courseName = textView.getText().toString();
                // todo this is a better way of doing it, we have to work with Course objects
                // adapterView.getItemAtPosition(position);
                AppUtils.addCourseToFavourites(view.getContext(), courseName, searchViewAdapter, position);

                displayTableView(courseName);

                searchView.closeSearch();
                Objects.requireNonNull(getSupportActionBar()).setSubtitle(getString(R.string.course_code) + ": " + courseName.split("-")[0]);
            }
        });
    }

    private void displayTableView(String courseName) {

        // ---------------------CREATE DATA LIST----------------------------------

        // Here we have to retrieve the course data by getting the name
        List<Lesson> fakeDailySchedule = new ArrayList<>();

        fakeDailySchedule.add(new Lesson("Aula A", 26654, "Informatica", "mon", "12:00", "Sterbini", "09:00", "Fondamenti di programmazione"));
        fakeDailySchedule.add(new Lesson("Aula B", 26655, "Informatica e pokemon del mondo di sterbini con la s", "mon", "11:00", "Sterbini", "08:00", "Biologia"));
        fakeDailySchedule.add(new Lesson("Aula C", 26652, "Informatica", "mon", "10:00", "Sterbini", "07:00", "Storia"));
        fakeDailySchedule.add(new Lesson("Aula D", 26651, "Informatica", "mon", "09:00", "Sterbini", "06:00", "Matematica"));
        fakeDailySchedule.add(new Lesson("Aula E", 266565, "Informatica", "mon", "12:00", "Sterbini", "05:00", "Filosofia"));
        fakeDailySchedule.add(new Lesson("Aula F", 26651, "Informatica", "mon", "10:00", "Sterbini", "04:00", "Algebra"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));
        fakeDailySchedule.add(new Lesson("Aula G", 26659, "Informatica", "mon", "11:00", "Sterbini", "03:00", "Grammatica"));

        scheduledLessons = new ArrayList<>();
        scheduledLessons.add(fakeDailySchedule);
        scheduledLessons.add(fakeDailySchedule);
        scheduledLessons.add(fakeDailySchedule);
        scheduledLessons.add(fakeDailySchedule);
        scheduledLessons.add(fakeDailySchedule);

        // ---------------------END OF DATA LIST----------------------------------


        // The lines below show the TableView and hides the welcome text.
        isTableVisible = true;

        final List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragmentList) {
            final WeekDayFragment mFragment = (WeekDayFragment) fragment;
            mFragment.displayTableView();
        }
    }

    public static boolean isIsTableVisible() {
        return isTableVisible;
    }

    public static List<Lesson> getScheduledLessonsForDay(int index) {

        return scheduledLessons.get(index);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTableVisible = false;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
