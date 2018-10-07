package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.TimeLineAdapter;
import com.sterbsociety.orarisapienza.models.StudyPlan;
import com.sterbsociety.orarisapienza.models.StudyPlanPresenter;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.sterbsociety.orarisapienza.models.StudyPlanBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.sterbsociety.orarisapienza.utils.AppUtils.applyThemeNoActionBar;
import static com.sterbsociety.orarisapienza.utils.AppUtils.saveStudyPlan;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;

public class StudyPlanActivity extends AppCompatActivity {

    private StudyPlan studyPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applyThemeNoActionBar(StudyPlanActivity.this);
        setLocale(StudyPlanActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);
        initActivity();
    }

    private void initActivity() {

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
        final StudyPlanPresenter studyPlanPresenter = i.getParcelableExtra(AppUtils.DEFAULT_KEY);

        // Here we should retrieve the real data by elaborating data in the studyPlanPresenter;
        createStudyPlan(studyPlanPresenter);

        final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(studyPlan.getDataList());
        mRecyclerView.setAdapter(mTimeLineAdapter);

        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-3940256099942544/6300978111");
    }

    private void createStudyPlan(StudyPlanPresenter studyPlanPresenter) {

        studyPlan = new StudyPlan();
        final StudyPlanBuilder spBuilder = new StudyPlanBuilder(AppUtils.getBuildingList(), AppUtils.MATRIX, studyPlanPresenter.getBuilding());
        final List<TimeLineModel> dataList = new ArrayList<>();
        int start, end;
        start = AppUtils.timeToInt(studyPlanPresenter.getHours()[0], AppUtils.dayToInt(studyPlanPresenter.getDays()[0]));
        end = AppUtils.timeToInt(studyPlanPresenter.getHours()[1], AppUtils.dayToInt(studyPlanPresenter.getDays()[1]));
        if (start == end) {
            end += 16;
        }
        spBuilder.createProgramInt(start, end);
        for (String[] s : spBuilder.getProgram()) {
            if (!TextUtils.isEmpty(s[3])) {
                dataList.add(new TimeLineModel(s[0] + s[1], s[0] + s[2], AppUtils.getClassroom(s[3])));
            }
        }
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
            StyleableToast.makeText(this,
                    getResources().getString(R.string.study_plan_created),
                    Toast.LENGTH_LONG, R.style.successToast).show();
            finish();
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }
}
