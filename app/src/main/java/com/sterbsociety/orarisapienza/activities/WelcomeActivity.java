package com.sterbsociety.orarisapienza.activities;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.SliderAdapter;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.sterbsociety.orarisapienza.utils.NetworkStatus;

import static com.sterbsociety.orarisapienza.utils.AppUtils.KEY_VERSION;
import static com.sterbsociety.orarisapienza.utils.AppUtils.areUpdatesAllowed;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isCurrentDatabaseOutDated;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isDBAvailable;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isFirstTimeStartApp;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isOfflineDBOutdated;
import static com.sterbsociety.orarisapienza.utils.AppUtils.loadSettings;
import static com.sterbsociety.orarisapienza.utils.AppUtils.moveDatabaseFromRawToInternalStorage;
import static com.sterbsociety.orarisapienza.utils.AppUtils.parseDatabase;
import static com.sterbsociety.orarisapienza.utils.AppUtils.parseRawDB;
import static com.sterbsociety.orarisapienza.utils.AppUtils.saveDatabase;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private Button mButton;
    private boolean databaseExists, isButtonClicked, isFirstTimeStartApp;
    private DatabaseReference onlineDatabase;
    private DataSnapshot currentDataSnapshot;
    private FirebaseAuth mAuth;
    private volatile boolean isAuthenticated, dbStatusFlag;
    private Handler mHandler;
    private Runnable mRunnable;
    private AlertDialog mProgressDialog;
    private ValueEventListener versionControlListener, databaseDownloadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initBaseActivity();
        databaseExists = isDBAvailable(this);
        if (isFirstTimeStartApp = isFirstTimeStartApp() || !databaseExists) {
            setTheme(R.style.AppTheme_NoActionBar);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_welcome);
            initWelcomeActivity();
        } else {
            super.onCreate(savedInstanceState);
            if (areUpdatesAllowed() && NetworkStatus.getInstance().isOnline(this)) {
                authUser();
            } else {
                startMainActivity();
            }
        }
    }

    /**
     * This utility method is responsible for the basic setup of this activity.
     */
    private void initBaseActivity() {

        loadSettings(this);
        setLocale(this);

        onlineDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(getResources().getString(R.string.updating))
                .build();

        versionControlListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (isCurrentDatabaseOutDated(dataSnapshot)) {
                        onlineDatabase.child(KEY_VERSION).removeEventListener(versionControlListener);
                        databaseExists = false;
                        mProgressDialog.show();
                        if (!NetworkStatus.getInstance().isOnline(WelcomeActivity.this)) {
                            mProgressDialog.setMessage(getResources().getString(R.string.updating_turn_on_connection));
                        }
                        updateDatabase();
                    } else {
                        startMainActivity();
                    }
                } catch (Exception ex) {
                    // If error is thrown then db is always updated
                    ex.printStackTrace();
                    onlineDatabase.child(KEY_VERSION).removeEventListener(versionControlListener);
                    databaseExists = false;
                    mProgressDialog.show();
                    updateDatabase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };

        databaseDownloadListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDataSnapshot = dataSnapshot;
                dbStatusFlag = true;
                AsyncTask.execute(() -> {
                    saveDatabase(WelcomeActivity.this, currentDataSnapshot);
                    parseDatabase(WelcomeActivity.this);
                });
                onlineDatabase.removeEventListener(databaseDownloadListener);
                databaseExists = true;
                mProgressDialog.dismiss();
                if (!isFirstTimeStartApp) {
                    startMainActivity();
                } else if (isButtonClicked) {
                    startMainActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                onlineDatabase.removeEventListener(databaseDownloadListener);
            }
        };
    }

    private void firstBootUserAuth() {
        mRunnable = () -> {
            if (!isAuthenticated) {
                getAuthentication();
                mHandler.postDelayed(mRunnable, 1000);
            } else {
                mHandler.removeCallbacks(mRunnable);
                onlineDatabase.child(KEY_VERSION).addValueEventListener(versionControlListener);
            }
        };

        if (mAuth.getCurrentUser() == null) {
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 0);
        } else {
            // This only happens if user has logged in in previous sessions.
            isAuthenticated = true;
            onlineDatabase.child(KEY_VERSION).addValueEventListener(versionControlListener);
        }
    }

    /**
     * Data stored in a Firebase Realtime Database is retrieved by attaching an
     * asynchronous listener to a database reference.
     * Visit DOC at: https://firebase.google.com/docs/database/admin/retrieve-data
     */
    private void updateDatabase() {
        if (isFirstTimeStartApp) {
            return;
        }
        if (databaseExists) {
            onlineDatabase.child(KEY_VERSION).addValueEventListener(versionControlListener);
        } else {
            onlineDatabase.addValueEventListener(databaseDownloadListener);
        }
    }

    /**
     * This method allows the user to login into the DB.
     */
    private void getAuthentication() {
        if (NetworkStatus.getInstance().isOnline(this)) {
            mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    isAuthenticated = true;
                }
            });
        }
    }

    /**
     * Useful method used to define mRunnable which is a task that runs under UI thread
     * and does a login-attempt every 2 sec until the user is authenticated.
     */
    private void authUser() {
        mRunnable = () -> {
            if (!isAuthenticated) {
                getAuthentication();
                mHandler.postDelayed(mRunnable, 1000);
            } else {
                mHandler.removeCallbacks(mRunnable);
                updateDatabase();
            }
        };

        if (mAuth.getCurrentUser() == null) {
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 0);
        } else {
            // This only happens if user has logged in in previous sessions.
            isAuthenticated = true;
            updateDatabase();
        }
    }

    /**
     * This method initializes the WelcomeActivity,
     */
    private void initWelcomeActivity() {

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        //AsyncTask.execute(() -> AppUtils.parseRawDB(this));
        AppUtils.parseRawDB(this);

        final ViewPager mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);
        mButton = findViewById(R.id.skip_button);

        sliderAdapter = new SliderAdapter(WelcomeActivity.this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);

        versionControlListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseExists = true;
                boolean isRawDBOutdated;
                try {
                    if (isRawDBOutdated = isOfflineDBOutdated(WelcomeActivity.this, dataSnapshot)) {
                        onlineDatabase.child(KEY_VERSION).removeEventListener(versionControlListener);
                        System.out.println("outdated");
                        onlineDatabase.addValueEventListener(databaseDownloadListener);
                    } else {
                        dbStatusFlag = true;
                        AsyncTask.execute(() -> {
                            moveDatabaseFromRawToInternalStorage(WelcomeActivity.this);
                            parseDatabase(WelcomeActivity.this);
                        });
                        //AsyncTask.execute(() -> moveDatabaseFromRawToInternalStorage(WelcomeActivity.this));
                    }
                    if (isRawDBOutdated) {
                        mProgressDialog.show();
                    } else if (isButtonClicked) {
                        startMainActivity();
                    }
                } catch (Exception ex) {
                    // If error is thrown then db is always updated
                    ex.printStackTrace();
                    onlineDatabase.child(KEY_VERSION).removeEventListener(versionControlListener);
                    onlineDatabase.addValueEventListener(databaseDownloadListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };

        if (NetworkStatus.getInstance().isOnline(this)) {
            firstBootUserAuth();
        } else {
            dbStatusFlag = true;
            AsyncTask.execute(() -> {
                moveDatabaseFromRawToInternalStorage(WelcomeActivity.this);
                parseDatabase(WelcomeActivity.this);
            });
            //AsyncTask.execute(() -> moveDatabaseFromRawToInternalStorage(this));
            databaseExists = true;
        }

        ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Nothing to do here.
            }

            @Override
            public void onPageSelected(int position) {

                addDotsIndicator(position);
                if (position == sliderAdapter.getCount() - 1)
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

        final TextView[] mDots = new TextView[sliderAdapter.getCount()];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {

            mDots[i] = new TextView(this);
            TextView current = mDots[i];
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i == position) {
                current.setText(Html.fromHtml("&#9670"));
                current.setTextSize(28);
                current.setTextColor(getResources().getColor(R.color.colorSecondary));
                params.setMargins(2, 0, 2, 4);
                current.setLayoutParams(params);

            } else {
                current.setText(Html.fromHtml("&#8226"));
                current.setTextSize(48);
                current.setTextColor(getResources().getColor(R.color.gray));
                params.setMargins(2, 0, 2, 0);
                current.setLayoutParams(params);
            }

            mDotLayout.addView(current);
        }
    }

    private void startMainActivity() {
        AppUtils.setFirstTimeStartApp(this);
        if (!dbStatusFlag) {
            // This means that the entire parsing action has been handled before.
            AsyncTask.execute(() -> parseDatabase(this));
        }
        mProgressDialog.dismiss();
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void startMainActivity(View v) {
        isButtonClicked = true;
        if (databaseExists) {
            startMainActivity();
        } else {
            if (!NetworkStatus.getInstance().isOnline(this)) {
                mProgressDialog.setMessage(getResources().getString(R.string.updating_turn_on_connection));
            }
            mProgressDialog.show();
        }
    }
}