package com.linktag.linkapp.ui.trp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.model.TRPModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.TrdVO;
import com.linktag.linkapp.value_object.TrpVO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTrp extends BaseActivity implements Serializable {

    private BaseHeader header;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<TrdVO> mList;
    private TrdRecycleAdapter mAdapter;


    private ImageView imageView;
    private EditText ed_name;
    private EditText ed_memo;
    private Spinner sp_count;
    private Spinner sp_timing;
    private EditText ed_target;

    private HashMap<String, String> map_count = new HashMap<String, String>();
    private HashMap<String, String> map_timing = new HashMap<String, String>();

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    // 여러개의 버튼을 배열로 처리하기 위해 버튼에 대해 배열 선언을 함
    Button[] mBtnArray = new Button[7];

    private Button bt_save;
    private Button btn_addAlarm;

    private String callBackTime = "";

    private TrpVO trpVO;

    private Calendar calendar = Calendar.getInstance();


    private String alarmTime;

    private String CTM_01;
    private String CTD_02;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trp2);


        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            CTM_01 = getIntent().getStringExtra("CTM_01");
            CTD_02 = getIntent().getStringExtra("CTD_02");
        }
    }

    public void requestTRP_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailTrp.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        Call<TRPModel> call = Http.trp(HttpBaseService.TYPE.POST).TRP_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                trpVO.TRP_ID,
                trpVO.TRP_01,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                trpVO.TRP_04,
                trpVO.TRP_05,
                trpVO.TRP_06,
                ed_target.getText().toString(),
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                trpVO.ARM_03
        );


        call.enqueue(new Callback<TRPModel>() {
            @Override
            public void onResponse(Call<TRPModel> call, Response<TRPModel> response) {

                if (GUBUN.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, CTM_01, CTD_02, trpVO.TRP_01);
                    ctds_control.requestCTDS_CONTROL();
                }
                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {
                    Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + "  해당 복약정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }

            @Override
            public void onFailure(Call<TRPModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


    public void onResume() {
        super.onResume();

        requestTRD_SELECT();


    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);


        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        linearLayout = findViewById(R.id.linearLayout);

        imageView = findViewById(R.id.imageView);
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_memo = (EditText) findViewById(R.id.ed_memo);

        sp_count = findViewById(R.id.sp_count);
        sp_timing = findViewById(R.id.sp_timing);
        ed_target = findViewById(R.id.ed_target);
        bt_save = (Button) findViewById(R.id.bt_save);
        btn_addAlarm = (Button) findViewById(R.id.btn_addAlarm);


        mBtnArray[0] = (Button) findViewById(R.id.btn_Sunday);
        mBtnArray[1] = (Button) findViewById(R.id.btn_Monday);
        mBtnArray[2] = (Button) findViewById(R.id.btn_Tuesday);
        mBtnArray[3] = (Button) findViewById(R.id.btn_Wednesday);
        mBtnArray[4] = (Button) findViewById(R.id.btn_Thursday);
        mBtnArray[5] = (Button) findViewById(R.id.btn_Friday);
        mBtnArray[6] = (Button) findViewById(R.id.btn_Saturday);

        trpVO = (TrpVO) getIntent().getSerializableExtra("TrpVO");

        String[] array_pattern;
        array_pattern = trpVO.TRP_04.split("");

        if (trpVO.TRP_04.equals("")) {
            for (int i = 0; i < mBtnArray.length; i++) {
                mBtnArray[i].setBackgroundResource(R.drawable.btn_round_yellow);
            }
        } else {
            for (int i = 0; i < mBtnArray.length; i++) {
                if (array_pattern[i + 1].equals("Y")) {
                    mBtnArray[i].setBackgroundResource(R.drawable.btn_round_yellow);
                } else {
                    mBtnArray[i].setBackgroundResource(R.drawable.btn_round_gray);
                }
            }
        }


        if (trpVO.ARM_03.equals("Y")) {
            imageView.setImageResource(R.drawable.alarm_state_on);
        } else {
            imageView.setImageResource(R.drawable.alarm_state_off);
        }

        ed_name.setText(trpVO.getTRP_02());
        ed_memo.setText(trpVO.getTRP_03());


        map_count.put("1회", "0");
        map_count.put("2회", "1");
        map_count.put("3회", "2");
        map_count.put("4회", "4");
        map_count.put("5회", "5");

        map_timing.put("식전 30분", "0");
        map_timing.put("식후 30분", "1");
        map_timing.put("상관없음", "2");


        String[] str = getResources().getStringArray(R.array.trp);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item4, str);
        sp_count.setAdapter(adapter);

        String[] str2 = getResources().getStringArray(R.array.trp2);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, R.layout.spinner_item4, str2);
        sp_timing.setAdapter(adapter2);


        if (trpVO.TRP_05.equals("")) {
            sp_count.setSelection(0);
        } else {
            sp_count.setSelection(Integer.parseInt(trpVO.TRP_05));
        }

        if (trpVO.TRP_06.equals("")) {
            sp_timing.setSelection(0);
        } else {
            sp_timing.setSelection(Integer.parseInt(trpVO.TRP_06));
        }


        ed_target.setText(trpVO.getTRP_07());
        callBackTime = formatTime.format(calendar.getTime());
        requestTRD_SELECT();
    }


    @Override
    protected void initialize() {

        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TrdRecycleAdapter(mContext, mList);
        mAdapter.setmAdapter(mAdapter);

        recyclerView.setAdapter(mAdapter);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trpVO.ARM_03.equals("Y")) {
                    imageView.setImageResource(R.drawable.alarm_state_off);
                    trpVO.setARM_03("N");
                } else if (trpVO.ARM_03.equals("N")) {
                    imageView.setImageResource(R.drawable.alarm_state_on);
                    trpVO.setARM_03("Y");
                }
            }
        });


        btn_addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                AlarmDialog alarmDialog = new AlarmDialog(mContext, callBackTime);

                alarmDialog.setDialogListener(new AlarmDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String time) {
                        callBackTime = time;
                        alarmTime = format.format(calendar.getTime()) + time;

                        requestTRD_CONTROL();
//                        trdVO.setT_96(jdmVO.getJDM_96().substring(0, 8) + time);
//                        callBackTime = time;
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                alarmDialog.show();
            }
        });


