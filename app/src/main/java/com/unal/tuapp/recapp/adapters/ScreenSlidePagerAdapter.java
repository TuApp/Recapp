package com.unal.tuapp.recapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/30/15.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> pages;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        pages = new ArrayList<>();
    }

    public void addFrament(Fragment fragment){
        pages.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }
}
