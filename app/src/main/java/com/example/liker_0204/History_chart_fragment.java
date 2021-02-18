package com.example.liker_0204;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


public class History_chart_fragment extends Fragment {

    private Spinner spinner_year, spinner_month, spinner_day;
    private TextView tv_result;

    EditText editText_Start, editText_End;
    Button btn_date;

    LineChartView lineChartView_heart;
    String[] axisData_heart = {"1","2","3","3","4","5","6","7","8","9","10","11","12",
            "13","14","15","16","17","18","19","20","21","22","23","24"};
    int[] yAxisData_heart = {80,70,75,90,100,90,85,90,82,88,85,90,
            80,78,78,80,80,81,75,90,80,70,75,90};

    List yAxisValues_heart;
    List axisValues_heart;

    //////  DB 연결 변수 //////
    UserService userService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_chart_heart,container,false);

        btn_date = v.findViewById(R.id.button_history_heart);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HR_historic_chart_dbconn(editText_Start.getText().toString(),editText_End.getText().toString(),MainActivity.sensorData.dsn.get(0));
            }
        });


        editText_Start = v.findViewById(R.id.editText_start_date);
        editText_End = v.findViewById(R.id.editText_end_date);

//        // Spinner
//
//        spinner_year = (Spinner) v.findViewById(R.id.spinner_heart_year);
//        spinner_month = (Spinner) v.findViewById(R.id.spinner_heart_month);
//        spinner_day = (Spinner) v.findViewById(R.id.spinner_heart_day);
//
//        tv_result = (TextView) v.findViewById(R.id.textView_history_heart);
//
//        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView)parent.getChildAt(0)).setTextColor(0xFF14064F);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView)parent.getChildAt(0)).setTextColor(0xFF14064F);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        spinner_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView)parent.getChildAt(0)).setTextColor(0xFF14064F);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


//        Heart History 그래프
        lineChartView_heart = (LineChartView) v.findViewById(R.id.chart_heart_history);

        List yAxisValues_heart = new ArrayList();
        List axisValues_heart = new ArrayList();

        Line line_heart = new Line(yAxisValues_heart).setColor(Color.parseColor("#14064F"));

        for (int i = 0; i < axisData_heart.length; i++) {
            axisValues_heart.add(i, new AxisValue(i).setLabel(axisData_heart[i]));
        }

        for (int i = 0; i < yAxisData_heart.length; i++) {
            yAxisValues_heart.add(new PointValue(i, yAxisData_heart[i]));
        }

        List lines_heart = new ArrayList();
        lines_heart.add(line_heart);

        LineChartData data_heart = new LineChartData();
        data_heart.setLines(lines_heart);

        Axis axis_heart = new Axis();
        axis_heart.setName("Time");
        axis_heart.setValues(axisValues_heart);
        axis_heart.setTextSize(16);
        axis_heart.setTextColor(Color.parseColor("#14064F"));
        data_heart.setAxisXBottom(axis_heart);

        Axis yAxis_heart = new Axis();
        yAxis_heart.setName("Heart Rate (BPM)");
        yAxis_heart.setTextColor(Color.parseColor("#14064F"));
        yAxis_heart.setTextSize(16);
        data_heart.setAxisYLeft(yAxis_heart);

        lineChartView_heart.setLineChartData(data_heart);
        Viewport viewport_heart = new Viewport(lineChartView_heart.getMaximumViewport());
        viewport_heart.top = 150;
        viewport_heart.bottom = 0;
        lineChartView_heart.setMaximumViewport(viewport_heart);
        lineChartView_heart.setCurrentViewport(viewport_heart);
        //HR_historic_chart_dbconn(editText_Start.toString(),editText_End.toString(),MainActivity.sensorData.dsn.get(0));
        return v;
    }
    String time, hr;
    public  void HR_historic_chart_dbconn(String start_time, String end_time, Integer dsn){
        Call<ResponseBody> call = userService.historic_heartrate_app(start_time,end_time,dsn);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String a = response.body().string();
                        Log.d("test", a);
//
                        JSONArray jsonArray =new JSONArray(a);
                        int idx = jsonArray.length();

                        String[] heart_rate = new String[idx];
                        String [] time = new String[idx];

                        for(int i=0; i<idx;i++){
                            JSONObject jsonObject= jsonArray.getJSONObject(i);
                            heart_rate[i] =jsonObject.getString("heart_rate");
                            time[i] = jsonObject.getString("time");
                        }



                        List yAxisValues_heart = new ArrayList();
                        List axisValues_heart = new ArrayList();


                        Line line_heart = new Line(yAxisValues_heart).setColor(Color.parseColor("#14064F"));
                        axisValues_heart.add(0, new AxisValue(0).setLabel(time[0]));
                        for (int i = 0; i < time.length; i++) {
                            //axisValues_heart.add(i, new AxisValue(i).setLabel(String.valueOf(i)));
                            if(i==time.length-1){
                                Log.d("dldldldldl",String.valueOf(i));
                                //axisData_heart[i]=time[i];
                                //int t = Integer.parseInt(time[i])/60;
                                axisValues_heart.add(new AxisValue(i).setLabel(time[i]));
                            }
                            //axisData_heart[i]=time[i];
                            //
                        }

                        //axisValues_heart.add(axisData_heart.length, new AxisValue(axisData_heart.length).setLabel(axisData_heart[axisData_heart.length]));
                        for (int i = 0; i < heart_rate.length; i++) {
                            //yAxisData_heart[i]=Integer.parseInt(heart_rate[i]);
                            yAxisValues_heart.add(new PointValue(i, Integer.parseInt(heart_rate[i])));
                        }

                        List lines_heart = new ArrayList();
                        lines_heart.add(line_heart);

                        LineChartData data_heart = new LineChartData();
                        data_heart.setLines(lines_heart);

                        Axis axis_heart = new Axis();
                        axis_heart.setName("Time");
                        axis_heart.setValues(axisValues_heart);
                        axis_heart.setTextSize(16);
                        axis_heart.setTextColor(Color.parseColor("#14064F"));
                        data_heart.setAxisXBottom(axis_heart);

                        Axis yAxis_heart = new Axis();
                        yAxis_heart.setName("Heart Rate (BPM)");
                        yAxis_heart.setTextColor(Color.parseColor("#14064F"));
                        yAxis_heart.setTextSize(16);
                        data_heart.setAxisYLeft(yAxis_heart);

                        lineChartView_heart.setLineChartData(data_heart);
                        Viewport viewport_heart = new Viewport(lineChartView_heart.getMaximumViewport());
                        viewport_heart.top = 150;
                        viewport_heart.bottom = 0;
                        lineChartView_heart.setMaximumViewport(viewport_heart);
                        lineChartView_heart.setCurrentViewport(viewport_heart);
                        } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//
//
                        }







            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

}
