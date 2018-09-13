package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sterbsociety.orarisapienza.utils.ExpandCollapseAnimation;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class FaqActivity extends AppCompatActivity {

    private static final int FAQ_ENTRIES = 8;
    private static final int ANIMATION_DURATION = 200;
    private static final int COLLAPSE_ACTION = 1;
    private static final int EXPAND_ACTION = 0;
    private boolean[] flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyTheme(FaqActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        initActivity();
    }


    /**
     * This method allows to go back to MainActivity.
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    private void initActivity() {

        AppUtils.setLocale(FaqActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.faq));
        }

        LinearLayout mAdsContainer = findViewById(R.id.ad_container);

        // This avoids the user to break the layout
        flags = new boolean[FAQ_ENTRIES];

        // From here is code for setting up the Q&A list
        ArrayList<View> questionList = new ArrayList<>(Arrays.asList(findViewById(R.id.first_q), findViewById(R.id.second_q), findViewById(R.id.third_q),
                findViewById(R.id.fourth_q), findViewById(R.id.fifth_q), findViewById(R.id.sixth_q),
                findViewById(R.id.seventh_q), findViewById(R.id.eighth_q)));
        ArrayList<View> answerList =  new ArrayList<>(Arrays.asList(findViewById(R.id.first_a), findViewById(R.id.second_a), findViewById(R.id.third_a),
                findViewById(R.id.fourth_a), findViewById(R.id.fifth_a), findViewById(R.id.sixth_a),
                findViewById(R.id.seventh_a), findViewById(R.id.eighth_a)));

        for (int i = 0; i < questionList.size(); i++) {

            final View currentAnswer = answerList.get(i);
            AppUtils.setHeightForWrapContent(FaqActivity.this, currentAnswer);

            final int index = i;
            LinearLayout currentQuestion = (LinearLayout) questionList.get(i);
            final ImageView mImageView = (ImageView)currentQuestion.getChildAt(0);

            currentQuestion.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    // Only if previous animation has finished other can restart.
                    if (flags[index]) return;

                    flags[index] = true;

                    ExpandCollapseAnimation animation;
                    if (currentAnswer.getVisibility() == View.VISIBLE) {
                        mImageView.setImageResource(R.drawable.ic_show_more);
                        animation = new ExpandCollapseAnimation(currentAnswer, ANIMATION_DURATION, COLLAPSE_ACTION);
                    }
                    else {
                        mImageView.setImageResource(R.drawable.ic_show_less);
                        animation = new ExpandCollapseAnimation(currentAnswer, ANIMATION_DURATION, EXPAND_ACTION);
                    }

                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            // nothing to do here
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            flags[index] = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // nothing to do here
                        }
                    });

                    currentAnswer.startAnimation(animation);
                }
            });
            // End of Q&A list setup
        }

        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-3940256099942544/6300978111");
    }
}