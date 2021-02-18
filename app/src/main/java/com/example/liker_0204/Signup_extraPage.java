package com.example.liker_0204;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Year;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class Signup_extraPage extends AppCompatActivity {

    TextView username_tv, email_tv;
    EditText password, c_password, birth, phone_number;
    ImageView correct, incorrect;
    RadioButton gender_M, gender_F;
    Button register_btn;
    Character gendervalue;
    Integer birth_buf, phonenumber_buf;
    Date date;
    String password_buf, password_ck_buf;
    UserService userService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liker_signup_extra);

        Intent intent = getIntent();
        username_tv = findViewById(R.id.TextView_signup_extra_username);
        email_tv = findViewById(R.id.TextView_Text_signup_extra_email);
        register_btn = findViewById(R.id.button_signup_extra_signup);
        birth = findViewById(R.id.editText_signup_extra_birth);
        phone_number = findViewById(R.id.editText_signup_extra_phone);
        gender_M = findViewById(R.id.radioButton_signup_extra_male);
        gender_F = findViewById(R.id.radioButton_signup_extra_female);

        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");
        username_tv.setText(username);
        email_tv.setText(email);

        ////////// ---- Password 입력 부분 ------ ///////////
        password = findViewById(R.id.editText_signup_extra_password);
        correct = findViewById(R.id.corrcet_img);
        incorrect = findViewById(R.id.incorrect_img);
        c_password = findViewById(R.id.editText_signup_extra_password_check);
        c_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password_buf = password.getText().toString();
                password_ck_buf = c_password.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (password_buf.equals(password_ck_buf)) {
                    incorrect.setVisibility(View.INVISIBLE);
                    correct.setVisibility(View.VISIBLE); // 체크 이미지 출력

                    birth.setEnabled(true);    // 생년 입력칸 활성화


                    gender_M.setEnabled(true);
                    gender_F.setEnabled(true);
                    phone_number.setEnabled(true);

                    register_btn.setEnabled(true);
                } else {
                    correct.setVisibility(View.INVISIBLE);
                    incorrect.setVisibility(View.VISIBLE);
                }
            }
        });
        gender_M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birth_buf = Integer.parseInt(birth.getText().toString());
                gendervalue = 'M';
            }
        });
        gender_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birth_buf = Integer.parseInt(birth.getText().toString());
                gendervalue = 'F';
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumber_buf = Integer.parseInt(phone_number.getText().toString());

                ///// DB에 유저 정보 insert /////
                userService = ApiUtils.getUserService();
                onRegister(username, password_ck_buf, email, phonenumber_buf.toString(), birth_buf.toString(), gendervalue);


            }
        });
    }

    private void onRegister(String username, String password, String email, String phone_number, String birth, Character gender) {
        Call<ResObj> call = userService.register(username, password, email, phone_number, birth, gender);
//        Log.d("username....", username);
//        Log.d("password.....", password);
//        Log.d("email......", email);
//        Log.d("phone....", phone_number);
//        Log.d("birth....", birth);
//        Log.d("gender...", gender.toString());
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {  //통신 성공 시
                    ResObj resObj = response.body();
                    Log.d("extra_page", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) {
                        Log.d("회원가입", "성공적으로 완료됨.");
                        Toast.makeText(getApplicationContext(), "Welcome to LIKER!!", Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent(Signup_extraPage.this, LoginPage.class);
                        startActivity(intent1);
                    } else {
                        Log.d("stateflag", "error");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(Signup_extraPage.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
