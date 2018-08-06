package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sterbsociety.orarisapienza.MailTask;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import static com.sterbsociety.orarisapienza.utils.AppUtils.hideKeyboard;

public class FeedbackActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private EditText userText, userName, userEmail;
    private static final String optionalValue = "Not provided";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyTheme(FeedbackActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initActivity();
    }

    private void initActivity() {

        AppUtils.setLocale(FeedbackActivity.this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.send_us_feedback));
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final LinearLayout linearLayout = findViewById(R.id.main_wrapper);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.requestFocus();
            }
        });

        userText = findViewById(R.id.user_text);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);

        View.OnFocusChangeListener mListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                    hideKeyboard(FeedbackActivity.this, view);
            }
        };

        userText.setOnFocusChangeListener(mListener);
        userName.setOnFocusChangeListener(mListener);
        userEmail.setOnFocusChangeListener(mListener);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_send_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.send_mail:
                sendFeedback();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendFeedback() {

        if (userText.getText().toString().trim().length() == 0) {
            userText.requestFocus();
            userText.setError(null);
            userText.setError(getString(R.string.write_here_feedback));
        } else {

            String subject = getString(R.string.feedback);
            String body = userText.getText().toString().replaceAll("^\\s+","");
            // The regex above is for left trim.

            String username = !userName.getText().toString().trim().equals("") ? userName.getText().toString() : optionalValue;
            String emailAddress = !userEmail.getText().toString().trim().equals("") ? userEmail.getText().toString().trim() : optionalValue;
            String progressBarTitle = FeedbackActivity.this.getResources().getString(R.string.sending_feedback);
            String progressBarMessage = FeedbackActivity.this.getResources().getString(R.string.please_wait);
            String positiveMsg = FeedbackActivity.this.getResources().getString(R.string.feedback_sent);
            String negativeMsg = FeedbackActivity.this.getResources().getString(R.string.feedback_not_sent);
            new MailTask(FeedbackActivity.this, progressBarTitle, progressBarMessage, positiveMsg, negativeMsg)
                    .execute(subject, body, username, emailAddress);
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {

        if ((view == userText || view == userName || view == userEmail) && !b)
            hideKeyboard(FeedbackActivity.this, view);
    }
}
