package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.SearchViewAdapter;
import com.sterbsociety.orarisapienza.adapters.WeekDayFragmentPagerAdapter;
import com.sterbsociety.orarisapienza.fragments.WeekDayFragment;
import com.sterbsociety.orarisapienza.models.Course;
import com.sterbsociety.orarisapienza.models.Lesson;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.sterbsociety.orarisapienza.utils.AppUtils.applyThemeNoActionBar;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getClassroomName;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getDayByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getHourByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setToolbarColor;

public class LessonTimetableActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private static boolean isTableVisible;
    private static List<List<Lesson>> scheduledLessons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applyThemeNoActionBar(LessonTimetableActivity.this);
        setLocale(LessonTimetableActivity.this);
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

        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        final SearchViewAdapter searchViewAdapter = new SearchViewAdapter(this, AppUtils.getCoursesList());
        searchView.setAdapter(searchViewAdapter);
        searchView.setOnItemClickListener((adapterView, view, position, id) -> {

            Course course = (Course) adapterView.getItemAtPosition(position);
            AppUtils.addCourseToFavourites(LessonTimetableActivity.this, course, searchViewAdapter, position);

            readDataFromDB(course);
            displayTableView();

            searchView.closeSearch();
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(getString(R.string.course_code) + ": " + course.getId());
        });
    }

    /**
     * @param course is the course selected by the user
     *               Inside here is the logic of retrieving correct course details such
     *               as lessons and timetables from DB.
     */
    private void readDataFromDB(Course course) {
        scheduledLessons = new ArrayList<>();
        scheduledLessons.add(new ArrayList<>());    // Monday
        scheduledLessons.add(new ArrayList<>());    // Tuesday
        scheduledLessons.add(new ArrayList<>());    // Wednesday
        scheduledLessons.add(new ArrayList<>());    // Thursday
        scheduledLessons.add(new ArrayList<>());    // Friday

        final HashMap<String, Integer> map = AppUtils.TIMETABLES.get(course.getCourseKey());
        for (String classroomCode : map.keySet()) {

            // Foreach lesson inside this course
            int scrollIndex = map.get(classroomCode);   // We go ahead with this index until we find 0
            final List<Integer> lessonList = AppUtils.MATRIX.get(classroomCode);  // List with integers
            final int lessonIndex = lessonList.get(scrollIndex);    // This is the index for the lesson
            final String[] lessonParts = AppUtils.LESSON_LIST.get(lessonIndex).split("_");  // We retrieve lesson's info
            final String day = getDayByIndex(lessonIndex);
            final String startLesson = getHourByIndex(scrollIndex);
            while (scrollIndex != 785 && lessonList.get(scrollIndex) == lessonIndex) {
                scrollIndex++;
            }
            scrollIndex--;
            scheduledLessons.get(scrollIndex / 157).add(new Lesson(getClassroomName(classroomCode), course.getId(), course.getName(), day, getHourByIndex(scrollIndex), lessonParts[4], startLesson, lessonParts[0], lessonParts[3]));
        }
    }

    private void displayTableView() {
        // The lines below show the TableView and hides the welcome text.
        isTableVisible = true;

        final List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
