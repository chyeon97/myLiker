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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Aqi_SensorCustomAdapter extends RecyclerView.Adapter<Aqi_SensorCustomAdapter.ViewHolder>  {

    private Context context;
    private FragmentManager fragmentManager;
    UserService userService;
    ImageButton img;

    public Aqi_SensorCustomAdapter(Context context, UdooSensorData udooSensorData, FragmentManager fragmentManager) {
        AQI_Sensor_Fragment.udooSensorData = udooSensorData;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.aqi_sensorlist_item, parent, false);
        img = view.findViewById(R.id.imageButton_delete_btn); // 센서 삭제버튼
        userService=ApiUtils.getUserService();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.sensorname.setText(AQI_Sensor_Fragment.udooSensorData.sensorname.get(position));

        img.setOnClickListener(new View.OnClickListener() { // 센서 삭제버튼 클릭 리스너
            @Override
            public void onClick(View v) {
                final Delete_Popup_Fragment delete_popup_fragment = new Delete_Popup_Fragment();

                delete_popup_fragment.rm_sensorname = AQI_Sensor_Fragment.udooSensorData.sensorname.get(position);

                delete_popup_fragment.show(fragmentManager, "true");
                delete_popup_fragment.setDialogResult(new Delete_Popup_Fragment.DialogResult() { // 팝업창에서 보낸 변수값 들고오기
                    @Override
                    public void finish(String result) {
                        Log.d("popup", result);
                        if (result.equals("delete")) {
                            Log.d("position",String.valueOf(position));


                            Log.d("sensorData_type value",String.valueOf(AQI_Sensor_Fragment.udooSensorData.sensor_type.get(position)));
                            Log.d("sensorData_mac value",String.valueOf(AQI_Sensor_Fragment.udooSensorData.mac_addr.get(position)));
                            Log.d("sensorData_name value",String.valueOf(AQI_Sensor_Fragment.udooSensorData.sensorname.get(position)));
                            Log.d("sensorData_dsn",String.valueOf(AQI_Sensor_Fragment.udooSensorData.dsn.get(position)));

                            delete_mySensor(MainActivity.username,AQI_Sensor_Fragment.udooSensorData.dsn.get(position));
                            //sensorData.sensorname.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
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
        return AQI_Sensor_Fragment.udooSensorData.sensorname.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sensorname;

        public ViewHolder(@NonNull View itemView) {
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
                        for(int i = AQI_Sensor_Fragment.udooSensorData.dsn.size()-1; i<AQI_Sensor_Fragment.udooSensorData.dsn.size();i++){
                            AQI_Sensor_Fragment.udooSensorData.dsn.remove(i);
                            AQI_Sensor_Fragment.udooSensorData.mac_addr.remove(i);
                            AQI_Sensor_Fragment.udooSensorData.sensorname.remove(i);
                            AQI_Sensor_Fragment.udooSensorData.sensor_type.remove(i);

                            Log.d("SensorData value",String.valueOf(AQI_Sensor_Fragment.udooSensorData.dsn.size()));


                            //polarsensor_disconnect();

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
