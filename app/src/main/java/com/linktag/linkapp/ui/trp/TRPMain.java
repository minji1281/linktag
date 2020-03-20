package com.linktag.linkapp.ui.trp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.model.TRPModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.jdm.DetailJdm;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.TrdVO;
import com.linktag.linkapp.value_object.TrpVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TRPMain extends BaseActivity {
    private BaseHeader header;
    private BaseFooter footer;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private TrpRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<TrpVO> mList;

    private EditText ed_search;
    private TextView empty;
    // 요거
    private CtdVO intentVO;

    public TRPMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trp);
        initLayout();
        initialize();

        if (getIntent().hasExtra("scanCode")) {
            String scancode = getIntent().getExtras().getString("scanCode");
            requestTRP_SELECT(scancode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        requestTRP_SELECT("");

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable edit) {
            }
        });


    }


    protected void initLayout() {
        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        initLayoutByContractType();

        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);

        ed_search = findViewById(R.id.ed_search);
        empty = findViewById(R.id.empty);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestTRP_SELECT("");
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    // 요거
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

            if(intentVO.CTM_04.equals(mUser.Value.OCM_01)){
                header.btnHeaderRight1.setVisibility(View.VISIBLE);
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AddSharedDetail.class);
                        intent.putExtra("type", "UPDATE");
                        intent.putExtra("intentVO", intentVO);
                        mContext.startActivity(intent);
                    }
                });
            }

            footer.btnFooterSetting.setVisibility(View.GONE);
            footer.btnFooterMember.setVisibility(View.VISIBLE);

            footer.btnFooterMember.setOnClickListener(v -> goMember());
        }
    }

    // 요거
    private void goMember(){
        Intent intent = new Intent(mContext, Member.class);
        intent.putExtra("intentVO", intentVO);
        mContext.startActivity(intent);
    }

    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TrpRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);
    }


    public void requestTRP_SELECT(String scancode) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<TRPModel> call = Http.trp(HttpBaseService.TYPE.POST).TRP_SELECT(
                BaseConst.URL_HOST,
                "TRP_LIST",
                intentVO.CTN_02,
                scancode,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<TRPModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TRPModel> call, Response<TRPModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<TRPModel> response = (Response<TRPModel>) msg.obj;

                            mList = response.body().Data;
                            if (scancode.equals("")) {
                                if (mList == null) mList = new ArrayList<>();

                                if(mList.size() ==0){
                                    empty.setVisibility(View.VISIBLE);
                                }
                                else{
                                    empty.setVisibility(View.GONE);
                                }

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);

                            } else {
                                if (mList.size() == 0) {

                                    TrpVO trpvo = new TrpVO();
                                    trpvo.setTRP_ID(intentVO.CTN_02);
                                    trpvo.setTRP_01(scancode);
                                    trpvo.setTRP_02("");
                                    trpvo.setTRP_03("");
                                    trpvo.setTRP_04("YYYYYYY");
                                    trpvo.setTRP_05("");
                                    trpvo.setTRP_06("");
                                    trpvo.setTRP_07("");
                                    trpvo.setTRP_97(mUser.Value.OCM_01);
                                    trpvo.setARM_03("N");
                                    Intent intent = new Intent(mContext, DetailTrp.class);
                                    intent.putExtra("TrpVO", trpvo);
                                    intent.putExtra("scanCode", scancode);
                                    intent.putExtra("intentVO", intentVO);  // 요래 넘깁시다
                                    mContext.startActivity(intent);
                                } else {
                                    TrpVO trpvo = mList.get(0);
                                    Intent intent = new Intent(mContext, DetailTrp.class);
                                    intent.putExtra("TrpVO", trpvo);
                                    mContext.startActivity(intent);
                                }
                            }
                            mAdapter.getFilter().filter(ed_search.getText());

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<TRPModel> call, Throwable t) {
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
