package com.share.responsive.sharemarket;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Binoy on 11/20/2017.
 */

public class SectionsPageAdapter extends FragmentPagerAdapter{

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment,String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
