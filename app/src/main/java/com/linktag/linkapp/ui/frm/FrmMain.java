package com.linktag.linkapp.ui.frm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.FRM_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private BaseFooter footer;
    private ListView listView;
    private TextView emptyText;
    private SwipeRefreshLayout swipeRefresh;
    private EditText etSearch;
//    private ImageView imgSearch;

    //======================
    // Variable
    //======================
    private FrmAdapter mAdapter;
    private ArrayList<FRM_VO> mList;
    private ArrayList<FRM_VO> mList2;
    private String scancode;
    private CtdVO intentVO;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frm_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        if (getIntent().hasExtra("scanCode")) {
            scancode = getIntent().getExtras().getString("scanCode");
            requestFRM_SELECT("DETAIL", scancode);
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        // 요거
        initLayoutByContractType();

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FRM_VO FRM = mList.get(position);

                Intent intent = new Intent(mContext, FrmDetail.class);
                intent.putExtra("FRM", FRM);
                intent.putExtra("intentVO", intentVO);

                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
        etSearch = findViewById(R.id.etSearch);
//        imgSearch = findViewById(R.id.imgSearch);
//        imgSearch.setOnClickListener(v -> requestFRM_SELECT("LIST", ""));

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestFRM_SELECT("LIST", "");
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new FrmAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestFRM_SELECT("LIST", "");

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

    private void requestFRM_SELECT(String GUBUN, String FRM_01){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String FRM_ID = intentVO.CTN_02; //컨테이너
        String FRM_02 = ""; //명칭
//        if(GUBUN.equals("LIST")){
//            FRM_02 = etName.getText().toString();
//        }
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<FRMModel> call = Http.frm(HttpBaseService.TYPE.POST).FRM_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                FRM_ID,
                FRM_01,
                FRM_02,
                OCM_01
        );

        call.enqueue(new Callback<FRMModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<FRMModel> call, Response<FRMModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<FRMModel> response = (Response<FRMModel>) msg.obj;

                            if(GUBUN.equals("LIST")){
                                mList = response.body().Data;
                                if(mList == null)
                                    mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                            else{ //DETAIL (스캔찍을때)
                                mList2 = response.body().Data;
                                if(mList2 == null)
                                    mList2 = new ArrayList<>();

                                if(mList2.size() == 0){ //등록된 정보가 없는경우
                                    goFrmNew();
                                }
                                else{ //등록된 정보가 있는경우
                                    FRM_VO FRM = mList2.get(0);

                                    Intent intent = new Intent(mContext, FrmDetail.class);
                                    intent.putExtra("FRM", FRM);
                                    intent.putExtra("intentVO", intentVO);

                                    mContext.startActivity(intent);
                                }
                            }

                            mAdapter.getFilter().filter(etSearch.getText());

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<FRMModel> call, Throwable t){
                Log.d("FRM_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void goFrmNew(){
        Intent intent = new Intent(mContext, FrmDetail.class);
        intent.putExtra("scancode", scancode);
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
