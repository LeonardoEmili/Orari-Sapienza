package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

public class ClassListActivity extends AppCompatActivity {

    SearchView searchView;
    CoordinatorLayout mainWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(ClassListActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        initActivity();

        AppUtils.askForGPSPermission(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppUtils.GPS_ACCESS) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "NOT GRANTED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void initActivity() {
        AppUtils.setLocale(ClassListActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        searchView = findViewById(R.id.search_view);
        mainWrapper = findViewById(R.id.main_content);

        // This method handles click inside the layout
        AppUtils.setupUIElements(this, mainWrapper);
    }


    public void openFilterActivity(View view) {
        Intent i = new Intent(this, FilterActivity.class);
        startActivityForResult(i, 1);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
    }
}
