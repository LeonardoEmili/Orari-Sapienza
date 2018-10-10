package com.sterbsociety.orarisapienza.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
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

import java.util.ArrayList;
import java.util.Objects;

import static com.sterbsociety.orarisapienza.utils.AppUtils.DEFAULT_KEY;
import static com.sterbsociety.orarisapienza.utils.AppUtils.applyTheme;
import static com.sterbsociety.orarisapienza.utils.AppUtils.setLocale;

public class FavouritesActivity extends AppCompatActivity implements SwipeItemClickListener {

    private SwipeMenuRecyclerView mRecyclerView;
    protected ClassListAdapter mAdapter;
    protected ArrayList<Classroom> mDataList, backupList;
    private boolean isBuildingClickable;

    @Override
    protected void onResume() {
        super.onResume();
        isBuildingClickable = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applyTheme(FavouritesActivity.this);
        setLocale(FavouritesActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        initActivity();
        initClassListView();
    }


    private void initActivity() {

        // This is needed for hiding the bottom navigation bar.
        AppUtils.hideSystemUI(getWindow().getDecorView());

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.favourite_classroom));
        }

        final LinearLayout mAdsContainer = findViewById(R.id.ad_container);
        AppUtils.setAdLayout(this, mAdsContainer, "ca-app-pub-9817701892167034/6833120711");
    }

    private void initClassListView() {

        CoordinatorLayout mainWrapper = findViewById(R.id.main_content);

        // This method handles click inside the layout
        AppUtils.setupUIElements(this, mainWrapper);

        mRecyclerView = findViewById(R.id.recycler_view);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        final RecyclerView.ItemDecoration mItemDecoration = new DefaultItemDecoration(ActivityCompat.getColor(this, R.color.divider_color));

        mDataList = AppUtils.getFavouriteClassroomList();
        backupList = new ArrayList<>(mDataList);
        mAdapter = new ClassListAdapter(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setSwipeItemClickListener(this);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
    }

    private final SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen._64sdp);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        final SwipeMenuItem starItem = new SwipeMenuItem(FavouritesActivity.this)
                .setBackground(R.drawable.selector_yellow)
                .setImage(R.drawable.ic_star)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(starItem);
    };

    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection();
            int adapterPosition = menuBridge.getAdapterPosition();
            int menuPosition = menuBridge.getPosition();
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                //noinspection StatementWithEmptyBody
                if (menuPosition == 0) {
                    TextView mTextView = Objects.requireNonNull(mRecyclerView.findViewHolderForAdapterPosition(adapterPosition)).itemView.findViewById(R.id.tv_classroom);
                    Classroom classroom = backupList.get(adapterPosition);
                    if (mTextView.getCompoundDrawables()[2] != null) {
                        AppUtils.removeClassFromFavourites(FavouritesActivity.this, classroom);
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    } else {
                        AppUtils.addClassroomToFavourites(FavouritesActivity.this, classroom);
                        mTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_starred), null);
                    }
                } else {
                    // Info button to be placed here
                }
            }
            // else we could ad a left swipe menu
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemClick(View itemView, int position) {
        if (isBuildingClickable) {
            isBuildingClickable = false;
            Intent i = new Intent(this, ClassDetailActivity.class);
            i.putExtra(DEFAULT_KEY, backupList.get(position));
            startActivity(i);
        }
    }
}