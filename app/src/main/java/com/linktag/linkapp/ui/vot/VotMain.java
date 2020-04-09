package com.linktag.linkapp.ui.vot;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.VOTModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.VOT_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VotMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private BaseFooter footer;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView empty;
    private SwipeRefreshLayout swipeRefresh;

    private ImageView imgSearch;
    private EditText etSearch;

    private ImageView imgNew;

    //======================
    // Variable
    //======================
    private VotRecycleAdapter mAdapter;
    private ArrayList<VOT_VO> mList;
    private CtdVO intentVO;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vot_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        // 요거
        initLayoutByContractType();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        empty = findViewById(R.id.empty);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestVOT_SELECT();
                swipeRefresh.setRefreshing(false);
            }
        });

        etSearch = findViewById(R.id.etSearch);

        imgNew = (ImageView) findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goVotNew());

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new VotRecycleAdapter(mContext, mList, intentVO);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestVOT_SELECT();

        etSearch.addTextChangedListener(new TextWatcher() {
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

    private void requestVOT_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = "LIST";
        String VOT_ID = intentVO.CTN_02; //컨테이너
        String VOT_01 = ""; //일련번호
        String OCM_01 = mUser.Value.OCM_01; //사용자
        String CTM_01 = intentVO.CTM_01; //계약코드

        Call<VOTModel> call = Http.vot(HttpBaseService.TYPE.POST).VOT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                VOT_ID,
                VOT_01,
                OCM_01,
                CTM_01
        );

        call.enqueue(new Callback<VOTModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VOTModel> call, Response<VOTModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<VOTModel> response = (Response<VOTModel>) msg.obj;

                            mList = response.body().Data;
                            if(mList == null)
                                mList = new ArrayList<>();

                            if(mList.size() ==0){
                                empty.setVisibility(View.VISIBLE);
                            }
                            else{
                                empty.setVisibility(View.GONE);
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

                            mAdapter.getFilter().filter(etSearch.getText());
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VOTModel> call, Throwable t){
                Log.d("VOT_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void goVotNew(){
        Intent intent = new Intent(mContext, VotDetail.class);
        intent.putExtra("intentVO", intentVO);

        mContext.startActivity(intent);
    }

    // 요거
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
