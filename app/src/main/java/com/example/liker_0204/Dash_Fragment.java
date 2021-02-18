package com.example.liker_0204;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.Dash;
import com.google.android.material.tabs.TabLayout;

public class Dash_Fragment extends Fragment {
    View v;
    TabLayout tabLayout;
    DashboardPagerAdpater dashboardPagerAdpater;
    ViewPager vp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dashboard_viewpager, container, false);
        tabLayout = v.findViewById(R.id.dashboard_tab);
        tabLayout.addTab(tabLayout.newTab().setText("Heart Rate"));
        tabLayout.addTab(tabLayout.newTab().setText("Air Quality"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

         vp = v.findViewById(R.id.dashboard_view);
        dashboardPagerAdpater = new DashboardPagerAdpater(getFragmentManager(), tabLayout.getTabCount());
        vp.setAdapter(dashboardPagerAdpater);
        vp.setPageTransformer(true, new ZoomOutPageTransformer());

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp));
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
       // vp.setOffscreenPageLimit(0);


        return v;
    }




}
