package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sterbsociety.orarisapienza.DatabaseHelper;
import com.sterbsociety.orarisapienza.NetworkStatus;
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

import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, ContactFragment.OnFragmentInteractionListener, ChangeFragmentListener {

    private static final String TAG = "MainActivity";
    private DatabaseReference onlineDatabase;
    private DataSnapshot currentDataSnapshot;
    private Handler mHandler;
    private Runnable mRunnable;
    private FirebaseAuth mAuth;
    private volatile boolean isAuthenticated;
    private FrameLayout fragmentContainer;
    private HomeFragment homeFragment;
    private ContactFragment contactFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                // Something else to do here
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
            boolean outcome = DatabaseHelper.getInstance(MainActivity.this).createDB(currentDataSnapshot);
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

        if (NetworkStatus.getInstance(this).isOnline()) {
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
                if (!MainActivity.this.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {
                    DatabaseHelper.getInstance(MainActivity.this).createDB(currentDataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }


    /**
     * This method is used to set the Toolbar and the buttons in the Activity.
     */
    private void initActivity() {

        //mPullToRefreshView = findViewById(R.id.pull_to_refresh);
        //mPullToRefreshView.setOnRefreshListener(MainActivity.this);

        /*
        findViewById(R.id.zero_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (offlineDBAvailable())
                    startActivity(new Intent(MainActivity.this, StudyPlanActivity.class));
            }
        });

        findViewById(R.id.first_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (offlineDBAvailable())
                    startActivity(new Intent(MainActivity.this, ListClassActivity.class));
            }
        });

        findViewById(R.id.second_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (offlineDBAvailable())
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

        findViewById(R.id.third_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FaqActivity.class));
            }
        });

        findViewById(R.id.fourth_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
            }
        });

        findViewById(R.id.fifth_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (offlineDBAvailable())
                    startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });

        findViewById(R.id.sixth_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        }); */


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert  actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_previous);

        fragmentContainer = findViewById(R.id.fragment_container);
        homeFragment = new HomeFragment();
        contactFragment = ContactFragment.newInstance(actionBar);
        homeFragment.setChangeFragmentListener(this);
        contactFragment.setChangeFragmentListener(this);
        showHomeFragment();

        // AdMob App ID: ca-app-pub-9817701892167034~2496155654
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    /**
     * This method is responsible for returning back to HomeFragment.
     */
    @Override
    public boolean onSupportNavigateUp() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (currentFragment != null && !currentFragment.isVisible()) {
            showHomeFragment();
            currentFragment.onCreateAnimation(CubeAnimation.LEFT, true, 500);
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
    public void onBackPressed(){
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        if (currentFragment != null && !currentFragment.isVisible()) {
            showHomeFragment();
            currentFragment.onCreateAnimation(CubeAnimation.LEFT, true, 500);
        } else {
            super.onBackPressed();
            MainActivity.this.finish();
            System.exit(0);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // boh
    }

    @Override
    public void onChangeFragmentLicked(String fragment) {
        switch (fragment){
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
}
