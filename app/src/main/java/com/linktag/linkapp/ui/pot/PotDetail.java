package com.linktag.linkapp.ui.pot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.R;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.master_log.MasterLog;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.LogVO;
import com.linktag.linkapp.value_object.PotVO;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private EditText etName;
    private EditText etMemo;

    private ImageView imgAlarm;
    private ImageView imgTime;
    private ImageView imgWater;
    private ImageView imgPreWaterDayIcon;

    private TextView tvDDAY;
    private TextView tvCycle;
    private TextView tvPreWaterDay;
    private TextView tvNextWaterDay;
    private TextView lbWater;
    private TextView tvLog;

    private View lineDDAY;
    private TextView tvDayLabel;

    private Button btnSave;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    private CtdVO intentVO;
    private PotVO POT;
    private String GUBUN;

    Calendar POT_03_C = Calendar.getInstance(); //최근물주기
    Calendar POT_96_C = Calendar.getInstance(); //다음물주기(알림)
    Calendar TODAY = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_detail);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(getIntent().hasExtra("POT")){
            POT = (PotVO) getIntent().getSerializableExtra("POT");
            GUBUN = "UPDATE";
        }
        else{
            POT = new PotVO();
            POT.POT_01 = getIntent().getStringExtra("scancode");
            GUBUN = "INSERT";
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        clearCalTime(TODAY);
        clearCalTime(POT_03_C);
        clearCalTime(POT_96_C);

        lineDDAY = (View) findViewById(R.id.lineDDAY);
        tvDayLabel = (TextView) findViewById(R.id.tvDayLabel);

        etName = (EditText) findViewById(R.id.etName);
        etMemo = (EditText) findViewById(R.id.etMemo);

        imgAlarm = (ImageView) findViewById(R.id.imgAlarm);
        imgAlarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(POT.ARM_03.equals("Y")){
                    POT.ARM_03 = "N";
                }
                else{
                    POT.ARM_03 = "Y";
                }

                setAlarm();
            }
        });

        imgTime = (ImageView) findViewById(R.id.imgTime);
        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog dialog = new TimePickerDialog(mActivity, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String tmp = "";
                        if(hour<10){
                            tmp = "0" + String.valueOf(hour);
                        }
                        else{
                            tmp = String.valueOf(hour);
                        }

                        if(min<10){
                            tmp += "0" + String.valueOf(min);
                        }
                        else{
                            tmp += String.valueOf(min);
                        }

                        POT.POT_96 = POT.POT_96.substring(0, 8) + tmp;
                    }
                }, Integer.valueOf(POT.POT_96.substring(8,10)), Integer.valueOf(POT.POT_96.substring(10,12)), false);

                dialog.show();

            }
        });
        imgWater = (ImageView) findViewById(R.id.imgWater);
        lbWater = (TextView) findViewById(R.id.lbWater);

        tvDDAY = (TextView) findViewById(R.id.tvDDAY);
        tvCycle = (TextView) findViewById(R.id.tvCycle);
        tvCycle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CycleDialog();
            }
        });
        imgPreWaterDayIcon = (ImageView) findViewById(R.id.imgPreWaterDayIcon);
        imgPreWaterDayIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                preWaterDayDialog();
            }
        });
        tvPreWaterDay = (TextView) findViewById(R.id.tvPreWaterDay);
        tvPreWaterDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                preWaterDayDialog();
            }
        });
        tvNextWaterDay = (TextView) findViewById(R.id.tvNextWaterDay);

        tvLog = (TextView) findViewById(R.id.tvLog);
        tvLog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LogVO LOG = new LogVO();
                LOG.LOG_ID = intentVO.CTN_02;
                LOG.LOG_01 = POT.POT_01;
                LOG.LOG_98 = mUser.Value.OCM_01;
                LOG.SP_NAME = "SP_POTL_CONTROL";

                Intent intent = new Intent(mContext, MasterLog.class);
                intent.putExtra("LOG", LOG);

                mContext.startActivity(intent);
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(validationCheck()){
                    requestPOT_CONTROL(GUBUN);
                }
            }
        });

        if(GUBUN.equals("UPDATE")){
            if(POT.POT_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage("해당 화분을 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPOT_CONTROL("DELETE");
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
        else{ //INSERT
            tvDDAY.setVisibility(View.GONE);
            lineDDAY.setVisibility(View.GONE);
            tvDayLabel.setVisibility(View.GONE);
            imgWater.setVisibility(View.GONE);
            lbWater.setVisibility(View.GONE);

            tvLog.setVisibility(View.GONE);

            getNewData();
        }

    }

    @Override
    protected void initialize() {
        setAlarm();
        setCycle();

        if(GUBUN.equals("UPDATE")){
            getDetail();
        }

    }

    private void getDetail() {
        POT_03_C.set(Integer.parseInt(POT.POT_03.substring(0,4)), Integer.parseInt(POT.POT_03.substring(4,6)) - 1, Integer.parseInt(POT.POT_03.substring(6,8)));
        POT_96_C.set(Integer.parseInt(POT.POT_96.substring(0,4)), Integer.parseInt(POT.POT_96.substring(4,6)) - 1, Integer.parseInt(POT.POT_96.substring(6,8)));

        etName.setText(POT.POT_02);
        etMemo.setText(POT.POT_06);

        setDDAY();
        setImgWater();

        tvPreWaterDay.setText(POT.POT_03.substring(0,4)+"."+POT.POT_03.substring(4,6)+"."+POT.POT_03.substring(6,8));
        tvNextWaterDay.setText(POT.POT_96.substring(0,4)+"."+POT.POT_96.substring(4,6)+"."+POT.POT_96.substring(6,8));
    }

    private void getNewData(){
        //초기값
        POT.ARM_03 = "N";
        POT.POT_04 = 1;
        POT.POT_05 = "";
        POT.POT_96 = "000000001200";

        int year = POT_03_C.get(Calendar.YEAR);
        int month = POT_03_C.get(Calendar.MONTH) + 1;
        int day = POT_03_C.get(Calendar.DATE);
        POT.POT_03 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day));

        setImgWater();
        setPreWaterDay();
    }

    private void requestPOT_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String POT_ID = intentVO.CTN_02; //컨테이너
        String POT_01 = POT.POT_01; //코드번호
        String POT_02 = etName.getText().toString(); //명칭
        String POT_03 = POT.POT_03; //최근 물주기 일자
        int POT_04 = POT.POT_04; //주기
        String POT_05 = POT.POT_05; //주기구분
        String POT_06 = etMemo.getText().toString(); //메모
        String POT_96 = POT.POT_96; //알림시간
        String POT_98 = mUser.Value.OCM_01; //사용자코드
        String ARM_03 = POT.ARM_03; //알림여부

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                POT_ID,
                POT_01,
                POT_02,
                POT_03,

                POT_04,
                POT_05,
                POT_06,
                POT_96,
                POT_98,

                ARM_03
        );

        call.enqueue(new Callback<POT_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                if(GUB.equals("INSERT")){
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, POT.POT_01);
                    ctds_control.requestCTDS_CONTROL();
                }

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            if(GUB.equals("WATER")){
                                setUserData(response.body().Data.get(0));
                            }
                            else{
                                finish();
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void setUserData(PotVO potVO) {
        POT.POT_03 = potVO.POT_03;
        POT.POT_96 = potVO.POT_96;
        POT_03_C.set(Integer.parseInt(POT.POT_03.substring(0,4)), Integer.parseInt(POT.POT_03.substring(4,6)) - 1, Integer.parseInt(POT.POT_03.substring(6,8)));
        POT_96_C.set(Integer.parseInt(POT.POT_96.substring(0,4)), Integer.parseInt(POT.POT_96.substring(4,6)) - 1, Integer.parseInt(POT.POT_96.substring(6,8)));
        setImgWater();
        setPreWaterDay();
        setNextWaterDay();
        setDDAY();
    }

    private void setDDAY(){
        long day = (POT_96_C.getTimeInMillis() - TODAY.getTimeInMillis()) / (24*60*60*1000);

        String DDAY = "";
        if(day > 0){
//            DDAY = "D-" + day;
            DDAY = String.valueOf(day);
        }
        else if(day == 0){
//            DDAY = "D-Day";
            DDAY = "0";
        }
        else{
//            DDAY = "D+" + (day * -1);
            DDAY = String.valueOf(day);
        }
        tvDDAY.setText(DDAY);
    }

    private void setAlarm(){
        if(POT.ARM_03.equals("Y")){
            imgAlarm.setImageResource(R.drawable.alarm_state_on);
        }
        else{ //N
            imgAlarm.setImageResource(R.drawable.alarm_state_off);
        }
    }

    private void CycleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cycle, null);
        builder.setView(view);

        Button btnCycleSave = (Button) view.findViewById(R.id.btnCycleSave);
        Button btnCycleCancel = (Button) view.findViewById(R.id.btnCycleCancel);

        NumberPicker npCycle = (NumberPicker) view.findViewById(R.id.npCycle);
        npCycle.setMinValue(1);
        npCycle.setMaxValue(60);
        npCycle.setValue(POT.POT_04);

        NumberPicker npCycle2 = (NumberPicker) view.findViewById(R.id.npCycle2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[] {"일", "개월"});
        if (POT.POT_05.equals("M")){
            npCycle2.setValue(1); //개월
        }
        else{ //D
            npCycle2.setValue(0); //일
        }

        AlertDialog dialog = builder.create();

        btnCycleSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                POT.POT_04 = npCycle.getValue();
                if(npCycle2.getValue() == 0){
                    POT.POT_05 = "D";
                }
                else{ // 1
                    POT.POT_05 = "M";
                }

                setCycle();
                setNextWaterDay();
                setImgWater();
                setDDAY();

                dialog.dismiss();
            }
        });
        btnCycleCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setCycle(){
        String cycle = "";
        int spanEndNum = 0;

        if(POT.POT_05.equals("D")){
            cycle = String.valueOf(POT.POT_04) + " 일";
            spanEndNum = 2;
        }
        else if(POT.POT_05.equals("M")){
            cycle = String.valueOf(POT.POT_04) + " 개월";
            spanEndNum = 3;
        }
        else{ //신규
            cycle = "선택";
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(cycle);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3498db")), 0, cycle.length() - spanEndNum, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCycle.setText(ssb);
    }

    private void setPreWaterDay(){
        tvPreWaterDay.setText(POT.POT_03.substring(0,4)+"."+POT.POT_03.substring(4,6)+"."+POT.POT_03.substring(6,8));
    }

    private void setNextWaterDay(){
        POT_96_C.setTimeInMillis(POT_03_C.getTimeInMillis());
        if(POT.POT_05.equals("D")){
            POT_96_C.add(Calendar.DATE, POT.POT_04);
        }
        else{
            POT_96_C.add(Calendar.MONTH, POT.POT_04);
        }

        int year = POT_96_C.get(Calendar.YEAR);
        int month = POT_96_C.get(Calendar.MONTH) + 1;
        int day = POT_96_C.get(Calendar.DATE);

        POT.POT_96 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day)) + POT.POT_96.substring(8);

        tvNextWaterDay.setText(POT.POT_96.substring(0,4)+"."+POT.POT_96.substring(4,6)+"."+POT.POT_96.substring(6,8));
    }

    private void preWaterDayDialog(){
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                POT_03_C.set(year, month, date);
                String tmp = String.valueOf(year);

                month++;
                if(month<10){
                    tmp += "0" + String.valueOf(month);
                }
                else{
                    tmp += String.valueOf(month);
                }

                if(date<10){
                    tmp += "0" + String.valueOf(date);
                }
                else{
                    tmp += String.valueOf(date);
                }

                POT.POT_03 = tmp;
                setPreWaterDay();
                if(!POT.POT_05.equals("")){
                    setNextWaterDay();
                }
                setImgWater();
            }
        }, POT_03_C.get(Calendar.YEAR), POT_03_C.get(Calendar.MONTH), POT_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private void setImgWater(){
        if(POT_96_C.compareTo(TODAY) == 1){
            imgWater.setImageResource(R.drawable.btn_round_skyblue_50dp);

            imgWater.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    new AlertDialog.Builder(mActivity)
                            .setMessage("예정일자 전입니다.\n물주기 완료 하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(validationCheck()){
                                        requestPOT_CONTROL("WATER");
                                    }
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
        else{
            imgWater.setImageResource(R.drawable.btn_round_shallowgray_50dp);

            imgWater.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(validationCheck()){
                        requestPOT_CONTROL("WATER");
                    }
                }
            });
        }
    }

    public boolean validationCheck(){
        boolean check = true;
        if(etName.getText().toString().equals("")){
            check = false;
            Toast.makeText(mActivity, "명칭을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if(POT.POT_05.equals("")){
            check = false;
            Toast.makeText(mActivity, "주기를 선택하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(TODAY.compareTo(POT_03_C) < 0){
            check = false;
            Toast.makeText(mActivity, "최근 물주기는 오늘일자 이전으로 설정 해주세요.", Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

}
