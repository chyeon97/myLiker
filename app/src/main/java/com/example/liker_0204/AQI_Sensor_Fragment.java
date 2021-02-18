package com.example.liker_0204;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AQI_Sensor_Fragment extends Fragment implements ServiceConnection, SerialListener {
    RecyclerView recyclerView;
    public static UdooSensorData udooSensorData;
    Button add_btn;
    // SerialListener serialListener;

    public BluetoothAdapter mBluetoothAdapter = null;
    private ArrayList<BluetoothDevice> item = new ArrayList<>();
    Set<BluetoothDevice> mDevices;
    private static final int REQUEST_ENABLE_BT = 3;
    int mPairedDeviceCount;
    Intent bluetooth_intent;

    UserService userService;
    FragmentManager fm;
    String user_s_name;
    public String DEVICE_TYPE = "udoo";
    Aqi_SensorCustomAdapter aqi_sensorCustomAdapter;
    int userdevice_count;
    private String deviceAddress;

    private enum Connected {False, Pending, True}

    private SerialSocket socket;
    private SerialService service;
    private boolean initialStart = true;
    private Connected connected = Connected.False;

    Thread thread;
    String co, no2, so2, o3, pm2_5, pm10, tmp;
    String db_input_time;
    public final static int Time = 10;

    final AQI_Sensor_Fragment.Realtime_aqi_handler handler = new AQI_Sensor_Fragment.Realtime_aqi_handler();
    String getTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.aqi_sensor_sensorlist, container, false);
        fm = getFragmentManager();
        userService = ApiUtils.getUserService();
        udooSensorData = new UdooSensorData();
        aqi_sensorCustomAdapter = new Aqi_SensorCustomAdapter(getActivity(), udooSensorData, fm);
        //  service= new SerialService();

        recyclerView = v.findViewById(R.id.aqi_sensor_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        add_btn = v.findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBluetooth();

            }
        });
        return v;
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
                getActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  // 휴대폰 기본 블루투스 창으로 이동
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
            item.add(device);

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

            //item= listItems.toArray(new CharSequence[listItems.size()]);
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
                        BluetoothDevice bluetoothDevice = item.get(0);

                        udooSensorData.mac_addr.add(bluetoothDevice.getAddress()); // 연결된 Udoo센서의 mac_addr 추가
                        Log.d("mac_addr", bluetoothDevice.getAddress());

                        deviceAddress = udooSensorData.mac_addr.get(0);
                        Log.d("deviceAddress", deviceAddress);

                        check_sensor(MainActivity.username, deviceAddress);  // 유저 테이블에 똑같은 디바이스 주소가 있는지 확인하는 함수

                    }
                }

            });
            builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
            AlertDialog alert = builder.create();
            alert.show();   //alert 시작
        }
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
                                aqi_dialog();
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

    public void aqi_dialog() {

        Bundle args = new Bundle();
        for (int i = 0; i < udooSensorData.mac_addr.size(); i++)
            args.putString("mac_addr", udooSensorData.mac_addr.get(i)); // fragmentdialog에 센서 mac 주소를 넘겨주기위함

        final Add_Popup_Fragment addPopup_fragment = new Add_Popup_Fragment();
        addPopup_fragment.setArguments(args); // fragmentdialog에 bundle 달기
        addPopup_fragment.show(fm, "hello");


        addPopup_fragment.setDialogResult(new Add_Popup_Fragment.DialogResult() {  // 팝업창에서 입력한 센서이름을 가져오기위한 함수
            @Override
            public void finish(String result) {
                user_s_name = result; // 유저가 입력한 센서이름 값 받아오기

                udooSensorData.sensorname.add(user_s_name); // 사용자가 입력한 sensorname을 sensordata로추가
                udooSensorData.sensor_type.add(DEVICE_TYPE); // 장치type 추가

                for (int i = 0; i < udooSensorData.sensorname.size(); i++) {
                    Log.d("새로운s_name", udooSensorData.sensorname.get(i));
                    Log.d("새로운mac_addr", udooSensorData.mac_addr.get(i));
                    Log.d("새로운s_type", udooSensorData.sensor_type.get(i));

                }

                Log.d("size", String.valueOf(udooSensorData.sensorname.size()));

                for (int i = udooSensorData.sensorname.size() - 1; i < udooSensorData.sensorname.size(); i++)
                    aqi_sensorCustomAdapter.notifyItemInserted(i);

                addPopup_fragment.dismiss(); // 팝업창 닫기


                for (int i = udooSensorData.sensorname.size() - 1; i < udooSensorData.sensorname.size(); i++)
                    sensorRegister(MainActivity.username, udooSensorData.sensorname.get(i), udooSensorData.mac_addr.get(i), udooSensorData.sensor_type.get(i)); // 새로운 센서 등록

            }

        });
        recyclerView.setAdapter(aqi_sensorCustomAdapter); // 어뎁터달기


    }

    public void sensorRegister(String username, String s_name, String mac_addr, String s_type) { // 센서등록 함수
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
                            JSONArray jsonArray = jsonObject.getJSONArray("udoo_results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                dsn = jsonObject1.getInt("dsn");
                                s_name = jsonObject1.getString("s_name");
                                mac_addr = jsonObject1.getString("mac_addr");
                                s_type = jsonObject1.getString("s_type");


                            }
                            ///기존에 있던 센서 정보를 sensorData로 넣기 /////

                            if (s_name != null && mac_addr != null && s_type != null) {
                                udooSensorData.dsn.add(dsn);
                                udooSensorData.sensorname.add(s_name);
                                udooSensorData.mac_addr.add(mac_addr);
                                udooSensorData.sensor_type.add(s_type);

                                for (int i = 0; i < udooSensorData.sensorname.size(); i++) {
                                    Log.d("dsn test", udooSensorData.dsn.get(i).toString());
                                    Log.d("기존s_name test", udooSensorData.sensorname.get(i));
                                    Log.d("기존mac_addr test", udooSensorData.mac_addr.get(i));
                                    Log.d("기존device_type", udooSensorData.sensor_type.get(i));
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

                userdevice_count = udooSensorData.sensorname.size();
                Log.d("갯수", String.valueOf(userdevice_count));

                if (userdevice_count != 0) { // 유저가 센서를 가지고있다면
                    for (int i = udooSensorData.sensorname.size() - 1; i < udooSensorData.sensorname.size(); i++) {
                        aqi_sensorCustomAdapter.notifyItemRemoved(i); // 기존에 insert된 아이템 삭제
                        aqi_sensorCustomAdapter.notifyItemInserted(i); // 어뎁터에 데이터 삽입
                    }

                    deviceAddress = udooSensorData.mac_addr.get(0);

                    connect();
                }
                recyclerView.setAdapter(aqi_sensorCustomAdapter); // 어뎁터달기
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void mynewHRdsn(String username) {  // 새롭게 할당한 dsn 값 가져오기
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
                            JSONArray jsonArray = jsonObject.getJSONArray("udoo_results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                new_dsn = jsonObject1.getInt("dsn");
                                Log.d("새로운 dsn", String.valueOf(new_dsn));

                            }

                            udooSensorData.dsn.add(new_dsn);

                            Log.d("uudooSensorData.dsn.크기", String.valueOf(udooSensorData.dsn.size()));
                            Log.d("udoo_Sensormac_addr크기", String.valueOf(udooSensorData.mac_addr.size()));
                            Log.d("udoo_sensorname크기", String.valueOf(udooSensorData.sensorname.size()));
                            Log.d("sensor_type", String.valueOf(udooSensorData.sensor_type.size()));

                            ////// 센서 연결 ///////
                            connect();

                            ///////////////////

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

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.d("start", "실행");
        super.onStart();
        if (service != null) {
            Log.d("service", "여기야~");
            service.attach(this);
        } else
            getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onResume() {
        Log.d("Resume", "실행");
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
        }
    }

    public void connect() {
        try {
            Log.d("connect", "실행");
            //  Log.d("mac주소",String.valueOf(udooSensorData.mac_addr.size()));

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            String deviceName = device.getName() != null ? device.getName() : device.getAddress();
            status("connecting...");
            connected = Connected.Pending;
            socket = new SerialSocket();
            service.connect(this, "Connected to " + deviceName);
            socket.connect(getContext(), service, device);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        socket.disconnect();
        socket = null;
    }

    private void send(String str) {
        Log.d("send", "실행");
        if (connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorNavyLiker)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            byte[] data = (str + "\n").getBytes();
            socket.write(data);


        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
        Log.d("receive", "실행");
        Log.d("data", new String(data));

        String s1= new String(data);

        String[] array = s1.split(",");
        for(int i=0;i<array.length; i++){
            Log.d("array"+i+":",array[i]);
        }

        String no2 = array[2];
        String o3=array[3];
        String co = array[4];
        String so2=array[5];
        String pm25 = array[6];
        int ran = (int) (Math.random() * ((100 - 10) +1) ) + 10;
        String pm10 = String.valueOf(ran);

////////////-/////////////////////------------DB에 연동하는 부분.//////////////////////////////////////////////
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    getTime = simpleDateFormat.format(calendar.getTime());
                    Message time_msg = handler.obtainMessage(Time, getTime);
                    handler.sendMessage(time_msg);

                    Message msg2 = handler.obtainMessage(2, no2);
                    handler.sendMessage(msg2);
                    Message msg3 = handler.obtainMessage(3, o3);
                    handler.sendMessage(msg3);
                    Message msg4 = handler.obtainMessage(4, co);
                    handler.sendMessage(msg4);
                    Message msg5 = handler.obtainMessage(5, so2);
                    handler.sendMessage(msg5);
                    Message msg6 = handler.obtainMessage(6, pm25);
                    handler.sendMessage(msg6);
                    Message msg7 = handler.obtainMessage(7, pm10);
                    handler.sendMessage(msg7);


                    //      Thread.sleep(1000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    public class Realtime_aqi_handler extends Handler {   // AQI 핸들러
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {


                case 2:
                    no2 = String.valueOf(msg.obj);
                    Log.d("no2 :", no2);
                    break;
                case 3:
                    o3 = String.valueOf(msg.obj);
                    Log.d("o3 :", o3);
                    break;
                case 4:
                    co = String.valueOf(msg.obj);
                    Log.d("co :", co);
                    break;
                case 5:
                    so2 = String.valueOf(msg.obj);
                    Log.d("so2 :", so2);
                    break;
                case 6:
                    pm2_5 = String.valueOf(msg.obj);
                    Log.d("pm25 :", pm2_5);
                    break;

                case 7:
                    pm10 = String.valueOf(msg.obj);
                    Log.d("pm10 :", pm10);
                    break;

                case Time:
                    db_input_time = String.valueOf(msg.obj);
                    Log.d("currnet_time", db_input_time);

                    for (int u = 0; u < udooSensorData.dsn.size(); u++)
                        sendUdooData(udooSensorData.dsn.get(u), db_input_time, co, no2, so2, o3, pm2_5, pm10, MainActivity.latitude, MainActivity.longitude); // DB에 UdooData 업로드


                    break;

            }


        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorNavyLiker)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Log.d("status", spn.toString());
        // Log.d("status",receiveText.getText().toString());

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        Log.d("실행,,?", "웅");
        service = ((SerialService.SerialBinder) binder).getService();

        myHRSensor(MainActivity.username); // 기존에 등록된 센서 실행

        if (initialStart && isResumed()) {
            initialStart = false;
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //  service = null;
    }

    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;

        send("start");
    }

    @Override
    public void onSerialConnectError(Exception e) {
        Log.d("에러,,?", "웅");
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        disconnect();
    }

    public void sendUdooData(Integer dsn, String time, String co, String no2, String so2, String o3, String pm2_5, String pm10, Double latitude, Double longitude) {
        Call<ResObj> call = userService.udoo_data(dsn, time, co, no2, so2, o3, pm2_5, pm10, latitude, longitude);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {
                    ResObj resObj = response.body();
                    Log.d("polar_value", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) {
                        Log.d("udoo_data", "Uploaded!!!!!:)");
                    } else {
                        Log.d("udoo_data", "Not Upload..:(");
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


}
