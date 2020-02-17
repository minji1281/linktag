package com.linktag.linkapp.ui.rfm;

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
import com.linktag.linkapp.model.RFDModel;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.RfdVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RFMMain extends BaseActivity {

    private BaseHeader header;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private RfdRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<RfdVO> mList;

    private String CTM_01;
    private String CTN_02;

    public RFMMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rfm);
        initLayout();
        initialize();


    }

    @Override
    public void onResume() {
        super.onResume();

        requestRFD_SELECT();


    }

    private void Push_goActivity(Intent intent) {
        intent.setClassName(mContext, getPackageName() + ".ui.jdm.DetailRfm");
        mContext.startActivity(intent);
    }


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
                requestRFD_SELECT();
                swipeRefresh.setRefreshing(false);
            }
        });
    }


    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RfdRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTN_02 = getIntent().getStringExtra("CTN_02");
    }


    public void requestRFD_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<RFDModel> call = Http.rfd(HttpBaseService.TYPE.POST).RFD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                CTN_02,
                "QR202001220004",
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<RFDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RFDModel> call, Response<RFDModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<RFDModel> response = (Response<RFDModel>) msg.obj;

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
            public void onFailure(Call<RFDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }
}
