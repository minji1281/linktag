package com.linktag.linkapp.ui.pot;

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
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PotList extends BaseActivity implements PotAdapter.AlarmClickListener {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private ListView listView;
    private TextView emptyText;

    //======================
    // Variable
    //======================
    private PotAdapter mAdapter;
    private ArrayList<PotVO> mList;
    private String scancode = "";
    private String CTM_01;
    private String CTD_02;
    private String CTN_02;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pot_list);

        initLayout();

        initialize();

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTD_02 = getIntent().getStringExtra("CTD_02");
        CTN_02 = getIntent().getStringExtra("CTN_02");
        if (getIntent().hasExtra("scanCode")) {
            scancode = getIntent().getExtras().getString("scanCode");
            requestPOT_SELECT("DETAIL", scancode);
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
                Intent intent = new Intent(mContext, PotDetail.class);
                intent.putExtra("POT_81", mList.get(position).POT_81);
                intent.putExtra("POT_02", mList.get(position).POT_02);
                intent.putExtra("POT_03_T", mList.get(position).POT_03_T);
                intent.putExtra("POT_04", mList.get(position).POT_04);
                intent.putExtra("POT_05", mList.get(position).POT_05);
                intent.putExtra("ARM_03", mList.get(position).ARM_03);
                intent.putExtra("POT_96", mList.get(position).POT_96);
                intent.putExtra("POT_06", mList.get(position).POT_06);
                intent.putExtra("POT_01", mList.get(position).POT_01);
                intent.putExtra("POT_97", mList.get(position).POT_97);
                intent.putExtra("ARM_04", mList.get(position).ARM_04);
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
        mAdapter = new PotAdapter(mContext, mList, this); //, this
        listView.setAdapter(mAdapter);

        //requestPOT_SELECT();
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestPOT_SELECT("LIST", "");
    }

    private void requestPOT_SELECT(String GUBUN, String POT_01){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String POT_ID = CTN_02; //컨테이너
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                OCM_01
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
//                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            mList = response.body().Data;

                            if(GUBUN.equals("LIST")){
                                if(mList == null)
                                    mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                            }
                            else{ //DETAIL (스캔했을때)
                                if(mList.size() == 0){ //등록된 정보가 없을때
                                    goPotNew();
                                }
                                else{ //등록된 정보가 있을때
                                    Intent intent = new Intent(mContext, PotDetail.class);
                                    intent.putExtra("POT_81", mList.get(0).POT_81);
                                    intent.putExtra("POT_02", mList.get(0).POT_02);
                                    intent.putExtra("POT_03_T", mList.get(0).POT_03_T);
                                    intent.putExtra("POT_04", mList.get(0).POT_04);
                                    intent.putExtra("POT_05", mList.get(0).POT_05);
                                    intent.putExtra("ARM_03", mList.get(0).ARM_03);
                                    intent.putExtra("POT_96", mList.get(0).POT_96);
                                    intent.putExtra("POT_06", mList.get(0).POT_06);
                                    intent.putExtra("POT_01", mList.get(0).POT_01);
                                    intent.putExtra("POT_97", mList.get(0).POT_97);
                                    intent.putExtra("ARM_04", mList.get(0).ARM_04);
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
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    //알람 주석처리
//    private void requestPOT_CONTROL(String GUB, PotVO pot) {
//        // 인터넷 연결 여부 확인
//        if (!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(mContext.getString(R.string.common_network_error));
//            return;
//        }
//
//        String GUBUN = GUB;
//        String POT_ID = pot.POT_ID; //컨테이너
//        String POT_01 = pot.POT_01; //코드번호
//        String POT_02 = "";
//        int POT_04 = 0;
//
//        String POT_05 = "";
//        String POT_06 = "";
//        String POT_81 = "";
//        String POT_96 = "";
//        String POT_98 = mUser.Value.OCM_01; //사용자 아이디
//
//        String ARM_03 = pot.ARM_03; //알림여부
//
//        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
//                BaseConst.URL_HOST,
//                GUBUN,
//                POT_ID,
//                POT_01,
//                POT_02,
//                POT_04,
//
//                POT_05,
//                POT_06,
//                POT_81,
//                POT_96,
//                POT_98,
//
//                ARM_03
//        );
//
//        call.enqueue(new Callback<POT_Model>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if (msg.what == 100){
//
//                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;
//
//                            if(response.body().Data.get(0).Validation){
//                                AlarmMain alarmMain = new AlarmMain();
//                                int ID = response.body().Data.get(0).ARM_04;
//
//                                if(ARM_03.equals("Y")){
//                                    String alarmTitle = "물주기 - " + response.body().Data.get(0).POT_02;
//                                    String alarmText = "식물에게 물을 주세요~";
//                                    String className = ".ui.pot.PotScan";
//
//                                    Intent intent = new Intent();
//                                    intent.putExtra("POT_81", response.body().Data.get(0).POT_81);
//                                    intent.putExtra("POT_02", response.body().Data.get(0).POT_02);
//                                    intent.putExtra("POT_03_T", response.body().Data.get(0).POT_03_T);
//                                    intent.putExtra("POT_04", response.body().Data.get(0).POT_04);
//                                    intent.putExtra("POT_05", response.body().Data.get(0).POT_05);
//                                    intent.putExtra("ARM_03", response.body().Data.get(0).ARM_03);
//                                    intent.putExtra("POT_96", response.body().Data.get(0).POT_96);
//                                    intent.putExtra("POT_06", response.body().Data.get(0).POT_06);
//                                    intent.putExtra("POT_01", response.body().Data.get(0).POT_01);
//                                    intent.putExtra("POT_97", response.body().Data.get(0).POT_97);
//                                    intent.putExtra("className", className);
//
//                                    intent.putExtra("ID", ID);
//                                    intent.putExtra("alarmTitle", alarmTitle);
//                                    intent.putExtra("alarmText", alarmText);
//
//                                    alarmMain.setAlarm(getApplicationContext(), intent); //푸시알람 설정
//                                }
//                                else{
//                                    alarmMain.deleteAlarm(getApplicationContext(), pot.ARM_04); //푸시알람 해제
//                                }
//
//                                onResume();
//                            } else {
//                                Toast.makeText(mContext, R.string.login_err, Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<POT_Model> call, Throwable t){
//                Log.d("Test", t.getMessage());
//
//            }
//        });
//
//    }

    @Override
    public void onListAlarmClick(int position) {
        PotVO data = mList.get(position);

        if(data.ARM_03.equals("Y")){
            data.ARM_03 = "N";
        }
        else{ //N
            data.ARM_03 = "Y";
        }

        Toast.makeText(mContext, "준비중 입니다.", Toast.LENGTH_LONG).show();

//        requestPOT_CONTROL("ALARM_UPDATE", data);
    }

    private void goPotNew(){
        Intent intent = new Intent(mContext, PotNew.class);
        intent.putExtra("POT_01", scancode);
        intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
        intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
        intent.putExtra("CTN_02", CTN_02);
        mContext.startActivity(intent);
    }

}
