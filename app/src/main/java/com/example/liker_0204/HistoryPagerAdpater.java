package com.example.liker_0204;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class HistoryPagerAdpater extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public HistoryPagerAdpater(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                History_chart_fragment heart = new History_chart_fragment();
                return heart;
            case 1:
                History_chart_fragment_aqi aqi = new History_chart_fragment_aqi();
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
