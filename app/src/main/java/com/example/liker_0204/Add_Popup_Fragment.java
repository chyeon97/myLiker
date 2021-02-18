package com.example.liker_0204;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Add_Popup_Fragment extends DialogFragment {
    DialogResult dialogResult;
    EditText s_name;
    TextView mac_addr;
    String buf;
    SensorData sensorData;

    public static  final String TAG_EVENT_DALOG = "dialog_event";
    public Add_Popup_Fragment(){}

    public static Add_Popup_Fragment getInstance(){
        Add_Popup_Fragment p = new Add_Popup_Fragment();
        return  p;
    }

    public interface DialogResult{
        void finish(String result);
    }

    public void setDialogResult(DialogResult mydialogRelsut){ // 상위 프레그먼트에게 값을 전달하기 위한 함수
        dialogResult = mydialogRelsut;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_popup_sensor,container,false);

        Bundle mArgs = getArguments();
        String mac = mArgs.getString("mac_addr");
        mac_addr= v.findViewById(R.id.mac_tv);
        mac_addr.setText(mac);

        s_name=v.findViewById(R.id.editText_sensor_add_sensorname);
    //    buf=s_name.toString();

        Button r_btn = v.findViewById(R.id.button_sensor_add_submit); // 센서추가버튼
        r_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(s_name.getText().length()==0){
                dismiss();
                }
                else {
                    dialogResult.finish(s_name.getText().toString()); // popup 창 닫을때 센서 이름값 저장함수



                }
                //Log.d("sensorname_popup", s_name.getText().toString());

            }
        });
        Button c_btn=v.findViewById(R.id.button_sensor_add_cancel);
        c_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCancelable(false);
        return v;
    }

    public void onResume(){
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





