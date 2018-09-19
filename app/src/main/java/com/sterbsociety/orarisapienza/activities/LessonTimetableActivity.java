package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.sterbsociety.orarisapienza.utils.AppUtils.addCourseToFavourites;
import static com.sterbsociety.orarisapienza.utils.AppUtils.applyThemeNoActionBar;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getClassroomName;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getDayByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getHourByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getStringByLocal;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isTableVisible;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setToolbarColor;

public class LessonTimetableActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private static List<List<Lesson>> scheduledLessons;
    private Set<String> courseTypologies;
    private String selectedType;

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
            addCourseToFavourites(LessonTimetableActivity.this, course, searchViewAdapter, position);
            readCourseDataFromDatabase(course);
            askUserWhichCourseToDisplay();
            searchView.closeSearch();
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(getString(R.string.course_code) + ": " + course.getId());
        });
    }

    /**
     * This method asks the user which course to show.
     */
    private void askUserWhichCourseToDisplay() {
        if (courseTypologies.size() > 1) {
            int tmpIndex = 0;
            final String[] listEntries = new String[courseTypologies.size()];
            final String[] listTypes = new String[courseTypologies.size()];

            for (String courseType : courseTypologies) {
                final String[] courseParts = courseType.split("#");
                final String year = AppUtils.getLiteralYearByNumber(this, courseParts[0]);
                listEntries[tmpIndex] = year + getStringByLocal(this, R.string.channel) + courseParts[1];
                listTypes[tmpIndex] = courseType;
                tmpIndex++;
            }

            // This string holds info about courseYear#channel
            selectedType = listTypes[0];

            new AlertDialog.Builder(this)
                    .setTitle(R.string.select_course)
                    .setSingleChoiceItems(listEntries, 0, (dialogInterface, which) -> {
                        selectedType = listTypes[which];
                    })
                    .setCancelable(false)
                    .setPositiveButton(AppUtils.getStringByLocal(this, R.string.yes), (dialog, index) -> {
                        final String[] courseParts = selectedType.split("#");
                        final String year = courseParts[0];
                        final String channel = courseParts[1];
                        for (List<Lesson> dailySchedule : scheduledLessons) {
                            Iterator<Lesson> iterator = dailySchedule.iterator();
                            while (iterator.hasNext()) {
                                Lesson lesson = iterator.next();
                                if (!lesson.getYear().equals(year) || !lesson.getChannel().equals(channel)) {
                                    // If a lesson is from another 'course type' it is not displayed
                                    iterator.remove();
                                }
                            }
                        }
                        displayTableView();
                    })
                    .show();
        } else {
            // If there is just one type of course it will be directly showed to the user.
            displayTableView();
        }
    }

    /**
     * @param course is the course selected by the user
     *               Inside here is the logic of retrieving correct course details such
     *               as lessons and timetables from DB.
     */
    private void readCourseDataFromDatabase(Course course) {

        courseTypologies = new HashSet<>();
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
            if (lessonIndex == 0) {
                // Just to be sure that some lesson index is not 0, this would make the app crash due to IndexOutOfBoundException
                continue;
            }
            final String[] lessonParts = AppUtils.LESSON_LIST.get(lessonIndex).split("_");  // We retrieve lesson's info

            final String subjectName = lessonParts[2];
            final String year = lessonParts[3];
            final String professor = lessonParts[4];
            final String channel = lessonParts[5];

            courseTypologies.add(year + "#" + channel);

            final String day = getDayByIndex(lessonIndex);
            final String startLesson = getHourByIndex(scrollIndex);
            while (scrollIndex != 785 && lessonList.get(scrollIndex) == lessonIndex) {
                scrollIndex++;
            }
            scrollIndex--;
            scheduledLessons.get(scrollIndex / 157).add(new Lesson(getClassroomName(classroomCode), course.getId(), course.getName(), day, getHourByIndex(scrollIndex), professor, startLesson, subjectName, year, channel));
        }
    }

    /**
     * It cares about displaying data in the TableView (in each active fragment).
     */
    private void displayTableView() {
        // The lines below show the TableView and hides the welcome text.
        isTableVisible = true;

        final List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            final WeekDayFragment mFragment = (WeekDayFragment) fragment;
            mFragment.displayTableView();
        }
    }

    /**
     * @param index is the index of each fragment
     * @return lesson list to be displayed
     */
    public static List<Lesson> getScheduledLessonsForDay(int index) {
        return scheduledLessons.get(index);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            Toast.makeText(this, "aperta", Toast.LENGTH_SHORT).show();
            searchView.closeSearch();
        } else {
            isTableVisible = false;
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
