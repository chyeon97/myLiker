package com.example.liker_0204;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Color;
import android.media.AsyncPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard_Fragment extends Fragment {
    View v;
    CardView heartrate_card, aqi_card;
    static TextView tv;
    FragmentManager fragmentManager;

//    LineChartView lineChartView_heart;
//
//    LineChart chart;
//    int DATA_RANGE = 60;
//    int X_RANGE = 30;
//
//    ArrayList<Entry> xVal;
//    LineDataSet setXcomp;
//    ArrayList<String> xVals;
//    ArrayList<ILineDataSet> lineDataSets;
//    LineData lineData;
//
//    String[] axisData_heart = {"1", "2", "3", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
//            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};



    //int[] yAxisData_heart = {50,41,54,21};
    ArrayList<Integer> yAxisData_heart = new ArrayList<>();
    ///// DB 연결을 위한 추가 변수 ////
    UserService userService;
    SensorData sensorData;
    String realtime_hr;  //
    ValueHandler handler ;

    List yAxisValues_heart = new ArrayList();
    List axisValues_heart = new ArrayList(); ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();
        sensorData = new SensorData();
        handler = new ValueHandler();

       // fragmentManager.beginTransaction().replace(R.id.nav_host_fragment,HeartRate_Sensor_Fragment.newInstance())
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.dashboard_layout, container, false);

        fragmentManager = getFragmentManager();


        tv = v.findViewById(R.id.realtime_heartrate);

        //Log.d("real_time_hr",realtime_hr);

//        yAxisValues_heart = new ArrayList();
//        axisValues_heart = new ArrayList();

//        Line line_heart = new Line(yAxisValues_heart).setColor(Color.parseColor("#14064F"));
//
//        for (int i = 0; i < axisData_heart.length; i++) {
//            axisValues_heart.add(i, new AxisValue(i).setLabel(axisData_heart[i]));
//        }
//
//        for (int i = 0; i < yAxisData_heart.size(); i++) {
//            yAxisValues_heart.add(new PointValue(i, yAxisData_heart.get(i)));
//        }
//
//        List lines_heart = new ArrayList();
//        lines_heart.add(line_heart);
//
//        LineChartData data_heart = new LineChartData();
//        data_heart.setLines(lines_heart);
//
//        Axis axis_heart = new Axis();
//        axis_heart.setName("Time (Hours)");
//        axis_heart.setValues(axisValues_heart);
//        axis_heart.setTextSize(16);
//        axis_heart.setTextColor(Color.parseColor("#14064F"));
//        data_heart.setAxisXBottom(axis_heart);
//
//        Axis yAxis_heart = new Axis();
//        yAxis_heart.setName("Heart Rate (BPM)");
//        yAxis_heart.setTextColor(Color.parseColor("#14064F"));
//        yAxis_heart.setTextSize(16);
//        data_heart.setAxisYLeft(yAxis_heart);
//
//        lineChartView_heart.setLineChartData(data_heart);
//        Viewport viewport_heart = new Viewport(lineChartView_heart.getMaximumViewport());
//        viewport_heart.top = 110;
//        viewport_heart.bottom = 0;
//        lineChartView_heart.setMaximumViewport(viewport_heart);
//        lineChartView_heart.setCurrentViewport(viewport_heart);

        return v;
    }

//
//    public void chartUpdate(int x){
//        if(xVal.size() > DATA_RANGE){
//            xVal.remove(0);
//            for (int i = 0; i < DATA_RANGE; i++) {
////                xVal.get(i).setX(i);
//            }
//        }
//        xVal.add(new Entry(x,xVal.size()));
//        setXcomp.notifyDataSetChanged();
//        chart.notifyDataSetChanged();
//        chart.invalidate();
//
//    }

    static class ValueHandler extends Handler{
       Dashboard_Fragment dashboard_fragment= new Dashboard_Fragment();

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            Log.d("bundle value",bundle.toString());
             String hr = bundle.getString("heart");
            tv.setText(hr);

//            int item = Integer.valueOf(hr);
            //int jtem = Integer.parseInt(hr);
//            for (int i = 0; i < axisData_heart.length; i++) {
////                axisValues_heart.add(i, new AxisValue(i).setLabel(axisData_heart[i]));
////            }
////
////            for (int i = 0; i < yAxisData_heart.size(); i++) {
////                yAxisValues_heart.add(new PointValue(i, yAxisData_heart.get(i)));
////            }
//            dashboard_fragment.yAxisValues_heart = new ArrayList();
//            dashboard_fragment.axisValues_heart = new ArrayList();

//            if(dashboard_fragment.yAxisData_heart.size()<60){
//                dashboard_fragment.yAxisData_heart.add(item);
//                int tmp = dashboard_fragment.yAxisData_heart.size();
//                int tmp2 = dashboard_fragment.yAxisData_heart.size();
//                Log.d("dd", "dldldlldld");
//                Log.d("dd", String.valueOf(dashboard_fragment.yAxisData_heart));
//                Log.d("dd", String.valueOf(dashboard_fragment.axisValues_heart));
//                Log.d("dd", String.valueOf(tmp));
//                dashboard_fragment.axisValues_heart.add(0, new AxisValue(0).setLabel(dashboard_fragment.axisData_heart[0]));
//                dashboard_fragment.yAxisValues_heart.add(new PointValue(0, dashboard_fragment.yAxisData_heart.get(0)));
//            }
//            else{
////                dashboard_fragment.yAxisData_heart.remove(0);
////                //int tmp = dashboard_fragment.axisData_heart.length-1;
////                //int tmp2 = dashboard_fragment.yAxisData_heart.size();
////                dashboard_fragment.axisValues_heart.add(tmp, new AxisValue(tmp).setLabel(dashboard_fragment.axisData_heart[tmp]));
////                dashboard_fragment.yAxisValues_heart.add(new PointValue(tmp2, dashboard_fragment.yAxisData_heart.get(tmp2)));
//            }


//            Line line_heart = new Line(dashboard_fragment.yAxisValues_heart).setColor(Color.parseColor("#14064F"));
//
//            List lines_heart = new ArrayList();
//            lines_heart.add(line_heart);
//
//            LineChartData data_heart = new LineChartData();
//            data_heart.setLines(lines_heart);
//
//            Axis axis_heart = new Axis();
//            axis_heart.setName("Time (Hours)");
//            axis_heart.setValues(dashboard_fragment.axisValues_heart);
//            axis_heart.setTextSize(16);
//            axis_heart.setTextColor(Color.parseColor("#14064F"));
//            data_heart.setAxisXBottom(axis_heart);
//
//            Axis yAxis_heart = new Axis();
//            yAxis_heart.setName("Heart Rate (BPM)");
//            yAxis_heart.setTextColor(Color.parseColor("#14064F"));
//            yAxis_heart.setTextSize(16);
//            data_heart.setAxisYLeft(yAxis_heart);
//
//            dashboard_fragment.lineChartView_heart.setLineChartData(data_heart);
//            Viewport viewport_heart = new Viewport(dashboard_fragment.lineChartView_heart.getMaximumViewport());
//            viewport_heart.top = 110;
//            viewport_heart.bottom = 0;
//            dashboard_fragment.lineChartView_heart.setMaximumViewport(viewport_heart);
//            dashboard_fragment.lineChartView_heart.setCurrentViewport(viewport_heart);
//
//            dashboard_fragment.realtime_hr = tv.getText().toString();
//            Log.d("realtime_hr",dashboard_fragment.realtime_hr);
//
            }

           // Log.d("kkk",hr);
        }
    }


