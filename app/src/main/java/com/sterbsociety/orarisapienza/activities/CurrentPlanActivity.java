package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.TimeLineAdapter;
import com.sterbsociety.orarisapienza.models.StudyPlan;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.sterbsociety.orarisapienza.utils.AppUtils.getFullDateFormatter;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getStringByLocal;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isThereAnActiveStudyPlan;
import static com.sterbsociety.orarisapienza.utils.AppUtils.sendSilentReport;


public class CurrentPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(CurrentPlanActivity.this);
        AppUtils.setLocale(CurrentPlanActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_plan);
        initActivity();

    }

    private void initActivity() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LinearLayout mAdsContainer = findViewById(R.id.ad_container);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.active_study_plan);
        }
        final LinearLayout alterNativeLayout = findViewById(R.id.study_plan_layout_wrapper);

        if (!isThereAnActiveStudyPlan()) {
            CoordinatorLayout studyListLayout = findViewById(R.id.study_list_wrapper);
            studyListLayout.setVisibility(View.GONE);
            ImageView imageView = alterNativeLayout.findViewById(R.id.img_view);
            Glide.with(this).asGif().load(R.drawable.storm_trooper).into(imageView);


        } else {
            alterNativeLayout.setVisibility(View.GONE);
            final StudyPlan studyPlan = AppUtils.getStudyPlan();
            final RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            final List<TimeLineModel> mDataList = studyPlan.getDataList();
            int currentClassPosition = getCurrentTimeLineIndex(mDataList);

            if (currentClassPosition != -1) {
                layoutManager.scrollToPosition(currentClassPosition);
            }

            mRecyclerView.smoothScrollToPosition(currentClassPosition);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);

            final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(mDataList, currentClassPosition);
            mRecyclerView.setAdapter(mTimeLineAdapter);

            AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-9817701892167034/4612235766");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isThereAnActiveStudyPlan()) {
            getMenuInflater().inflate(R.menu.menu_current_plan, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_plan:
                new AlertDialog.Builder(this)
                        .setTitle(getStringByLocal(this, R.string.delete_study_plan))
                        .setMessage(getStringByLocal(this, R.string.delete_study_plan_hint))
                        .setCancelable(false)
                        .setPositiveButton(getStringByLocal(this, R.string.yes), (dialog, id) -> {
                            AppUtils.clearCachedStudyPlan(CurrentPlanActivity.this);
                            StyleableToast.makeText(this,
                                    getResources().getString(R.string.study_plan_deleted),
                                    Toast.LENGTH_LONG, R.style.successToast).show();
                            finish();
                        })
                        .setNegativeButton(getStringByLocal(this, R.string._no), null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private int getCurrentTimeLineIndex(List<TimeLineModel> dataList) {
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return 0;
        }
        try {
            final SimpleDateFormat simpleDateFormat = getFullDateFormatter();
            final Date currentDate = new Date();
            for (int i = 0; i < dataList.size(); i++) {
                final TimeLineModel lineModel = dataList.get(i);
                System.out.println(currentDate);
                if (currentDate.before(simpleDateFormat.parse(lineModel.getEndDate()))) {
                    return i;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sendSilentReport(this, 76, ex, CurrentPlanActivity.class.getSimpleName());
        }
        return dataList.size() - 1;
    }
}