package com.example.androidacademy2;


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
        IntroFragment fr=new IntroFragment();
        return fr.newInstance(position);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
