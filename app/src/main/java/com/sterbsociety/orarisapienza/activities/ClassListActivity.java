package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapter.ClassListAdapter;
import com.sterbsociety.orarisapienza.model.Classroom;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Objects;

public class ClassListActivity extends AppCompatActivity implements SwipeItemClickListener {

    SearchView searchView;
    CoordinatorLayout mainWrapper;
    SwipeMenuRecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.ItemDecoration mItemDecoration;
    private LinearLayout mAdsContainer;
    private AdView mAdView;
    private AdRequest mAdRequest;
    protected ClassListAdapter mAdapter;
    protected ArrayList<Classroom> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(ClassListActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        initActivity();

        AppUtils.askForGPSPermission(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private final SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen._64sdp);

            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem starItem = new SwipeMenuItem(ClassListActivity.this)
                    .setBackground(R.drawable.selector_yellow)
                    .setImage(R.drawable.ic_star)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(starItem);
        }
    };

    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection();
            int adapterPosition = menuBridge.getAdapterPosition();
            int menuPosition = menuBridge.getPosition();
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 0) {

                    TextView mTextView = Objects.requireNonNull(mRecyclerView.findViewHolderForAdapterPosition(adapterPosition)).itemView.findViewById(R.id.tv_title);
                    String classID = mDataList.get(adapterPosition).getCode();
                    if (mTextView.getCompoundDrawables()[2] != null) {
                        AppUtils.removeClassFromFavourites(ClassListActivity.this, classID);
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    } else {
                        AppUtils.addClassToFavourites(ClassListActivity.this, classID);
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_starred), null);
                    }
                } else {
                    // Second button to be placed here
                }
            }
            // else we could ad a left swipe menu
        }
    };


    private void initActivity() {
        AppUtils.setLocale(ClassListActivity.this);

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.search_view);
        mainWrapper = findViewById(R.id.main_content);
        mAdsContainer = findViewById(R.id.ad_container);

        // This method handles click inside the layout
        AppUtils.setupUIElements(this, mainWrapper);

        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mItemDecoration = new DefaultItemDecoration(ActivityCompat.getColor(this, R.color.divider_color));

        mDataList = AppUtils.createClassesList();
        mAdapter = new ClassListAdapter(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setSwipeItemClickListener(this);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filterClassroomsByQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filterClassroomsByQuery(newText);
                return true;
            }
        });

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppUtils.GPS_ACCESS:
                if (resultCode == RESULT_OK)
                    Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "NOT GRANTED", Toast.LENGTH_SHORT).show();
                break;

            case AppUtils.FILTER_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    AppUtils.updateFilters(data);
                }
                break;
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void openFilterActivity(View view) {
        Intent i = new Intent(this, FilterActivity.class);
        startActivityForResult(i, AppUtils.FILTER_ACTIVITY);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay_still);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        Toast.makeText(this, "You clicked the element number: " + position, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, ClassDetailActivity.class);
        i.putExtra("KEY", mDataList.get(position));
        startActivity(i);
    }
}
