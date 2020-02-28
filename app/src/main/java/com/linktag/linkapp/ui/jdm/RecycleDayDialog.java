package com.linktag.linkapp.ui.jdm;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import com.linktag.linkapp.R;

import java.util.Date;

public class RecycleDayDialog extends Dialog {

    private CustomDialogListener customDialogListener;

    private TabLayout tabLayout;
    private Context context;
    private String val1 = "";
    private String val2 = "";

    private NumberPicker npCycle;
    private NumberPicker npCycle2;
    private DatePicker datePicker;

    private int pos;

    public RecycleDayDialog(Context context, String val1, String val2) {
        super(context);
        this.context = context;
        this.val1 = val1;
        this.val2 = val2;
    }


    //인터페이스 설정
    interface CustomDialogListener {
        void onPositiveClicked(String val1, String val2, String val3);

        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_recycleday);

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

        final Button okButton = findViewById(R.id.okButton);
        final Button cancelButton = findViewById(R.id.cancelButton);

        npCycle = findViewById(R.id.numberPicker);
        npCycle.setMinValue(0);
        npCycle.setMaxValue(60);
        npCycle.setValue(Integer.parseInt(val1));
        npCycle2 = findViewById(R.id.numberPicker2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[]{"일", "개월"});
        npCycle.setValue(Integer.parseInt(val2));

        datePicker = findViewById(R.id.datePicker);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos == 0){
                    customDialogListener.onPositiveClicked(String.valueOf(npCycle.getValue()), String.valueOf(npCycle2.getValue()), "");
                }
                else if (pos == 1){
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
                    customDialogListener.onPositiveClicked(year,monthString,dayOfMonthString);
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
