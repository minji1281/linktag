package com.linktag.linkapp.ui.pot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.util.ClsDateTime;
import com.linktag.base.util.ClsImage;
import com.linktag.linkapp.R;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    private TextView tvDDAY;
    private TextView tvCycle;
    private TextView tvPreWaterDay;
    private TextView tvNextWaterDay;

    private Button btnSave;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    public String ARM_03 = "N";
    public int ARM_04 = 0;
    private String CTM_01;
    private String CTD_02;
    private String CTN_02;
    private PotVO POT;
    private String GUBUN;

    Calendar POT_03_Calendar = Calendar.getInstance();
    Calendar POT_96_Calendar = Calendar.getInstance();
    Calendar TODAY = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_detail);

        if(getIntent().hasExtra("POT")){
            POT = (PotVO) getIntent().getSerializableExtra("POT");
            GUBUN = "UPDATE";
        }
        else{
            POT = new PotVO();
            POT.POT_01 = getIntent().getStringExtra("scancode");
            GUBUN = "INSERT";
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

        if(POT.POT_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
            header.btnHeaderRight1.setVisibility((View.VISIBLE));
            header.btnHeaderRight1.setMaxWidth(50);
            header.btnHeaderRight1.setMaxHeight(50);
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

        etName = (EditText) findViewById(R.id.etName);
        etMemo = (EditText) findViewById(R.id.etMemo);

        imgAlarm = (ImageView) findViewById(R.id.imgAlarm);
        imgAlarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(POT.ARM_03.equals("Y")){
                    POT.ARM_03 = "N";
                    imgAlarm.setImageResource(R.drawable.alarm_state_off);
                }
                else{
                    POT.ARM_03 = "Y";
                    imgAlarm.setImageResource(R.drawable.alarm_state_on);
                }
            }
        });
        imgTime = (ImageView) findViewById(R.id.imgTime);
        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog dialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
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

        tvDDAY = (TextView) findViewById(R.id.tvDDAY);
        tvCycle = (TextView) findViewById(R.id.tvCycle);
        tvCycle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CycleDialog();
            }
        });
        tvPreWaterDay = (TextView) findViewById(R.id.tvPreWaterDay);
        tvNextWaterDay = (TextView) findViewById(R.id.tvNextWaterDay);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                requestPOT_CONTROL("UPDATE");
            }
        });

    }

    @Override
    protected void initialize() {
        getDetail();
    }

    private void getDetail() {
        /* 최초 로딩시 intent 가져옴 */

        etName.setText(POT.POT_02);
        etMemo.setText(POT.POT_06);

        String DDAY = "";
        if(Integer.parseInt(POT.DDAY) > 0){
            DDAY = "D-" + POT.DDAY;
        }
        else if(Integer.parseInt(POT.DDAY) == 0){
            DDAY = "D-Day";
        }
        else{
            DDAY = "D+" + (Integer.parseInt(POT.DDAY) * -1);
        }
        tvDDAY.setText(DDAY);

        tvPreWaterDay.setText(POT.POT_03_T.substring(0, 10).replace("-", "."));
        POT_03_Calendar.set(Integer.parseInt(POT.POT_03_T.substring(0,4)), Integer.parseInt(POT.POT_03_T.substring(5,7)) - 1, Integer.parseInt(POT.POT_03_T.substring(8,10)));
//        tvNextWaterDay.setText(POT.POT_96.substring(0, 4) + "." + POT.POT_96.substring(4, 6) + "." + POT.POT_96.substring(6, 8));
        POT_96_Calendar.set(Integer.parseInt(POT.POT_96.substring(0,4)), Integer.parseInt(POT.POT_96.substring(4,6)) - 1, Integer.parseInt(POT.POT_96.substring(6,8)));

        if(POT.ARM_03.equals("Y")){
            imgAlarm.setImageResource(R.drawable.alarm_state_on);
        }
        else{
            imgAlarm.setImageResource(R.drawable.alarm_state_off);
        }

        changeCycleText();
        updateImgWater();

    }

    private void requestPOT_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String POT_ID = CTN_02; //컨테이너
        String POT_01 = POT.POT_01; //코드번호
        String POT_02 = etName.getText().toString(); //명칭
        int POT_04 = POT.POT_04; //주기
        String POT_05 = POT.POT_05; //주기구분
        String POT_06 = etMemo.getText().toString(); //메모
        String POT_81 = ""; //이미지 (사용안함) 수정해야돼!!!
        String POT_96 = POT.POT_96; //알림시간
        String POT_98 = mUser.Value.OCM_01; //사용자코드
        String ARM_03 = POT.ARM_03; //알림여부

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                POT_02,
                POT_04,

                POT_05,
                POT_06,
                POT_81,
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

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            if(GUB.equals("WATER")){
                                callBack(GUB, response.body().Data.get(0));
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

    private void callBack(String GUB, PotVO data){
        if(data.Validation){
            switch(GUB){
                case "WATER":
                    POT.POT_03_T = data.POT_03_T;
                    updatePOT_03();
                    break;
            }
        }

    }

    private void CycleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pot_cycle, null);
        builder.setView(view);

        TextView tvCycleMenu = (TextView) view.findViewById(R.id.tvCycleMenu);
        TextView tvFixDayMenu = (TextView) view.findViewById(R.id.tvFixDayMenu);

        RelativeLayout rlayout1 = (RelativeLayout) view.findViewById(R.id.rlayout1);
        RelativeLayout rlayout2 = (RelativeLayout) view.findViewById(R.id.rlayout2);

        Button btnCycleSave = (Button) view.findViewById(R.id.btnCycleSave);
        Button btnCycleCancel = (Button) view.findViewById(R.id.btnCycleCancel);

        DatePicker dpFixDay = (DatePicker) view.findViewById(R.id.dpFixDay);
        dpFixDay.updateDate(POT_96_Calendar.get(Calendar.YEAR), POT_96_Calendar.get(Calendar.MONTH), POT_96_Calendar.get(Calendar.DATE));

        NumberPicker npCycle = (NumberPicker) view.findViewById(R.id.npCycle);
        npCycle.setMinValue(0);
        npCycle.setMaxValue(60);
        npCycle.setValue(POT.POT_04);

        NumberPicker npCycle2 = (NumberPicker) view.findViewById(R.id.npCycle2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[] {"일", "개월"});
        if(POT.POT_05.equals("D")){
            npCycle2.setValue(0); //일
        }
        else if (POT.POT_05.equals("M")){
            npCycle2.setValue(1); //개월
        }

        AlertDialog dialog = builder.create();

        tvCycleMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rlayout1.setVisibility(View.VISIBLE);
                rlayout2.setVisibility(View.GONE);

                tvCycleMenu.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tvFixDayMenu.setBackgroundColor(Color.parseColor("#F2F5F7"));
            }
        });
        tvFixDayMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rlayout1.setVisibility(View.GONE);
                rlayout2.setVisibility(View.VISIBLE);

                tvCycleMenu.setBackgroundColor(Color.parseColor("#F2F5F7"));
                tvFixDayMenu.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        if(POT.POT_05.equals("F")){
            tvFixDayMenu.performClick();
        }
        else{
            tvCycleMenu.performClick();
        }

        btnCycleSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(rlayout1.getVisibility() == View.VISIBLE){ // tvCycleMenu click
                    POT.POT_04 = npCycle.getValue();
                    if(npCycle2.getValue() == 0){
                        POT.POT_05 = "D";
                    }
                    else{ // 1
                        POT.POT_05 = "M";
                    }

                    updatePOT_96();
                }
                else{ // tvFixDayMenu click
                    POT.POT_05 = "F";

                    int year = dpFixDay.getYear();
                    int month = dpFixDay.getMonth() + 1;
                    int day = dpFixDay.getDayOfMonth();

                    POT_96_Calendar.set(year, month - 1, day);

                    POT.POT_96 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day)) + POT.POT_96.substring(8);

                    updateDDAY();
                }

                changeCycleText();

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

    private void changeCycleText(){
        String Cycle = "";
        int spanEndNum = 0;

        if(POT.POT_05.equals("F")){
            Cycle = "지정일";
        }
        else if(POT.POT_05.equals("D")){
            Cycle = String.valueOf(POT.POT_04) + " 일";
            spanEndNum = 2;
        }
        else{ //M
            Cycle = String.valueOf(POT.POT_04) + " 개월";
            spanEndNum = 3;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(Cycle);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3498db")), 0, Cycle.length() - spanEndNum, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCycle.setText(ssb);
        tvNextWaterDay.setText(POT.POT_96.substring(0,4)+"."+POT.POT_96.substring(4,6)+"."+POT.POT_96.substring(6,8));

        updateImgWater();
    }

    private void updatePOT_96(){
        int POT_04 = POT.POT_04;
        String POT_05 = POT.POT_05;

        if(!POT_05.equals("F")){
            POT_96_Calendar.setTimeInMillis(POT_03_Calendar.getTimeInMillis());
            if(POT_05.equals("D")){
                POT_96_Calendar.add(Calendar.DATE, POT_04);
            }
            else{
                POT_96_Calendar.add(Calendar.MONTH, POT_04);
            }

            int year = POT_96_Calendar.get(Calendar.YEAR);
            int month = POT_96_Calendar.get(Calendar.MONTH) + 1;
            int day = POT_96_Calendar.get(Calendar.DATE);

            POT.POT_96 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day)) + POT.POT_96.substring(8);

            updateDDAY();
        }

        updateImgWater();

    }

    private void updatePOT_03(){
        tvPreWaterDay.setText(POT.POT_03_T.substring(0, 10).replace("-", "."));
        POT_03_Calendar.set(Integer.parseInt(POT.POT_03_T.substring(0,4)), Integer.parseInt(POT.POT_03_T.substring(5,7)) - 1, Integer.parseInt(POT.POT_03_T.substring(8,10)));

        updatePOT_96();
    }

    private void updateDDAY(){
        long day = (POT_96_Calendar.getTimeInMillis() - TODAY.getTimeInMillis()) / (24*60*60*1000);
//        int day = (int)(Math.floor(TimeUnit.HOURS.convert(DayDiff, TimeUnit.MILLISECONDS) / 24f));

        String DDAY = "";
        if(day > 0){
            DDAY = "D-" + day;
        }
        else if(day == 0){
            DDAY = "D-Day";
        }
        else{
            DDAY = "D+" + (day * -1);
        }
        tvDDAY.setText(DDAY);
    }

    private void updateImgWater(){
        if(POT_96_Calendar.compareTo(Calendar.getInstance()) == 1){
            imgWater.setImageResource(R.drawable.ic_check_on);

            imgWater.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    new AlertDialog.Builder(mActivity)
                            .setMessage("예정일자 전입니다.\n물주기완료 하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPOT_CONTROL("WATER");
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
            imgWater.setImageResource(R.drawable.ic_check_off);

            imgWater.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    requestPOT_CONTROL("WATER");
                }
            });
        }
    }

}
