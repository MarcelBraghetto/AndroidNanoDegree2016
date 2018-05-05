package com.lilarcor.popularmovies.features.home.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * View pager adapter for the home screen. This adapter
 * expects to be provided with the fragments it should
 * display and their titles.
 *
 * The assumption is also that the number of fragments in
 * the fragments array argument is the same as the number
 * of titles in the titles array argument.
 *
 * No bounds checking is performed based on this assumption.
 */
public class HomeActivityViewPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] mFragments;
    private String[] mTitles;

    /**
     * Instantiate a new instance of the home view pager adapter.
     *
     * Note: The length of the fragments argument and the titles argument
     * should be the same. No bounds checking is performed before
     * accessing the array elements.
     *
     * @param fragmentManager host fragment manager
     * @param fragments to display in the view pager
     * @param titles to associate with each view pager fragment
     */
    public HomeActivityViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Fragment[] fragments, @NonNull String[] titles) {
        super(fragmentManager);

        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}