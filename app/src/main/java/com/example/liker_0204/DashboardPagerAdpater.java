package com.example.liker_0204;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class DashboardPagerAdpater extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public DashboardPagerAdpater(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Dashboard_Fragment heart = new Dashboard_Fragment();
                return heart;
            case 1:
                Dashboard_Fragment_AQI aqi = new Dashboard_Fragment_AQI();
                return  aqi;
            default:
                return  null;
        }

    }

    @Override
    public int getItemPosition( Object object) {
        return POSITION_UNCHANGED;
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
