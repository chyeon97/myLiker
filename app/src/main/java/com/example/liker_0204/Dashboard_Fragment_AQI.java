package com.example.liker_0204;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Dashboard_Fragment_AQI extends Fragment {
    CardView heartrate_card, aqi_card;

    MapView mMapView;
    private GoogleMap googleMap;

    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dashboard_layout_aqi, container, false);

        fragmentManager = getFragmentManager();


//        aqi_card = v.findViewById(R.id.aqi_cardview);
//        aqi_card.setOnClickListener(new View.OnClickListener() { // heartcardview 클릭 이벤트
//            @Override
//            public void onClick(View v) {
//                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, new Google_Fragment_AQI()).addToBackStack("dashboard_aqi_detail").commit();
//
//                Toast.makeText(getContext(), "click to aqi", Toast.LENGTH_LONG).show();
//            }
//        });

        PieChart pieChart1, pieChart2, pieChart3, pieChart4, pieChart5;

        pieChart1 = v.findViewById(R.id.piechart1);
        pieChart1.setUsePercentValues(true);
        pieChart1.setDescription(null);  // 설명 없애기
        pieChart1.setExtraOffsets(5,10,5,10);

        pieChart1.setDragDecelerationFrictionCoef(0.95f);
        pieChart1.setDrawHoleEnabled(true);    // 가운데 구멍
        pieChart1.setHoleColor(0xFFFFFFFF);    // 구멍 색
        pieChart1.setTransparentCircleRadius(120);
        pieChart1.setHoleRadius(70);            // 구멍 크기

        pieChart1.setCenterText("17");
        pieChart1.setCenterTextSize(30);
        pieChart1.getLegend().setEnabled(false);   // Hide the legend(범주)

        ArrayList<PieEntry> yvalues1 = new ArrayList<>();
        yvalues1.add(new PieEntry(100f, ""));
        PieDataSet dataSet1 = new PieDataSet(yvalues1,"Countries");
        dataSet1.setSliceSpace(3f);
        dataSet1.setSelectionShift(5f);
        dataSet1.setColors(Color.GREEN);

        PieData data1 = new PieData(dataSet1);
        data1.setValueTextSize(10f);
        data1.setValueTextColor(Color.TRANSPARENT);

        pieChart1.setData(data1);



        pieChart2 = v.findViewById(R.id.piechart2);
        pieChart2.setUsePercentValues(true);
        pieChart2.setDescription(null);  // 설명 없애기
        pieChart2.setExtraOffsets(5,10,5,10);

        pieChart2.setDragDecelerationFrictionCoef(0.95f);
        pieChart2.setDrawHoleEnabled(true);    // 가운데 구멍
        pieChart2.setHoleColor(0xFFFFFFFF);    // 구멍 색
        pieChart2.setTransparentCircleRadius(61f);
        pieChart2.setHoleRadius(70);            // 구멍 크기

        pieChart2.setCenterText("23");
        pieChart2.setCenterTextSize(30);
        pieChart2.getLegend().setEnabled(false);   // Hide the legend(범주)

        ArrayList<PieEntry> yvalues2 = new ArrayList<>();
        yvalues2.add(new PieEntry(100f, ""));
        PieDataSet dataSet2 = new PieDataSet(yvalues2,"Countries");
        dataSet2.setSliceSpace(3f);
        dataSet2.setSelectionShift(5f);
        dataSet2.setColors(Color.YELLOW);

        PieData data2 = new PieData(dataSet2);
        data2.setValueTextSize(10f);
        data2.setValueTextColor(Color.TRANSPARENT);

        pieChart2.setData(data2);


        pieChart3 = v.findViewById(R.id.piechart3);
        pieChart3.setUsePercentValues(true);
        pieChart3.setDescription(null);  // 설명 없애기
        pieChart3.setExtraOffsets(5,10,5,10);

        pieChart3.setDragDecelerationFrictionCoef(0.95f);
        pieChart3.setDrawHoleEnabled(true);    // 가운데 구멍
        pieChart3.setHoleColor(0xFFFFFFFF);    // 구멍 색
        pieChart3.setTransparentCircleRadius(61f);
        pieChart3.setHoleRadius(70);            // 구멍 크기

        pieChart3.setCenterText("22");
        pieChart3.setCenterTextSize(30);
        pieChart3.getLegend().setEnabled(false);   // Hide the legend(범주)

        ArrayList<PieEntry> yvalues3 = new ArrayList<>();
        yvalues3.add(new PieEntry(100f, ""));
        PieDataSet dataSet3 = new PieDataSet(yvalues3,"Countries");
        dataSet3.setSliceSpace(3f);
        dataSet3.setSelectionShift(5f);
        dataSet3.setColors(Color.YELLOW);

        PieData data3 = new PieData(dataSet3);
        data3.setValueTextSize(10f);
        data3.setValueTextColor(Color.TRANSPARENT);

        pieChart3.setData(data3);



        pieChart4 = v.findViewById(R.id.piechart4);
        pieChart4.setUsePercentValues(true);
        pieChart4.setDescription(null);  // 설명 없애기
        pieChart4.setExtraOffsets(5,10,5,10);

        pieChart4.setDragDecelerationFrictionCoef(0.95f);
        pieChart4.setDrawHoleEnabled(true);    // 가운데 구멍
        pieChart4.setHoleColor(0xFFFFFFFF);    // 구멍 색
        pieChart4.setTransparentCircleRadius(61f);
        pieChart4.setHoleRadius(70);            // 구멍 크기

        pieChart4.setCenterText("41");
        pieChart4.setCenterTextSize(30);
        pieChart4.getLegend().setEnabled(false);   // Hide the legend(범주)

        ArrayList<PieEntry> yvalues4 = new ArrayList<>();
        yvalues4.add(new PieEntry(100f, ""));
        PieDataSet dataSet4 = new PieDataSet(yvalues4,"Countries");
        dataSet4.setSliceSpace(3f);
        dataSet4.setSelectionShift(5f);
        dataSet4.setColors(Color.YELLOW);

        PieData data4 = new PieData(dataSet2);
        data4.setValueTextSize(10f);
        data4.setValueTextColor(Color.TRANSPARENT);

        pieChart4.setData(data4);

        pieChart5 = v.findViewById(R.id.piechart5);
        pieChart5.setUsePercentValues(true);
        pieChart5.setDescription(null);  // 설명 없애기
        pieChart5.setExtraOffsets(5,10,5,10);

        pieChart5.setDragDecelerationFrictionCoef(0.95f);
        pieChart5.setDrawHoleEnabled(true);    // 가운데 구멍
        pieChart5.setHoleColor(0xFFFFFFFF);    // 구멍 색
        pieChart5.setTransparentCircleRadius(61f);
        pieChart5.setHoleRadius(70);            // 구멍 크기

        pieChart5.setCenterText("33");
        pieChart5.setCenterTextSize(30);
        pieChart5.getLegend().setEnabled(false);   // Hide the legend(범주)

        ArrayList<PieEntry> yvalues5 = new ArrayList<>();
        yvalues5.add(new PieEntry(100f, ""));
        PieDataSet dataSet5 = new PieDataSet(yvalues5,"Countries");
        dataSet5.setSliceSpace(3f);
        dataSet5.setSelectionShift(5f);
        dataSet5.setColors(Color.RED);

        PieData data5 = new PieData(dataSet5);
        data5.setValueTextSize(10f);
        data5.setValueTextColor(Color.TRANSPARENT);

        pieChart5.setData(data5);


        //구글맵
        mMapView = (MapView) v.findViewById(R.id.mapview_aqi_dashboard);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng QI = new LatLng(32.882498, -117.234678);
                LatLng Marriot = new LatLng(32.870141, -117.237197);

                googleMap.addMarker(new MarkerOptions().position(QI).title("Marker Title").snippet("Marker Description"));
                googleMap.addMarker(new MarkerOptions().position(Marriot).title("Marker Title").snippet("Marker Description"));


                // For zooming automatically to the location of the marker

                CameraPosition cameraPosition_QI = new CameraPosition.Builder().target(QI).zoom(12).build();
                googleMap.addCircle(new CircleOptions().center(QI).radius(500).strokeColor(Color.RED).strokeWidth(3).fillColor(0x2FFFFF00));

                CameraPosition cameraPosition_Marriot = new CameraPosition.Builder().target(Marriot).zoom(12).build();
                googleMap.addCircle(new CircleOptions().center(Marriot).radius(500).strokeColor(Color.RED).strokeWidth(3).fillColor(0x2FFFFF00));

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_QI));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_Marriot));
            }
        });

        return v;
    }

}
