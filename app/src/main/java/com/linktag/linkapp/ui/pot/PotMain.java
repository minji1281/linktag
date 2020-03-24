package com.linktag.linkapp.ui.pot;

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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.PotVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PotMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private BaseFooter footer;
    private GridView gridView;
    private TextView emptyText;
    private SwipeRefreshLayout swipeRefresh;
    private EditText etSearch;
//    private ImageView imgSearch;

    //======================
    // Variable
    //======================
    private PotAdapter mAdapter;
    private ArrayList<PotVO> mList;
    private String scancode = "";
    private CtdVO intentVO;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pot_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        if (getIntent().hasExtra("scanCode")) {
            scancode = getIntent().getExtras().getString("scanCode");
            requestPOT_SELECT("DETAIL", scancode);
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

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PotVO POT = mList.get(position);

                Intent intent = new Intent(mContext, PotDetail.class);
                intent.putExtra("POT", POT);
                intent.putExtra("intentVO", intentVO);

                mContext.startActivity(intent);
            }
        });

        emptyText = findViewById(R.id.empty);
        gridView.setEmptyView(emptyText);
        etSearch = findViewById(R.id.etSearch);
//        imgSearch = findViewById(R.id.imgSearch);
//        imgSearch.setOnClickListener(v -> requestPOT_SELECT("LIST", ""));

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestPOT_SELECT("LIST", "");
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new PotAdapter(mContext, mList);
        gridView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume(){
        super.onResume();

        requestPOT_SELECT("LIST", "");

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

    private void requestPOT_SELECT(String GUBUN, String POT_01){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String POT_ID = intentVO.CTN_02; //컨테이너
        String POT_02 = "";
//        if(GUBUN.equals("LIST")){
//            POT_02 = etName.getText().toString();
//        }
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                POT_02,
                OCM_01
        );

        call.enqueue(new Callback<POT_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            mList = response.body().Data;

                            if(GUBUN.equals("LIST")){
                                if(mList == null)
                                    mList = new ArrayList<>();

                                mAdapter.updateData(mList);
                                mAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                            else{ //DETAIL (스캔했을때)
                                if(mList.size() == 0){ //등록된 정보가 없을때
                                    goPotNew();
                                }
                                else{ //등록된 정보가 있을때
                                    PotVO POT = mList.get(0);

                                    Intent intent = new Intent(mContext, PotDetail.class);
                                    intent.putExtra("POT", POT);
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
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void goPotNew(){
        Intent intent = new Intent(mContext, PotDetail.class);
        intent.putExtra("scancode", scancode); //scancode
        intent.putExtra("intentVO", intentVO);

        mContext.startActivity(intent);
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

    @Override
    protected void scanResult(String str){
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }

}
