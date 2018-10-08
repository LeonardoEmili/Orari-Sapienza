package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

public class DevActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyTheme(DevActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devs);
        initActivity();
    }

    private void initActivity() {

        AppUtils.setLocale(DevActivity.this);

        LinearLayout mAdsContainer = findViewById(R.id.ads_container);
        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-9817701892167034/5245950144");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.authors));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
