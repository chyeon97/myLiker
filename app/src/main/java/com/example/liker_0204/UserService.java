package com.example.liker_0204;

import java.time.Year;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @FormUrlEncoded
    @POST("/signin_app")  //로그인
    Call<ResObj> login(@Field("username") String username, @Field("password") String password);

    @GET("/signout_app")  // 로그아웃
    Call<ResObj> signout(@Query("username") String username);

    @GET("/check_duplicate_app/{id}")  // 회원가입 시 아이디 중복
   Call<ResObj> duplicate(@Query("id") String username);

    @GET("/self_verify_app/{username}/{email}") // 회원가입 시 이메일 인증
    Call<ResObj> verify(@Query("username") String username , @Query("email")String email);


    @GET("/self_confirm_verify_app") // 본인인증 시 링크 출력을 위한 함수
    Call<ResObj> confirm_verify();

    @GET("/check_it_app") // 본인인증 시 auth 테이블의 stateflag값
    Call<ResObj> stateflag_check(@Query("username") String username);

    @FormUrlEncoded
    @POST("/register_app")  //회원 등록
    Call<ResObj> register(@Field("username") String username, @Field("first_password") String password,
                          @Field("email") String email, @Field("phone_number")String phone_number,
                          @Field("birth") String birth , @Field("gender") Character gender);
    @FormUrlEncoded
    @POST("/verify_app")
    Call<ResObj> forgot_password(@Field("username") String username);


    @GET("/userinfo_app/{username}")  // 유저 정보
    Call<ResponseBody> userinfo(@Query("username") String username);

    @FormUrlEncoded
    @POST("/check_pwd_app/{current_pwd}")  //로그인
    Call<ResObj> check_pwd(@Field("username") String username, @Field("current_pwd") String password);

    @FormUrlEncoded
    @POST("/check_sensor_app") // 센서 중복 확인
    Call<ResponseBody> check_sensor(@Field("username") String username, @Field("mac_addr") String mac_addr);

    @FormUrlEncoded
    @POST("/regist_sensor_app/{s_name}/{s_type}/{mac_addr}") // 센서 등록
    Call<ResObj> regist_sensor(@Field("username") String username, @Field("s_name") String s_name, @Field("mac_addr") String mac_addr , @Field("s_type") String s_type);


    @FormUrlEncoded
    @POST(" /remove_sensor_app/{dsn") // 센서 등록해지
    Call<ResObj> remove_sensor(@Field("username") String username, @Field("dsn") Integer dsn);

    @FormUrlEncoded
    @POST(" /send_polardata_app") // 폴라센서 value 등록
    Call<ResObj> polar_data(@Field("dsn") Integer dsn, @Field("time") String time, @Field("latitude") Double latitude, @Field("longitude") Double longitude, @Field("heartrate") String heartrate);


 @FormUrlEncoded
 @POST(" /send_udoodata_app") // 폴라센서 value 등록
 Call<ResObj> udoo_data(@Field("dsn") Integer dsn, @Field("time") String time, @Field("co_aqi") String co, @Field("no2_aqi") String no2,
                        @Field("so2_aqi") String so2,@Field("o3_aqi") String o3,@Field("pm2_5_aqi") String pm2_5,@Field("pm10_aqi") String pm10,
                        @Field("latitude") Double latitude, @Field("longitude") Double longitude);


 @FormUrlEncoded
 @POST("/realtime_heartrate_app") // realtime
 Call<ResponseBody> realtime_heartrate_app(@Field("dsn")Integer dsn);


 @FormUrlEncoded
 @POST("/historic_heartrate_app/{start_time}/{end_time}") // 센서 중복 확인
 Call<ResponseBody> historic_heartrate_app(@Field("start_time") String start_time, @Field("end_time") String end_time, @Field("dsn") Integer dsn);
 @GET("/get_aqi_app")  // udoo
 Call<ResponseBody> get_aqi_app(@Query("username") String username);

 @GET("/historic_aqi_app/{start_time}/{end_time}/{dsn}")  // udoo his
 Call<ResponseBody> get_hisaqi(@Query("username") String username,@Query("start_time") String start_time,@Query("end_time") String end_time,@Query("dsn") Integer dsn );



}
