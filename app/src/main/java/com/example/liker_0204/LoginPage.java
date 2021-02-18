package com.example.liker_0204;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class LoginPage extends AppCompatActivity {

    Button login_btn;
    TextView create_account ;
    TextView forgotpwd;
    EditText EditText_username,EditText_password;
    UserService userService;

    @Override
    protected void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.liker_signin_main);


        EditText_username = findViewById(R.id.EditText_username);
        EditText_password = findViewById(R.id.EditText_password);

        // ------ 로그인 버튼 클릭 시 ------///
        login_btn = findViewById(R.id.Button_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userService = ApiUtils.getUserService();

                String username_buf = EditText_username.getText().toString();
                String password = EditText_password.getText().toString();
                    Log.d("login??", username_buf);
                    Log.d("login?",password);

                if(validateLogin(username_buf,password)){
                    doLogin(username_buf,password);
                }
            }
        });

         // Creat Account 클릭시 ////
        create_account = findViewById(R.id.TextView_creataccount);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
         public void onClick(View v) {

                   // Toast.makeText(getApplicationContext(),"select gallery",Toast.LENGTH_LONG).show();
               Intent intent = new Intent(LoginPage.this,Signup_mainPage.class);
               startActivity(intent);
            }
        });

        /// forgot password 클릭 시 /////
        forgotpwd = findViewById(R.id.TextView_forgotpwd);
        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "forgot_password", Toast.LENGTH_SHORT).show();
                Intent intent1= new Intent(LoginPage.this,ForgottenPassword_Page.class);
                startActivity(intent1);
            }
        });

    }

    private boolean validateLogin(String username, String password){
        if(username==null || username.trim().length() == 0){
            Toast.makeText(this,"Username is required" , Toast.LENGTH_LONG).show();
            return  false;
        }
        if(password == null || password.trim().length()==0){
            Toast.makeText(this,"Password is required", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    private void doLogin(final String username, final String password){
        Call<ResObj> call = userService.login(username,password);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if(response.isSuccessful()){
                    ResObj resObj = response.body();
                    if(resObj.getMessage().equals("true")){
                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                       intent.putExtra("username",username);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginPage.this,"The username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Log.d("error",response.message());
                    Toast.makeText(LoginPage.this,"Error! Please try again!",Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error",t.getMessage());
                Toast.makeText(LoginPage.this, t.getMessage() ,Toast.LENGTH_LONG).show();
            }
        });
    }
}
