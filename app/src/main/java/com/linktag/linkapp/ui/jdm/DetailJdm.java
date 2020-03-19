package com.linktag.linkapp.ui.jdm;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm.AlarmDialog;
import com.linktag.linkapp.ui.master_log.MasterLog;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.LogVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailJdm extends BaseActivity {

    private BaseHeader header;

    private LinearLayout check_area;
    private EditText ed_name;
    private EditText ed_memo;

    private ImageView imageView;
    private ImageView imageView2;
    private ImageView imageView_check;
    private LinearLayout datePicker;
    private TextView tv_datePicker;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    private String callBackTime = "";
    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private Button bt_save;
    private TextView tv_D_day;
    private TextView tv_nextDate;


    private TextView tv_Log;

    private Spinner sp_size;
    private TextView tv_recycleDay;

    private CtdVO intentVO;
    private JdmVO jdmVO;
    private Calendar calendar = Calendar.getInstance();
    private Calendar dialogcalendar = Calendar.getInstance();

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    private HashMap<String, String> map_size = new HashMap<String, String>();


    private String recycleDayVal1;
    private String recycleDayVal2;



    private String[] str_datetime;
    private String[] str_save_text;
    private String[] str_cycle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jdm2);

        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
            check_area.setVisibility(View.GONE);
        }

    }

    public void requestJMD_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailJdm.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        Calendar sCalendar = Calendar.getInstance();

        switch (recycleDayVal2) {
            case "0":
                sCalendar.add(Calendar.DATE, Integer.parseInt(recycleDayVal1));
                tv_nextDate.setText(format.format(sCalendar.getTime()));
                jdmVO.setJDM_96(formatDate.format(sCalendar.getTime()) + jdmVO.getJDM_96().substring(8, 12));
                break;
            case "1":
                sCalendar.add(Calendar.MONTH, Integer.parseInt(recycleDayVal1));
                tv_nextDate.setText(format.format(sCalendar.getTime()));
                jdmVO.setJDM_96(formatDate.format(sCalendar.getTime()) + jdmVO.getJDM_96().substring(8, 12));
                break;
            case "2":
                jdmVO.setJDM_96(tv_nextDate.getText().toString().replace(".", "") + jdmVO.getJDM_96().substring(8, 12));
                jdmVO.setJDM_07("2");
                break;
        }

        if (GUBUN.equals("UPDATE_NEXT")) {
            jdmVO.setJDM_08(formatDate.format(calendar.getTime()));
        }

        Call<JDMModel> call = Http.jdm(HttpBaseService.TYPE.POST).JDM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                jdmVO.JDM_ID,
                jdmVO.JDM_01,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                jdmVO.JDM_04,
                jdmVO.JDM_05,
                jdmVO.JDM_06,
                jdmVO.JDM_07,
                jdmVO.JDM_08,
                jdmVO.JDM_96,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                jdmVO.ARM_03
        );


        call.enqueue(new Callback<JDMModel>() {
            @Override
            public void onResponse(Call<JDMModel> call, Response<JDMModel> response) {

                Calendar calendar = Calendar.getInstance();
                boolean dateBool = Long.parseLong(formatDate.format(calendar.getTime()) + formatTime.format(calendar.getTime())) < Long.parseLong(jdmVO.JDM_96);

                if (GUBUN.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, jdmVO.JDM_01);
                    ctds_control.requestCTDS_CONTROL();
                    onBackPressed();
                }
                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {

                    if (jdmVO.ARM_03.equals("Y") && dateBool) {
                        Toast.makeText(mContext,"[" + ed_name.getText().toString() + "]" + str_save_text[0] +"\n"+
                                str_save_text[1]+ jdmVO.JDM_96.substring(0,4)+str_datetime[0] + jdmVO.JDM_96.substring(4,6)+str_datetime[1]+ jdmVO.JDM_96.substring(6,8)+str_datetime[2] +
                                jdmVO.JDM_96.substring(8,10)+str_datetime[3] + jdmVO.JDM_96.substring(10,12)+str_datetime[4] + str_save_text[2], Toast.LENGTH_LONG ).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + str_save_text[0], Toast.LENGTH_SHORT).show();
                    }
                    onBackPressed();
                }
                if(GUBUN.equals("DELETE")){
                    onBackPressed();
                }
                if (GUBUN.equals("UPDATE_NEXT")) {
                    imageView_check.setImageResource(R.drawable.btn_round_skyblue_50dp);

                    if (jdmVO.ARM_03.equals("Y") && dateBool) {
                        Toast.makeText(mContext,"[" + ed_name.getText().toString() + "]" + str_save_text[0] +"\n"+
                                str_save_text[1]+ jdmVO.JDM_96.substring(0,4)+str_datetime[0] + jdmVO.JDM_96.substring(4,6)+str_datetime[1]+ jdmVO.JDM_96.substring(6,8)+str_datetime[2] +
                                jdmVO.JDM_96.substring(8,10)+str_datetime[3] + jdmVO.JDM_96.substring(10,12)+str_datetime[4] + str_save_text[2], Toast.LENGTH_LONG ).show();
                    }

                }

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

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        check_area = findViewById(R.id.check_area);
        linearLayout = findViewById(R.id.linearLayout);
        datePicker = findViewById(R.id.datePicker);
        tv_nextDate = findViewById(R.id.tv_nextDate);
        ed_name = findViewById(R.id.ed_name);
        ed_memo = findViewById(R.id.ed_memo);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView_check = findViewById(R.id.imageView_check);
        tv_datePicker = findViewById(R.id.tv_datePicker);
        bt_save = findViewById(R.id.bt_save);
        tv_D_day = findViewById(R.id.tv_D_day);
        tv_Log = findViewById(R.id.tv_Log);
        tv_recycleDay = findViewById(R.id.tv_recycleDay);
        jdmVO = (JdmVO) getIntent().getSerializableExtra("JdmVO");


        str_datetime = getResources().getStringArray(R.array.datetime);
        str_cycle = getResources().getStringArray(R.array.jdm_cycle);
        str_save_text = getResources().getStringArray(R.array.jdm_save_text);

        setRecycleDay(jdmVO.JDM_06, jdmVO.JDM_07, "");


        ed_name.setText(jdmVO.getJDM_02());
        ed_memo.setText(jdmVO.getJDM_03());


        sp_size = findViewById(R.id.sp_size);


        String[] str = getResources().getStringArray(R.array.jdm_size);
        final ArrayAdapter<String> adapter_size = new ArrayAdapter<String>(mContext, R.layout.spinner_detail_item, str);
        sp_size.setAdapter(adapter_size);

        map_size.put(str[0], "0");
        map_size.put(str[1], "1");
        map_size.put(str[2], "2");



        if (jdmVO.getJDM_04().equals("")) {
            tv_datePicker.setText(format.format(calendar.getTime()));
        } else {
            String year = jdmVO.getJDM_04().substring(0, 4);
            String month = jdmVO.getJDM_04().substring(4, 6);
            String dayOfMonth = jdmVO.getJDM_04().substring(6, 8);
            tv_datePicker.setText(year + "." + month + "." + dayOfMonth);


            Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(dayOfMonth));

            int count = (int) ((calendar.getTimeInMillis() - dCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000));
            startCountAnimation(count);
        }


        if (jdmVO.getJDM_96().equals("")) {
            calendar.add(Calendar.DATE, Integer.parseInt(jdmVO.JDM_06));
            tv_nextDate.setText(format.format(calendar.getTime()));
            jdmVO.setJDM_96(formatDate.format(calendar.getTime()) + formatTime.format(calendar.getTime()));

        } else {
            tv_nextDate.setText(jdmVO.JDM_96.substring(0, 4) + "." + jdmVO.JDM_96.substring(4, 6) + "." + jdmVO.JDM_96.substring(6, 8));

        }

        callBackTime = jdmVO.JDM_96.substring(8, 12);

        if (jdmVO.ARM_03.equals("Y")) {
            imageView.setImageResource(R.drawable.alarm_state_on);
        } else {
            imageView.setImageResource(R.drawable.alarm_state_off);
        }

        if (jdmVO.JDM_08.equals("") || Integer.parseInt(jdmVO.JDM_96.substring(0, 8)) < Integer.parseInt(formatDate.format(calendar.getTime()))) {
            imageView_check.setImageResource(R.drawable.btn_round_shallowgray_50dp);
        } else {
            imageView_check.setImageResource(R.drawable.btn_round_skyblue_50dp);
        }


        if (jdmVO.JDM_05.equals("")) {
            sp_size.setSelection(0);
        } else {
            sp_size.setSelection(Integer.parseInt(jdmVO.JDM_05));
        }

        dialogcalendar.set(Calendar.YEAR, Integer.parseInt(jdmVO.JDM_96.substring(0, 4)));
        dialogcalendar.set(Calendar.MONTH, Integer.parseInt(jdmVO.JDM_96.substring(4, 6)) - 1);
        dialogcalendar.set(Calendar.DATE, Integer.parseInt(jdmVO.JDM_96.substring(6, 8)));
    }

    @Override
    protected void initialize() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jdmVO.ARM_03.equals("Y")) {
                    imageView.setImageResource(R.drawable.alarm_state_off);
                    jdmVO.setARM_03("N");
                } else if (jdmVO.ARM_03.equals("N")) {
                    imageView.setImageResource(R.drawable.alarm_state_on);
                    jdmVO.setARM_03("Y");
                }
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
                tv_datePicker.setText(year + "." + monthString + "." + dayOfMonthString);
            }
        };


        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailJdm.this, callbackMethod, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();
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

                jdmVO.setJDM_05(map_size.get(sp_size.getSelectedItem()));
                jdmVO.setJDM_04(tv_datePicker.getText().toString().replace(".", ""));
                if (getIntent().hasExtra("scanCode")) {
                    requestJMD_CONTROL("INSERT");
                    requestLOG_CONTROL("1",str_save_text[5]);
                } else {
                    requestJMD_CONTROL("UPDATE");
                }
            }
        });


        tv_recycleDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecycleDayDialog Dialog = new RecycleDayDialog(mContext, jdmVO.JDM_06, jdmVO.JDM_07, dialogcalendar);

                Dialog.setDialogListener(new RecycleDayDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String val1, String val2, String val3, Calendar calendar) {

                        setRecycleDay(val1, val2, val3);
                        dialogcalendar = calendar;
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                Dialog.show();
            }

        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                AlarmDialog alarmDialog = new AlarmDialog(mContext, callBackTime);

                alarmDialog.setDialogListener(new AlarmDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String time) {
                        jdmVO.setJDM_96(jdmVO.getJDM_96().substring(0, 8) + time);
                        callBackTime = time;
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                alarmDialog.show();
            }
        });

        imageView_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(jdmVO.JDM_96.substring(0, 8)) < Integer.parseInt(formatDate.format(calendar.getTime()))) {
                    requestJMD_CONTROL("UPDATE_NEXT");
                    requestLOG_CONTROL("2",str_save_text[6]);
                } else {

                    new AlertDialog.Builder(mActivity)
                            .setMessage(str_save_text[3])
                            .setPositiveButton(getResources().getString(com.linktag.base.R.string.common_yes), new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestJMD_CONTROL("UPDATE_NEXT");
                                    requestLOG_CONTROL("2",str_save_text[6]);
                                }
                            })
                            .setNegativeButton(getResources().getString(com.linktag.base.R.string.common_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                    return;
                }
            }
        });


        tv_Log.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LogVO LOG = new LogVO();
                LOG.LOG_ID = jdmVO.JDM_ID;
                LOG.LOG_01 = jdmVO.JDM_01;
                LOG.LOG_98 = mUser.Value.OCM_01;
                LOG.SP_NAME = "SP_JDML_CONTROL";

                Intent intent = new Intent(mContext, MasterLog.class);
                intent.putExtra("LOG", LOG);

                mContext.startActivity(intent);
            }
        });

        if (jdmVO.JDM_97.equals(mUser.Value.OCM_01)) { //작성자만 삭제버튼 보임
            header.btnHeaderRight1.setVisibility((View.VISIBLE));
            header.btnHeaderRight1.setMaxWidth(50);
            header.btnHeaderRight1.setMaxHeight(50);
            header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
            header.btnHeaderRight1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteDialog();

                }
            });
        }

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
                if(etDeleteName.getText().toString().equals(jdmVO.JDM_02)){
                    dialog.dismiss();
                    requestJMD_CONTROL("DELETE");
                }
                else{
                    Toast.makeText(mActivity,  getResources().getString(com.linktag.base.R.string.common_confirm_delete), Toast.LENGTH_SHORT).show();
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

    private void setRecycleDay(String val1, String val2, String val3) {

        if (!val2.equals("2") && val3.equals("")) {
            recycleDayVal1 = val1;
            recycleDayVal2 = val2;
            Calendar sCalendar = Calendar.getInstance();
            if(!jdmVO.JDM_08.equals("")){
                sCalendar.set(Calendar.YEAR, Integer.parseInt(jdmVO.JDM_08.substring(0, 4)));
                sCalendar.set(Calendar.MONTH, Integer.parseInt(jdmVO.JDM_08.substring(4, 6)) - 1);
                sCalendar.set(Calendar.DATE, Integer.parseInt(jdmVO.JDM_08.substring(6, 8)));
            }

            jdmVO.setJDM_06(val1);
            jdmVO.setJDM_07(val2);

            switch (val2) {
                case "0":
                    sCalendar.add(Calendar.DATE, Integer.parseInt(val1));
                    tv_recycleDay.setText(val1 +" "+ str_cycle[0]);
                    tv_nextDate.setText(format.format(sCalendar.getTime()));

                    break;
                case "1":
                    sCalendar.add(Calendar.MONTH, Integer.parseInt(val1));
                    tv_recycleDay.setText(val1 +" "+ str_cycle[1]);
                    tv_nextDate.setText(format.format(sCalendar.getTime()));
                    break;
            }
        } else {
//            jdmVO.setJDM_06("1"); //1일
//            jdmVO.setJDM_07("0"); //일
            recycleDayVal2 = "2";
            tv_recycleDay.setText(str_cycle[2]);
            tv_nextDate.setText(val1 + "." + val2 + "." + val3);
        }
    }


    private void requestLOG_CONTROL(String LOG_03, String LOG_04){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, getResources().getString(com.linktag.base.R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                jdmVO.JDM_ID,
                jdmVO.JDM_01,
                "",
                LOG_03,
                LOG_04,
                "",
                mUser.Value.OCM_01,
                "SP_JDML_CONTROL"
        );

        call.enqueue(new Callback<LOG_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOG_Model> call, Response<LOG_Model> response){

            }

            @Override
            public void onFailure(Call<LOG_Model> call, Throwable t){
                Log.d("LOG_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });
    }


    private void startCountAnimation(int count) {

        ValueAnimator animator = ValueAnimator.ofInt(0, count); //0 is min number, 600 is max number
        animator.setDuration(500); //Duration is in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {

                tv_D_day.setText(animation.getAnimatedValue().toString());
            }
        });

        animator.start();
    }


}
