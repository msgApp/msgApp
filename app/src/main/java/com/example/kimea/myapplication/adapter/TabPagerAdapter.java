package com.example.kimea.myapplication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kimea.myapplication.ChattingTabFragment;
import com.example.kimea.myapplication.FriendTabFragment;
import com.example.kimea.myapplication.SettingTabFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    //private ArrayList<GetChatRoomItem> complimentList;
    private TabPagerAdapter.OnSendPop mCallback;
    public interface OnSendPop {

    }
    public TabPagerAdapter(FragmentManager fm, int tabCount, OnSendPop listner) {
        super(fm);
        this.tabCount = tabCount;
        this.mCallback = listner;
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
                //complimentList.clear();
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
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

}
