package com.linktag.linkapp.ui.rmm;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.FRMModel;
import com.linktag.linkapp.model.RMDModel;
import com.linktag.linkapp.model.RMMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.frm.FrmAdapter;
import com.linktag.linkapp.ui.frm.FrmDetail;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.FRM_VO;
import com.linktag.linkapp.value_object.RMD_VO;
import com.linktag.linkapp.value_object.RMM_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RmdMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private BaseFooter footer;
//    private ListView listView;
    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView emptyText;
    private SwipeRefreshLayout swipeRefresh;
//    private EditText etSearch;

    //======================
    // Variable
    //======================
//    private FrmAdapter mAdapter;
    private RmdRecycleAdapter mAdapter;
    private ArrayList<RMD_VO> mList;
    private ArrayList<RMM_VO> mRmmList;
//    private String scancode;
    private CtdVO intentVO;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rmd_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
//        if (getIntent().hasExtra("scanCode")) {
//            scancode = getIntent().getExtras().getString("scanCode");
//            requestFRM_SELECT("DETAIL", scancode);
//        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        // 요거
        initLayoutByContractType();

        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);

//        listView = (ListView) findViewById(R.id.listView);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                FRM_VO FRM = mList.get(position);
//
//                Intent intent = new Intent(mContext, FrmDetail.class);
//                intent.putExtra("FRM", FRM);
//                intent.putExtra("intentVO", intentVO);
//
//                mContext.startActivity(intent);
//            }
//        });
        emptyText = findViewById(R.id.empty);
//        listView.setEmptyView(emptyText);
//        etSearch = findViewById(R.id.etSearch);
//        imgSearch = findViewById(R.id.imgSearch);
//        imgSearch.setOnClickListener(v -> requestFRM_SELECT("LIST", ""));

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                requestFRM_SELECT("LIST", "");
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RmdRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestRMM_SELECT();

        requestRMD_SELECT("LIST", "");

//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                mAdapter.getFilter().filter(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable edit) {
//            }
//        });
    }

    private void requestRMM_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = "DETAIL";
        String RMM_ID = intentVO.CTN_02; //컨테이너
        String RMM_01 = intentVO.CTM_01; //Master일련번호(RMM_01)

        Call<RMMModel> call = Http.rmm(HttpBaseService.TYPE.POST).RMM_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                RMM_ID,
                RMM_01
        );

        call.enqueue(new Callback<RMMModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RMMModel> call, Response<RMMModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<RMMModel> response = (Response<RMMModel>) msg.obj;

                            //공지설정, 시간설정 등등 여기서해용!!!

//                            if(GUBUN.equals("LIST")){
//                                mList = response.body().Data;
//                                if(mList == null)
//                                    mList = new ArrayList<>();
//
//                                mAdapter.updateData(mList);
//                                mAdapter.notifyDataSetChanged();
//                                swipeRefresh.setRefreshing(false);
//                            }
//                            else{ //DETAIL (스캔찍을때)
//                                mList2 = response.body().Data;
//                                if(mList2 == null)
//                                    mList2 = new ArrayList<>();
//
//                                if(mList2.size() == 0){ //등록된 정보가 없는경우
//                                    goFrmNew();
//                                }
//                                else{ //등록된 정보가 있는경우
//                                    FRM_VO FRM = mList2.get(0);
//
//                                    Intent intent = new Intent(mContext, FrmDetail.class);
//                                    intent.putExtra("FRM", FRM);
//                                    intent.putExtra("intentVO", intentVO);
//
//                                    mContext.startActivity(intent);
//                                }
//                            }
//
//                            mAdapter.getFilter().filter(etSearch.getText());

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMMModel> call, Throwable t){
                Log.d("RMM_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void requestRMD_SELECT(String GUBUN, String RMD_02){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String RMD_ID = intentVO.CTN_02; //컨테이너
        String RMD_01 = intentVO.CTM_01; //Master일련번호(RMM_01)

        Call<RMDModel> call = Http.rmd(HttpBaseService.TYPE.POST).RMD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                RMD_ID,
                RMD_01,
                RMD_02
        );

        call.enqueue(new Callback<RMDModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RMDModel> call, Response<RMDModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<RMDModel> response = (Response<RMDModel>) msg.obj;

                            if(GUBUN.equals("LIST")){
                                mList = response.body().Data;
                                if(mList == null)
                                    mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
//                            else{ //DETAIL (스캔찍을때)
//                                mList2 = response.body().Data;
//                                if(mList2 == null)
//                                    mList2 = new ArrayList<>();
//
//                                if(mList2.size() == 0){ //등록된 정보가 없는경우
//                                    goFrmNew();
//                                }
//                                else{ //등록된 정보가 있는경우
//                                    FRM_VO FRM = mList2.get(0);
//
//                                    Intent intent = new Intent(mContext, FrmDetail.class);
//                                    intent.putExtra("FRM", FRM);
//                                    intent.putExtra("intentVO", intentVO);
//
//                                    mContext.startActivity(intent);
//                                }
//                            }
//
//                            mAdapter.getFilter().filter(etSearch.getText());

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMDModel> call, Throwable t){
                Log.d("RMD_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

//    private void goFrmNew(){
//        Intent intent = new Intent(mContext, FrmDetail.class);
//        intent.putExtra("scancode", scancode);
//        intent.putExtra("intentVO", intentVO);
//
//        mContext.startActivity(intent);
//    }

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

    @Override
    protected void scanResult(String str){
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }

}
