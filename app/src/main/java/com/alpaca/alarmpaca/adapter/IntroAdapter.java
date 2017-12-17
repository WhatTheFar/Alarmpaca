package com.alpaca.alarmpaca.adapter;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class IntroAdapter extends FragmentPagerAdapter {

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return IntroFragment.newInstance(Color.parseColor("#00796B"), position); // blue_ocean
            default:
                return IntroFragment.newInstance(Color.parseColor("#4CAF50"), position); // green

        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

