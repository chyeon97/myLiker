package com.example.liker_0204;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class User_Popup_Removal_Fragment extends DialogFragment {
    User_Popup_Removal_Fragment.DialogResult dialogResult;
    EditText s_name;
    TextView mac_addr;
    String buf;
    static public String s_name_buf,mac_addr_buf;

    public static  final String TAG_EVENT_DALOG = "dialog_event";
    public User_Popup_Removal_Fragment(){}

    public static User_Popup_Removal_Fragment getInstance(){
        User_Popup_Removal_Fragment p = new User_Popup_Removal_Fragment();
        return  p;
    }

    public interface DialogResult{
        void finish(String result);
    }

    public void setDialogResult(User_Popup_Removal_Fragment.DialogResult mydialogRelsut){ // 상위 프레그먼트에게 값을 전달하기 위한 함수
        dialogResult = mydialogRelsut;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_popup_id_removal,container,false);
        Button c_btn=v.findViewById(R.id.button_change_cancel);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return  dialog;
    }



}
