package com.sterbsociety.orarisapienza.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.adapter.ListViewAdapter;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import xyz.sahildave.widget.SearchViewLayout;

public class SearchStaticListFragment extends Fragment {

    private static SearchStaticListFragment instance;
    private SearchViewLayout mSearchViewLayout;
    private MenuItem mClearHistoryButton;
    private ListViewAdapter mAdapter;


    public SearchStaticListFragment() {
        setHasOptionsMenu(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static SearchStaticListFragment newInstance(SearchViewLayout searchViewLayout) {
        if (instance == null) {
            instance = new SearchStaticListFragment();
        }
        instance.mSearchViewLayout = searchViewLayout;
        return instance;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        mClearHistoryButton = menu.findItem(R.id.clear_history);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clear_history:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_static_list, container, false);
        ListView listView = rootView.findViewById(R.id.search_static_list);

        // todo check if this is called every time onShow fragment, I think not with Singleton pattern now
        AppUtils.createCoursesList();

        mAdapter = new ListViewAdapter(getActivity(), AppUtils.getFavouriteCourses());
        listView.setAdapter(mAdapter);

        // Suppress the IDE alert.
        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String courseName = ((TextView)view.findViewById(R.id.card_title)).getText().toString();
                mSearchViewLayout.collapse();
                AppUtils.addCourseToFavourites(view.getContext(), courseName);
                if (AppUtils.areFavouritesPresent()) {
                    mClearHistoryButton.setVisible(true);
                }
                Snackbar.make(mSearchViewLayout, courseName, Snackbar.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    public ListViewAdapter getAdapter() {
        return mAdapter;
    }
}