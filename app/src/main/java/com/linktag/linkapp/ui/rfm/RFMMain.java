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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

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
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.RfdVO;
import com.linktag.linkapp.value_object.RfmVO;

import java.util.ArrayList;
import java.util.Arrays;

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
    private ArrayList<RfmVO> mRfmList;

    private String CTM_01;
    private String CTN_02;

    private Spinner headerSpinner;
    private ArrayList<SpinnerList> mSpinnerList;

    private ImageView imgNew;

    public static String RFM_01 = "";

    private int pos = -1;


    private String[] str;
    private String[] index;

    public RFMMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rfm);
        initLayout();
        initialize();


        if (getIntent().hasExtra("scanCode")) {

            requestRFM_SELECT("DETAIL", getIntent().getStringExtra("scanCode"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        requestRFM_SELECT();


    }


    protected void initLayout() {

        mSpinnerList = new ArrayList<>();
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        headerSpinner = findViewById(R.id.spHeaderRight);
        header.spHeaderRight.setVisibility(View.VISIBLE);

        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);

        imgNew = findViewById(R.id.imgNew);

        swipeRefresh = findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(() -> requestJMD_SELECT());
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });

        header.spHeaderRight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (pos != -1) {
                    headerSpinner.setSelection(pos);
                    requestRFD_SELECT(mSpinnerList.get(pos).getCode());
                    RFM_01 = mSpinnerList.get(pos).getCode();
                    pos = -1;
                } else {
                    headerSpinner.setSelection(position);
                    requestRFD_SELECT(mSpinnerList.get(position).getCode());
                    RFM_01 = mSpinnerList.get(position).getCode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

        //requestRFM_SELECT();


        imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RfdVO rfdvo = new RfdVO();
                rfdvo.setRFD_ID(CTN_02);
                rfdvo.setRFD_01(RFM_01);
                rfdvo.setRFD_02("");
                rfdvo.setRFD_03("");
                rfdvo.setRFD_04("");
                rfdvo.setRFD_05("");
                rfdvo.setRFD_06("");
                rfdvo.setRFD_96("");
                rfdvo.setARM_03("N");
                rfdvo.setARM_04(0);

                Intent intent = new Intent(mContext, DetailRfd.class);
                intent.putExtra("RfdVO", rfdvo);

                intent.putExtra("GUBUN", "INSERT");
                mContext.startActivity(intent);
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
                CTN_02,
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

                            Response<RFMModel> response = (Response<RFMModel>) msg.obj;
                            mRfmList = response.body().Data;

                            str = new String[response.body().Total];
                            index = new String[response.body().Total];

                            mSpinnerList.clear();
                            if (response.body().Total > 0) {
                                for (int i = 0; i < response.body().Total; i++) {
                                    mSpinnerList.add(new SpinnerList(response.body().Data.get(i).RFM_01, response.body().Data.get(i).RFM_02));

                                    index[i] = response.body().Data.get(i).RFM_01;
                                    str[i] = response.body().Data.get(i).RFM_02;
                                }
                                final ArrayAdapter<String> hAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item2, str);
                                headerSpinner.setAdapter(hAdapter);
                                if (RFM_01.equals("")) {
                                    RFM_01 = mSpinnerList.get(0).getCode();
                                }
                            }
                            if (index != null) {
                                ArrayList<String> rfm_index = new ArrayList<>(Arrays.asList(index));
                                pos = rfm_index.indexOf(RFM_01);
                                headerSpinner.setSelection(rfm_index.indexOf(RFM_01) == -1 ? 0 : rfm_index.indexOf(RFM_01));
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


    public void requestRFM_SELECT(String GUBUN, String scancode) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<RFMModel> call = Http.rfm(HttpBaseService.TYPE.POST).RFM_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                CTN_02,
                scancode,
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

                            Response<RFMModel> response = (Response<RFMModel>) msg.obj;
                            mRfmList = response.body().Data;
                            if (mRfmList == null) mRfmList = new ArrayList<>();
                            if (mRfmList.size() == 0) {
                                RfmVO rfmvo = new RfmVO();
                                rfmvo.setRFM_ID(getIntent().getStringExtra("CTN_02"));
                                rfmvo.setRFM_01(getIntent().getStringExtra("scanCode"));
                                rfmvo.setRFM_02("");
                                rfmvo.setRFM_03("");
                                rfmvo.setRFM_97(mUser.Value.OCM_01);
                                rfmvo.setRFM_98(mUser.Value.OCM_01);

                                Intent intent = new Intent(mContext, DetailRfm.class);
                                intent.putExtra("RfmVO", rfmvo);

                                intent.putExtra("scanCode", getIntent().getStringExtra("scanCode"));
                                intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
                                intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
                                mContext.startActivity(intent);
                            }
                            else{
                                RFM_01 = mRfmList.get(0).RFM_01;
                            }
//                            else {
//                                requestRFD_SELECT(mRfmList.get(0).RFM_01);
//                            }


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

    public void requestRFD_SELECT(String RFM_01) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        Call<RFDModel> call = Http.rfd(HttpBaseService.TYPE.POST).RFD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                CTN_02,
                RFM_01,
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
