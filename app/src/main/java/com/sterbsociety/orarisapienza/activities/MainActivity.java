package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.sterbsociety.orarisapienza.fragments.ChangeFragmentListener;
import com.sterbsociety.orarisapienza.fragments.ContactFragment;
import com.sterbsociety.orarisapienza.fragments.HomeFragment;
import com.sterbsociety.orarisapienza.R;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sterbsociety.orarisapienza.DatabaseHelper;
import com.sterbsociety.orarisapienza.utils.NetworkStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import net.sqlcipher.database.SQLiteDatabase;


public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener, ChangeFragmentListener {

    private static final String TAG = "MainActivity";
    private DatabaseReference onlineDatabase;
    private DataSnapshot currentDataSnapshot;
    private Handler mHandler;
    private Runnable mRunnable;
    private FirebaseAuth mAuth;
    private volatile boolean isAuthenticated;
    private HomeFragment homeFragment;
    private ContactFragment contactFragment;
    private FragmentTransaction fragmentTransaction;
    private boolean mStartTheme;
    public Intent mStartIntent;
    private LinearLayout mAdsContainer;
    private AdView mAdView;
    private AdRequest mAdRequest;
    public ImageView favouritesImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.loadSettings(MainActivity.this);

        AppUtils.applyThemeNoActionBar(MainActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActivity();
        SQLiteDatabase.loadLibs(MainActivity.this);
        onlineDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        addCallbacks();
        callAuthTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefresh() {
        if (currentDataSnapshot == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //mPullToRefreshView.setRefreshing(false);
                    StyleableToast.makeText(MainActivity.this, "I dati non possono essere recuperati al momento",
                            Toast.LENGTH_LONG, R.style.errorToast).show();
                }
            }, 500);
        } else {
            boolean outcome = DatabaseHelper.getInstance(MainActivity.this).createDB(MainActivity.this, currentDataSnapshot);
//            mPullToRefreshView.setRefreshing(false);
            if (outcome)
                StyleableToast.makeText(MainActivity.this, "Dati aule aggiornati!",
                        Toast.LENGTH_LONG, R.style.successToast).show();
            else
                StyleableToast.makeText(MainActivity.this, "Errore aggiornamento dati",
                        Toast.LENGTH_LONG, R.style.successToast).show();
        }
    }


    /**
     * This method allows the user to login into the DB.
     */
    private void getAuthentication() {

        if (NetworkStatus.getInstance().isOnline(this)) {
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                        isAuthenticated = true;
                }
            });
        }
    }


    /**
     * Useful method used to create repetitive task using Handler.
     */
    private void callAuthTask() {

        if (mAuth.getCurrentUser() == null) {
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 1000);
        } else {
            // This happens if user has logged in in previous sessions.
            isAuthenticated = true;
            updateDataSnapshot();
        }
    }

    /**
     * Data stored in a Firebase Realtime Database is retrieved by attaching an
     * asynchronous listener to a database reference.
     * Visit DOC at: https://firebase.google.com/docs/database/admin/retrieve-data
     */
    private void updateDataSnapshot() {

        onlineDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentDataSnapshot = dataSnapshot;
                if (!MainActivity.this.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists() &&
                        AppUtils.areUpdatesAllowed()) {
                    DatabaseHelper.getInstance(MainActivity.this).createDB(MainActivity.this, currentDataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppUtils.isRebootScheduled()) {
            AppUtils.reboot(MainActivity.this, mStartIntent);
        } else if (!mStartTheme == AppUtils.isDarkTheme()) {
            mStartTheme = AppUtils.isDarkTheme();
            AppUtils.reboot(MainActivity.this, mStartIntent);
        }
        mutateFavouriteImg();

    }

    /**
     * This method is used to set the Toolbar and the buttons in the Activity.
     */
    private void initActivity() {

        //mPullToRefreshView = findViewById(R.id.pull_to_refresh);
        //mPullToRefreshView.setOnRefreshListener(MainActivity.this);

        AppUtils.setLocale(MainActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        mStartIntent = getIntent();
        mStartTheme = AppUtils.isDarkTheme();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_previous);

        favouritesImg = findViewById(R.id.show_favourites);
        mAdsContainer = findViewById(R.id.ad_container);
        homeFragment = HomeFragment.newInstance(MainActivity.this);
        contactFragment = ContactFragment.newInstance(actionBar);
        homeFragment.setChangeFragmentListener(this);
        contactFragment.setChangeFragmentListener(this);
        showHomeFragment();

        // AdMob App ID: ca-app-pub-9817701892167034~2496155654
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = AppUtils.hash(androidId).toUpperCase();

        mAdView = new AdView(getApplicationContext());
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(deviceId)
                .build();
        mAdView.loadAd(mAdRequest);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mAdsContainer.addView(mAdView, params);

        Log.d("Distance", AppUtils.distance(41.904472, 12.512889, 41.902917, 12.511694) + "");
    }

    /**
     * This method is responsible for returning back to HomeFragment.
     */
    @Override
    public boolean onSupportNavigateUp() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (currentFragment != null && !currentFragment.isVisible()) {
            showHomeFragment();
            if (AppUtils.areAnimationsAllowed()) {
                currentFragment.onCreateAnimation(CubeAnimation.LEFT, true, 500);
            }
        }
        return true;
    }


    /**
     * Useful method used to define mRunnable which is a task that runs under UI thread
     * and does a login-attempt every 2 sec until the user is authenticated.
     * Moreover here are defined onClickEvents for: refreshBtn.
     */
    private void addCallbacks() {

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAuthenticated) {
                    getAuthentication();
                    mHandler.postDelayed(mRunnable, 2000);
                } else {
                    mHandler.removeCallbacks(mRunnable);
                    updateDataSnapshot();
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (currentFragment != null && !currentFragment.isVisible()) {
            showHomeFragment();
            if (AppUtils.areAnimationsAllowed()) {
                currentFragment.onCreateAnimation(CubeAnimation.LEFT, true, 500);
            }
        } else {
            if (AppUtils.isSecureExitAllowed()) {
                new AlertDialog.Builder(this)
                        .setMessage(AppUtils.getStringByLocal(this, R.string.confirm_exit))
                        .setCancelable(false)
                        .setPositiveButton(AppUtils.getStringByLocal(this, R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            } else {
                MainActivity.this.finish();
                System.exit(0);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // boh
    }

    @Override
    public void onChangeFragmentLicked(String fragment) {
        switch (fragment) {
            case ContactFragment.TAG:
                showContactFragment();
                break;
        }
    }

    private void showContactFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (contactFragment.isAdded())
            fragmentTransaction.show(contactFragment);
        else
            fragmentTransaction.add(R.id.fragment_container, contactFragment, ContactFragment.TAG);

        if (homeFragment.isAdded())
            fragmentTransaction.hide(homeFragment);

        fragmentTransaction.commit();
    }

    private void showHomeFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (homeFragment.isAdded())
            fragmentTransaction.show(homeFragment);
        else
            fragmentTransaction.add(R.id.fragment_container, homeFragment, HomeFragment.TAG);

        if (contactFragment.isAdded())
            fragmentTransaction.hide(contactFragment);

        fragmentTransaction.commit();
    }

    public void mutateFavouriteImg() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        assert  fragment != null;
        if (!AppUtils.getFavouriteClassSet().isEmpty() && (getSupportFragmentManager().getBackStackEntryCount() == 0 || fragment.isVisible()))
            favouritesImg.setVisibility(View.VISIBLE);
        else
            favouritesImg.setVisibility(View.GONE);
    }

    public void showFavouritesActivity(View view) {
        startActivity(new Intent(MainActivity.this, FavouritesActivity.class));
    }
}