package com.example.liker_0204;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeartRate_SensorCustomAdpater extends RecyclerView.Adapter<HeartRate_SensorCustomAdpater.ViewHolder> {
 //   private SensorData sensorData;
   // private SensorData sensorData2;
    HeartRate_Sensor_Fragment heartRate_sensor_fragment= new HeartRate_Sensor_Fragment();
    private  SensorData sensorData;
    private Context context;
    private FragmentManager fragmentManager;
    ImageButton img;
    UserService userService;
  //  PolarBleApi api;
    public HeartRate_SensorCustomAdpater(Context context, SensorData sensorData, FragmentManager fragmentManager) {
        this.sensorData= sensorData;
        this.context = context;
        this.fragmentManager = fragmentManager;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.heartrate_sensorlist_item, parent, false);
        img = view.findViewById(R.id.imageButton_delete_btn); // 센서 삭제버튼




        userService=ApiUtils.getUserService();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.sensorname.setText(sensorData.sensorname.get(position));

        img.setOnClickListener(new View.OnClickListener() { // 센서 삭제버튼 클릭 리스너
            @Override
            public void onClick(View v) {
                final Delete_Popup_Fragment delete_popup_fragment = new Delete_Popup_Fragment();
              //  Log.d("sensorname_delete",sensorData.sensorname.toString());
               delete_popup_fragment.rm_sensorname =sensorData.sensorname.get(position);
                delete_popup_fragment.show(fragmentManager, "true");
                delete_popup_fragment.setDialogResult(new Delete_Popup_Fragment.DialogResult() { // 팝업창에서 보낸 변수값 들고오기
                    @Override
                    public void finish(String result) {
                  //      Log.d("popup", result);
                        if (result.equals("delete")) {

                            Log.d("position",String.valueOf(position));


                            Log.d("sensorData_type value",String.valueOf(sensorData.sensor_type.get(position)));
                            Log.d("sensorData_mac value",String.valueOf(sensorData.mac_addr.get(position)));
                            Log.d("sensorData_name value",String.valueOf(sensorData.sensorname.get(position)));
                            Log.d("sensorData_dsn",String.valueOf(sensorData.dsn.get(position)));

//                            for(int i= HeartRate_Sensor_Fragment.sensorData.dsn.size() ; i < HeartRate_Sensor_Fragment.sensorData.dsn.size(); i++)
                            delete_mySensor(MainActivity.username,sensorData.dsn.get(position));

                            HeartRate_Sensor_Fragment.polarsensor_disconnect();
                            Toast.makeText(context,"Disconnected!!!",Toast.LENGTH_LONG).show();


                        }
                        else{
                            Toast.makeText(context,"존재하는 센서가 없습니다.",Toast.LENGTH_LONG).show();
                        }
                        delete_popup_fragment.dismiss();

                    }
                });

            }
        });
        holder.getLayoutPosition();

    }

    @Override
    public int getItemCount() {
        return sensorData.sensorname.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sensorname;

        public ViewHolder(View itemView) {
            super(itemView);
            sensorname = itemView.findViewById(R.id.itemText);
        }
    }


    public void delete_mySensor(String username, Integer dsn){
        Call<ResObj> call = userService.remove_sensor(username,dsn);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {
                    ResObj resObj = response.body();
                    Log.d("test,,,", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) {
                        for(int i = sensorData.dsn.size()-1; i<sensorData.dsn.size();i++){



                            heartRate_sensor_fragment.polarsensor_disconnect();

                            notifyItemRemoved(i);
                            notifyDataSetChanged();
                        }
                       Log.d("remove_sensor","성공");
                    } else {
                       Log.d("remove_sensor","에러");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
              //  Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    }


