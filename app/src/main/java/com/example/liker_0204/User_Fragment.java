package com.example.liker_0204;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class User_Fragment extends Fragment {
    View v;
    TabLayout tabLayout;
    UserPagerAdpater userPagerAdpater;
    ViewPager vp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.user_sensor_viewpager, container, false);
        tabLayout = v.findViewById(R.id.user_tab);
        tabLayout.addTab(tabLayout.newTab().setText("HeartRate Sensor"));
        tabLayout.addTab(tabLayout.newTab().setText("AQI Sensor"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

         vp = v.findViewById(R.id.user_view);


        userPagerAdpater = new UserPagerAdpater(getFragmentManager(), tabLayout.getTabCount());
        vp.setAdapter(userPagerAdpater);
        vp.setPageTransformer(true, new ZoomOutPageTransformer());

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp));
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
       // vp.setOffscreenPageLimit(0);



        return v;
    }




}
