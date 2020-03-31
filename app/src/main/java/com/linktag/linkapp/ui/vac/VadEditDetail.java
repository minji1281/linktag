package com.linktag.linkapp.ui.vac;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.VADModel;
import com.linktag.linkapp.model.VAMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.VadVO;
import com.linktag.linkapp.value_object.VamVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VadEditDetail extends BaseActivity {

    private BaseHeader header;

    private EditText ed_vadInfo;

    private LinearLayout datePicker;
    private String callBackTime = "";

    private String VAC_ID;
    private String VAC_01;
    public static String VAM_02 ="";
    private String ARM_03;

    private InterfaceUser mUser = InterfaceUser.getInstance();

    private RecyclerView recyclerView_vam;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<VamVO> mList;
    private VamRecycleAdapter2 mAdapter;

    public static RecyclerView recyclerView_vad;
    private LinearLayoutManager linearLayoutManager_vad;
    private ArrayList<VadVO> mList_vad;
    private VadRecycleAdapter mAdapter_vad;


    public static TextView tv_vam_nodata;
    public static TextView tv_vamCnt;

    public static TextView tv_vad_nodata;
    public static TextView tv_vadCnt;

    private TextView tv_datePicker;

    private Button btn_addItem;

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    private Calendar calendar = Calendar.getInstance();
    private Calendar dialogcalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vad_edit);

        initLayout();
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initLayout() {

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        datePicker = findViewById(R.id.datePicker);
        tv_datePicker = findViewById(R.id.tv_datePicker);
        btn_addItem = findViewById(R.id.btn_addItem);

        VAC_ID = getIntent().getStringExtra("VAC_ID");
        VAC_01 = getIntent().getStringExtra("VAC_01");
        ARM_03 = getIntent().getStringExtra("ARM_03");

        recyclerView_vad = findViewById(R.id.recyclerView_vad);
        tv_vadCnt = findViewById(R.id.tv_vadCnt);
        tv_vad_nodata = findViewById(R.id.tv_vad_nodata);


        mList_vad = new ArrayList<>();
        linearLayoutManager_vad = new LinearLayoutManager(mContext);
        recyclerView_vad.setLayoutManager(linearLayoutManager_vad);
        mAdapter_vad = new VadRecycleAdapter(mContext, mList_vad);
        mAdapter_vad.setmAdapter(mAdapter_vad);
        recyclerView_vad.setAdapter(mAdapter_vad);


        recyclerView_vam = findViewById(R.id.recyclerView_vam);
        tv_vamCnt = findViewById(R.id.tv_vamCnt);
        tv_vam_nodata = findViewById(R.id.tv_vam_nodata);

        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView_vam.setLayoutManager(linearLayoutManager);
        mAdapter = new VamRecycleAdapter2(mContext, mList);
        mAdapter.setmAdapter(mAdapter_vad);
        recyclerView_vam.setAdapter(mAdapter);

    }

    @Override
    protected void initialize() {

        requestVAM_SELECT();

        ed_vadInfo = findViewById(R.id.ed_vadInfo);



        callBackTime = formatDate.format(calendar.getTime()) + formatTime.format(calendar.getTime());


        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(VAM_02.equals("")){
                    Toast.makeText(mContext,"접종기관을 먼저 선택하세요.",Toast.LENGTH_LONG);
                }
                else if (tv_datePicker.getText().equals("접종 예정일 선택")){
                    Toast.makeText(mContext,"접종예정 일자를 선택하세요.",Toast.LENGTH_LONG);
                }
                else if (ed_vadInfo.getText().equals("")){
                    Toast.makeText(mContext,"접종정보를 입력하세요.",Toast.LENGTH_LONG);
                }
                else{
                    requestVAD_CONTROL("INSERT");
                }
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                VadAlarmDialog alarmDialog = new VadAlarmDialog(mContext, dialogcalendar);

                alarmDialog.setDialogListener(new VadAlarmDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(Calendar calendar) {
                        dialogcalendar = calendar;
                        tv_datePicker.setText(format.format(calendar.getTime()) + " " + format2.format(calendar.getTime()));
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                alarmDialog.show();
            }
        });

    }

    public void requestVAD_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(VadEditDetail.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        Call<VADModel> call = Http.vad(HttpBaseService.TYPE.POST).VAD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                VAC_ID,
                VAC_01,
                VAM_02,
                "",
                ed_vadInfo.getText().toString(),
                formatDate.format(dialogcalendar.getTime()) + formatTime.format(dialogcalendar.getTime()),
                mUser.Value.OCM_01,
                ARM_03
        );


        call.enqueue(new Callback<VADModel>() {
            @Override
            public void onResponse(Call<VADModel> call, Response<VADModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VADModel> response = (Response<VADModel>) msg.obj;

                            mList_vad = response.body().Data;
                            if (mList_vad == null)
                                mList_vad = new ArrayList<>();
                            tv_vadCnt.setText("("+mList_vad.size()+")");
                            if(mList_vad.size()==0){
                                tv_vad_nodata.setVisibility(View.VISIBLE);
                                recyclerView_vad.setVisibility(View.GONE);
                            }
                            else{
                                recyclerView_vad.setVisibility(View.VISIBLE);
                                tv_vad_nodata.setVisibility(View.GONE);
                            }

                            mAdapter_vad.updateData(mList_vad);
                            mAdapter_vad.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VADModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


    public void requestVAM_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<VAMModel> call = Http.vam(HttpBaseService.TYPE.POST).VAM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                VAC_ID,
                VAC_01
        );


        call.enqueue(new Callback<VAMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VAMModel> call, Response<VAMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VAMModel> response = (Response<VAMModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();

                            tv_vamCnt.setText("(" + mList.size() + ")");
                            if (mList.size() == 0) {
                                tv_vam_nodata.setVisibility(View.VISIBLE);
                                recyclerView_vam.setVisibility(View.GONE);

                            } else {
                                recyclerView_vam.setVisibility(View.VISIBLE);
                                tv_vam_nodata.setVisibility(View.GONE);
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }


}
