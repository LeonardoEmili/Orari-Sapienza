package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.TimeLineAdapter;
import com.sterbsociety.orarisapienza.models.StudyPlan;
import com.sterbsociety.orarisapienza.models.StudyPlanPresenter;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import static com.sterbsociety.orarisapienza.utils.AppUtils.getClassroomList;
import static com.sterbsociety.orarisapienza.utils.AppUtils.saveStudyPlan;

public class StudyPlanActivity extends AppCompatActivity {

    private StudyPlan studyPlan;

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
        final StudyPlanPresenter studyPlanPresenter= i.getParcelableExtra(AppUtils.DEFAULT_KEY);

        // Here we should retrieve the real data by elaborating data in the studyPlanPresenter;
        setFakeData(studyPlanPresenter);

        final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(studyPlan.getDataList());
        mRecyclerView.setAdapter(mTimeLineAdapter);

        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-3940256099942544/6300978111");
    }

    private void setFakeData(StudyPlanPresenter studyPlanPresenter) {

        // Each TimeLine model should have a date and a classroom
        studyPlan = new StudyPlan();
        List<TimeLineModel> dataList = new ArrayList<>();
        dataList.add(new TimeLineModel("07:00", "07:30", getClassroomList().get(0)));
        dataList.add(new TimeLineModel("08:00", "10:20", getClassroomList().get(3)));
        dataList.add(new TimeLineModel("11:30", "12:00", getClassroomList().get(6)));
        dataList.add(new TimeLineModel("12:10", "12:20", getClassroomList().get(9)));
        dataList.add(new TimeLineModel("12:30", "14:20", getClassroomList().get(12)));
        dataList.add(new TimeLineModel("14:30", "15:30", getClassroomList().get(15)));
        dataList.add(new TimeLineModel("16:00", "17:00", getClassroomList().get(18)));
        dataList.add(new TimeLineModel("17:30", "18:20", getClassroomList().get(21)));
        dataList.add(new TimeLineModel("19:00", "20:00", getClassroomList().get(24)));
        studyPlan.setDataList(dataList);
        studyPlan.setRequestDates(studyPlanPresenter.getStartDate(), studyPlanPresenter.getEndDate());
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
                Gson gson = new GsonBuilder().create();
                saveStudyPlan(this, gson.toJson(studyPlan));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void goBackToMainActivity(boolean outcome) {
        Intent returnIntent = new Intent();
        if (outcome) {
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }
}
