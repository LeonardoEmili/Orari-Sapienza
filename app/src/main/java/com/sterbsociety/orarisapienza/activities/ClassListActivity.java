package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

public class ClassListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(ClassListActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        initActivity();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    private void initActivity() {
        AppUtils.setLocale(ClassListActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.classrooms_list));
        }
    }
}
