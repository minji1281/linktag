package com.linktag.linkapp.ui.rfm;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.linktag.linkapp.model.RFDModel;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm.AlarmDialog;
import com.linktag.linkapp.value_object.RfdVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRfd extends BaseActivity {

    private BaseHeader header;

    private EditText ed_name;
    private EditText ed_memo;
    private ImageView imageView;
    private ImageView imageView2;
    private LinearLayout datePicker;
    private TextView tv_datePicker;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private String callBackTime = "";
    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private TextView tv_D_day;
    private Spinner sp_rfm;
    private Spinner sp_pos;
    private String[] RFM_02;
    private Button bt_save;
    private LinearLayout rfm_move;

    private RfdVO rfdVO;

    private Calendar calendar = Calendar.getInstance();

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    private HashMap<String, String> map_pos = new HashMap<String, String>();
    private HashMap<String, String> map_rfm = new HashMap<String, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rfd);


        initLayout();

        initialize();

        if (getIntent().hasExtra("GUBUN")) {  //신규등록시 넘어오는 인텐트
            rfm_move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"등록진행 중인 식품은 이동할 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
            });
        }

    }

    public void requestRFD_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailRfd.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<RFDModel> call = Http.rfd(HttpBaseService.TYPE.POST).RFD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                rfdVO.RFD_ID,
                rfdVO.RFD_01,
                rfdVO.RFD_02,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                rfdVO.RFD_05,
                rfdVO.RFD_06,
                rfdVO.RFD_96,
                mUser.Value.OCM_01,
                rfdVO.ARM_03
        );


        call.enqueue(new Callback<RFDModel>() {
            @Override
            public void onResponse(Call<RFDModel> call, Response<RFDModel> response) {

                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {
                    Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + "  해당 식품정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    RFMMain.RFM_01 = rfdVO.RFD_01;
                } else {
                    Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + "  해당 식품정보가 이동되었습니다.", Toast.LENGTH_SHORT).show();
                    RFMMain.RFM_01 = rfdVO.RFD_01;
                }
                onBackPressed();
            }

            @Override
            public void onFailure(Call<RFDModel> call, Throwable t) {
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

        rfm_move = (LinearLayout) findViewById(R.id.rfm_move);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        datePicker = (LinearLayout) findViewById(R.id.datePicker);

        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_memo = (EditText) findViewById(R.id.ed_memo);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        tv_datePicker = (TextView) findViewById(R.id.tv_datePicker);
        bt_save = (Button) findViewById(R.id.bt_save);
        tv_D_day = findViewById(R.id.tv_D_day);

        sp_rfm = findViewById(R.id.sp_rfm);
        sp_pos = findViewById(R.id.sp_pos);

        String[] str = getResources().getStringArray(R.array.rfd);
        final ArrayAdapter<String> adapter_pos = new ArrayAdapter<String>(mContext, R.layout.spinner_detail_item, str);
        sp_pos.setAdapter(adapter_pos);


        rfdVO = (RfdVO) getIntent().getSerializableExtra("RfdVO");


        if (rfdVO.getRFD_96().equals("")) {
            calendar.add(Calendar.DATE, 14);
            tv_datePicker.setText(format.format(calendar.getTime()));

            rfdVO.setRFD_96(formatDate.format(calendar.getTime()) + formatTime.format(calendar.getTime()));
        } else {
            String year = rfdVO.getRFD_96().substring(0, 4);
            String month = rfdVO.getRFD_96().substring(4, 6);
            String dayOfMonth = rfdVO.getRFD_96().substring(6, 8);
            tv_datePicker.setText(year + "." + month + "." + dayOfMonth);

            Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(dayOfMonth));


            calendar.clear(Calendar.HOUR);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화

            dCalendar.clear(Calendar.HOUR);
            dCalendar.clear(Calendar.MINUTE);
            dCalendar.clear(Calendar.SECOND);
            dCalendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화

            long dDayDiff = dCalendar.getTimeInMillis() - calendar.getTimeInMillis();
            int day = (int)(Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));

            startCountAnimation(day);


        }

        callBackTime = rfdVO.RFD_96.substring(8, 12);

        if (rfdVO.ARM_03.equals("Y")) {
            imageView.setImageResource(R.drawable.alarm_state_on);
        } else {
            imageView.setImageResource(R.drawable.alarm_state_off);
        }



        if (rfdVO.RFD_06.equals("")) {
            sp_pos.setSelection(0);
        } else {
            sp_pos.setSelection(Integer.parseInt(rfdVO.RFD_06));
        }

        ed_name.setText(rfdVO.getRFD_03());
        ed_memo.setText(rfdVO.getRFD_04());

        map_pos.put("냉장실", "0");
        map_pos.put("냉동실", "1");
        map_pos.put("야채실", "2");
        map_pos.put("기타", "3");

        requestRFM_SELECT();
    }

    @Override
    protected void initialize() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rfdVO.ARM_03.equals("Y")) {
                    imageView.setImageResource(R.drawable.alarm_state_off);
                    rfdVO.setARM_03("N");
                } else if (rfdVO.ARM_03.equals("N")) {
                    imageView.setImageResource(R.drawable.alarm_state_on);
                    rfdVO.setARM_03("Y");
                }
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
                        rfdVO.setRFD_96(rfdVO.getRFD_96().substring(0, 8) + time);
                        callBackTime = time;
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                alarmDialog.show();
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
                rfdVO.setRFD_96(year + monthString + dayOfMonthString + rfdVO.RFD_96.substring(8,12));
                tv_datePicker.setText(year + "." + monthString + "." + dayOfMonthString);
            }
        };


        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(DetailRfd.this, callbackMethod, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
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

                rfdVO.setRFD_06(map_pos.get(sp_pos.getSelectedItem()));

                if (getIntent().hasExtra("GUBUN")) {
                    requestRFD_CONTROL("INSERT");
                } else {
                    requestRFD_CONTROL("UPDATE");
                }

            }
        });

        rfm_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRFD_CONTROL("DELETE");
                rfdVO.setRFD_01(map_rfm.get(sp_rfm.getSelectedItem()));
                requestRFD_CONTROL("RFD_MOVE");
            }
        });

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
                                requestRFD_CONTROL("DELETE");
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

    public void requestRFM_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<RFMModel> call = Http.rfm(HttpBaseService.TYPE.POST).RFM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                rfdVO.RFD_ID,
                "",
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<RFMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RFMModel> call, Response<RFMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            map_rfm.clear();
                            Response<RFMModel> response = (Response<RFMModel>) msg.obj;
                            RFM_02 = new String[response.body().Total];

                            if (response.body().Total > 0) {
                                for (int i = 0; i < response.body().Total; i++) {
                                    RFM_02[i] = response.body().Data.get(i).RFM_02;
                                    map_rfm.put(response.body().Data.get(i).RFM_02, response.body().Data.get(i).RFM_01);
                                }
                                final ArrayAdapter<String> adapter_rtm = new ArrayAdapter<String>(mContext, R.layout.spinner_detail_item, RFM_02);
                                sp_rfm.setAdapter(adapter_rtm);
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RFMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

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
