package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.SearchViewAdapter;
import com.sterbsociety.orarisapienza.adapters.WeekDayFragmentPagerAdapter;
import com.sterbsociety.orarisapienza.fragments.WeekDayFragment;
import com.sterbsociety.orarisapienza.models.Course;
import com.sterbsociety.orarisapienza.models.Lesson;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.sterbsociety.orarisapienza.utils.AppUtils.DAY_LENGTH;
import static com.sterbsociety.orarisapienza.utils.AppUtils.SPECIAL_COURSES;
import static com.sterbsociety.orarisapienza.utils.AppUtils.WEEK_LENGTH;
import static com.sterbsociety.orarisapienza.utils.AppUtils.addCourseToFavourites;
import static com.sterbsociety.orarisapienza.utils.AppUtils.applyThemeNoActionBar;
import static com.sterbsociety.orarisapienza.utils.AppUtils.doesPDFTableExist;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getClassroomName;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getDayByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getHourByIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getLiteralNumber;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getLiteralYearByNumber;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getStringByLocal;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isTableVisible;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setToolbarColor;

public class LessonTimetableActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private static List<List<Lesson>> scheduledLessons;
    private Set<String> courseTypologies;
    private String selectedType;
    private PDFDownloadTask pdfDownloadTask;

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
        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-9817701892167034/3277019084");
    }

    private void launchPDFDownloadTask(String courseName, String url) {
        pdfDownloadTask = new PDFDownloadTask(this);
        pdfDownloadTask.setListener(isDownloadCompleted -> {
            if (isDownloadCompleted) {
                openSpecialCourse(this, courseName);
            } else {
                StyleableToast.makeText(this, getStringByLocal(this, R.string.download_course_fail), Toast.LENGTH_LONG, R.style.errorToast).show();
            }
        });
        pdfDownloadTask.execute(this, courseName, url);
    }

    private void initSearchView() {

        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        final SearchViewAdapter searchViewAdapter = new SearchViewAdapter(this, AppUtils.getCoursesList());
        searchView.setAdapter(searchViewAdapter);

        searchView.setOnItemClickListener((adapterView, view, position, id) -> {

            final Course course = (Course) adapterView.getItemAtPosition(position);
            addCourseToFavourites(LessonTimetableActivity.this, course, searchViewAdapter, position);
            final String courseCode = course.getId();
            final HashMap<String, String> currentSpecialCourse;
            if ((currentSpecialCourse = SPECIAL_COURSES.get(course.getName() + "_" + course.getId())) != null) {
                // This means that this is a special course
                if (currentSpecialCourse.size() > 1) {
                    // The special course has more than 1 year|channel|semester
                    final ArrayList<String> tmpListEntries = new ArrayList<>();
                    final ArrayList<String> tmpListTypes = new ArrayList<>();
                    final String[] listEntries, listTypes;
                    int lastYear = -1;
                    for (String courseType : currentSpecialCourse.keySet()) {
                        final String[] courseParts = courseType.split("_");
                        final String year = getLiteralYearByNumber(this, courseParts[0]);
                        final int tmpYear;
                        final String value;
                        if (!courseParts[2].equals("0")) {
                            value = year + getLiteralNumber(this, courseParts[1]) + getStringByLocal(this, R.string.semester) + getStringByLocal(this, R.string.channel) + courseParts[2];
                        } else {
                            value = year + getLiteralNumber(this, courseParts[1]) + getStringByLocal(this, R.string.semester);
                        }
                        if ((tmpYear = Integer.parseInt(courseParts[0])) > lastYear) {
                            tmpListEntries.add(value);
                            tmpListTypes.add(courseType);
                        } else {
                            tmpListEntries.add(0, value);
                            tmpListTypes.add(0, courseType);
                        }
                        lastYear = tmpYear;
                    }
                    listEntries = tmpListEntries.toArray(new String[0]);
                    listTypes = tmpListTypes.toArray(new String[0]);
                    selectedType = listTypes[0];
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.select_course)
                            .setSingleChoiceItems(listEntries, 0, (dialogInterface, which) -> {
                                selectedType = listTypes[which];
                            })
                            .setCancelable(false)
                            .setPositiveButton(AppUtils.getStringByLocal(this, R.string.ok), (dialog, index) -> {
                                if (doesPDFTableExist(this, courseCode + selectedType)) {
                                    openSpecialCourse(this, courseCode + selectedType);
                                } else {
                                    launchPDFDownloadTask(courseCode + selectedType, currentSpecialCourse.get(selectedType));
                                }
                            })
                            .show();
                } else {
                    // If there is just one type of course, just check if its existence
                    if (doesPDFTableExist(this, courseCode)) {
                        openSpecialCourse(this, courseCode);
                    } else {
                        launchPDFDownloadTask(courseCode, currentSpecialCourse.get(currentSpecialCourse.keySet().toArray(new String[0])[0]));
                    }
                }
            } else {
                // It's a normal course case
                readCourseDataFromDatabase(course);
                displayRightCourse();
            }
            searchView.closeSearch();
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(getString(R.string.course_code) + ": " + courseCode);
        });
    }

    private void openSpecialCourse(Activity activity, String courseCode) {
        try {
            final File pdfFile = new File(activity.getFilesDir().getAbsolutePath(), courseCode + ".pdf");
            final Uri path = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".my.package.name.provider", pdfFile);
            final Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            StyleableToast.makeText(activity, getString(R.string.no_app_for_pdf_error), Toast.LENGTH_LONG, R.style.errorToast).show();
        }
    }

    /**
     * This method asks the user which course to show, and then displays it.
     */
    private void displayRightCourse() {

        if (courseTypologies.size() > 1) {
            final ArrayList<String> tmpListEntries = new ArrayList<>();
            final ArrayList<String> tmpListTypes = new ArrayList<>();
            final String[] listEntries, listTypes;
            int lastYear = -1;
            for (String courseType : courseTypologies) {
                final int tmpYear;
                final String[] courseParts = courseType.split("#");
                final String year = getLiteralYearByNumber(this, courseParts[0]);
                if ((tmpYear = Character.getNumericValue(courseParts[0].charAt(0))) > lastYear) {
                    tmpListEntries.add(year + getStringByLocal(this, R.string.channel) + courseParts[1]);
                    tmpListTypes.add(courseType);
                } else {
                    tmpListEntries.add(0, year + getStringByLocal(this, R.string.channel) + courseParts[1]);
                    tmpListTypes.add(0, courseType);
                }
                lastYear = tmpYear;
            }
            listEntries = tmpListEntries.toArray(new String[0]);
            listTypes = tmpListTypes.toArray(new String[0]);
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
        scheduledLessons.add(new ArrayList<>());    // Saturday

        final HashMap<String, List<Integer>> map = AppUtils.TIMETABLES.get(course.getCourseKey());
        for (String classroomCode : map.keySet()) {
            for (int scrollIndex : map.get(classroomCode)) {
                // Foreach lesson inside this course
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
                final String startLesson = getHourByIndex(scrollIndex + 1);
                while (scrollIndex != WEEK_LENGTH && lessonList.get(scrollIndex) == lessonIndex) {
                    scrollIndex++;
                }
                if (scrollIndex >= WEEK_LENGTH) {
                    scrollIndex = WEEK_LENGTH - 1;
                }
                scheduledLessons.get(scrollIndex / DAY_LENGTH).add(new Lesson(getClassroomName(classroomCode), course.getId(), course.getName(), day, getHourByIndex(scrollIndex + 1), professor, startLesson, subjectName, year, channel));
            }
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
    protected void onDestroy() {
        // Prevent leak after activity is destroyed
        if (pdfDownloadTask != null) {
            pdfDownloadTask.setListener(null);
        }
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
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

    /**
     * Here we go a static AsyncTask to avoid any memory leak.
     */
    static class PDFDownloadTask extends AsyncTask<Object, Void, Boolean> {

        private AsyncTaskListener listener;
        private ProgressDialog mProgressDialog;

        PDFDownloadTask(Activity activity) {
            mProgressDialog = ProgressDialog.show(activity, getStringByLocal(activity, R.string.download_course_title),
                    getStringByLocal(activity, R.string.download_course_desc), true);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                final Activity activity = (Activity) params[0];
                final String courseCode = (String) params[1];
                final String fileUrl = (String) params[2];
                HttpURLConnection c = (HttpURLConnection) new URL(fileUrl).openConnection();
                c.connect();
                final FileOutputStream fileOutputStream = activity.openFileOutput(courseCode + ".pdf", Context.MODE_PRIVATE);
                final InputStream in = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1;
                while ((len1 = in.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len1);
                }
                fileOutputStream.close();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean outcome) {
            super.onPostExecute(outcome);
            if (listener != null) {
                listener.onTaskFinished(outcome);
            }
            mProgressDialog.dismiss();
        }

        void setListener(AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onTaskFinished(boolean value);
        }
    }
}
