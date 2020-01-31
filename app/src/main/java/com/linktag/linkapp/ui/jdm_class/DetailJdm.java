package com.linktag.linkapp.ui.jdm_class;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
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
import com.linktag.linkapp.value_object.JdmVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailJdm extends BaseActivity {

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
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<JDMModel> call, Response<JDMModel> response) {

                onBackPressed();
                Toast.makeText(getApplicationContext(), "["+jdmVO.JDM_02+"]"+"  해당 장독정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JDMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

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
        String dayOfTime = jdmVO.getJDM_04().substring(8);
        tv_datePicker.setText(year + "년" + month + "월" + dayOfMonth + "일");

        year = jdmVO.getJDM_96().substring(0, 4);
        month = jdmVO.getJDM_96().substring(4, 6);
        dayOfMonth = jdmVO.getJDM_96().substring(6, 8);
        dayOfTime = jdmVO.getJDM_96().substring(8);
        tv_datePicker2.setText(year + "년" + month + "월" + dayOfMonth + "일");

        ed_name.setText(jdmVO.getJDM_02());
        ed_memo.setText(jdmVO.getJDM_03());

        if (jdmVO.ARM_03.equals("Y")) {
            switch_alarm.setChecked(true);

        } else {
            switch_alarm.setChecked(false);
        }


        timePicker.setCurrentHour(Integer.valueOf(dayOfTime.substring(0,2)));
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
                    monthString = "0" + String.valueOf(month);
                } else {
                    monthString = String.valueOf(month);
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
                    monthString = "0" + String.valueOf(month);
                } else {
                    monthString = String.valueOf(month);
                }
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0" + String.valueOf(dayOfMonth);
                } else {
                    dayOfMonthString = String.valueOf(dayOfMonth);
                }
                tv_datePicker2.setText(year + "년" + monthString + "월" + dayOfMonthString + "일");
            }
        };


        tv_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailJdm.this, callbackMethod, 2019, 5, 24);
                dialog.show();
            }
        });

        btn_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailJdm.this, callbackMethod2, 2019, 5, 24);
                dialog.show();
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                String date = tv_datePicker2.getText().toString().replace("년", "").replace("월", "").replace("일", "");
                String hourOfDayString = "";
                String minuteString = "";
                if (hourOfDay < 12) {
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
