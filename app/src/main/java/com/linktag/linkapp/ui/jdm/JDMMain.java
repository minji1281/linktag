package com.linktag.linkapp.ui.jdm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.JdmVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JDMMain extends BaseActivity {

    private BaseHeader header;
    private BaseFooter footer;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private JdmRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<JdmVO> mList;

    private CtdVO intentVO;

    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

    public JDMMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_jdm);
        initLayout();
        initialize();

        if (getIntent().hasExtra("scanCode")) {
            String scancode = getIntent().getExtras().getString("scanCode");
            requestJMD_SELECT(scancode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        requestJMD_SELECT("");


    }



    protected void initLayout() {

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        initLayoutByContractType();

        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);


        swipeRefresh = findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(() -> requestJMD_SELECT());
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestJMD_SELECT("");
                swipeRefresh.setRefreshing(false);
            }
        });
    }


    protected void initialize() {
        mList = new ArrayList<>();
//        linearLayoutManager = new GridLayoutManager(mContext,2);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new JdmRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);

    }


    private void initLayoutByContractType(){
        footer = findViewById(R.id.footer);
        footer.btnFooterScan.setOnClickListener(v -> goScan());

        if(intentVO.CTM_19.equals("P")){
            // privateService
            footer.btnFooterSetting.setVisibility(View.VISIBLE);
            footer.btnFooterMember.setVisibility(View.GONE);
        } else {
            // sharedService
            header.tvHeaderTitle2.setVisibility(View.VISIBLE);
            header.tvHeaderTitle2.setText(intentVO.CTM_17);

            footer.btnFooterSetting.setVisibility(View.GONE);
            footer.btnFooterMember.setVisibility(View.VISIBLE);

            footer.btnFooterMember.setOnClickListener(v -> goMember());
        }
    }

    private void goMember(){
        Intent intent = new Intent(mContext, Member.class);
        intent.putExtra("intentVO", intentVO);
        mContext.startActivity(intent);
    }

    public void requestJMD_SELECT(String scancode) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<JDMModel> call = Http.jdm(HttpBaseService.TYPE.POST).JDM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                intentVO.CTN_02,
                scancode,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<JDMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<JDMModel> call, Response<JDMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<JDMModel> response = (Response<JDMModel>) msg.obj;
                            mList = response.body().Data;

                            if (scancode.equals("")) {
                                if (mList == null) mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);

                            } else {
                                if (mList.size() == 0) {
                                    JdmVO jdmvo = new JdmVO();
                                    jdmvo.setJDM_ID(intentVO.CTN_02);
                                    jdmvo.setJDM_01(scancode);
                                    jdmvo.setJDM_02("");
                                    jdmvo.setJDM_03("");
                                    jdmvo.setJDM_04("");
                                    jdmvo.setJDM_05("");
                                    jdmvo.setJDM_06("7");  //기본 7일
                                    jdmvo.setJDM_07("0");  // 일
                                    jdmvo.setJDM_08("");
                                    jdmvo.setJDM_96("");
                                    jdmvo.setJDM_97(mUser.Value.OCM_01);
                                    jdmvo.setARM_03("N");
                                    jdmvo.setARM_04(0);
                                    Intent intent = new Intent(mContext, DetailJdm.class);
                                    intent.putExtra("JdmVO", jdmvo);
                                    intent.putExtra("scanCode", scancode);
                                    intent.putExtra("intentVO", intentVO);
                                    mContext.startActivity(intent);
                                } else {
                                    JdmVO jdmvo = mList.get(0);
                                    Intent intent = new Intent(mContext, DetailJdm.class);
                                    intent.putExtra("JdmVO", jdmvo);

                                    mContext.startActivity(intent);
                                }
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<JDMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    @Override
    protected void scanResult(String str){
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }
}
