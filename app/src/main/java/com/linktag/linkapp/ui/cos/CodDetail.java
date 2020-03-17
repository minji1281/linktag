package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.COD_VO;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

//    private Spinner spCos;

    private TextView tvDDAY;
    private TextView tvEndDay;
    private TextView tvUseEndLabel;

    private EditText etName;
    private EditText etMemo;
    private EditText etBrand;
    private EditText etMoney;

    private ImageView imgAlarm;
    private ImageView imgTime;
    private ImageView imgEndDayIcon;
    private ImageView imgUseEnd;

    @BindView(R.id.spCos)
    Spinner spCos;

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
    private COD_VO COD;
    private String GUBUN;
    ArrayList<SpinnerList> cosList = new ArrayList<>();

    Calendar COD_05_C = Calendar.getInstance(); //사용일자
    Calendar COD_06_C = Calendar.getInstance(); //유효기간일자
    Calendar COD_07_C = Calendar.getInstance(); //사용종료일자
    Calendar TODAY = Calendar.getInstance(); //오늘

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_detail);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(getIntent().hasExtra("COD")){
            COD = (COD_VO) getIntent().getSerializableExtra("COD");
            COD.COD_96 = COD.COD_96.substring(8);
            GUBUN = "UPDATE";
        }
        else{
            COD = new COD_VO();
            COD.COD_95 = getIntent().getStringExtra("COD_95");
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
        clearCalTime(COD_05_C);
        clearCalTime(COD_06_C);
        clearCalTime(COD_07_C);

        lineDDAY = (View) findViewById(R.id.lineDDAY);
        tvDayLabel = (TextView) findViewById(R.id.tvDayLabel);

        etName = (EditText) findViewById(R.id.etName);
        etMemo = (EditText) findViewById(R.id.etMemo);
        etBrand = (EditText) findViewById(R.id.etBrand);
        etMoney = (EditText) findViewById(R.id.etMoney);

        imgAlarm = (ImageView) findViewById(R.id.imgAlarm);
        imgAlarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(COD.ARM_03.equals("Y")){
                    COD.ARM_03 = "N";
                }
                else{
                    COD.ARM_03 = "Y";
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

                        COD.COD_96 = tmp;
                    }
                }, Integer.valueOf(COD.COD_96.substring(0,2)), Integer.valueOf(COD.COD_96.substring(2)), false);

                dialog.show();

            }
        });
        imgEndDayIcon = (ImageView) findViewById(R.id.imgEndDayIcon);
        imgUseEnd = (ImageView) findViewById(R.id.imgUseEnd);
        imgUseEnd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(validationCheck()){
                    if(COD.COD_07.equals("")){ //기존detail
                        requestCOD_CONTROL("USEEND");
                    }
                    else{ //사용종료detail
                        requestCOD_CONTROL("USESTART");
                    }
                }
            }
        });

        tvDDAY = (TextView) findViewById(R.id.tvDDAY);
        tvEndDay = (TextView) findViewById(R.id.tvEndDay);
        tvUseEndLabel = (TextView) findViewById(R.id.tvUseEndLabel);

        spCos = (Spinner) findViewById(R.id.spCos);
        cosInitial();

        imgEndDayIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                endDayDialog();
            }
        });
        tvEndDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                endDayDialog();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(validationCheck()){
                    requestCOD_CONTROL(GUBUN);
                }
            }
        });

        if(GUBUN.equals("UPDATE")){
            if(COD.COD_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
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
            getNewData();
            tvDDAY.setVisibility(View.GONE);
            lineDDAY.setVisibility(View.GONE);
            tvDayLabel.setVisibility(View.GONE);
            imgUseEnd.setVisibility(View.GONE);
            tvUseEndLabel.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initialize() {
        setAlarm();
        setEndDay();
        etMoney.setText(String.valueOf(Math.round(COD.COD_04)));

        if(GUBUN.equals("UPDATE")){
            getDetail();
        }
    }

    private void getDetail() {

        COD_06_C.set(Integer.parseInt(COD.COD_06.substring(0,4)), Integer.parseInt(COD.COD_06.substring(4,6))-1, Integer.parseInt(COD.COD_06.substring(6)));
        if(!COD.COD_07.equals("")){
            COD_07_C.set(Integer.parseInt(COD.COD_07.substring(0,4)), Integer.parseInt(COD.COD_07.substring(4,6))-1, Integer.parseInt(COD.COD_07.substring(6)));
        }

        etName.setText(COD.COD_02);
        etMemo.setText(COD.COD_08);
        etBrand.setText(COD.COD_03);

        if(COD.COD_07.equals("")){ //기존 detail
            COD_05_C.set(Integer.parseInt(COD.COD_05.substring(0,4)), Integer.parseInt(COD.COD_05.substring(4,6))-1, Integer.parseInt(COD.COD_05.substring(6)));

            setDDAY();
        }
        else{ //사용종료 detail
            int year = COD_05_C.get(Calendar.YEAR);
            int month = COD_05_C.get(Calendar.MONTH) + 1;
            int day = COD_05_C.get(Calendar.DATE);
            COD.COD_05 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day));

            tvDDAY.setVisibility(View.GONE);
            lineDDAY.setVisibility(View.GONE);
            tvDayLabel.setVisibility(View.GONE);
            tvUseEndLabel.setText("사용시작");
            imgUseEnd.setImageResource(R.drawable.btn_round_skyblue_50dp);
        }
    }

    private void getNewData(){
        //초기값
        COD.ARM_03 = "N";
        imgAlarm.setImageResource(R.drawable.alarm_state_off);

        int year = COD_06_C.get(Calendar.YEAR);
        int month = COD_06_C.get(Calendar.MONTH) + 1;
        int day = COD_06_C.get(Calendar.DATE);
        COD.COD_06 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day));
        COD.COD_07 = "";
        COD.COD_96 = "1200";
    }

    private void requestCOD_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String COD_ID = intentVO.CTN_02; //컨테이너
        String COD_01 = ""; //코드번호
        if(GUBUN.equals("UPDATE")){
            COD_01 = COD.COD_01;
        }
        String COD_02 = etName.getText().toString(); //명칭
        String COD_03 = etBrand.getText().toString(); //브랜드명
        double COD_04 = 0; //가격
        if(!etMoney.getText().toString().equals("")){
            COD_04 = Double.parseDouble(etMoney.getText().toString().replace(",", ""));
        }
        String COD_05 = ""; //사용일자
        String COD_06 = tvEndDay.getText().toString().replace(".", ""); //유효기간일자
        String COD_07 = ""; //사용종료일자
        String COD_08 = etMemo.getText().toString(); //메모
        String COD_95 = COD.COD_95; //화장대코드
        String COD_96 = COD.COD_96; //알림시간
        String COD_98 = mUser.Value.OCM_01; //최종수정자
        String ARM_03 = COD.ARM_03; //알림여부

        Call<CODModel> call = Http.cod(HttpBaseService.TYPE.POST).COD_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                COD_ID,
                COD_01,
                COD_02,
                COD_03,

                COD_04,
                COD_05,
                COD_06,
                COD_07,
                COD_08,

                COD_95,
                COD_96,
                COD_98,
                ARM_03
        );

        call.enqueue(new Callback<CODModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CODModel> call, Response<CODModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<CODModel> response = (Response<CODModel>) msg.obj;

                            CodList.COS.COS_01 = COD_95;

                            if(GUB.equals("INSERT") || GUB.equals("UPDATE") || GUB.equals("USESTART")){
                                if(COD.ARM_03.equals("Y")){
                                    Toast.makeText(mContext,"다음알람 "+ COD.COD_06.substring(0,4)+"년 " + COD.COD_06.substring(4,6)+"월 "+ COD.COD_06.substring(6,8)+"일 " +
                                            COD.COD_96.substring(0,2)+"시 " + COD.COD_96.substring(2,4)+"분 예정입니다.", Toast.LENGTH_LONG ).show();
                                }
                            }

                            finish();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CODModel> call, Throwable t){
                Log.d("COD_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private void setDDAY(){
//        if(COD.COD_07.equals("")){
//            tvDDAY.setVisibility(View.VISIBLE);
//        }

        int day = (int) ((COD_06_C.getTimeInMillis() - TODAY.getTimeInMillis()) / (24*60*60*1000));

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
        if(COD.ARM_03.equals("Y")){
            imgAlarm.setImageResource(R.drawable.alarm_state_on);
        }
        else{ //N
            imgAlarm.setImageResource(R.drawable.alarm_state_off);
        }
    }

    private void setEndDay(){
        tvEndDay.setText(COD.COD_06.substring(0,4)+"."+COD.COD_06.substring(4,6)+"."+COD.COD_06.substring(6));
    }

    private void endDayDialog(){
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                COD_06_C.set(year, month, date);
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

                COD.COD_06 = tmp;
                setEndDay();
                setDDAY();
            }
        }, COD_06_C.get(Calendar.YEAR), COD_06_C.get(Calendar.MONTH), COD_06_C.get(Calendar.DATE));

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
                if(etDeleteName.getText().toString().equals(COD.COD_02)){
                    dialog.dismiss();
                    requestCOD_CONTROL("DELETE");
                }
                else{
                    Toast.makeText(mActivity, "명칭을 정확하게 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
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

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void cosInitial(){
        CosInfo cosinfo = new CosInfo(cosList, mActivity, "spCos", "LIST2", intentVO.CTN_02, COD.COD_95, "D");
        cosinfo.execute();
        spCos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                COD.COD_95 = cosList.get(position).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public boolean validationCheck(){
        boolean check = true;
        if(etName.getText().toString().equals("")){
            check = false;
            Toast.makeText(mActivity, "명칭을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if(COD.COD_06.equals("")){
            check = false;
            Toast.makeText(mActivity, "유효기간을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(COD_06_C.compareTo(COD_05_C) <= 0){
            check = false;
            String COD_05_T = COD.COD_05.substring(0,4)+"."+COD.COD_05.substring(4,6)+"."+COD.COD_05.substring(6);
            Toast.makeText(mActivity, "유효기간은 사용시작일자(" + COD_05_T + ") 이후로 설정 해주세요.", Toast.LENGTH_SHORT).show();
        }
        return check;
    }

}
