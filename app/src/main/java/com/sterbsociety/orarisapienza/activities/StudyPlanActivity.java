package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.TimeLineAdapter;
import com.sterbsociety.orarisapienza.models.Classroom;
import com.sterbsociety.orarisapienza.models.StudyPlanPresenter;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class StudyPlanActivity extends AppCompatActivity {

    private List<TimeLineModel> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(StudyPlanActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        initActivity();
    }

    private void initActivity() {

        AppUtils.setLocale(StudyPlanActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final LinearLayout mAdsContainer = findViewById(R.id.ad_container);
        final RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);

        // Here we get the user requests about where and when
        Intent i = getIntent();
        final StudyPlanPresenter presenter = i.getParcelableExtra(AppUtils.DEFAULT_KEY);

        // Here we should retrieve the real data
        setFakeData();

        // todo to remove this line and use the one below it in this Activity
        final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(mDataList, -1);
        // final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);

        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-3940256099942544/6300978111");
    }

    private void setFakeData() {

        // Each TimeLine model should have a date and a classroom
        mDataList = new ArrayList<>();
        mDataList.add(new TimeLineModel("07:00", "07:30", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("08:00", "10:20", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("11:30", "12:00", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("12:10", "12:20", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("12:30", "14:20", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("14:30", "15:30", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("16:00", "17:00", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("17:30", "18:20", new Classroom("P1", "42", 42)));
        mDataList.add(new TimeLineModel("19:00", "20:00", new Classroom("P1", "42", 42)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_study_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_it:
                // todo save study plan
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
