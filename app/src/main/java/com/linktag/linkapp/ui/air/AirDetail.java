package com.linktag.linkapp.ui.air;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.AIRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.AIR_VO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AirDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private EditText etName;
    private EditText etBuyDay;
    private EditText etFilterDay;
    private EditText etMemo;

    private NumberPicker npCycle;
    private NumberPicker npCycle2;

    private Switch swAlarm;

    private TimePicker tpAlarmTime;

    private Button btnSave;
    private Button btnFilterDayUpdate;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    private String ARM_03 = "N";
    private EditText etFocusDay;
    private AIR_VO AIR;
    private String g;
    private String CTM_01;
    private String CTD_02;
    private String CTN_02;

    Calendar AIR_03_Calendar = Calendar.getInstance();
    Calendar AIR_04_Calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    DatePickerDialog.OnDateSetListener AIR_03_DatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            AIR_03_Calendar.set(Calendar.YEAR, year);
            AIR_03_Calendar.set(Calendar.MONTH, month);
            AIR_03_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etFocusDay.setText(sdf.format(AIR_03_Calendar.getTime()));
        }
    };
    DatePickerDialog.OnDateSetListener AIR_04_DatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            AIR_04_Calendar.set(Calendar.YEAR, year);
            AIR_04_Calendar.set(Calendar.MONTH, month);
            AIR_04_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etFocusDay.setText(sdf.format(AIR_04_Calendar.getTime()));
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_detail);

        if(getIntent().hasExtra("AIR")){
            AIR = (AIR_VO) getIntent().getSerializableExtra("AIR");
            g = "UPDATE";
        }
        else{
            AIR = new AIR_VO();
            AIR.AIR_01 = getIntent().getStringExtra("scancode");
            g = "INSERT";
        }

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTD_02 = getIntent().getStringExtra("CTD_02");
        CTN_02 = getIntent().getStringExtra("CTN_02");

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        if(g.equals("UPDATE")){
            if(AIR.AIR_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setMaxWidth(50);
                header.btnHeaderRight1.setMaxHeight(50);
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage("해당 공기청정기를 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestAIR_CONTROL("DELETE");
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                })
                                .show();

                    }
                });
            }
        }

        etName = (EditText) findViewById(R.id.etName);
        etBuyDay = (EditText) findViewById(R.id.etBuyDay);
        etBuyDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFocusDay = (EditText) findViewById(R.id.etBuyDay);
                new DatePickerDialog(mContext, AIR_03_DatePicker, AIR_03_Calendar.get(Calendar.YEAR), AIR_03_Calendar.get(Calendar.MONTH), AIR_03_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etFilterDay = (EditText) findViewById(R.id.etFilterDay);
        etFilterDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFocusDay = (EditText) findViewById(R.id.etFilterDay);
                new DatePickerDialog(mContext, AIR_04_DatePicker, AIR_04_Calendar.get(Calendar.YEAR), AIR_04_Calendar.get(Calendar.MONTH), AIR_04_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etMemo = (EditText) findViewById(R.id.etMemo);

        npCycle = (NumberPicker) findViewById(R.id.npCycle);
        npCycle.setMinValue(0);
        npCycle.setMaxValue(60);
        npCycle2 = (NumberPicker) findViewById(R.id.npCycle2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[] {"일", "개월"});

        swAlarm = (Switch) findViewById(R.id.swAlarm);

        tpAlarmTime = (TimePicker) findViewById(R.id.tpAlarmTime);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                requestAIR_CONTROL(g);
            }
        });
        btnFilterDayUpdate = (Button) findViewById(R.id.btnFilterDayUpdate);
        btnFilterDayUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(mActivity)
                        .setMessage("최근 필터교체 시기를 오늘 일자로 업데이트 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public  void onClick(DialogInterface dialog, int which){
                                requestAIR_CONTROL("CHANGE");
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                            @Override
                            public  void onClick(DialogInterface dialog, int which){
                                return;
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
        if(g.equals("INSERT")){
            btnFilterDayUpdate.setVisibility((View.GONE));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initialize() {
        if(g.equals("UPDATE")){
            getDetail();
        }
        else{
            etBuyDay.setText(sdf.format(AIR_03_Calendar.getTime()));
            etFilterDay.setText(sdf.format(AIR_04_Calendar.getTime()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDetail() {
        etName.setText(AIR.AIR_02);
        etBuyDay.setText(sDateFormat(AIR.AIR_03));
        etFilterDay.setText(sDateFormat(AIR.AIR_04));
        etMemo.setText(AIR.AIR_07);

        npCycle.setValue(AIR.AIR_05);
        if(AIR.AIR_06.equals("D")){
            npCycle2.setValue(0); //일
        }
        else{ //M
            npCycle2.setValue(1); //개월
        }

        Boolean alarm = false;
        if(AIR.ARM_03.equals("Y")){
            alarm = true;
        }
        swAlarm.setChecked(alarm);

        tpAlarmTime.setHour(Integer.parseInt(AIR.AIR_96.substring(8, 10)));
        tpAlarmTime.setMinute(Integer.parseInt(AIR.AIR_96.substring(10)));

        AIR_03_Calendar.set(Integer.parseInt(AIR.AIR_03.substring(0,4)), Integer.parseInt(AIR.AIR_03.substring(4,6))-1, Integer.parseInt(AIR.AIR_03.substring(6,8)));
        AIR_04_Calendar.set(Integer.parseInt(AIR.AIR_04.substring(0,4)), Integer.parseInt(AIR.AIR_04.substring(4,6))-1, Integer.parseInt(AIR.AIR_04.substring(6,8)));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestAIR_CONTROL(String GUBUN) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String AIR_ID = CTN_02; //컨테이너
        String AIR_01 = AIR.AIR_01; //코드번호
        String AIR_02 = etName.getText().toString(); //명칭
        String AIR_03 = etBuyDay.getText().toString().replace("-", ""); //구매일자
        String AIR_04 = etFilterDay.getText().toString().replace("-", ""); //최근 필터 교체일자
        int AIR_05 = npCycle.getValue(); //주기
        String AIR_06 = "M";
        if(npCycle2.getValue() == 0){ //주기구분
            AIR_06 = "D"; //일
        }
        String AIR_07 = etMemo.getText().toString(); //메모
        String AIR_96 = (tpAlarmTime.getHour()<10 ? "0" + String.valueOf(tpAlarmTime.getHour()) : String.valueOf(tpAlarmTime.getHour())) + (tpAlarmTime.getMinute()<10 ? "0" + String.valueOf(tpAlarmTime.getMinute()) : String.valueOf(tpAlarmTime.getMinute())); //알림시간
        String AIR_98 = mUser.Value.OCM_01; //사용자코드
        if(swAlarm.isChecked()){ //알림여부
            ARM_03 = "Y";
        }

        Call<AIRModel> call = Http.air(HttpBaseService.TYPE.POST).AIR_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                AIR_ID,
                AIR_01,
                AIR_02,
                AIR_03,

                AIR_04,
                AIR_05,
                AIR_06,
                AIR_07,
                AIR_96,

                AIR_98,
                ARM_03
        );

        call.enqueue(new Callback<AIRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<AIRModel> call, Response<AIRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                if(GUBUN.equals("INSERT")){
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, CTM_01, CTD_02, AIR.AIR_01);
                    ctds_control.requestCTDS_CONTROL();
                }

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<AIRModel> response = (Response<AIRModel>) msg.obj;

                            if(GUBUN.equals("CHANGE")){
                                callBack(GUBUN, response.body().Data.get(0));
                            }
                            else{
                                finish();
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<AIRModel> call, Throwable t){
                Log.d("AIR_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void callBack(String GUB, AIR_VO data){
        if(data.Validation){
            switch(GUB){
                case "INSERT":
                    finish();
                    break;
                case "UPDATE":
                    finish();
                    break;
                case "CHANGE":
                    setUserData(data);
                    break;
            }
        }

    }

    private void setUserData(AIR_VO airVO) {
        etFilterDay.setText(sDateFormat(airVO.AIR_04));
    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "-" + sDate.substring(4,6) + "-" + sDate.substring(6,8);

        return result;
    }

}
