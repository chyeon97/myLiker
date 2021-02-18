package com.example.liker_0204;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;

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


public class History_chart_fragment_aqi extends Fragment {
    UserService userService;
    EditText start_day, end_day;
    Button co,so2,no2,o3,pm25;
    LineChartView lineChartView;


    String[] co_tmp ;
    String[] no2_tmp;
    String[] so2_tmp;
    String[] o3_tmp;
    String[] pm25_tmp;
    String[] time_tmp;

    ArrayList<String> find_co = new ArrayList<>();
    ArrayList<String> find_no2 = new ArrayList<>();
    ArrayList<String> find_o3 = new ArrayList<>(); ArrayList<String>  find_time = new ArrayList<>();
    ArrayList<String> find_pm25 = new ArrayList<>(); ArrayList<String> find_so2 = new ArrayList<>();

    LineChartData update;
    Boolean state = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_chart_aqi,container,false);



        start_day=v.findViewById(R.id.editText_aqi_start);
        end_day  = v.findViewById(R.id.editText_aqi_end);
        co=v.findViewById(R.id.co_btn);
        so2=v.findViewById(R.id.so2_btn);
        no2=v.findViewById(R.id.no2_btn);
        o3=v.findViewById(R.id.o3_btn);
        pm25=v.findViewById(R.id.pm25_btn);
        lineChartView=v.findViewById(R.id.chart);


        co.setOnClickListener(new View.OnClickListener() {  // co 버튼
            @Override
            public void onClick(View v) {
                gethistory_aqi(MainActivity.username, start_day.getText().toString(), end_day.getText().toString(), 129);
                for (int i = 0; i < MainActivity.udooSensorData.dsn.size(); i++){
                    Log.d("tlqkf!!!","tlqkf!!!");
                    gethistory_aqi(MainActivity.username, start_day.getText().toString(), end_day.getText().toString(), MainActivity.udooSensorData.dsn.get(i));
                    Log.d("tlqkf!!!","tlqkf!!!");
                }

                    draw_chart(find_so2, find_time);

            }
        });

        so2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i<MainActivity.udooSensorData.dsn.size();i++)
                    gethistory_aqi(MainActivity.username,start_day.getText().toString(),end_day.getText().toString(),MainActivity.udooSensorData.dsn.get(i));
//
//               if(data_aqi !=null)
//                   data_aqi.update(0);

                draw_chart(find_so2,find_time);


            }
        });

        no2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = MainActivity.udooSensorData.dsn.size()-1; i<MainActivity.udooSensorData.dsn.size();i++)
                    gethistory_aqi(MainActivity.username,start_day.getText().toString(),end_day.getText().toString(),MainActivity.udooSensorData.dsn.get(i));


                draw_chart(find_no2,find_time);
            }
        });
        o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   gethistory_aqi(MainActivity.username,start_day.getText().toString(),end_day.getText().toString(),MainActivity.udooSensorData.dsn.get(0));

                for(int i = MainActivity.udooSensorData.dsn.size()-1; i<MainActivity.udooSensorData.dsn.size();i++)
                    gethistory_aqi(MainActivity.username,start_day.getText().toString(),end_day.getText().toString(),MainActivity.udooSensorData.dsn.get(i));


                draw_chart(find_o3,find_time);
            }
        });
        pm25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = MainActivity.udooSensorData.dsn.size()-1; i<MainActivity.udooSensorData.dsn.size();i++)
                    gethistory_aqi(MainActivity.username,start_day.getText().toString(),end_day.getText().toString(),MainActivity.udooSensorData.dsn.get(i));


                draw_chart(find_pm25,find_time);
            }
        });

        return v;
    }

    public void draw_chart(ArrayList<String> value, ArrayList<String> time){
        List x_time =new ArrayList();
        List y_aqi = new ArrayList();
        List lines_aqi = new ArrayList();
        LineChartData data_aqi = new LineChartData();


        Line line_aqi = new Line(y_aqi).setColor(Color.parseColor("#14064F")); // value 값
        for (int i = 0; i < value.size(); i++) {
            x_time.add(i, new AxisValue(i).setLabel(time.get(i)));
        }

        for (int i = 0; i < value.size(); i++) {
            y_aqi.add(new PointValue(Float.valueOf(i), Float.valueOf(value.get(i))));
        }

        lines_aqi.add(line_aqi);


        data_aqi.setLines(lines_aqi);

        Axis axis_aqi = new Axis(); // x축
        axis_aqi.setName("Days");
        axis_aqi.setValues(x_time);
        axis_aqi.setTextSize(16);
        axis_aqi.setTextColor(Color.parseColor("#14064F"));
        data_aqi.setAxisXBottom(axis_aqi);

        Axis yAxis_aqi = new Axis();
        yAxis_aqi.setName("AQI");
        yAxis_aqi.setTextColor(Color.parseColor("#14064F"));
        yAxis_aqi.setTextSize(16);
        data_aqi.setAxisYLeft(yAxis_aqi);

        lineChartView.setLineChartData(data_aqi);
        Viewport viewport_aqi = new Viewport(lineChartView.getMaximumViewport());
        viewport_aqi.top = 50;
        lineChartView.setMaximumViewport(viewport_aqi);
        lineChartView.setCurrentViewport(viewport_aqi);




    }


    public void gethistory_aqi(String username, String start_time, String end_time, Integer dsn) {
        Call<ResponseBody> call = userService.get_hisaqi(username, start_time, end_time, dsn);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.d("tlqkf!!!","tlqkf!!!~~~~~");
                        String a = response.body().string();
                        JSONArray array = new JSONArray(a);
                        int index_count = array.length();

                        co_tmp = new String[index_count];
                        no2_tmp = new String[index_count];
                        so2_tmp = new String[index_count];
                        o3_tmp = new String[index_count];
                        pm25_tmp = new String[index_count];
                        time_tmp = new String[index_count];

                        for (int i = 0; i < index_count; i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            o3_tmp[i] = jsonObject.getString("o3_aqi");
                            co_tmp[i] = jsonObject.getString("co_aqi");
                            no2_tmp[i] = jsonObject.getString("no2_aqi");
                            so2_tmp[i] = jsonObject.getString("so2_aqi");
                            pm25_tmp[i] = jsonObject.getString("pm2_5_aqi");
                            time_tmp[i] = jsonObject.getString("time");

                            find_co.add(i,co_tmp[i]);
                            find_no2.add(i,no2_tmp[i]);
                            find_o3.add(i,o3_tmp[i]);
                            find_pm25.add(i,pm25_tmp[i]);
                            find_time.add(i,time_tmp[i]);
                            find_so2.add(i,so2_tmp[i]);

                            Log.d("co :",find_co.get(i));
                            Log.d("no2 :",find_no2.get(i));
                            Log.d("so2 :",find_so2.get(i));
                            Log.d("o3 : ",find_o3.get(i));
                            Log.d("pm25 : ",find_pm25.get(i));
                            Log.d("time : ",find_time.get(i) );

                            //      Log.d("JsonParsing", "co:" + co_tmp[i] + "no2:" + no2_tmp[i] + "so2:" + so2_tmp[i] + "o3:" + o3_tmp[i] + "pm25:" + pm25_tmp[i] + "time:" + time_tmp[i]);
                        }





                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }
}