package com.example.liker_0204;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfo_Fragment extends Fragment {
    private static final String TAG_JSON="user_results";
    TextView username_tv, email_tv,birth_tv,phone_tv,gender_tv;
    Button btn_change, btn_removal;
    FragmentManager fm;
    UserService userService;
    String mJsonString;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_info, container, false);
        userService= ApiUtils.getUserService();

        Log.d("username_value",MainActivity.username);
        doUserinfo(MainActivity.username);

        username_tv = v.findViewById(R.id.textView_userinfo_username);
        username_tv.setText(MainActivity.username);

        email_tv = v.findViewById(R.id.textView_userinfo_email);
        birth_tv=v.findViewById(R.id.textView_userinfo_birthyear);
        phone_tv=v.findViewById(R.id.textView_userinfo_phone);
        gender_tv=v.findViewById(R.id.textView_userinfo_gender);

        btn_change = v.findViewById(R.id.button_change_password);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm = getFragmentManager();
                final User_Popup_Change_Fragment user_popup_change_fragment = new User_Popup_Change_Fragment();
                user_popup_change_fragment.show(fm, "hello");
            }
        });

        btn_removal = v.findViewById(R.id.button_id_removal);
        btn_removal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm = getFragmentManager();
                final User_Popup_Removal_Fragment user_popup_removal_fragment = new User_Popup_Removal_Fragment();
                user_popup_removal_fragment.show(fm, "hello");
            }
        });

        return v;
    }

    private void doUserinfo(final String username){
        Call<ResponseBody> call = userService.userinfo(username);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                       String a = response.body().string();
                       Log.d("test",a);

                       try{
                           String email=null;
                           String birth =null;
                           String gender=null;
                           String phone_number=null;
                           JSONObject jsonObject= new JSONObject(a);
                            JSONArray jsonArray = jsonObject.getJSONArray("user_results");
                           for(int i=0;i<jsonArray.length();i++){
                               JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                               email = jsonObject1.getString("email");
                               birth=jsonObject1.getString("birth");
                               gender=jsonObject1.getString("gender");
                               phone_number=jsonObject1.getString("phone_number");
                           }
                           Log.d("email test",email);
                           Log.d("birth test",birth);
                           Log.d("gender test",gender);
                           Log.d("phone_number test",phone_number);

                           email_tv.setText(email);
                           birth_tv.setText(birth);
                           gender_tv.setText(gender);
                           phone_tv.setText(phone_number);


                       }catch (JSONException e){
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
}


