package com.unal.tuapp.recapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 7/13/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList;
    private final List<String> mFragmentTitleList;


    public ViewPagerAdapter(FragmentManager manager){
        super(manager);
        mFragmentList = new ArrayList<>();
        mFragmentTitleList = new ArrayList<>();

    }
    @Override
    public Fragment getItem(int position){
        return mFragmentList.get(position);
    }
    @Override
    public int getCount(){
        return mFragmentList.size();
    }
    @Override
    public CharSequence getPageTitle(int position){
        return mFragmentTitleList.get(position);
    }
    public void addFragment(Fragment fragment,String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

}
