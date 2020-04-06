package com.linktag.linkapp.ui.vac;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.linkapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VadAlarmDialog extends Dialog {

    private CustomDialogListener customDialogListener;

    private Context mContext;
    private String alarmTime ="";
    private String callBackTime ="";

    private Button okButton;
    private Button cancelButton;
    private TabLayout tabLayout;

    private DatePicker datePicker;
    private TimePicker timePicker;

    private Calendar calendar;
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    private int pos;

    public VadAlarmDialog(Context context, Calendar calendar) {
        super(context);
        this.mContext = context;
        this.calendar =calendar;
    }


    //인터페이스 설정
    public interface CustomDialogListener {
        void onPositiveClicked(Calendar calendar);

        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_vad_alarm);


        alarmTime = callBackTime;
        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);


        datePicker = findViewById(R.id.datePicker);


        tabLayout = findViewById(R.id.tab_layout);
        changeView(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pos = tab.getPosition();
                changeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year,monthOfYear,dayOfMonth);

                    }
                });



        timePicker = findViewById(R.id.timePicker);

        timePicker.setCurrentHour(Integer.valueOf(formatTime.format(calendar.getTime()).substring(0,2)));
        timePicker.setCurrentMinute(Integer.valueOf(formatTime.format(calendar.getTime()).substring(2,4)));


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

                calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(alarmTime.substring(0,2)));
                calendar.set(Calendar.MINUTE,Integer.parseInt(alarmTime.substring(2,4)));
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar sCalendar = Calendar.getInstance();
                String monthString = "";
                String dayOfMonthString = "";
                String year = String.valueOf(datePicker.getYear());
                if (datePicker.getMonth()+1 < 10) {
                    monthString = "0" + String.valueOf(datePicker.getMonth() + 1);
                } else {
                    monthString = String.valueOf(datePicker.getMonth()+1);
                }
                if (datePicker.getDayOfMonth() < 10) {
                    dayOfMonthString = "0" + String.valueOf(datePicker.getDayOfMonth());
                } else {
                    dayOfMonthString = String.valueOf(datePicker.getDayOfMonth());
                }

                if(Integer.parseInt(year + monthString + dayOfMonthString) <= Integer.parseInt(formatDate.format(sCalendar.getTime()))){
                    Toast.makeText(mContext, mContext.getString(R.string.vac_dialog_text4), Toast.LENGTH_LONG).show();
                    return;
                }
                customDialogListener.onPositiveClicked(calendar);
                // 커스텀 다이얼로그를 종료한다.
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

    private void changeView(int index) {
        RelativeLayout relativeLayout1 = findViewById(R.id.tab1_layout);
        RelativeLayout RelativeLayout2 = findViewById(R.id.tab2_layout);

        switch (index) {
            case 0:
                relativeLayout1.setVisibility(View.VISIBLE);
                RelativeLayout2.setVisibility(View.INVISIBLE);
                pos = 0;
                break;
            case 1:
                relativeLayout1.setVisibility(View.INVISIBLE);
                RelativeLayout2.setVisibility(View.VISIBLE);
                pos = 1;
                break;

        }
    }

}
