package com.linktag.linkapp.ui.pcm;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.PCDModel;
import com.linktag.linkapp.model.PCMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PcdVO;
import com.linktag.linkapp.value_object.PcmVO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPcm extends BaseActivity implements Serializable {

    private RecyclerView recyclerView_hw;
    private RecyclerView recyclerView_sw;
    private LinearLayoutManager linearLayoutManager_HW;
    private LinearLayoutManager linearLayoutManager_SW;
    public static ArrayList<PcdVO> mList_HW;
    public static ArrayList<PcdVO> mList_SW;
    private PcdHwRecycleAdapter mHwAdapter;
    private PcdSwRecycleAdapter mSwAdapter;


    private EditText ed_name;
    private EditText ed_memo;

    private Spinner sp_sw;
    private EditText et_sw;
    private Spinner sp_hw;
    private EditText et_hw;
    private HashMap<String, String> map_hw = new HashMap<String, String>();
    private HashMap<String, String> map_sw = new HashMap<String, String>();


    private TextView tv_datePicker;
    private Button btn_datePicker;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private TimePicker timePicker;
    private Switch switch_alarm;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private TextView tv_manageDay;
    private Button btn_update;
    private Button bt_save;
    private Button btn_addItem_hw;
    private Button btn_addItem_sw;
    private PcmVO pcmVO;

    private Calendar calendar = Calendar.getInstance();


    private String hourOfDayString;
    private String minuteString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pcm);


        initLayout();

        initialize();

    }

    public static void setmList_HW(ArrayList<PcdVO> mList_HW) {
        DetailPcm.mList_HW = mList_HW;
    }


    public void requestPCD_SELECT(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<PCDModel> call = Http.pcd(HttpBaseService.TYPE.POST).PCD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                pcmVO.PCM_ID,
                pcmVO.PCM_01,
                ""
        );


        call.enqueue(new Callback<PCDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<PCDModel> call, Response<PCDModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<PCDModel> response = (Response<PCDModel>) msg.obj;

                            if (GUBUN.equals("LIST_HW")) {

                                mList_HW = response.body().Data;
                                if (mList_HW == null)
                                    mList_HW = new ArrayList<>();
                                mHwAdapter.updateData(mList_HW);
                                mHwAdapter.notifyDataSetChanged();
                            } else if (GUBUN.equals("LIST_SW")) {
                                mList_SW = response.body().Data;
                                if (mList_SW == null)
                                    mList_SW = new ArrayList<>();
                                mSwAdapter.updateData(mList_SW);
                                mSwAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<PCDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    public void requestPCD_CONTROL(PcdVO pcdVO, String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<PCDModel> call = Http.pcd(HttpBaseService.TYPE.POST).PCD_CONTROL(
                BaseConst.URL_HOST,
                pcdVO.GUBUN,
                pcdVO.PCD_ID,
                pcdVO.PCD_01,
                pcdVO.PCD_02,
                pcdVO.PCD_03,
                pcdVO.PCD_04,
                pcdVO.PCD_05,
                pcdVO.PCD_98
        );


        call.enqueue(new Callback<PCDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<PCDModel> call, Response<PCDModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<PCDModel> response = (Response<PCDModel>) msg.obj;

                            if (GUBUN.equals("HW")) {
                                mList_HW = response.body().Data;
                                if (mList_HW == null)
                                    mList_HW = new ArrayList<>();
                                mHwAdapter.updateData(mList_HW);
                                mHwAdapter.notifyDataSetChanged();
                            } else if (GUBUN.equals("SW")) {
                                mList_SW = response.body().Data;
                                if (mList_SW == null)
                                    mList_SW = new ArrayList<>();
                                mSwAdapter.updateData(mList_SW);
                                mSwAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<PCDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }

    public void requestPCM_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailPcm.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();


//        if (pcmVO.ARM_03.equals("Y")) {
//
//            Intent intent = new Intent(mContext, Alarm_Receiver.class);
//            intent.putExtra("notify_id",pcmVO.getARM_04());
//            intent.putExtra("calDateTime",pcmVO.getPCM_96());
//            intent.putExtra("contentTitle","장독관리" + pcmVO.getPCM_02());
//            intent.putExtra("contentText",pcmVO.getPCM_03());
//            intent.putExtra("className", ".ui.intro.Intro");
//            intent.putExtra("gotoActivity", ".ui.pcm.PCMMain");
//
//
//            PcmVO pcmvo = new PcmVO();
//            pcmvo.setPCM_01(pcmVO.getPCM_01());
//            pcmvo.setPCM_02(pcmVO.getPCM_02());
//            pcmvo.setPCM_03(pcmVO.getPCM_03());
//            pcmvo.setPCM_96(pcmVO.getPCM_96());
//            pcmvo.setARM_03(pcmVO.getARM_03());
//
//            intent.putExtra("PcmVO", pcmvo);
//
//            new AlarmHATT(mContext).Alarm(intent);
//        }
//        else{
//            cancelAlarm(mContext, pcmVO.getARM_04());
//        }


        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Call<PCMModel> call = Http.pcm(HttpBaseService.TYPE.POST).PCM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                mUser.Value.CTM_01,
                pcmVO.PCM_01,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                format1.format(calendar.getTime()),
                pcmVO.PCM_96,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                pcmVO.ARM_03
        );


        call.enqueue(new Callback<PCMModel>() {
            @Override
            public void onResponse(Call<PCMModel> call, Response<PCMModel> response) {

                if(GUBUN.equals("UPDATE")){
                    onBackPressed();
                    Toast.makeText(getApplicationContext(), "[" + pcmVO.PCM_02 + "]" + "  해당 PC관리 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    tv_manageDay.setText(format1.format(calendar.getTime()));
                }
            }

            @Override
            public void onFailure(Call<PCMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

//    public void cancelAlarm(Context context, int alarmId) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, Alarm_Receiver.class);
//        intent.putExtra("notify_id", alarmId);
//        intent.putExtra("ContentTitle", "");
//        intent.putExtra("contentText", "");
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.cancel(pendingIntent);
//        pendingIntent.cancel();
//    }

    public void onResume() {
        super.onResume();

        requestPCD_SELECT("LIST_HW");
        requestPCD_SELECT("LIST_SW");

    }


    @Override
    protected void initLayout() {

        recyclerView_hw = findViewById(R.id.recyclerView_hw);
        recyclerView_sw = findViewById(R.id.recyclerView_sw);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_memo = (EditText) findViewById(R.id.ed_memo);
        sp_sw = (Spinner) findViewById(R.id.sp_sw);
        et_sw = (EditText) findViewById(R.id.et_sw);

        sp_hw = (Spinner) findViewById(R.id.sp_hw);
        et_hw = (EditText) findViewById(R.id.et_hw);


        map_sw.put("선택", "0");
        map_sw.put("운영체제", "1");
        map_sw.put("그래픽드라이버", "2");
        map_sw.put("어도비", "3");
        map_sw.put("백신", "4");
        map_sw.put("기타", "5");


        map_hw.put("선택", "0");
        map_hw.put("CPU", "1");
        map_hw.put("메인보드", "2");
        map_hw.put("그래픽카드", "3");
        map_hw.put("RAM", "4");
        map_hw.put("SSD", "5");
        map_hw.put("HDD", "6");
        map_hw.put("파워", "7");
        map_hw.put("쿨러", "8");
        map_hw.put("케이스", "9");



        tv_datePicker = (TextView) findViewById(R.id.tv_datePicker2);
        btn_datePicker = (Button) findViewById(R.id.btn_datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        btn_addItem_hw = (Button) findViewById(R.id.btn_addItem_hw);
        btn_addItem_sw = (Button) findViewById(R.id.btn_addItem_sw);
        tv_manageDay = (TextView) findViewById(R.id.tv_manageDay);
        btn_update = (Button) findViewById(R.id.btn_update);
        bt_save = (Button) findViewById(R.id.bt_save);
        switch_alarm = (Switch) findViewById(R.id.switch_alarm);

        pcmVO = (PcmVO) getIntent().getSerializableExtra("PcmVO");

        String year = pcmVO.getPCM_96().substring(0, 4);
        String month = pcmVO.getPCM_96().substring(4, 6);
        String dayOfMonth = pcmVO.getPCM_96().substring(6, 8);
        String dayOfTime = pcmVO.getPCM_96().substring(8);

        hourOfDayString = dayOfTime.substring(0, 2);
        minuteString = dayOfTime.substring(2);


        tv_manageDay.setText(pcmVO.getPCM_04());
        tv_datePicker.setText(year + "년" + month + "월" + dayOfMonth + "일");

        ed_name.setText(pcmVO.getPCM_02());

        //명칭은 읽기전용으로 일단은...
        ed_name.setEnabled(false);

        ed_memo.setText(pcmVO.getPCM_03());

        if (pcmVO.ARM_03.equals("Y")) {
            switch_alarm.setChecked(true);

        } else {
            switch_alarm.setChecked(false);
        }


        timePicker.setCurrentHour(Integer.valueOf(dayOfTime.substring(0, 2)));
        timePicker.setCurrentMinute(Integer.valueOf(dayOfTime.substring(2)));

        requestPCD_SELECT("LIST_HW");
        requestPCD_SELECT("LIST_SW");
    }

    @Override
    protected void initialize() {

        mList_HW = new ArrayList<>();
        linearLayoutManager_HW = new LinearLayoutManager(mContext);
        recyclerView_hw.setLayoutManager(linearLayoutManager_HW);
        mHwAdapter = new PcdHwRecycleAdapter(mContext, mList_HW);
        mHwAdapter.setmAdapter(mHwAdapter);

        recyclerView_hw.setAdapter(mHwAdapter);

        mList_SW = new ArrayList<>();
        linearLayoutManager_SW = new LinearLayoutManager(mContext);
        recyclerView_sw.setLayoutManager(linearLayoutManager_SW);
        mSwAdapter = new PcdSwRecycleAdapter(mContext, mList_SW);
        mSwAdapter.setmAdapter(mSwAdapter);

        recyclerView_sw.setAdapter(mSwAdapter);


        btn_addItem_hw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (map_hw.get(sp_hw.getSelectedItem()).equals("0")) {
                    Toast.makeText(mContext, "선택 필요", Toast.LENGTH_SHORT).show();
                    return;
                }

                PcdVO pcdVO = new PcdVO();
                pcdVO.GUBUN = "INSERT";
                pcdVO.PCD_ID = mUser.Value.CTM_01;
                pcdVO.PCD_01 = pcmVO.PCM_01;
                pcdVO.PCD_02 = "";
                pcdVO.PCD_03 = "1";
                pcdVO.PCD_04 = map_hw.get(sp_hw.getSelectedItem());
                pcdVO.PCD_05 = et_hw.getText().toString();
                pcdVO.PCD_98 = mUser.Value.OCM_01;
                requestPCD_CONTROL(pcdVO, "HW");
            }
        });


        btn_addItem_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (map_sw.get(sp_sw.getSelectedItem()).equals("0")) {
                    Toast.makeText(mContext, "선택 필요", Toast.LENGTH_SHORT).show();
                    return;
                }

                PcdVO pcdVO = new PcdVO();
                pcdVO.GUBUN = "INSERT";
                pcdVO.PCD_ID = "1";
                pcdVO.PCD_01 = pcmVO.PCM_01;
                pcdVO.PCD_02 = "";
                pcdVO.PCD_03 = "2";
                pcdVO.PCD_04 = map_sw.get(sp_sw.getSelectedItem());
                pcdVO.PCD_05 = et_sw.getText().toString();
                pcdVO.PCD_98 = "M191100001";
                requestPCD_CONTROL(pcdVO, "SW");
            }
        });


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
                tv_datePicker.setText(year + "년" + monthString + "월" + dayOfMonthString + "일");
                pcmVO.setPCM_96(year + monthString + dayOfMonthString + hourOfDayString + minuteString);
            }
        };


        btn_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailPcm.this, callbackMethod, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                String date = tv_datePicker.getText().toString().replace("년", "").replace("월", "").replace("일", "");
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
                pcmVO.setPCM_96(date + hourOfDayString + minuteString);

            }
        });


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
                pcmVO.setPCM_04(format1.format(calendar.getTime()));
                requestPCM_CONTROL("UPDATE_DATE");
                Toast.makeText(mContext,"최근 관리일자 업데이트",Toast.LENGTH_LONG).show();
            }
        });


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPCM_CONTROL("UPDATE");
            }
        });


        switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    pcmVO.setARM_03("Y");
                } else {
                    switch_alarm.setChecked(false);
                    pcmVO.setARM_03("N");
                }
            }
        });
    }

}
