package com.example.liker_0204;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    FragmentManager fragmentManager;
    static String username;
    TextView tb_tv;
    private Fragment chart, user, dashboard, userinfo;

    public List<String> listProviders;
    public String TAG = "LocationProvider";
    private LocationManager locationManager;
    static public double latitude = 0.0;
    static public double longitude = 0.0;
    static SensorData sensorData;
    static UdooSensorData udooSensorData;
  //  public Handler mhandler;
   // long now = System.currentTimeMillis(); // 현재 시간 가져오기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dashboard = new Dash_Fragment();
      sensorData = new SensorData();
      udooSensorData = new UdooSensorData();
      HeartRate_Sensor_Fragment heartRate_sensor_fragment = new HeartRate_Sensor_Fragment();

     //   Date date = new Date(now);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        String getTime = simpleDateFormat.format(date);
//
//        Log.d("현재시간", getTime);


        //HeartRate_SensorCustomAdpater heartRate_sensorCustomAdpater = new HeartRate_SensorCustomAdpater(getApplicationContext(),);

        //GPS권한체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation ;

        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();

            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
        }


        listProviders = locationManager.getAllProviders();

        boolean isEnable = false;
        if (listProviders.get(0).equals(LocationManager.NETWORK_PROVIDER)) {
            Log.d("Networkenable", String.valueOf(isEnable));
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }





        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        this.InitializeLayout();


        fragmentManager = getSupportFragmentManager();
        tb_tv = findViewById(R.id.toolbar_tv); //toolbar의 txtview id 값 가져오기
        tb_tv.setText("DashBoard"); //toolbar text 변경

        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboard).commit(); // 초기 화면 dashboard 화면으로 함

    }


    public void InitializeLayout() {

        //// -------- Toolbar 부분 ------ ////////
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tb_tv = toolbar.getRootView().findViewById(R.id.toolbar_tv); //toolbar의 txtview id 값 가져오기
        tb_tv.setText("MainPage");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        final View nav_header_view = navigationView.getHeaderView(0);


        TextView tv = nav_header_view.findViewById(R.id.nav_username); // 네비게이션 프로필에 username 출력
        tv.setText(username);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);


        ///////////////////////////////// ----네비게이션 드로어 클릭 이벤트 부분-------- //////////////////////////////////////////////
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                TextView tb;
                switch (menuItem.getItemId()) {
                    case R.id.nav_dashboard: //앱 시작시 초기화면
                        tb = findViewById(R.id.toolbar_tv); //toolbar의 txtview id 값 가져오기
                        tb.setText("DashBoard"); //toolbar text 변경
                        if (dashboard == null) {
                            dashboard = new Dash_Fragment();
                            fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboard).commit();
                        }
                        if (dashboard != null) {
                            fragmentManager.beginTransaction().show(dashboard).commit();
                            //       fragmentManager.beginTransaction().hide(user).commit();
                        }
                        if (user != null) {
                            fragmentManager.beginTransaction().hide(user).commit();
                        }
                        if (chart != null) fragmentManager.beginTransaction().hide(chart).commit();
                  //      if (dashboard != null
                        if(userinfo !=null) fragmentManager.beginTransaction().hide(userinfo).commit();
                        break;


                    case R.id.nav_chart: //차트화면
                        tb = findViewById(R.id.toolbar_tv); //toolbar의 txtview id 값 가져오기
                        tb.setText("User Chart"); //toolbar text 변경
                        if (chart == null) {
                            Log.d("test_", "dkkk");
                            chart = new History_Fragment();
                            fragmentManager.beginTransaction().add(R.id.nav_host_fragment, chart).commit();
                        }
                        if (chart != null) fragmentManager.beginTransaction().show(chart).commit();
                        if (dashboard != null) {
                            fragmentManager.beginTransaction().hide(dashboard).commit();
                        }
                        if (user != null) fragmentManager.beginTransaction().hide(user).commit();
                        if(userinfo !=null) fragmentManager.beginTransaction().hide(userinfo).commit();
//
                        break;

                    case R.id.nav_user: //유저센서화면
                        tb = findViewById(R.id.toolbar_tv); //toolbar의 txtview id 값 가져오기
                        tb.setText("Sensor List"); //toolbar text 변경
                        if (user == null) { // 바로 user fragment 실행
                            Log.d("스택갯수", String.valueOf(fragmentManager.getBackStackEntryCount()));
                            Log.d("첫번째", "실행");
                            user = new User_Fragment();
                            fragmentManager.beginTransaction().add(R.id.nav_host_fragment, user).commit();
                        }
                        if (dashboard != null)
                            fragmentManager.beginTransaction().hide(dashboard).commit();
                        if (user != null) {
                            fragmentManager.beginTransaction().show(user).commit();
                        }
                        if (chart != null) {
                            fragmentManager.beginTransaction().hide(chart).commit();
                        }
                        if(userinfo !=null) fragmentManager.beginTransaction().hide(userinfo).commit();


                        break;
                    case R.id.nav_user_info: //유저 정보
                        tb = findViewById(R.id.toolbar_tv); //toolbar의 txtview id 값 가져오기
                        tb.setText("User Information"); //toolbar text 변경
                        if (userinfo == null) {
                            userinfo = new UserInfo_Fragment();
                            fragmentManager.beginTransaction().add(R.id.nav_host_fragment, userinfo).commit();
                        }
                        if (dashboard != null) {
                            fragmentManager.beginTransaction().hide(dashboard).commit();
                        }
                        if (chart != null) fragmentManager.beginTransaction().hide(chart).commit();
                        if (user != null) fragmentManager.beginTransaction().hide(user).commit();
                        if (userinfo != null)
                            fragmentManager.beginTransaction().show(userinfo).commit();

                        break;

                    case R.id.nav_sign_out: //로그아웃
                        SignOut_Popup_Fragment signOut_popup_fragment = new SignOut_Popup_Fragment();
                        signOut_popup_fragment.show(getSupportFragmentManager(), "SignOut");

                        break;
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    //// 이전 버튼 클릭시 네비게이션 드로어 닫기 ////


    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onLocationChanged(Location location) {  /// 실시간 위치정보


        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude(); // 실시간 위도
            longitude = location.getLongitude(); // 실시간 경도

            Log.d(TAG + " NETWORK : ", Double.toString(latitude) + '/' + Double.toString(longitude));
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);


    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
//        try{
//            .polarsensor_disconnect();
//            //       HeartRate_SensorCustomAdpater heartRate_sensorCustomAdpater = new HeartRate_SensorCustomAdpater(getApplicationContext(),this)
//            Toast.makeText(getApplicationContext(),"어플이 종료되어 모든 센서 연결 해제", Toast.LENGTH_LONG).show();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            HeartRate_Sensor_Fragment.polarsensor_disconnect();
         //       HeartRate_SensorCustomAdpater heartRate_sensorCustomAdpater = new HeartRate_SensorCustomAdpater(getApplicationContext(),this)
             Toast.makeText(getApplicationContext(),"어플이 종료되어 모든 센서 연결 해제", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}

