package com.linktag.linkapp.ui.air;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.AIRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.air.AirAdapter;
import com.linktag.linkapp.value_object.AIR_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AirList extends BaseActivity implements AirAdapter.AlarmClickListener {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private ListView listView;
    private TextView emptyText;

    //======================
    // Variable
    //======================
    private AirAdapter mAdapter;
    private ArrayList<AIR_VO> mList;
    private ArrayList<AIR_VO> mList2;
    private String CTN_02;
    private String scancode;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_air_list);

        initLayout();

        initialize();

        CTN_02 = getIntent().getStringExtra("CTN_02");

        if (getIntent().hasExtra("scanCode")) {
            scancode = getIntent().getExtras().getString("scanCode");
            requestAIR_SELECT("DETAIL", scancode);
        }
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility((View.GONE));

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, AirDetail.class);
                AIR_VO AIR = mList.get(position);
                intent.putExtra("AIR", AIR);
                intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
                intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
                intent.putExtra("CTN_02", CTN_02);
                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new AirAdapter(mContext, mList, this);
        listView.setAdapter(mAdapter);

        //requestAIR_SELECT();
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestAIR_SELECT("LIST", "");
    }

    private void requestAIR_SELECT(String GUBUN, String AIR_01){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String AIR_ID = CTN_02; //컨테이너
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<AIRModel> call = Http.air(HttpBaseService.TYPE.POST).AIR_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                AIR_ID,
                AIR_01,
                OCM_01
        );

        call.enqueue(new Callback<AIRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<AIRModel> call, Response<AIRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<AIRModel> response = (Response<AIRModel>) msg.obj;



                            if(GUBUN.equals("LIST")){
                                mList = response.body().Data;
                                if(mList == null)
                                    mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                            }
                            else{ //DETAIL (스캔찍을때)
                                mList2 = response.body().Data;
                                if(mList2 == null)
                                    mList2 = new ArrayList<>();

                                if(mList2.size() == 0){ //등록된 정보가 없는경우
                                    goAirNew();
                                }
                                else{ //등록된 정보가 있는경우
                                    AIR_VO AIR = mList2.get(0);
                                    Intent intent = new Intent(mContext, AirDetail.class);
                                    intent.putExtra("AIR", AIR);
                                    intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
                                    intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
                                    intent.putExtra("CTN_02", CTN_02);

                                    mContext.startActivity(intent);
                                }
                            }



                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<AIRModel> call, Throwable t){
                Log.d("AIR_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void requestAIR_CONTROL(String GUBUN, AIR_VO AIR) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String AIR_ID = CTN_02; //컨테이너
        String AIR_01 = AIR.AIR_01; //코드번호
        String AIR_02 = "";
        String AIR_03 = "";
        String AIR_04 = "";
        int AIR_05 = 0;
        String AIR_06 = "";
        String AIR_07 = "";
        String AIR_96 = "";
        String AIR_98 = mUser.Value.OCM_01; //사용자코드
        String ARM_03 = AIR.ARM_03;

        Call<AIRModel> call = Http.air(HttpBaseService.TYPE.POST).AIR_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                AIR_ID,
                AIR_01,
                AIR_02,
                AIR_03,

                AIR_04,
                AIR_05,
                AIR_06,
                AIR_07,
                AIR_96,

                AIR_98,
                ARM_03
        );

        call.enqueue(new Callback<AIRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<AIRModel> call, Response<AIRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<AIRModel> response = (Response<AIRModel>) msg.obj;

                            onResume();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<AIRModel> call, Throwable t){
                Log.d("AIR_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    @Override
    public void onListAlarmClick(int position) {
        AIR_VO data = mList.get(position);

        if(data.ARM_03.equals("Y")){
            data.ARM_03 = "N";
        }
        else{ //N
            data.ARM_03 = "Y";
        }

        requestAIR_CONTROL("ALARM_UPDATE", data);
    }

    private void goAirNew(){
        Intent intent = new Intent(mContext, AirDetail.class);
        intent.putExtra("scancode", scancode);
        intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
        intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
        intent.putExtra("CTN_02", CTN_02);
        mContext.startActivity(intent);
    }

}
