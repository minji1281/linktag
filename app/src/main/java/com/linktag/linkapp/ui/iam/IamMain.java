package com.linktag.linkapp.ui.iam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.IAMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.iam.IamDetail;
import com.linktag.linkapp.ui.iam.IamRecycleAdapter;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.ChangeActivityCls;
import com.linktag.linkapp.ui.menu.ChooseOne;
import com.linktag.linkapp.ui.menu.ChooseScan;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.IamVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IamMain extends BaseActivity {

    private BaseHeader header;
    private BaseFooter footer;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private IamRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<IamVO> mList;

    private CtdVO intentVO;

    private EditText ed_search;
    private TextView empty;

    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

    private FrameLayout linearLayout;
    private InputMethodManager imm;

    public IamMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_iam);
        initLayout();
        initialize();

        if (getIntent().hasExtra("scanCode")) {
            String scancode = getIntent().getExtras().getString("scanCode");
            requestIAM_SELECT(scancode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        requestIAM_SELECT("");

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

        linearLayout = findViewById(R.id.linearLayout);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);

        ed_search = findViewById(R.id.ed_search);
        empty = findViewById(R.id.empty);

        swipeRefresh = findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestIAM_SELECT("");
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
        mAdapter = new IamRecycleAdapter(mContext, mList, intentVO);
        recyclerView.setAdapter(mAdapter);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }

        });

    }


    private void initLayoutByContractType(){
        footer = findViewById(R.id.footer);
        footer.btnFooterScan.setOnClickListener(v -> goScan());

        if(intentVO.CTD_09.equals("P")){
            // privateService
            footer.btnFooterSetting.setVisibility(View.VISIBLE);
            footer.btnFooterMember.setVisibility(View.GONE);
        } else {
            // sharedService
            header.tvHeaderTitle2.setVisibility(View.VISIBLE);
            header.tvHeaderTitle2.setText(intentVO.CTD_10);

            if(intentVO.CTD_07.equals(mUser.Value.OCM_01)){
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

    private void goMember() {
        Intent intent = new Intent(mContext, Member.class);
        intent.putExtra("intentVO", intentVO);
        mContext.startActivity(intent);
    }


    public void requestIAM_SELECT(String scancode) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<IAMModel> call = Http.iam(HttpBaseService.TYPE.POST).IAM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                intentVO.CTN_02,
                scancode,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<IAMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<IAMModel> call, Response<IAMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<IAMModel> response = (Response<IAMModel>) msg.obj;
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
                                    IamVO iamvo = new IamVO();
                                    iamvo.setIAM_ID(intentVO.CTN_02);
                                    iamvo.setIAM_01(scancode);
                                    iamvo.setIAM_02(0);
                                    iamvo.setIAM_03("");
                                    iamvo.setIAM_96("");
                                    iamvo.setIAM_97(mUser.Value.OCM_01);
                                    Intent intent = new Intent(mContext, IamDetail.class);
                                    intent.putExtra("IamVO", iamvo);
                                    intent.putExtra("scanCode", scancode);
                                    intent.putExtra("intentVO", intentVO);
                                    mContext.startActivity(intent);
                                } else {
                                    IamVO iamvo = mList.get(0);
                                    Intent intent = new Intent(mContext, IamDetail.class);
                                    intent.putExtra("intentVO", intentVO);
                                    intent.putExtra("IamVO", iamvo);

                                    mContext.startActivity(intent);
                                }
                            }
                            mAdapter.getFilter().filter(ed_search.getText());
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<IAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    @Override
    protected void scanResult(String str) {
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }
}
