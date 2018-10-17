package com.example.kimea.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        //Returning the current tabs
        switch (position){
            case 0:
                FriendTabFragment mainTabFragment1 = new FriendTabFragment();
                return mainTabFragment1;
            case 1:
                ChattingTabFragment mainTabFragment2 = new ChattingTabFragment();

                return mainTabFragment2;
            case 2:
                SettingTabFragment mainTabFragment3 = new SettingTabFragment();
                return mainTabFragment3;
                default:

                    return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //	super.destroyItem(container, position, object); }
    }

}
