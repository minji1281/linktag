package com.linktag.linkapp.ui.dam;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.DAMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.rfm.RfdDetail;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.DamVO;
import com.linktag.linkapp.value_object.RfdVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DamMain extends BaseActivity {

    private BaseHeader header;
    private BaseFooter footer;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private DamRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<DamVO> mList;

    private CtdVO intentVO;

    private EditText ed_search;
    private TextView empty;

    private ImageView imgNew;

    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

    private FrameLayout linearLayout;
    private InputMethodManager imm;

    public DamMain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dam);
        initLayout();
        initialize();

        if (getIntent().hasExtra("scanCode")) {
            String scancode = getIntent().getExtras().getString("scanCode");
            requestDAM_SELECT(scancode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        requestDAM_SELECT("");

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
        imgNew = findViewById(R.id.imgNew);

        swipeRefresh = findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDAM_SELECT("");
                swipeRefresh.setRefreshing(false);
            }
        });
    }


    protected void initialize() {

        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new DamRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DamVO damvo = new DamVO();
                damvo.setDAM_ID(intentVO.CTN_02);
                damvo.setDAM_01("");
                damvo.setDAM_02("");
                damvo.setDAM_03("");
                damvo.setDAM_04("1");
                damvo.setDAM_96("");
                damvo.setDAM_97(mUser.Value.OCM_01);
                damvo.setDAM_98(mUser.Value.OCM_01);
                damvo.setARM_03("N");
                Intent intent = new Intent(mContext, DamDetail.class);
                intent.putExtra("DamVO", damvo);
                intent.putExtra("intentVO", intentVO);
                intent.putExtra("GUBUN", "INSERT");
                mContext.startActivity(intent);

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

    private void goMember() {
        Intent intent = new Intent(mContext, Member.class);
        intent.putExtra("intentVO", intentVO);
        mContext.startActivity(intent);
    }

    public void requestDAM_SELECT(String scancode) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<DAMModel> call = Http.dam(HttpBaseService.TYPE.POST).DAM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                intentVO.CTN_02,
                scancode,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<DAMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<DAMModel> call, Response<DAMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<DAMModel> response = (Response<DAMModel>) msg.obj;
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
                                    DamVO damvo = new DamVO();
                                    damvo.setDAM_ID(intentVO.CTN_02);
                                    damvo.setDAM_01(scancode);
                                    damvo.setDAM_02("");
                                    damvo.setDAM_03("");
                                    damvo.setDAM_04("1");
                                    damvo.setDAM_96("");
                                    damvo.setDAM_97(mUser.Value.OCM_01);
                                    damvo.setDAM_98(mUser.Value.OCM_01);
                                    damvo.setARM_03("N");
                                    Intent intent = new Intent(mContext, DamDetail.class);
                                    intent.putExtra("DamVO", damvo);
                                    intent.putExtra("scanCode", scancode);
                                    intent.putExtra("intentVO", intentVO);
                                    mContext.startActivity(intent);
                                } else {
                                    DamVO damvo = mList.get(0);
                                    Intent intent = new Intent(mContext, DamDetail.class);
                                    intent.putExtra("DamVO", damvo);

                                    mContext.startActivity(intent);
                                }
                            }
                            mAdapter.getFilter().filter(ed_search.getText());
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<DAMModel> call, Throwable t) {
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
