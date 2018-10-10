package com.sterbsociety.orarisapienza.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapters.ClassListAdapter;
import com.sterbsociety.orarisapienza.models.Classroom;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.Objects;

import static com.sterbsociety.orarisapienza.utils.AppUtils.SELECTED_DAY_BTN_INDEX;
import static com.sterbsociety.orarisapienza.utils.AppUtils.askForGPSPermission;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getDistanceFromCurrentPosition;
import static com.sterbsociety.orarisapienza.utils.AppUtils.getSelectedClassBtnIndex;
import static com.sterbsociety.orarisapienza.utils.AppUtils.updateCachedFilters;

public class ClassListActivity extends AppCompatActivity implements SwipeItemClickListener {

    SearchView searchView;
    CoordinatorLayout mainWrapper;
    SwipeMenuRecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.ItemDecoration mItemDecoration;
    protected ClassListAdapter mAdapter;
    private static Location lastLocation;
    private String lastQuery = "";
    private boolean isBuildingClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyThemeNoActionBar(ClassListActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        initActivity();
        askForGPSPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBuildingClickable = true;
        mAdapter.notifyDataSetChanged();
    }

    private final SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen._64sdp);

        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        SwipeMenuItem starItem = new SwipeMenuItem(ClassListActivity.this)
                .setBackground(R.drawable.selector_yellow)
                .setImage(R.drawable.ic_star)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(starItem);
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
                    TextView mTextView = Objects.requireNonNull(mRecyclerView.findViewHolderForAdapterPosition(adapterPosition)).itemView.findViewById(R.id.tv_classroom);
                    Classroom classroom = mAdapter.getClassroom(adapterPosition);
                    if (mTextView.getCompoundDrawables()[2] != null) {
                        AppUtils.removeClassFromFavourites(ClassListActivity.this, classroom);
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    } else {
                        AppUtils.addClassroomToFavourites(ClassListActivity.this, classroom);
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

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.search_view);
        mainWrapper = findViewById(R.id.main_content);
        LinearLayout mAdsContainer = findViewById(R.id.ad_container);

        // This method handles click inside the layout
        AppUtils.setupUIElements(this, mainWrapper);
        AppUtils.initClassroomFilters();

        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mItemDecoration = new DefaultItemDecoration(ActivityCompat.getColor(this, R.color.divider_color));

        //mDataList = AppUtils.getClassroomList();
        mAdapter = new ClassListAdapter(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setSwipeItemClickListener(this);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.filterClassroomList();
        mAdapter.notifyDataSetChanged();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filterClassroomListOnlyByQuery(query);
                lastQuery = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filterClassroomListOnlyByQuery(newText);
                lastQuery = newText;
                return true;
            }
        });

        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-9817701892167034/7074747576");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtils.FILTER_ACTIVITY && resultCode == RESULT_OK) {
            updateCachedFilters(data);
            mAdapter.filterClassroomList(SELECTED_DAY_BTN_INDEX, getSelectedClassBtnIndex(), lastQuery, getDistanceFromCurrentPosition(), lastLocation);
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
        if (isBuildingClickable) {
            isBuildingClickable = false;
            Intent i = new Intent(this, ClassDetailActivity.class);
            i.putExtra(AppUtils.DEFAULT_KEY, mAdapter.getClassroom(position));
            startActivity(i);
        }
    }

    public static void setLastLocation(@NonNull Location location) {
        lastLocation = location;
    }
}