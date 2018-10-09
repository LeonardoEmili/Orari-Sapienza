package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.fragments.ChangeFragmentListener;
import com.sterbsociety.orarisapienza.fragments.ContactFragment;
import com.sterbsociety.orarisapienza.fragments.HomeFragment;
import com.sterbsociety.orarisapienza.R;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sterbsociety.orarisapienza.utils.AppUtils;
import org.codechimp.apprater.AppRater;

import static com.sterbsociety.orarisapienza.utils.AppUtils.applyThemeNoActionBar;
import static com.sterbsociety.orarisapienza.utils.AppUtils.isDBAvailable;
import static com.sterbsociety.orarisapienza.utils.AppUtils.parseDatabase;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;


public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener, ChangeFragmentListener {

    private HomeFragment homeFragment;
    private ContactFragment contactFragment;
    private FragmentTransaction fragmentTransaction;
    private boolean mStartTheme;
    public Intent mStartIntent;
    public ImageView favouritesImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applyThemeNoActionBar(MainActivity.this);
        setLocale(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isDBAvailable(this)) {
            AsyncTask.execute(() -> parseDatabase(this));
        } else {
            StyleableToast.makeText(this, getResources().getString(R.string.some_errors_occured),
                    Toast.LENGTH_LONG, R.style.errorToast).show();
            new Handler().postDelayed(this::finish, 400);
        }
        initActivity();

        // DOCS here: https://github.com/codechimp-org/AppRater
        AppRater.app_launched(this);
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

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        mStartIntent = getIntent();
        mStartTheme = AppUtils.isDarkTheme();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_previous);
        }

        favouritesImg = findViewById(R.id.show_favourites);
        final LinearLayout mAdsContainer = findViewById(R.id.ad_container);

        homeFragment = HomeFragment.newInstance(MainActivity.this);
        contactFragment = ContactFragment.newInstance(actionBar);

        homeFragment.setChangeFragmentListener(this);
        contactFragment.setChangeFragmentListener(this);
        showHomeFragment();

        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-9817701892167034/6189339725");
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
                        .setPositiveButton(AppUtils.getStringByLocal(this, R.string.yes), (dialog, id) -> {
                            MainActivity.this.finish();
                            System.exit(0);
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