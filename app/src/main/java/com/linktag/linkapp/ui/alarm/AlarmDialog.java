package com.linktag.linkapp.ui.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.linktag.linkapp.R;

public class AlarmDialog extends Dialog {

    private CustomDialogListener customDialogListener;

    private Context context;
    private String alarmTime ="";
    private String callBackTime ="";

    private TimePicker timePicker;

    public AlarmDialog(Context context, String callBackTime) {
        super(context);
        this.context = context;
        this.callBackTime = callBackTime;
    }


    //인터페이스 설정
    public interface CustomDialogListener {
        void onPositiveClicked(String time);

        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_alarm);


        alarmTime = callBackTime;
        final Button okButton = findViewById(R.id.okButton);
        final Button cancelButton = findViewById(R.id.cancelButton);
        timePicker = findViewById(R.id.timePicker);

        timePicker.setCurrentHour(Integer.valueOf(callBackTime.substring(0,2)));
        timePicker.setCurrentMinute(Integer.valueOf(callBackTime.substring(2,4)));

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                alarmTime = "";
                if (hourOfDay < 10) {
                    alarmTime += "0" + String.valueOf(hourOfDay);
                } else {
                    alarmTime += String.valueOf(hourOfDay);
                }
                if (minute < 10) {
                    alarmTime += "0" + String.valueOf(minute);
                } else {
                    alarmTime += String.valueOf(minute);
                }

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                customDialogListener.onPositiveClicked(alarmTime);
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dismiss();
            }
        });
    }


}
