package com.linktag.linkapp.ui.jdm;

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
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.JdmVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JDMMain extends BaseActivity {

    private BaseHeader header;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private JdmRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<JdmVO> mList;

    private String CTM_01;
    private String CTN_02;

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

//    private void Push_goActivity(Intent intent) {
//        String scancode = getIntent().getExtras().getString("scancode");
//
//        intent.setClassName(mContext, getPackageName() + ".ui.jdm.DetailJdm");
//
//        mContext.startActivity(intent);
//    }


    protected void initLayout() {


        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

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
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new JdmRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTN_02 = getIntent().getStringExtra("CTN_02");
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
                CTN_02,
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
                                    jdmvo.setJDM_ID(CTN_02);
                                    jdmvo.setJDM_01(scancode);
                                    jdmvo.setJDM_02("");
                                    jdmvo.setJDM_03("");
                                    jdmvo.setJDM_04("");
                                    jdmvo.setJDM_05("");
                                    jdmvo.setJDM_06("");
                                    jdmvo.setJDM_96("");
                                    jdmvo.setJDM_97(mUser.Value.OCM_01);
                                    jdmvo.setARM_03("N");
                                    jdmvo.setARM_04(0);
                                    Intent intent = new Intent(mContext, DetailJdm.class);
                                    intent.putExtra("JdmVO", jdmvo);
                                    intent.putExtra("scanCode", scancode);
                                    intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
                                    intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
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
}
