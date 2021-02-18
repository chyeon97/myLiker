package com.example.liker_0204;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class History_Fragment extends Fragment {
    View v;
    TabLayout tabLayout;
    HistoryPagerAdpater historyPagerAdpater;
    ViewPager vp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.history_viewpager, container, false);
        tabLayout = v.findViewById(R.id.history_tab);
        tabLayout.addTab(tabLayout.newTab().setText("Heart Rate"));
        tabLayout.addTab(tabLayout.newTab().setText("Air Quality"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

         vp = v.findViewById(R.id.history_view);
        historyPagerAdpater = new HistoryPagerAdpater(getFragmentManager(), tabLayout.getTabCount());
        vp.setAdapter(historyPagerAdpater);
        vp.setPageTransformer(true, new ZoomOutPageTransformer());

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp));
        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
       // vp.setOffscreenPageLimit(0);


        return v;
    }




}
