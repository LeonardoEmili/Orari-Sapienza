package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.model.StudyPlanPresenter;

public class StudyPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        Intent i = getIntent();
        StudyPlanPresenter presenter = i.getParcelableExtra("KEY");
    }
}
