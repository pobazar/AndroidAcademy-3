package com.example.androidacademy2.Intro;


import com.example.androidacademy2.Intro.IntroFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 3;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return IntroFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
