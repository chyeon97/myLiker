package com.example.liker_0204;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.IpSecManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarExerciseEntry;
import polar.com.sdk.api.model.PolarHrData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeartRate_Sensor_Fragment extends Fragment {

    private final static String TAG = MainActivity.class.getSimpleName();
    FragmentManager fm;
    Button add_btn;
    RecyclerView recyclerView;
  //  public SensorData sensorData;
    String user_s_name;
    HeartRate_SensorCustomAdpater heartRateSensorCustomAdpater;
    // public Handler mhandler;

    final Realtime_hr_Handler handler = new Realtime_hr_Handler();
    final Dashboard_Fragment.ValueHandler handler2 = new Dashboard_Fragment.ValueHandler();
    static PolarBleApi api;
 static    Disposable ecgDisposable;
    static   Disposable accDisposable;
  static   Disposable ppgDisposable;
  static   Disposable ppiDisposable;

    private ArrayList<BluetoothDevice> hr_item = new ArrayList<>();
    public String DEVICE_TYPE = "polar";
    static public String DEVICE_ID; // or bt address like F5:A7:B8:EF:7A:D1 // TODO replace with your device id

    private static final int REQUEST_ENABLE_BT = 3;
    public BluetoothAdapter mBluetoothAdapter = null;
    Set<BluetoothDevice> mDevices;
    int mPairedDeviceCount;
    int i = 0;
    String realtime_hr = null;
    Thread thread;
    // Dashboard_Fragment dashboard_fragment = new Dashboard_Fragment();
    UserService userService;
    public final static int Thread_State = 1;
    public final static int Thread_State2 = 2;
    int userdevice_count;
    Intent bluetooth_intent;
    String getTime;

    String DB_Input_Time;
    String DB_Input_hr;
    String value;

    Fragment fragment = new Dashboard_Fragment();
    Bundle bundle = new Bundle();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fm = getFragmentManager();
    //    sensorData = new SensorData();
        heartRateSensorCustomAdpater = new HeartRate_SensorCustomAdpater(getActivity(), MainActivity.sensorData, fm);


        api = PolarBleApiDefaultImpl.defaultImplementation(this.getContext(), PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);
        api.setApiLogger(new PolarBleApi.PolarBleApiLogger() {
            @Override
            public void message(String s) {
                Log.d(TAG, s);
            }
        });

        Log.d(TAG, "version: " + PolarBleApiDefaultImpl.versionInfo());
        api.setApiCallback(new PolarBleApiCallback() {
            @Override
            public void blePowerStateChanged(boolean powered) {
                Log.d("MyApp", "BLE power: " + powered);
            }

            @Override
            public void deviceConnected(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG, "CONNECTED: " + polarDeviceInfo.deviceId);
                DEVICE_ID = polarDeviceInfo.deviceId;
            }

            @Override
            public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG, "CONNECTING: " + polarDeviceInfo.deviceId);
                DEVICE_ID = polarDeviceInfo.deviceId;
            }

            @Override
            public void deviceDisconnected(PolarDeviceInfo polarDeviceInfo) {
                Toast.makeText(getContext(), "hi~!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "DISCONNECTED: " + polarDeviceInfo.deviceId);
                ecgDisposable = null;
                accDisposable = null;
                ppgDisposable = null;
                ppiDisposable = null;
            }

            @Override
            public void ecgFeatureReady(String identifier) {
                Log.d(TAG, "ECG READY: " + identifier);
            }

            @Override
            public void accelerometerFeatureReady(String identifier) {
                Log.d(TAG, "ACC READY: " + identifier);
            }

            @Override
            public void ppgFeatureReady(String identifier) {
                Log.d(TAG, "PPG READY: " + identifier);
            }

            @Override
            public void ppiFeatureReady(String identifier) {
            }

            @Override
            public void biozFeatureReady(String identifier) {

            }

            @Override
            public void hrFeatureReady(String identifier) {
                Log.d("MyApp", "HR READY: " + identifier);
            }

            @Override
            public void disInformationReceived(String identifier, UUID uuid, String value) {
                Log.d(TAG, "uuid: " + uuid + " value: " + value);
            }

            @Override
            public void batteryLevelReceived(String identifier, int level) {
                Log.d(TAG, "BATTERY LEVEL: " + level);

            }

            @Override
            public void hrNotificationReceived(String identifier, final PolarHrData data) {
                // Log.d("MyApp", "HR: " + data.hr);

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            realtime_hr = String.valueOf(data.hr); // HR 받아오는 변수
                            Message msg = handler.obtainMessage(HeartRate_Sensor_Fragment.Thread_State, realtime_hr);
                            handler.sendMessage(msg);

                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            getTime = simpleDateFormat.format(calendar.getTime());
                            Message time_msg = handler.obtainMessage(HeartRate_Sensor_Fragment.Thread_State2, getTime);
                            handler.sendMessage(time_msg);


                            HR_realtime_chart_dbconn(MainActivity.sensorData.dsn.get(0));



                            Thread.sleep(1000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.setDaemon(true);
                thread.start();

            }


            @Override
            public void polarFtpFeatureReady(String identifier) {
                Log.d(TAG, "FTP ready");
            }

        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.heartrate_sensor_sensorlist, container, false);
        userService = ApiUtils.getUserService();

        recyclerView = v.findViewById(R.id.heart_sensor_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        myHRSensor(MainActivity.username);

        add_btn = v.findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBluetooth();
            }


        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        api.backgroundEntered();
    }

    @Override
    public void onResume() {
        super.onResume();
        api.foregroundEntered();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        api.shutDown();
    }

    public void CheckBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) { // 장치가 블루투스 지원하지 않는 경우
            Toast.makeText(getActivity(), "Bluetooth no available", Toast.LENGTH_LONG).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // 블루투스를 지원하지만 비활성 상태인 경우
                // 블루투스를 활성상태로 바꾸기 위해 사용자 동의 요청
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                getActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // 블루투스를 지원하며 활성상태인 경우
                //페어링 된 기기 목록을 보여주고 연결할 장치를 선택.
                selectDevice();
            }
        }
    }

    private void selectDevice() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        //페어링되었던 기기 갯수
        mPairedDeviceCount = mDevices.size();
        //Alertdialog 생성
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog 제목 설정
        builder.setTitle("Select device");

        // 페어링 된 블루투스 장치의 이름 목록 작성
        final List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
            hr_item.add(i, device);
            Log.d("mmm", String.valueOf(hr_item.get(i)));
            i++;

        }
        if (listItems.size() == 0) {
            Log.d("Bluetooth", "No bonded deviec");
            bluetooth_intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(bluetooth_intent);
        } else {
            Log.d("Bluetooth", "Find bonded device");
            //취소 항목 추가
            listItems.add("Cancel");
            listItems.add("Find more devices");

            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dialog dialog_ = (Dialog) dialog;
                    // 연결할 장치를 선택하지 않고 '취소'를 누른 경우
                    if (which == listItems.size() - 2) {
                        Toast.makeText(dialog_.getContext(), "Choose cancel", Toast.LENGTH_SHORT).show();
                    }
                    // 블루투스 설정창으로 이동..
                    else if (which == listItems.size() - 1) {
                        bluetooth_intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(bluetooth_intent);
                    } else {   //취소가 아닌 디바이스를 선택한 경우 해당 기기에 연결
                        BluetoothDevice bluetoothDevice = hr_item.get(which);
                        Log.d("polar_mac", bluetoothDevice.toString());

                        DEVICE_ID = bluetoothDevice.toString();
                        MainActivity.sensorData.mac_addr.add(DEVICE_ID); // 새로운 mac주소 추가
                        for (int i = MainActivity.sensorData.mac_addr.size() - 1; i < MainActivity.sensorData.mac_addr.size(); i++)
                            check_sensor(MainActivity.username, MainActivity.sensorData.mac_addr.get(i));  // 유저 테이블에 똑같은 디바이스 주소가 있는지 확인하는 함수

                    }
                }

            });
            builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
            AlertDialog alert = builder.create();
            alert.show();   //alert 시작
        }
    }

    public void heartrate_dialog() {
        Bundle args = new Bundle();
        for (int i = 0; i < MainActivity.sensorData.mac_addr.size(); i++)
            args.putString("mac_addr",MainActivity. sensorData.mac_addr.get(i)); // fragmentdialog에 센서 mac 주소를 넘겨주기위함

        final Add_Popup_Fragment addPopup_fragment = new Add_Popup_Fragment();
        addPopup_fragment.setArguments(args); // fragmentdialog에 bundle 달기
        addPopup_fragment.show(fm, "hello");


        addPopup_fragment.setDialogResult(new Add_Popup_Fragment.DialogResult() {  // 팝업창에서 입력한 센서이름을 가져오기위한 함수
            @Override
            public void finish(String result) {
                user_s_name = result; // 유저가 입력한 센서이름 값 받아오기

                MainActivity.sensorData.sensorname.add(user_s_name); // 사용자가 입력한 sensorname을 sensordata로추가
                MainActivity. sensorData.sensor_type.add(DEVICE_TYPE); // 장치type 추가

                for (int i = 0; i < MainActivity.sensorData.sensorname.size(); i++) {
                    Log.d("새로운s_name", MainActivity.sensorData.sensorname.get(i));
                    Log.d("새로운mac_addr",MainActivity. sensorData.mac_addr.get(i));
                    Log.d("새로운s_type",MainActivity. sensorData.sensor_type.get(i));

                }

                Log.d("size", String.valueOf(MainActivity.sensorData.sensorname.size()));

                for (int i = MainActivity.sensorData.sensorname.size() - 1; i < MainActivity.sensorData.sensorname.size(); i++)
                    heartRateSensorCustomAdpater.notifyItemInserted(i);

                addPopup_fragment.dismiss(); // 팝업창 닫기


                for (int i = MainActivity.sensorData.sensorname.size() - 1; i < MainActivity.sensorData.sensorname.size(); i++)
                    sensorRegister(MainActivity.username,MainActivity. sensorData.sensorname.get(i), MainActivity.sensorData.mac_addr.get(i), MainActivity.sensorData.sensor_type.get(i)); // 새로운 센서 등록

            }

        });
        recyclerView.setAdapter(heartRateSensorCustomAdpater); // 어뎁터달기


    }


    public void check_sensor(String username, String mac) { // 유저에게 중복된 센서가 있는지 검사함.
        Log.d("check_sensor", "정상..");
        Call<ResponseBody> call = userService.check_sensor(username, mac);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String a = response.body().string();
                        Log.d("sensor_state_test", a);

                        try {
                            JSONObject jsonObject = new JSONObject(a);
                            String state = jsonObject.getString("status");
                            Log.d("state값", state);
                            if (state.equals("0")) {  // 새로운 센서를 등록한다면...
                                heartrate_dialog();
                            } else { // 기존에 등록한 센서라면..

                                Toast.makeText(getContext(), "Select new device...", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getContext(), "The username or password is incorrect", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

    public void sensorRegister(String username, final String s_name, final String mac_addr, final String s_type) { // DB에 센서등록 하기 위한 함수
        Call<ResObj> call = userService.regist_sensor(username, s_name, mac_addr, s_type);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {
                    ResObj resObj = response.body();
                    Log.d("test,,,", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) {
                        mynewHRdsn(MainActivity.username);

                    } else {
                        Toast.makeText(getContext(), "The username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void myHRSensor(final String username) { // 이미 존재하는 Polar센서 출력함수
        Call<ResponseBody> call = userService.userinfo(username);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String a = response.body().string();
                        Log.d("test", a);

                        try {
                            int dsn = 0;
                            String s_name = null;
                            String mac_addr = null;
                            String s_type = null;
                            JSONObject jsonObject = new JSONObject(a);
                            JSONArray jsonArray = jsonObject.getJSONArray("polar_results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                dsn = jsonObject1.getInt("dsn");
                                s_name = jsonObject1.getString("s_name");
                                mac_addr = jsonObject1.getString("mac_addr");
                                s_type = jsonObject1.getString("s_type");


                            }
                            ///기존에 있던 센서 정보를 sensorData로 넣기 /////

                            if (s_name != null && mac_addr != null && s_type != null) {
                                MainActivity. sensorData.dsn.add(dsn);
                                MainActivity. sensorData.sensorname.add(s_name);
                                MainActivity. sensorData.mac_addr.add(mac_addr);
                                MainActivity.  sensorData.sensor_type.add(s_type);

                                for (int i = 0; i < MainActivity.sensorData.sensorname.size(); i++) {
                                    Log.d("dsn test",MainActivity. sensorData.dsn.get(i).toString());
                                    Log.d("기존s_name test",MainActivity. sensorData.sensorname.get(i));
                                    Log.d("기존mac_addr test",MainActivity. sensorData.mac_addr.get(i));
                                    Log.d("기존device_type", MainActivity.sensorData.sensor_type.get(i));
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(getContext(), "sensor not exist ", Toast.LENGTH_SHORT).show();
                }

                userdevice_count =MainActivity.sensorData.sensorname.size();
                Log.d("갯수", String.valueOf(userdevice_count));

                if (userdevice_count != 0) { // 유저가 센서를 가지고있다면
                    for (int i = 0; i < MainActivity.sensorData.sensorname.size(); i++) {
                        heartRateSensorCustomAdpater.notifyItemInserted(i); // 어뎁터에 데이터 삽입
                        recyclerView.setAdapter(heartRateSensorCustomAdpater); // 어뎁터달기
                    }


                    DEVICE_ID = MainActivity.sensorData.mac_addr.get(0);

                    polarsensor_connect(); // 기존 센서 연결


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }


        });

    }

    public void mynewHRdsn(final String username) {  // 새롭게 할당한 센서의 dsn 가져오기
        Call<ResponseBody> call = userService.userinfo(username);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String a = response.body().string();
                        Log.d("test", a);

                        try {
                            int new_dsn = 0;
                            JSONObject jsonObject = new JSONObject(a);
                            JSONArray jsonArray = jsonObject.getJSONArray("polar_results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                new_dsn = jsonObject1.getInt("dsn");
                                Log.d("새로운 dsn", String.valueOf(new_dsn));

                            }
                            MainActivity. sensorData.dsn.add(new_dsn);

                            polarsensor_connect(); // 센서 연결


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(getContext(), "sensor not exist ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

    public void polarsensor_connect() { // 센서연결함수
        try {
            api.connectToDevice(DEVICE_ID);


        } catch (PolarInvalidArgument polarInvalidArgument) {
            polarInvalidArgument.printStackTrace();
        }
    }

    public void sendPolarData(Integer dsn, String time, Double latitude, Double longitude, String heartrate) {
        Call<ResObj> call = userService.polar_data(dsn, time, latitude, longitude, heartrate);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {
                    ResObj resObj = response.body();
                    Log.d("polar_value", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) {
                        Log.d("polar_vlaue", "Uploaded!!!!!:)");
                    } else {
                        Log.d("polar_vlaue", "Not Upload..:(");
                    }
                }


            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class Realtime_hr_Handler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case HeartRate_Sensor_Fragment.Thread_State:
                    DB_Input_hr = String.valueOf(msg.obj);
                    Log.d("MyHR:", realtime_hr);

                    break;

                case HeartRate_Sensor_Fragment.Thread_State2:
                    DB_Input_Time = String.valueOf(msg.obj);
                    Log.d("Realtime", DB_Input_Time);
                    try{
                        for (int i =MainActivity. sensorData.dsn.size() - 1; i <MainActivity. sensorData.dsn.size(); i++)
                            //   Log.d("polar_value count",String.valueOf(i));
                            sendPolarData(MainActivity.sensorData.dsn.get(i), DB_Input_Time, MainActivity.latitude, MainActivity.longitude, realtime_hr); // DB에 측정한 PolarData 업로드
                    }catch (Exception e){
                        e.getMessage();
                    }



                    break;


            }


        }

    }   // 핸들러


    ////////////////////////추가 /////////////////////////
    public void HR_realtime_chart_dbconn(Integer dsn) {
        Log.d("들어왔닝,,,", "hr_realtime");
        Call<ResponseBody> call = userService.realtime_heartrate_app(dsn);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               // Log.d("test", a);
                try {
                    if (response.isSuccessful()) {
                        String a = response.body().string();
                                         Log.d("test_a",a);
                        a= a.replace("[","");
                        a= a.replace("]","");

                        JSONObject jsonObject = new JSONObject(a);
                        value = jsonObject.getString("heart_rate");
                        Log.d("value..",value);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg= handler2.obtainMessage();
                                bundle.putString("heart", value);
                                fragment.setArguments(bundle);
                                Log.d("value",value);
                                msg.setData(bundle);
                                handler2.sendMessage(msg);
                            }
                        });
                        thread.start();



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


   static public void polarsensor_disconnect() {  //센서 연결해제 함수
        if (MainActivity.sensorData.dsn.size() != 0 && MainActivity.sensorData.mac_addr.size() != 0 && MainActivity.sensorData.sensor_type.size() != 0 && MainActivity.sensorData.sensorname.size() != 0) {
            for (int i = MainActivity.sensorData.dsn.size() - 1; i < MainActivity.sensorData.dsn.size(); i++) {
                //     MainActivity.sensorData.dsn.remove(i);
                MainActivity.sensorData.sensorname.remove(i);
                MainActivity.sensorData.sensor_type.remove(i);
                MainActivity.sensorData.mac_addr.remove(i);

            }
            ecgDisposable = null;
            accDisposable = null;
            ppgDisposable = null;
            ppiDisposable = null;


            MainActivity.sensorData.dsn.remove(0);
        }
    }
}



