package com.linktag.linkapp.ui.jdm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.linktag.linkapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecycleDayDialog extends Dialog {

    private CustomDialogListener customDialogListener;

    private TabLayout tabLayout;
    private Context mContext;
    private String val1 = "";
    private String val2 = "";

    private NumberPicker npCycle;
    private NumberPicker npCycle2;
    private DatePicker datePicker;

    private Calendar calendar;
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

    private int pos;

    public RecycleDayDialog(Context context, String val1, String val2, Calendar calendar) {
        super(context);
        this.mContext = context;
        this.val1 = val1;
        this.val2 = val2;
        this.calendar =calendar;
    }


    //인터페이스 설정
    interface CustomDialogListener {
        void onPositiveClicked(String val1, String val2, String val3, Calendar calendar);

        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_recycleday);

        tabLayout = findViewById(R.id.tab_layout);

        npCycle = findViewById(R.id.numberPicker);
        npCycle.setMinValue(0);
        npCycle.setMaxValue(60);
        npCycle.setValue(Integer.parseInt(val1));
        npCycle2 = findViewById(R.id.numberPicker2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[]{"일", "개월"});
        npCycle2.setValue(Integer.parseInt(val2 == "3"? "0":val2));

        if(val2.equals("3")){
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
            changeView(1);
        }
        else{
            changeView(0);
        }
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

        final Button okButton = findViewById(R.id.okButton);
        final Button cancelButton = findViewById(R.id.cancelButton);

        datePicker = findViewById(R.id.datePicker);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year,monthOfYear,dayOfMonth);

                    }
                });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos == 0){
                    customDialogListener.onPositiveClicked(String.valueOf(npCycle.getValue()), String.valueOf(npCycle2.getValue()), "", calendar);
                }
                else if (pos == 1){


                    Calendar sCalendar = Calendar.getInstance();
                    String monthString = "";
                    String dayOfMonthString = "";
                    String year = String.valueOf(datePicker.getYear());
                    if (datePicker.getMonth() < 10) {
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
                        Toast.makeText(mContext,"청소 예정일은 현재일자보다 커야합니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    customDialogListener.onPositiveClicked(year,monthString,dayOfMonthString,calendar);
                }
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
                break;
            case 1:
                relativeLayout1.setVisibility(View.INVISIBLE);
                RelativeLayout2.setVisibility(View.VISIBLE);
                break;

        }
    }


}
