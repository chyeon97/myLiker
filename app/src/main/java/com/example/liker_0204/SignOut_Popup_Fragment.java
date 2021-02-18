package com.example.liker_0204;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignOut_Popup_Fragment extends DialogFragment {

    private Button btn_sign_out;
    UserService userService;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);

        View v =  inflater.inflate(R.layout.liker_popup_sign_out,container,false);
        userService=ApiUtils.getUserService();

        btn_sign_out = v.findViewById(R.id.button_signout_go_main); // signout dialogfragment 버튼
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             onSignout(MainActivity.username);
            }
        });

        return v;
    }

    public void onSignout(final String username){
        Call<ResObj> call = userService.signout(username);
        call.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if(response.isSuccessful()){
                    ResObj resObj = response.body();
                    if(resObj.getMessage().equals("true")){
                        Log.d("signout test",resObj.getMessage());

                   //     onDestroy();
                        Log.d("값",String.valueOf(MainActivity.sensorData.dsn.size()));



                        Intent intent = new Intent(getActivity(),LoginPage.class);
                        startActivity(intent);


                       // intent.putExtra("username",username);



                    }else{
                        Toast.makeText(getContext(),"sign out error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Log.d("error",t.getMessage());
                Toast.makeText(getContext(), t.getMessage() ,Toast.LENGTH_LONG).show();
            }
        });
    }



}
