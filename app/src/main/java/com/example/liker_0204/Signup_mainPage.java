package com.example.liker_0204;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup_mainPage extends AppCompatActivity {
    EditText editText_username_login, editText_email_login;
    Button verify_btn, button_username_check, next_btn;
    String username_buffer, email_buffer;
    UserService userService;
    Intent extraintent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liker_signup_main);


        ///// ---------- Duplicate username ---- //////////
        button_username_check = findViewById(R.id.button_username_check);
        editText_username_login = findViewById(R.id.editText_username_login);


        editText_username_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력 시
                button_username_check.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력 끝났을때

                button_username_check.setEnabled(true);
            }
        });

        /// ------ duplicate 버튼 ---------////
        button_username_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_buffer = editText_username_login.getText().toString();
                userService = ApiUtils.getUserService();
                //Toast.makeText(getApplicationContext(),"We are working....",Toast.LENGTH_LONG).show();
                Log.d("buffer", username_buffer);
                doDuplicate(username_buffer);

            }
        });

        ////// --------- Verify 버튼 --------- /////////
        verify_btn = findViewById(R.id.button_email_verify);
        editText_email_login = findViewById(R.id.editText_email_login);
        editText_email_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //   verify_btn.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verify_btn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                verify_btn.setEnabled(true);
            }
        });

        verify_btn.setOnClickListener(new View.OnClickListener() { // verify 버튼 이벤트
            @Override
            public void onClick(View v) {
                //  Toast.makeText(getApplicationContext(),"We are working....",Toast.LENGTH_LONG).show();
                email_buffer = editText_email_login.getText().toString();
                doVerify(username_buffer, email_buffer);
                confirm_verify();

            }

        });


        next_btn=findViewById(R.id.next_btn); // 다음버튼
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_stateflag(username_buffer);
            }
        });
    }

    private void doDuplicate(String id) {
        Call<ResObj> call = userService.duplicate(id);
        Log.d("parameter", id);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {  //통신 성공 시
                    ResObj resObj = response.body();
                    Log.d("message", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) { // 사용가능한 id 일때
                        Log.d("input_username", username_buffer);
                        Toast.makeText(Signup_mainPage.this, "사용가능한 ID입니다.", Toast.LENGTH_LONG).show();
                        editText_email_login.setEnabled(true); // eamil editText 활성화
                    } else { // 불가능한 id 일때
                        Log.d("no use", "존재하는 아이디입니다.");
                        Toast.makeText(Signup_mainPage.this, "아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                        editText_email_login.setEnabled(false); // email editText 비활성화
                    }
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(Signup_mainPage.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void doVerify(final String username, String email) { // 사용자 본인인증 함수
        Call<ResObj> call = userService.verify(username, email);
        Log.d("parameter1", username);
        Log.d("parameter2", email);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {  //통신 성공 시
                    ResObj resObj = response.body();
                    Log.d("message", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) { // 이메일 전송 성공
                        Toast.makeText(Signup_mainPage.this, "Send Email To U", Toast.LENGTH_LONG).show(); // gmail.com 으로 화면전환
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://gmail.com"));
                        startActivity(intent);
                    } else {
                        Toast.makeText(Signup_mainPage.this, "Failed to send eamil", Toast.LENGTH_LONG).show();
                        Log.d("실패", "다시 시도하세요");
                    }
                }
                next_btn.setEnabled(true); // next_btn 활성화
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(Signup_mainPage.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirm_verify(){ // 본인인증 시 넌스값 확인 함수
        Call<ResObj> call = userService.confirm_verify();
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
             //   next_btn.setEnabled(true);
            }
            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {

            }
        });

    }

    private void check_stateflag(String username) { // auth 테이블의 stateflag값에 따른 함수
        Call<ResObj> call = userService.stateflag_check(username);
        Log.d("유저",username);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {  //통신 성공 시
                    ResObj resObj = response.body();
                    Log.d("stateflag",resObj.getMessage());
                    if (resObj.getMessage().equals("true")) {

                        extraintent = new Intent(Signup_mainPage.this,Signup_extraPage.class);
                        extraintent.putExtra("username",username_buffer);
                        extraintent.putExtra("email",email_buffer);
                        startActivity(extraintent);
                    } else {
                        Log.d("stateflag", "error");
                    }


                }

            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(Signup_mainPage.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}



