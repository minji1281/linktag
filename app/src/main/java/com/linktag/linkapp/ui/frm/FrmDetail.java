package com.linktag.linkapp.ui.frm;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.FRMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm.AlarmDialog;
import com.linktag.linkapp.ui.master_log.MasterLog;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.FRM_VO;
import com.linktag.linkapp.value_object.LogVO;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrmDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private EditText etName;
    private EditText etMemo;

    private ImageView imgAlarm;
    private ImageView imgTime;
    private ImageView imgFilter;
    private ImageView imgPreFilterDayIcon;

    private TextView tvDDAY;
    private TextView tvCycle;
    private TextView tvPreFilterDay;
    private TextView tvNextFilterDay;
    private TextView lbFilter;
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
    private FRM_VO FRM;
    private String GUBUN;

    Calendar TODAY = Calendar.getInstance();
    Calendar FRM_03_C = Calendar.getInstance(); //최근 필터교체
    Calendar FRM_96_C = Calendar.getInstance(); //다음 필터교체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_detail);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(getIntent().hasExtra("FRM")){
            FRM = (FRM_VO) getIntent().getSerializableExtra("FRM");
            GUBUN = "UPDATE";
        }
        else{
            FRM = new FRM_VO();
            FRM.FRM_01 = getIntent().getStringExtra("scancode");
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
        clearCalTime(FRM_03_C);
        clearCalTime(FRM_96_C);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        lineDDAY = (View) findViewById(R.id.lineDDAY);
        tvDayLabel = (TextView) findViewById(R.id.tvDayLabel);

        etName = (EditText) findViewById(R.id.etName);
        etMemo = (EditText) findViewById(R.id.etMemo);

        imgAlarm = (ImageView) findViewById(R.id.imgAlarm);
        imgAlarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(FRM.ARM_03.equals("Y")){
                    FRM.ARM_03 = "N";
                }
                else{
                    FRM.ARM_03 = "Y";
                }

                setAlarm();
            }
        });
        imgTime = (ImageView) findViewById(R.id.imgTime);
        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmDialog alarmDialog = new AlarmDialog(mContext, FRM.FRM_96.substring(8, 12));

                alarmDialog.setDialogListener(new AlarmDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String time) {
                        FRM.FRM_96 = FRM.FRM_96.substring(0, 8) + time;
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                alarmDialog.show();

            }
        });

        tvDDAY = (TextView) findViewById(R.id.tvDDAY);
        tvCycle = (TextView) findViewById(R.id.tvCycle);
        tvCycle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CycleDialog();
            }
        });
        tvPreFilterDay = (TextView) findViewById(R.id.tvPreFilterDay);
        tvPreFilterDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                preFilterDayDialog();
            }
        });
        imgPreFilterDayIcon = (ImageView) findViewById(R.id.imgPreFilterDayIcon);
        imgPreFilterDayIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                preFilterDayDialog();
            }
        });
        tvNextFilterDay = (TextView) findViewById(R.id.tvNextFilterDay);

        tvLog = (TextView) findViewById(R.id.tvLog);
        tvLog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LogVO LOG = new LogVO();
                LOG.LOG_ID = intentVO.CTN_02;
                LOG.LOG_01 = FRM.FRM_01;
                LOG.LOG_98 = mUser.Value.OCM_01;
                LOG.SP_NAME = "SP_FRML_CONTROL";

                Intent intent = new Intent(mContext, MasterLog.class);
                intent.putExtra("LOG", LOG);
                intent.putExtra("func_text", mContext.getString(R.string.frm_log_func_text));

                mContext.startActivity(intent);
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(validationCheck()){
                    requestFRM_CONTROL(GUBUN);
                }
            }
        });
        imgFilter = (ImageView) findViewById(R.id.imgFilter);
        lbFilter = (TextView) findViewById(R.id.lbFilter);

        if(GUBUN.equals("UPDATE")){
            if(FRM.FRM_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        deleteDialog();
                    }
                });
            }
        }
        else{ //INSERT
//            tvDDAY.setVisibility(View.GONE);
//            lineDDAY.setVisibility(View.GONE);
//            tvDayLabel.setVisibility(View.GONE);
            imgFilter.setVisibility(View.GONE);
            lbFilter.setVisibility(View.GONE);

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
        FRM_03_C.set(Integer.parseInt(FRM.FRM_03.substring(0,4)), Integer.parseInt(FRM.FRM_03.substring(4,6)) - 1, Integer.parseInt(FRM.FRM_03.substring(6)));
        FRM_96_C.set(Integer.parseInt(FRM.FRM_96.substring(0,4)), Integer.parseInt(FRM.FRM_96.substring(4,6)) - 1, Integer.parseInt(FRM.FRM_96.substring(6,8)));

        etName.setText(FRM.FRM_02);
        etMemo.setText(FRM.FRM_06);

        setDDAY();
        setImgFilter();

        tvPreFilterDay.setText(FRM.FRM_03.substring(0,4)+"."+FRM.FRM_03.substring(4,6)+"."+FRM.FRM_03.substring(6));
        tvNextFilterDay.setText(FRM.FRM_96.substring(0,4)+"."+FRM.FRM_96.substring(4,6)+"."+FRM.FRM_96.substring(6,8));
    }

    private void getNewData(){
        //초기값
        FRM.ARM_03 = "N";
        FRM.FRM_04 = 1;
        FRM.FRM_05 = "";
        FRM.FRM_96 = "000000001200";

        int year = FRM_03_C.get(Calendar.YEAR);
        int month = FRM_03_C.get(Calendar.MONTH) + 1;
        int day = FRM_03_C.get(Calendar.DATE);
        FRM.FRM_03 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day));

        setImgFilter();
        setPreFilterDay();
    }

    private void requestFRM_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String FRM_ID = intentVO.CTN_02; //컨테이너
        String FRM_01 = FRM.FRM_01; //코드번호
        String FRM_02 = etName.getText().toString(); //명칭
        String FRM_03 = FRM.FRM_03; //최근 필터 교체일자

        int FRM_04 = FRM.FRM_04; //주기
        String FRM_05 = FRM.FRM_05; //주기구분
        String FRM_06 = etMemo.getText().toString(); //메모
        String FRM_96 = FRM.FRM_96; //알림시간
        String FRM_98 = mUser.Value.OCM_01; //사용자코드

        String ARM_03 = FRM.ARM_03; //알림여부

        Call<FRMModel> call = Http.frm(HttpBaseService.TYPE.POST).FRM_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                FRM_ID,
                FRM_01,
                FRM_02,
                FRM_03,

                FRM_04,
                FRM_05,
                FRM_06,
                FRM_96,
                FRM_98,

                ARM_03
        );

        call.enqueue(new Callback<FRMModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<FRMModel> call, Response<FRMModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            if(GUB.equals("INSERT")){
                                CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, FRM.FRM_01);
                                ctds_control.requestCTDS_CONTROL();
                            }

                            Response<FRMModel> response = (Response<FRMModel>) msg.obj;

                            if(!GUB.equals("DELETE")){
                                if(FRM.ARM_03.equals("Y")){
                                    if(GUB.equals("WATER")){
                                        FRM.FRM_96 = response.body().Data.get(0).FRM_96;
                                    }
                                    String NextDay = FRM.FRM_96;
                                    Toast.makeText(mContext,mContext.getString(R.string.dialog_alarm_toast_text) + " " + NextDay.substring(0,4)+"." + NextDay.substring(4,6)+"."+ NextDay.substring(6,8)+" " +
                                            NextDay.substring(8,10)+":" + NextDay.substring(10,12), Toast.LENGTH_LONG ).show();
                                }
                            }

                            if(GUB.equals("FILTER")){
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
            public void onFailure(Call<FRMModel> call, Throwable t){
                Log.d("FRM_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private void setUserData(FRM_VO frmVO) {
        FRM.FRM_03 = frmVO.FRM_03;
        FRM.FRM_96 = frmVO.FRM_96;
        FRM_03_C.set(Integer.parseInt(FRM.FRM_03.substring(0,4)), Integer.parseInt(FRM.FRM_03.substring(4,6)) - 1, Integer.parseInt(FRM.FRM_03.substring(6)));
        FRM_96_C.set(Integer.parseInt(FRM.FRM_96.substring(0,4)), Integer.parseInt(FRM.FRM_96.substring(4,6)) - 1, Integer.parseInt(FRM.FRM_96.substring(6,8)));
        setImgFilter();
        setPreFilterDay();
        setNextFilterDay();
        setDDAY();
    }

    private void setDDAY(){
        int day = (int) ((FRM_96_C.getTimeInMillis() - TODAY.getTimeInMillis()) / (24*60*60*1000));

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
        if(FRM.ARM_03.equals("Y")){
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
        npCycle.setValue(FRM.FRM_04);

        NumberPicker npCycle2 = (NumberPicker) view.findViewById(R.id.npCycle2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[] {mContext.getString(R.string.dialog_cycle_day), mContext.getString(R.string.dialog_cycle_month)});
        if (FRM.FRM_05.equals("M")){
            npCycle2.setValue(1); //개월
        }
        else{ //D
            npCycle2.setValue(0); //일
        }

            AlertDialog dialog = builder.create();

        btnCycleSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FRM.FRM_04 = npCycle.getValue();
                if(npCycle2.getValue() == 0){
                    FRM.FRM_05 = "D";
                }
                else{ // 1
                    FRM.FRM_05 = "M";
                }

                setCycle();
                setNextFilterDay();
                setImgFilter();
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

        if(FRM.FRM_05.equals("D")){
            cycle = String.valueOf(FRM.FRM_04) + " " + mContext.getString(R.string.dialog_cycle_day);
            spanEndNum = Integer.valueOf(mContext.getString(R.string.dialog_cycle_day_num));
        }
        else if(FRM.FRM_05.equals("M")){
            cycle = String.valueOf(FRM.FRM_04) + " " + mContext.getString(R.string.dialog_cycle_month);
            spanEndNum = Integer.valueOf(mContext.getString(R.string.dialog_cycle_month_num));
        }
        else{ //신규
            cycle = mContext.getString(R.string.dialog_cycle_empty);
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(cycle);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3498db")), 0, cycle.length() - spanEndNum, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCycle.setText(ssb);
    }

    private void setPreFilterDay(){
        tvPreFilterDay.setText(FRM.FRM_03.substring(0,4)+"."+FRM.FRM_03.substring(4,6)+"."+FRM.FRM_03.substring(6));
    }

    private void setNextFilterDay(){
        FRM_96_C.setTimeInMillis(FRM_03_C.getTimeInMillis());
        if(FRM.FRM_05.equals("D")){
            FRM_96_C.add(Calendar.DATE, FRM.FRM_04);
        }
        else{
            FRM_96_C.add(Calendar.MONTH, FRM.FRM_04);
        }

        int year = FRM_96_C.get(Calendar.YEAR);
        int month = FRM_96_C.get(Calendar.MONTH) + 1;
        int day = FRM_96_C.get(Calendar.DATE);

        FRM.FRM_96 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day)) + FRM.FRM_96.substring(8);

        tvNextFilterDay.setText(FRM.FRM_96.substring(0,4)+"."+FRM.FRM_96.substring(4,6)+"."+FRM.FRM_96.substring(6,8));
    }

    private void setImgFilter(){
        if(FRM_96_C.compareTo(TODAY) == 1){
            imgFilter.setImageResource(R.drawable.btn_round_skyblue_50dp);

            imgFilter.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    new AlertDialog.Builder(mActivity)
                            .setMessage(R.string.frm_detail_replace_text)
                            .setPositiveButton(R.string.onPositive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(validationCheck()){
                                        requestFRM_CONTROL("FILTER");
                                    }
                                }
                            })
                            .setNegativeButton(R.string.onNegative, new DialogInterface.OnClickListener() {
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
            imgFilter.setImageResource(R.drawable.btn_round_shallowgray_50dp);

            imgFilter.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(validationCheck()){
                        requestFRM_CONTROL("FILTER");
                    }
                }
            });
        }
    }

    private void preFilterDayDialog(){
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                FRM_03_C.set(year, month, date);
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

                FRM.FRM_03 = tmp;
                setPreFilterDay();
                if(!FRM.FRM_05.equals("")){
                    setNextFilterDay();
                }
                setImgFilter();
                setDDAY();
            }
        }, FRM_03_C.get(Calendar.YEAR), FRM_03_C.get(Calendar.MONTH), FRM_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        EditText etDeleteName = (EditText) view.findViewById(R.id.etDeleteName);

        AlertDialog dialog = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etDeleteName.getText().toString().equals(FRM.FRM_02)){
                    dialog.dismiss();
                    requestFRM_CONTROL("DELETE");
                }
                else{
                    Toast.makeText(mActivity, R.string.dialog_delete_check_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean validationCheck(){
        boolean check = true;
        if(etName.getText().toString().equals("")){
            check = false;
            Toast.makeText(mActivity, R.string.frm_validation_check1, Toast.LENGTH_SHORT).show();
        }
        else if(FRM.FRM_05.equals("")){
            check = false;
            Toast.makeText(mActivity, R.string.frm_validation_check2, Toast.LENGTH_SHORT).show();
        }
        else if(TODAY.compareTo(FRM_03_C) < 0){
            check = false;
            Toast.makeText(mActivity, R.string.frm_validation_check3, Toast.LENGTH_SHORT).show();
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
