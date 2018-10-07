package com.sterbsociety.orarisapienza.adapters;

import android.app.Activity;
import android.content.Context;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.fragments.FridayFragment;
import com.sterbsociety.orarisapienza.fragments.MondayFragment;
import com.sterbsociety.orarisapienza.fragments.SaturdayFragment;
import com.sterbsociety.orarisapienza.fragments.ThursdayFragment;
import com.sterbsociety.orarisapienza.fragments.TuesdayFragment;
import com.sterbsociety.orarisapienza.fragments.WednesdayFragment;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WeekDayFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mActivity;

    public WeekDayFragmentPagerAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        mActivity = activity;
        AppUtils.setLocale(activity);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MondayFragment();
            case 1:
                return new TuesdayFragment();
            case 2:
                return new WednesdayFragment();
            case 3:
                return new ThursdayFragment();
            case 4:
                return new FridayFragment();
            case 5:
                return new SaturdayFragment();
            default:
                return new MondayFragment();
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mActivity.getString(R.string.mon);
            case 1:
                return mActivity.getString(R.string.tue);
            case 2:
                return mActivity.getString(R.string.wed);
            case 3:
                return mActivity.getString(R.string.thu);
            case 4:
                return mActivity.getString(R.string.fri);
            case 5:
                return mActivity.getString(R.string.sat);
            default:
                return mActivity.getString(R.string.mon);
        }
    }
}
