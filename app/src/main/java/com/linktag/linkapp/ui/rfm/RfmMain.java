package com.linktag.linkapp.ui.rfm;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.RFDModel;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.RfdVO;
import com.linktag.linkapp.value_object.RfmVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RfmMain extends BaseActivity {

    private BaseHeader header;
    private BaseFooter footer;

    private View view;
    private SwipeRefreshLayout swipeRefresh;

    private RfdRecycleAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<RfdVO> mList;
    private ArrayList<RfmVO> mRfmList;

    private CtdVO intentVO;

    private Spinner headerSpinner;
    private ArrayList<SpinnerList> mSpinnerList;

    private ImageView imgNew;
    private Button btnEdit;

    public static String RFM_01 = "";
    public String RFM_02 = "";
    public String RFM_03 = "";

    private int pos = -1;


    private String[] str;
    private String[] index;

    private EditText ed_search;

    private TextView empty;
    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");

    public RfmMain() {
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

        ed_search = findViewById(R.id.ed_search);

        mSpinnerList = new ArrayList<>();

        headerSpinner = findViewById(R.id.headerSpinner);


        view = findViewById(R.id.recyclerView);
        recyclerView = view.findViewById(R.id.recyclerView);

        empty = findViewById(R.id.empty);
        imgNew = findViewById(R.id.imgNew);
        btnEdit = findViewById(R.id.btnEdit);

        swipeRefresh = findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(() -> requestJMD_SELECT());
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });

        headerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(mSpinnerList.get(position).getCode().equals("")){
                   return;
                }
                if (pos != -1) {
                    headerSpinner.setSelection(pos);
                    requestRFD_SELECT(mSpinnerList.get(pos).getCode());
                    RFM_01 = mSpinnerList.get(pos).getCode();
                    RFM_02 = mSpinnerList.get(pos).getName();
                    RFM_03 = mSpinnerList.get(pos).getMemo();
                    pos = -1;
                } else {
                    headerSpinner.setSelection(position);
                    requestRFD_SELECT(mSpinnerList.get(position).getCode());
                    RFM_01 = mSpinnerList.get(position).getCode();
                    RFM_02 = mSpinnerList.get(position).getName();
                    RFM_03 = mSpinnerList.get(position).getMemo();
                    ed_search.setText("");
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
        imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RfdVO rfdvo = new RfdVO();
                rfdvo.setRFD_ID(intentVO.CTN_02);
                rfdvo.setRFD_01(RFM_01);
                rfdvo.setRFD_02("");
                rfdvo.setRFD_03("");
                rfdvo.setRFD_04("");
                rfdvo.setRFD_05(formatDate.format(calendar.getTime()) + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE));
                rfdvo.setRFD_06("");
                rfdvo.setRFD_07("");
                rfdvo.setRFD_96("");
                rfdvo.setARM_03("N");
                rfdvo.setARM_04(0);

                Intent intent = new Intent(mContext, RfdDetail.class);
                intent.putExtra("RfdVO", rfdvo);

                intent.putExtra("GUBUN", "INSERT");
                mContext.startActivity(intent);
            }
        });

        btnEdit .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = 0;
                if (pos == -1) {
                    position = 0;
                } else {
                    position = pos;
                }
                RfmEditDialog rfmEditDialog = new RfmEditDialog(mContext, intentVO.CTN_02, RFM_01, RFM_02, RFM_03);

                rfmEditDialog.setDialogListener(new RfmEditDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String name, String memo) {

                        requestRFM_CONTROL("UPDATE", name, memo);
                    }

                    @Override
                    public void onNegativeClicked() {
                        requestRFM_SELECT();
                    }
                });

                rfmEditDialog.show();
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


    public void requestRFM_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<RFMModel> call = Http.rfm(HttpBaseService.TYPE.POST).RFM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                intentVO.CTN_02,
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

                            if(response.body().Total >0){
                                str = new String[response.body().Total];
                                index = new String[response.body().Total];
                                btnEdit.setVisibility(View.VISIBLE);
                                imgNew.setVisibility(View.VISIBLE);
                            }
                            else{
                                str = new String[1];
                                index = new String[1];
                                btnEdit.setVisibility(View.GONE);
                                imgNew.setVisibility(View.GONE);
                            }

                            mSpinnerList.clear();
                            if (response.body().Total > 0) {
                                for (int i = 0; i < response.body().Total; i++) {
                                    mSpinnerList.add(new SpinnerList(response.body().Data.get(i).RFM_01, response.body().Data.get(i).RFM_02, response.body().Data.get(i).RFM_03));

                                    index[i] = response.body().Data.get(i).RFM_01;
                                    str[i] = response.body().Data.get(i).RFM_02;
                                }
                                final ArrayAdapter<String> hAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_list, str);
                                headerSpinner.setAdapter(hAdapter);
                                if (RFM_01.equals("")) {
                                    RFM_01 = mSpinnerList.get(0).getCode();
                                }
                            }
                            else{
                                mSpinnerList.add(new SpinnerList("", getString(R.string.rfm_list_empty), ""));
                                index[0] = "";
                                str[0] = getString(R.string.rfm_list_empty);
                                final ArrayAdapter<String> hAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_list, str);
                                headerSpinner.setAdapter(hAdapter);
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
                intentVO.CTN_02,
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
                                rfmvo.setRFM_ID(intentVO.CTN_02);
                                rfmvo.setRFM_01(scancode);
                                rfmvo.setRFM_02("");
                                rfmvo.setRFM_03("");
                                rfmvo.setRFM_97(mUser.Value.OCM_01);
                                rfmvo.setRFM_98(mUser.Value.OCM_01);

                                Intent intent = new Intent(mContext, RfmDetail.class);
                                intent.putExtra("RfmVO", rfmvo);

                                intent.putExtra("scanCode", getIntent().getStringExtra("scanCode"));
                                intent.putExtra("intentVO", intentVO);
                                mContext.startActivity(intent);
                            } else {
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

    public void requestRFM_CONTROL(String GUBUN, String name, String memo) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<RFMModel> call = Http.rfm(HttpBaseService.TYPE.POST).RFM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                intentVO.CTN_02,
                RFM_01,
                name,
                memo,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<RFMModel>() {
            @Override
            public void onResponse(Call<RFMModel> call, Response<RFMModel> response) {

                requestRFM_SELECT();
                Toast.makeText(getApplicationContext(), "[" + name + "]" + "냉장고정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
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
                intentVO.CTN_02,
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

                            if(mList.size() ==0){
                                empty.setVisibility(View.VISIBLE);
                            }
                            else{
                                empty.setVisibility(View.GONE);
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

                        }
                        mAdapter.getFilter().filter(ed_search.getText());
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

    @Override
    protected void scanResult(String str){
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }

}
