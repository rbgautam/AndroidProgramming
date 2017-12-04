package com.iaai.onyard.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class SwipePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList;
    public SwipePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);

        mFragmentList = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
