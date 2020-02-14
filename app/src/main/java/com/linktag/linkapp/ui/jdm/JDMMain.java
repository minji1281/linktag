package com.linktag.linkapp.ui.main;

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

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private JdmRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<JdmVO> mList;

    private String SVC_01;
    private String CTN_02;

    public JDMMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_jdm);
        initLayout();
        initialize();

        if (getIntent().hasExtra("className")) {
            Intent intent = getIntent();
            Push_goActivity(intent);
        }



    }

    @Override
    public void onResume() {
        super.onResume();

        requestJMD_SELECT();


    }

    private void Push_goActivity(Intent intent) {
        intent.setClassName(mContext, getPackageName() + ".ui.jdm_class.DetailJdm");
        mContext.startActivity(intent);
    }


    protected void initLayout() {

        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);


        swipeRefresh = findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(() -> requestJMD_SELECT());
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestJMD_SELECT();
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

        SVC_01 = getIntent().getStringExtra("SVC_01");
        CTN_02 = getIntent().getStringExtra("CTN_02");
    }


    public void requestJMD_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<JDMModel> call = Http.jdm(HttpBaseService.TYPE.POST).JDM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                CTN_02,
                "",
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
                            if (mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

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
