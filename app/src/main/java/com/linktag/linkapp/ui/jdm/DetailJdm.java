package com.linktag.linkapp.ui.jdm_class;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm_service.AlarmHATT;
import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;
import com.linktag.linkapp.value_object.JdmVO;

import java.io.Serializable;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailJdm  extends BaseActivity implements Serializable{

    private EditText ed_name;
    private EditText ed_memo;
    private TextView tv_datePicker;
    private TextView tv_datePicker2;
    private Button btn_datePicker;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private DatePickerDialog.OnDateSetListener callbackMethod2;
    private TimePicker timePicker;
    private Switch switch_alarm;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private Button bt_save;

    private JdmVO jdmVO;

    private Calendar calendar = Calendar.getInstance();


    private String hourOfDayString;
    private String minuteString ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jdm);


        initLayout();

        initialize();

    }

    public void requestJMD_CONTROL() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailJdm.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        if (jdmVO.ARM_03.equals("Y")) {

            Intent intent = new Intent(mContext, Alarm_Receiver.class);
            intent.putExtra("notify_id",jdmVO.getARM_04());
            intent.putExtra("calDateTime",jdmVO.getJDM_96());
            intent.putExtra("contentTitle","장독관리" + jdmVO.getJDM_02());
            intent.putExtra("contentText",jdmVO.getJDM_03());
            intent.putExtra("className", ".ui.intro.Intro");
            intent.putExtra("gotoActivity", ".ui.main.JDMMain");


            JdmVO jdmvo = new JdmVO();
            jdmvo.setJDM_01(jdmVO.getJDM_01());
            jdmvo.setJDM_02(jdmVO.getJDM_02());
            jdmvo.setJDM_03(jdmVO.getJDM_03());
            jdmvo.setJDM_04(jdmVO.getJDM_04());
            jdmvo.setJDM_96(jdmVO.getJDM_96());
            jdmvo.setARM_03(jdmVO.getARM_03());

            intent.putExtra("JdmVO", jdmvo);

            new AlarmHATT(mContext).Alarm(intent);
        }
        else{
            cancelAlarm(mContext, jdmVO.getARM_04());
        }

        Call<JDMModel> call = Http.jdm(HttpBaseService.TYPE.POST).JDM_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE",
                "1",
                jdmVO.JDM_01,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                jdmVO.JDM_04,
                jdmVO.JDM_96,
                "M191100001",
                "M191100001",
                jdmVO.ARM_03
        );


        call.enqueue(new Callback<JDMModel>() {
            @Override
            public void onResponse(Call<JDMModel> call, Response<JDMModel> response) {

                onBackPressed();
                Toast.makeText(getApplicationContext(), "[" + jdmVO.JDM_02 + "]" + "  해당 장독정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JDMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    public void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm_Receiver.class);
        intent.putExtra("notify_id", alarmId);
        intent.putExtra("ContentTitle", "");
        intent.putExtra("contentText", "");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    protected void initLayout() {

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_memo = (EditText) findViewById(R.id.ed_memo);
        tv_datePicker = (TextView) findViewById(R.id.tv_datePicker);
        tv_datePicker2 = (TextView) findViewById(R.id.tv_datePicker2);
        btn_datePicker = (Button) findViewById(R.id.btn_datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        bt_save = (Button) findViewById(R.id.bt_save);
        switch_alarm = (Switch) findViewById(R.id.switch_alarm);

        jdmVO = (JdmVO) getIntent().getSerializableExtra("JdmVO");


        String year = jdmVO.getJDM_04().substring(0, 4);
        String month = jdmVO.getJDM_04().substring(4, 6);
        String dayOfMonth = jdmVO.getJDM_04().substring(6, 8);


        tv_datePicker.setText(year + "년" + month + "월" + dayOfMonth + "일");

        year = jdmVO.getJDM_96().substring(0, 4);
        month = jdmVO.getJDM_96().substring(4, 6);
        dayOfMonth = jdmVO.getJDM_96().substring(6, 8);
        String dayOfTime = jdmVO.getJDM_96().substring(8);

        hourOfDayString = dayOfTime.substring(0,2);
        minuteString = dayOfTime.substring(2);



        tv_datePicker2.setText(year + "년" + month + "월" + dayOfMonth + "일");

        ed_name.setText(jdmVO.getJDM_02());

        //명칭은 읽기전용으로 일단은...
        ed_name.setEnabled(false);

        ed_memo.setText(jdmVO.getJDM_03());

        if (jdmVO.ARM_03.equals("Y")) {
            switch_alarm.setChecked(true);

        } else {
            switch_alarm.setChecked(false);
        }


        timePicker.setCurrentHour(Integer.valueOf(dayOfTime.substring(0, 2)));
        timePicker.setCurrentMinute(Integer.valueOf(dayOfTime.substring(2)));
    }

    @Override
    protected void initialize() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthString = "";
                String dayOfMonthString = "";
                if (month < 10) {
                    monthString = "0" + String.valueOf(month + 1);
                } else {
                    monthString = String.valueOf(month + 1);
                }
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0" + String.valueOf(dayOfMonth);
                } else {
                    dayOfMonthString = String.valueOf(dayOfMonth);
                }
                jdmVO.setJDM_04(String.valueOf(year) + monthString + dayOfMonth);
                tv_datePicker.setText(year + "년" + monthString + "월" + dayOfMonthString + "일");
            }
        };

        callbackMethod2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String monthString = "";
                String dayOfMonthString = "";
                if (month < 10) {
                    monthString = "0" + String.valueOf(month + 1);
                } else {
                    monthString = String.valueOf(month + 1);
                }
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0" + String.valueOf(dayOfMonth);
                } else {
                    dayOfMonthString = String.valueOf(dayOfMonth);
                }
                tv_datePicker2.setText(year + "년" + monthString + "월" + dayOfMonthString + "일");
                jdmVO.setJDM_96(year+monthString+dayOfMonthString + hourOfDayString + minuteString);
            }
        };


        tv_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailJdm.this, callbackMethod, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();
            }
        });

        btn_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailJdm.this, callbackMethod2, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                String date = tv_datePicker2.getText().toString().replace("년", "").replace("월", "").replace("일", "");
                if (hourOfDay < 10) {
                    hourOfDayString = "0" + String.valueOf(hourOfDay);
                } else {
                    hourOfDayString = String.valueOf(hourOfDay);
                }
                if (minute < 10) {
                    minuteString = "0" + String.valueOf(minute);
                } else {
                    minuteString = String.valueOf(minute);
                }
                jdmVO.setJDM_96(date + hourOfDayString + minuteString);

            }
        });


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestJMD_CONTROL();
           }
        });


        switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    jdmVO.setARM_03("Y");
                } else {
                    switch_alarm.setChecked(false);
                    jdmVO.setARM_03("N");
                }
            }
        });
    }

}
