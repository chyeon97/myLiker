package com.example.liker_0204;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgottenPassword_Page extends AppCompatActivity {
    EditText username;
    Button button_forgotPwd_sendEmail;
    UserService userService;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liker_forgot_password);

        username = findViewById(R.id.editText_username_forgot_pwd);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                button_forgotPwd_sendEmail.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable s) {
                button_forgotPwd_sendEmail.setEnabled(true);
            }
        });
        button_forgotPwd_sendEmail = findViewById(R.id.button_username_check);
        button_forgotPwd_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("username,,,", username.getText().toString());
                userService = ApiUtils.getUserService();
                onTmpPassword(username.getText().toString());
                //  Toast.makeText(getApplicationContext(),"Send to your email,,,",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onTmpPassword(String username) {
        Call<ResObj> call = userService.forgot_password(username);
        //Log.d("username,", username);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {  //통신 성공 시
                    ResObj resObj = response.body();
                 //   Log.d("connection_state", resObj.getMessage());
                    if (resObj.getMessage().equals("true")) {
                        Toast.makeText(getApplicationContext(), "Send email to U", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://gmail.com"));
                        startActivity(intent);
                    } else {
                        Log.d("stateflag", "error");
                    }
                    intent = new Intent(ForgottenPassword_Page.this, LoginPage.class);
                }
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(ForgottenPassword_Page.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


}
