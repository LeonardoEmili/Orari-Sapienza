package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import xyz.sahildave.widget.SearchViewLayout;

import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.fragments.SearchStaticListFragment;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import static com.sterbsociety.orarisapienza.utils.AppUtils.areFavouritesPresent;

public class LessonTimetableActivity extends AppCompatActivity {

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
        if (!areFavouritesPresent())
            menu.findItem(R.id.clear_history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clear_history:
                AppUtils.clearAllCoursesFromFavourites(LessonTimetableActivity.this);
                item.setVisible(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        }

        initSearchView(toolbar);
    }

    private void initSearchView(Toolbar toolbar) {

        final SearchViewLayout searchViewLayout = findViewById(R.id.search_view_container);
        final SearchStaticListFragment searchStaticListFragment = SearchStaticListFragment.newInstance(searchViewLayout);
        searchViewLayout.setExpandedContentSupportFragment(this, searchStaticListFragment);
        searchViewLayout.handleToolbarAnimation(toolbar);
        searchViewLayout.setCollapsedHint("Collapsed Hint");
        searchViewLayout.setExpandedHint("Expanded Hint");
        //searchViewLayout.setHint("Global Hint");

        searchViewLayout.setSearchListener(new SearchViewLayout.SearchListener() {
            @Override
            public void onFinished(String searchKeyword) {
                searchViewLayout.collapse();
                Snackbar.make(searchViewLayout, "Start Search for - " + searchKeyword, Snackbar.LENGTH_LONG).show();
            }
        });

        searchViewLayout.setSearchBoxListener(new SearchViewLayout.SearchBoxListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable query) {
                searchStaticListFragment.getAdapter().filterCoursesByQuery(query.toString());
            }
        });

        searchViewLayout.setSearchListener(new SearchViewLayout.SearchListener() {
            @Override
            public void onFinished(String searchKeyword) {
                searchViewLayout.collapse();
                Snackbar.make(searchViewLayout, "Search Done - " + searchKeyword, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
