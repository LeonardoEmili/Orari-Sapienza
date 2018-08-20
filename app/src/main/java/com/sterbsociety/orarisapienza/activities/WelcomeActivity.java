package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapter.SliderAdapter;
import com.sterbsociety.orarisapienza.utils.AppUtils;

public class WelcomeActivity extends AppCompatActivity {

    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(!isFirstTimeStartApp())
            startMainActivity(null);

        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initActivity();
    }

    private void initActivity() {

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        ViewPager mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);
        mButton = findViewById(R.id.skip_button);

        sliderAdapter = new SliderAdapter(WelcomeActivity.this);

        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);

        ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Nothing to do here.
            }

            @Override
            public void onPageSelected(int position) {

                addDotsIndicator(position);
                if (position == sliderAdapter.getCount()-1)
                    mButton.setText(R.string.lets_start);
                else
                    mButton.setText(R.string.skip_tutorial);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Nothing to do here.
            }
        };

        mSlideViewPager.addOnPageChangeListener(viewListener);

        // From API 21 it's allowed, this will make the status bar transparent
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    public void addDotsIndicator(int position) {

        mDots = new TextView[sliderAdapter.getCount()];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length ; i++) {

            mDots[i] = new TextView(this);
            TextView current = mDots[i];
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i == position) {
                current.setText(Html.fromHtml("&#9670"));
                current.setTextSize(28);
                current.setTextColor(getResources().getColor(R.color.colorSecondary));
                params.setMargins(2,0,2,4);
                current.setLayoutParams(params);

            } else {
                current.setText(Html.fromHtml("&#8226"));
                current.setTextSize(48);
                current.setTextColor(getResources().getColor(R.color.gray));
                params.setMargins(2,0,2,0);
                current.setLayoutParams(params);
            }

            mDotLayout.addView(current);
        }
    }


    private boolean isFirstTimeStartApp() {
        SharedPreferences ref = getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE);
        return ref.getBoolean("FirstTimeStartFlag", true);
    }


    private void setFirstTimeStartStatus(boolean stt) {
        SharedPreferences ref = getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("FirstTimeStartFlag", stt);
        editor.apply();
    }


    public void startMainActivity(View v) {

        setFirstTimeStartStatus(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
