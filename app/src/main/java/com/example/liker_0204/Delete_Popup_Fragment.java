package com.example.liker_0204;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delete_Popup_Fragment extends DialogFragment {
    Delete_Popup_Fragment.DialogResult dialogResult;
    EditText s_name;
    TextView mac_addr;
    String rm_sensorname;
    UserService userService;
    public Delete_Popup_Fragment(){}


    public interface DialogResult{
        void finish(String result);
    }

    public void setDialogResult(Delete_Popup_Fragment.DialogResult mydialogRelsut){ // 상위 프레그먼트에게 값을 전달하기 위한 함수
        dialogResult = mydialogRelsut;
    }


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.liker_popup_sensor_remove,container,false);
        Log.d("rm_sensorname",rm_sensorname);
        TextView delete_sensorname = v.findViewById(R.id.pop_senssorname);
       delete_sensorname.setText(rm_sensorname+"?");

        Button delete_btn = v.findViewById(R.id.button_sensor_remove_remove); // remove 버튼
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogResult.finish("delete"); // popup 창 닫을때 변수넣기
            }

        });

        Button delete_cancel_btn = v.findViewById(R.id.button_sensor_remove_cancel); // cancel 버튼
        delete_cancel_btn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // 팝업창 닫기
            }
        });

        setCancelable(false);
        return v;
    }

    public void onResume(){ // dialog 크기설정
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.dialog_height);
        getDialog().getWindow().setLayout(width,height);
        super.onResume();
    }


    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return  dialog;
    }



}
