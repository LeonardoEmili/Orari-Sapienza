package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.models.StudyPlan;
import com.sterbsociety.orarisapienza.utils.AppUtils;

public class CurrentPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyTheme(CurrentPlanActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_plan);
        initActivity();

    }

    private void initActivity() {

        AppUtils.setLocale(CurrentPlanActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        if (AppUtils.isThereAnActiveStudyPlan()) {
            final StudyPlan studyPlan = AppUtils.getStudyPlan();

        } else {

        }
    }
}
