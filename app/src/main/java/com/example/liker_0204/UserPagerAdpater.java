package com.example.liker_0204;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class UserPagerAdpater extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public UserPagerAdpater(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HeartRate_Sensor_Fragment heart = new HeartRate_Sensor_Fragment();
                return heart;
            case 1:
                AQI_Sensor_Fragment aqi = new AQI_Sensor_Fragment();
                return  aqi;
            default:
                return  null;
        }

    }

    @Override
    public int getItemPosition( Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