//        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            @Override
//            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
//
//                alarmTime = alarmTime.substring(0, 8);
//
//                if (hourOfDay < 10) {
//                    alarmTime += "0" + String.valueOf(hourOfDay);
//                } else {
//                    alarmTime += String.valueOf(hourOfDay);
//                }
//                if (minute < 10) {
//                    alarmTime += "0" + String.valueOf(minute);
//                } else {
//                    alarmTime += String.valueOf(minute);
//                }
//
//            }
//        });


        if (trpVO.TRP_97.equals(mUser.Value.OCM_01)) { //작성자만 삭제버튼 보임
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
                                    requestTRP_CONTROL("DELETE");
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
                trpVO.setTRP_05(map_count.get(sp_count.getSelectedItem()));
                trpVO.setTRP_06(map_timing.get(sp_timing.getSelectedItem()));

                requestTRP_CONTROL("UPDATE");
            }
        });

//        btn_addAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestTRD_CONTROL();
//            }
//        });


        // 버튼들에 대한 클릭리스너 등록 및 각 버튼이 클릭되었을 때
        for (int i = 0; i < mBtnArray.length; i++) {
            // 버튼의 포지션(배열에서의 index)를 태그로 저장
            // 클릭 리스너 등록
            mBtnArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String[] array_pattern;
                    array_pattern = trpVO.TRP_04.split("");

                    switch (v.getId()) {
                        case R.id.btn_Sunday:
                            if (array_pattern[1].equals("Y")) {
                                mBtnArray[0].setBackgroundResource(R.drawable.btn_round_gray);
                                array_pattern[1] = "N";
                            } else {
                                mBtnArray[0].setBackgroundResource(R.drawable.btn_round_yellow);
                                array_pattern[1] = "Y";
                            }
                            break;
                        case R.id.btn_Monday:
                            if (array_pattern[2].equals("Y")) {
                                mBtnArray[1].setBackgroundResource(R.drawable.btn_round_gray);
                                array_pattern[2] = "N";
                            } else {
                                mBtnArray[1].setBackgroundResource(R.drawable.btn_round_yellow);
                                array_pattern[2] = "Y";
                            }
                            break;
                        case R.id.btn_Tuesday:
                            if (array_pattern[3].equals("Y")) {
                                mBtnArray[2].setBackgroundResource(R.drawable.btn_round_gray);
                                array_pattern[3] = "N";
                            } else {
                                mBtnArray[2].setBackgroundResource(R.drawable.btn_round_yellow);
                                array_pattern[3] = "Y";
                            }
                            break;
                        case R.id.btn_Wednesday:
                            if (array_pattern[4].equals("Y")) {
                                mBtnArray[3].setBackgroundResource(R.drawable.btn_round_gray);
                                array_pattern[4] = "N";
                            } else {
                                mBtnArray[3].setBackgroundResource(R.drawable.btn_round_yellow);
                                array_pattern[4] = "Y";
                            }
                            break;
                        case R.id.btn_Thursday:
                            if (array_pattern[5].equals("Y")) {
                                mBtnArray[4].setBackgroundResource(R.drawable.btn_round_gray);
                                array_pattern[5] = "N";
                            } else {
                                mBtnArray[4].setBackgroundResource(R.drawable.btn_round_yellow);
                                array_pattern[5] = "Y";
                            }
                            break;
                        case R.id.btn_Friday:
                            if (array_pattern[6].equals("Y")) {
                                mBtnArray[5].setBackgroundResource(R.drawable.btn_round_gray);
                                array_pattern[6] = "N";
                            } else {
                                mBtnArray[5].setBackgroundResource(R.drawable.btn_round_yellow);
                                array_pattern[6] = "Y";
                            }
                            break;
                        case R.id.btn_Saturday:
                            if (array_pattern[7].equals("Y")) {
                                mBtnArray[6].setBackgroundResource(R.drawable.btn_round_gray);
                                array_pattern[7] = "N";
                            } else {
                                mBtnArray[6].setBackgroundResource(R.drawable.btn_round_yellow);
                                array_pattern[7] = "Y";
                            }
                            break;

                    }
                    trpVO.setTRP_04(array_pattern[1] + array_pattern[2] + array_pattern[3] + array_pattern[4] + array_pattern[5] + array_pattern[6] + array_pattern[7]);
                }
            });

        }


    }


    public void requestTRD_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<TRDModel> call = Http.trd(HttpBaseService.TYPE.POST).TRD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                trpVO.TRP_ID,
                trpVO.TRP_01,
                ""
        );


        call.enqueue(new Callback<TRDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TRDModel> call, Response<TRDModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<TRDModel> response = (Response<TRDModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<TRDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


    public void requestTRD_CONTROL() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<TRDModel> call = Http.trd(HttpBaseService.TYPE.POST).TRD_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                trpVO.TRP_ID,
                trpVO.TRP_01,
                "",
                alarmTime,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<TRDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TRDModel> call, Response<TRDModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<TRDModel> response = (Response<TRDModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
                Toast.makeText(mContext, "추가되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<TRDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }


}
