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
import com.sterbsociety.orarisapienza.models.StudyPlanPresenter;
import com.sterbsociety.orarisapienza.models.TimeLineModel;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.sterbsociety.orarisapienza.utils.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class StudyPlanActivity extends AppCompatActivity {

    private  List<TimeLineModel> mDataList;

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
        StudyPlanPresenter presenter = i.getParcelableExtra(AppUtils.DEFAULT_KEY);

        // Here we should retrieve the real data
        setFakeData();

        // todo to remove this line and use the one below it
        final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(this, mDataList, 2);
        // final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(this, mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);

        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-3940256099942544/6300978111");
    }

    private void setFakeData() {
        mDataList = new ArrayList<>();
        mDataList.add(new TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE));
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "08:00, 02-12-2017", OrderStatus.ACTIVE));
        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "21:00, 02-11-2017", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item has been given to the courier", "18:00, 2017-02-11", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item is packed and will dispatch soon", "09:30, 2017-02-11", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order is being readied for dispatch", "08:00, 2017-02-11", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order processing initiated", "15:00, 2017-02-10", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order confirmed by seller", "14:30, 2017-02-10", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order placed successfully", "14:00, 2017-02-10", OrderStatus.COMPLETED));
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
