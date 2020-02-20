package com.linktag.linkapp.ui.pcm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.PCMModel;
import com.linktag.linkapp.model.TRPModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.jdm.DetailJdm;
import com.linktag.linkapp.ui.trp.TrpRecycleAdapter;
import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.PcmVO;
import com.linktag.linkapp.value_object.TrpVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCMMain extends BaseActivity {

    private BaseHeader header;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private PcmRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<PcmVO> mList;

    private String CTM_01;
    private String CTN_02;


    public PCMMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pcm);
        initLayout();
        initialize();

        if (getIntent().hasExtra("scanCode")) {
            String scancode = getIntent().getExtras().getString("scanCode");
            requestPCM_SELECT(scancode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        requestPCM_SELECT("");


    }


    protected void initLayout() {

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTN_02 = getIntent().getStringExtra("CTN_02");

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);


        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestPCM_SELECT("");
                swipeRefresh.setRefreshing(false);
            }
        });
    }


    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new PcmRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);
    }


    public void requestPCM_SELECT(String scancode) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<PCMModel> call = Http.pcm(HttpBaseService.TYPE.POST).PCM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                CTN_02,
                scancode,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<PCMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<PCMModel> call, Response<PCMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<PCMModel> response = (Response<PCMModel>) msg.obj;

                            mList = response.body().Data;
                            if (scancode.equals("")) {
                                if (mList == null) mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);

                            } else {
                                if (mList.size() == 0) {

                                    PcmVO pcmvo = new PcmVO();
                                    pcmvo.setPCM_ID(CTN_02);
                                    pcmvo.setPCM_01(scancode);
                                    pcmvo.setPCM_02("");
                                    pcmvo.setPCM_03("");
                                    pcmvo.setPCM_04("");
                                    pcmvo.setPCM_96("");
                                    pcmvo.setPCM_97(mUser.Value.OCM_01);
                                    pcmvo.setARM_03("N");
                                    pcmvo.setARM_04(0);

                                    Intent intent = new Intent(mContext, DetailPcm.class);
                                    intent.putExtra("PcmVO", pcmvo);
                                    intent.putExtra("scanCode", scancode);
                                    intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
                                    intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
                                    mContext.startActivity(intent);
                                } else {
                                    PcmVO pcmvo = mList.get(0);
                                    Intent intent = new Intent(mContext, DetailPcm.class);
                                    intent.putExtra("PcmVO", pcmvo);

                                    mContext.startActivity(intent);
                                }
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<PCMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

}
