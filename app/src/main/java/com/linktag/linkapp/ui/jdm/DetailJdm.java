package com.linktag.linkapp.ui.jdm;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
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
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.JdmVO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailJdm extends BaseActivity implements Serializable {

    private BaseHeader header;

    private EditText ed_name;
    private EditText ed_memo;

    private ImageView imageView;
    private ImageView imageView_check;
    private LinearLayout datePicker;
    private LinearLayout datePicker2;
    private TextView tv_datePicker;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private DatePickerDialog.OnDateSetListener callbackMethod2;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private Button bt_save;
    private TextView tv_D_day;
    private TextView tv_nextDate;


    private Spinner sp_size;
    private Spinner sp_recycleDay;

    private JdmVO jdmVO;

    private Calendar calendar = Calendar.getInstance();
    private Calendar nextDay = Calendar.getInstance();

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

    private HashMap<String, String> map_size = new HashMap<String, String>();
    private HashMap<String, String> map_day = new HashMap<String, String>();

    private String CTM_01;
    private String CTD_02;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_jdm);
        setContentView(R.layout.activity_detail_jdm2);


        sp_size = findViewById(R.id.sp_size);

        String[] str = getResources().getStringArray(R.array.jdm);
        final ArrayAdapter<String> adapter_size = new ArrayAdapter<String>(mContext, R.layout.spinner_item3, str);
        sp_size.setAdapter(adapter_size);

        sp_recycleDay = findViewById(R.id.sp_recycleDay);

        String[] str2 = getResources().getStringArray(R.array.jdm2);
        final ArrayAdapter<String> adapter_recycleDay = new ArrayAdapter<String>(mContext, R.layout.spinner_item3, str2);
        sp_recycleDay.setAdapter(adapter_recycleDay);


        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            CTM_01 = getIntent().getStringExtra("CTM_01");
            CTD_02 = getIntent().getStringExtra("CTD_02");
        }

    }

    public void requestJMD_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailJdm.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        if (GUBUN.equals("UPDATE_NEXT")) {
            nextDay.set(Calendar.YEAR, Integer.parseInt(jdmVO.JDM_96.substring(0, 4)));
            nextDay.set(Calendar.MONTH, Integer.parseInt(jdmVO.JDM_96.substring(4, 6))-1);
            nextDay.set(Calendar.DATE, Integer.parseInt(jdmVO.JDM_96.substring(6, 8)));

            boolean date = true;
            while (date){
                switch (map_day.get(sp_recycleDay.getSelectedItem())) {
                    case "0":
                        nextDay.add(Calendar.DATE, 3);
                        break;
                    case "1":
                        nextDay.add(Calendar.DATE, 5);
                        break;
                    case "2":
                        nextDay.add(Calendar.DATE, 7);
                        break;
                    case "3":
                        nextDay.add(Calendar.DATE, 15);
                        break;
                    case "4":
                        nextDay.add(Calendar.DATE, 30);
                        break;
                }
                if(Integer.parseInt(formatDate.format(nextDay.getTime())) > Integer.parseInt(formatDate.format(calendar.getTime()))){
                    date = false;
                }
            }


            jdmVO.setJDM_96(formatDate.format(nextDay.getTime()) + "1100");

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
                jdmVO.JDM_96,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                jdmVO.ARM_03
        );


        call.enqueue(new Callback<JDMModel>() {
            @Override
            public void onResponse(Call<JDMModel> call, Response<JDMModel> response) {

                if (GUBUN.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, CTM_01, CTD_02, jdmVO.JDM_01);
                    ctds_control.requestCTDS_CONTROL();
                    onBackPressed();
                }
                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {
                    Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + "  해당 장독정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                if (GUBUN.equals("UPDATE_NEXT")) {

                    tv_nextDate.setText(format.format(nextDay.getTime()));
                    imageView_check.setImageResource(R.drawable.ic_check_on);
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

        linearLayout = findViewById(R.id.linearLayout);
        datePicker = findViewById(R.id.datePicker);
        datePicker2 = findViewById(R.id.datePicker2);
        tv_nextDate = findViewById(R.id.tv_nextDate);
        ed_name = findViewById(R.id.ed_name);
        ed_memo = findViewById(R.id.ed_memo);
        imageView = findViewById(R.id.imageView);
        imageView_check = findViewById(R.id.imageView_check);
        tv_datePicker = findViewById(R.id.tv_datePicker);
        bt_save = findViewById(R.id.bt_save);
        tv_D_day = findViewById(R.id.tv_D_day);

        jdmVO = (JdmVO) getIntent().getSerializableExtra("JdmVO");


        ed_name.setText(jdmVO.getJDM_02());
        ed_memo.setText(jdmVO.getJDM_03());


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
            calendar.add(Calendar.DATE, 3);
            tv_nextDate.setText(format.format(calendar.getTime()));
            jdmVO.setJDM_96(formatDate.format(calendar.getTime()) + "1100");

        } else {
            tv_nextDate.setText(jdmVO.JDM_96.substring(0, 4) + "." + jdmVO.JDM_96.substring(4, 6) + "." + jdmVO.JDM_96.substring(6, 8));

        }


        if (jdmVO.ARM_03.equals("Y")) {
            imageView.setImageResource(R.drawable.alarm_state_on);
        } else {
            imageView.setImageResource(R.drawable.alarm_state_off);
        }

        if (Integer.parseInt(jdmVO.JDM_96.substring(0, 8)) <  Integer.parseInt(formatDate.format(calendar.getTime()))) {
            imageView_check.setImageResource(R.drawable.ic_check_off);
        } else {
            imageView_check.setImageResource(R.drawable.ic_check_on);
        }


        map_size.put("대", "0");
        map_size.put("중", "1");
        map_size.put("소", "2");

        map_day.put("3일", "0");
        map_day.put("5일", "1");
        map_day.put("7일", "2");
        map_day.put("15일", "3");
        map_day.put("30일", "4");

        if (jdmVO.JDM_05.equals("")) {
            sp_size.setSelection(0);
        } else {
            sp_size.setSelection(Integer.parseInt(jdmVO.JDM_05));
        }
        if (jdmVO.JDM_06.equals("")) {
            sp_size.setSelection(0);
        } else {
            sp_recycleDay.setSelection(Integer.parseInt(jdmVO.JDM_06));
        }
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
                jdmVO.setJDM_96(String.valueOf(year) + monthString + dayOfMonth+"1100");
                tv_nextDate.setText(year + "." + monthString + "." + dayOfMonthString);
            }
        };

        datePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailJdm.this, callbackMethod2, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
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
                jdmVO.setJDM_06(map_day.get(sp_recycleDay.getSelectedItem()));
                if (getIntent().hasExtra("scanCode")) {
                    requestJMD_CONTROL("INSERT");
                } else {
                    requestJMD_CONTROL("UPDATE");
                }
            }
        });

        imageView_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( Integer.parseInt(jdmVO.JDM_96.substring(0,8)) < Integer.parseInt(formatDate.format(calendar.getTime()))){
                    requestJMD_CONTROL("UPDATE_NEXT");
                }else{
                    Toast.makeText(mContext,"이미 체크하셨습니다.",Toast.LENGTH_LONG).show();
                    return;
                }
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
                    new AlertDialog.Builder(mActivity)
                            .setMessage("해당 정보를 삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestJMD_CONTROL("DELETE");
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

    private void startCountAnimation(int count) {

        ValueAnimator animator = ValueAnimator.ofInt(0, count); //0 is min number, 600 is max number
        animator.setDuration(3000); //Duration is in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {

                tv_D_day.setText(animation.getAnimatedValue().toString());
            }
        });

        animator.start();
    }


}
