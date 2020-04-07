package com.linktag.linkapp.ui.vot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.VOT_VO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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

    private TextView tvNotice;
    private Button btnReserve;
    private ImageView imgDay;
    private TextView tvDay;
    private TextView tvFilter;
    private ImageView imgFilter;

    //======================
    // Variable
    //======================
    private VotRecycleAdapter mAdapter;
    private ArrayList<VOT_VO> mList;
    private ArrayList<VOT_VO> mList2;
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
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                requestRMD_SELECT("LIST", "");
//                swipeRefresh.setRefreshing(false);
//            }
//        });
//
//        tvNotice = findViewById(R.id.tvNotice);
//        tvNotice.setMovementMethod(new ScrollingMovementMethod());
//        btnReserve = findViewById(R.id.btnReserve);
//        btnReserve.setOnClickListener(new View.OnClickListener(){ //user일때
//            @Override
//            public void onClick(View v){
//                myReserveDialog();
//            }
//        });
//        imgDay = findViewById(R.id.imgDay);
//        imgDay.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                dayDialog();
//            }
//        });
//        tvDay = findViewById(R.id.tvDay);
//        tvDay.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                dayDialog();
//            }
//        });
////        tvDay.setText(sDateFormat(RMR_03));
//        tvFilter = findViewById(R.id.tvFilter);
//        tvFilter.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                filterDialog();
//            }
//        });
//        imgFilter = findViewById(R.id.imgFilter);
//        imgFilter.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                filterDialog();
//            }
//        });

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        setRmdAdapter();
    }

    private void setRmdAdapter(){
        mAdapter = new VotRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

//        requestRMM_SELECT();
//
//        requestRMD_SELECT("LIST", "");
    }

//    private void requestRMM_SELECT(){
//        //인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
////        openLoadingBar();
//
//        String GUBUN = "DETAIL";
//        String RMM_ID = intentVO.CTN_02; //컨테이너
//        String RMM_01 = intentVO.CTD_01; //Master일련번호(RMM_01)
//
//        Call<RMMModel> call = Http.rmm(HttpBaseService.TYPE.POST).RMM_SELECT(
//                BaseConst.URL_HOST,
//                GUBUN,
//                RMM_ID,
//                RMM_01
//        );
//
//        call.enqueue(new Callback<RMMModel>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<RMMModel> call, Response<RMMModel> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if(msg.what == 100){
////                            closeLoadingBar();
//
//                            Response<RMMModel> response = (Response<RMMModel>) msg.obj;
//
//                            mRmmList = response.body().Data;
//                            if(mRmmList == null)
//                                mRmmList = new ArrayList<>();
//
//                            if(mRmmList.size() > 0){
//                                tvNotice.setText(mRmmList.get(0).RMM_05); //공지
//                                RMM.RMM_02 = mRmmList.get(0).RMM_02;
//                                RMM.RMM_03 = mRmmList.get(0).RMM_03;
//                                RMM.RMM_04 = mRmmList.get(0).RMM_04;
//                                RMM.RMM_98 = mRmmList.get(0).RMM_98;
//                                RMR_04ST_FILTER = RMM.RMM_03;
//                                RMR_04ED_FILTER = RMM.RMM_04;
//                                setFilterText();
////                                fnFilter();
//
//                                if(mRmmList.get(0).RMM_98.equals(mUser.Value.OCM_01)){
//                                    setMaster();
//                                }
//
//                                setRmdAdapter();
//
//                                if (scancode != null && !scancode.equals("")) {
//                                    requestRMD_SELECT("DETAIL", scancode);
//                                }
//
//                            }
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<RMMModel> call, Throwable t){
//                Log.d("RMM_SELECT", t.getMessage());
//                closeLoadingBar();
//            }
//        });
//    }

//    private void requestRMD_SELECT(String GUBUN, String RMD_02){
//        //인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
////        openLoadingBar();
//
//        String RMD_ID = intentVO.CTN_02; //컨테이너
//        String RMD_01 = intentVO.CTD_01; //Master일련번호(RMM_01)
//        String LIST_GUB = RMR_FILTER_GUB; //LIST구분자
//        String RMR_04ST = RMR_04ST_FILTER; //시작시간
//        String RMR_04ED = RMR_04ED_FILTER; //종료일자
//
//        Call<RMDModel> call = Http.rmd(HttpBaseService.TYPE.POST).RMD_SELECT(
//                BaseConst.URL_HOST,
//                GUBUN,
//                RMD_ID,
//                RMD_01,
//                RMD_02,
//                LIST_GUB,
//
//                RMR_03,
//                RMR_04ST,
//                RMR_04ED
//        );
//
//        call.enqueue(new Callback<RMDModel>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<RMDModel> call, Response<RMDModel> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if(msg.what == 100){
////                            closeLoadingBar();
//
//                            Response<RMDModel> response = (Response<RMDModel>) msg.obj;
//
//                            if(GUBUN.equals("LIST")){
//                                mList = response.body().Data;
//                                if(mList == null)
//                                    mList = new ArrayList<>();
//
//                                if(mList.size() ==0){
//                                    empty.setVisibility(View.VISIBLE);
//                                }
//                                else{
//                                    empty.setVisibility(View.GONE);
//                                }
//
//                                setRmdAdapter();
//                                swipeRefresh.setRefreshing(false);
//                            }
//                            else{ //DETAIL (스캔찍을때)
//                                mList2 = response.body().Data;
//                                if(mList2 == null)
//                                    mList2 = new ArrayList<>();
//
//                                if(mList2.size() == 0){ //등록된 정보가 없는경우
//                                    goRmdNew();
//                                }
//                                else{ //등록된 정보가 있는경우
//                                    VOT_VO RMD = mList2.get(0);
//                                    RMD.RMM_03 = RMM.RMM_03;
//                                    RMD.RMM_04 = RMM.RMM_04;
//                                    RMD.RMM_98 = RMM.RMM_98;
//
//                                    Intent intent = new Intent(mContext, RmdDetail.class);
//                                    intent.putExtra("RMD", RMD);
//                                    intent.putExtra("intentVO", intentVO);
//
//                                    mContext.startActivity(intent);
//                                }
//                            }
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<RMDModel> call, Throwable t){
//                Log.d("RMD_SELECT", t.getMessage());
////                closeLoadingBar();
//            }
//        });
//    }

    private void goRmdNew(){
//        Intent intent = new Intent(mContext, RmdDetail.class);
//        intent.putExtra("scancode", scancode);
//        intent.putExtra("intentVO", intentVO);
//        intent.putExtra("RMM_98", RMM.RMM_98);
//
//        mContext.startActivity(intent);
    }

//    private void noticeDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_rmm_notice, null);
//        builder.setView(view);
//
//        EditText etNotice = (EditText) view.findViewById(R.id.etNotice);
//        etNotice.setText(tvNotice.getText());
//        Button btnSave = (Button) view.findViewById(R.id.btnSave);
//        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
//
//        AlertDialog dialog = builder.create();
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.dismiss();
//                requestRMM_CONTROL("UPDATE_NOTICE", "", "", "", etNotice.getText().toString());
//            }
//        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

    private void dayDialog(){
//        Locale locale = getResources().getConfiguration().locale;
//        Locale.setDefault(locale);
//        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
//                RMR_03_C.set(year, month, date);
//                String tmp = String.valueOf(year);
//
//                month++;
//                if(month<10){
//                    tmp += "0" + String.valueOf(month);
//                }
//                else{
//                    tmp += String.valueOf(month);
//                }
//
//                if(date<10){
//                    tmp += "0" + String.valueOf(date);
//                }
//                else{
//                    tmp += String.valueOf(date);
//                }
//
//                RMR_03 = tmp;
//                tvDay.setText(sDateFormat(RMR_03));
//
//                VotRecycleAdapter.RMR_04_list.clear();
//
//                setRmdAdapter();
//            }
//        }, RMR_03_C.get(Calendar.YEAR), RMR_03_C.get(Calendar.MONTH), RMR_03_C.get(Calendar.DATE));
//
//        dialog.show();
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

//    public void clearCalTime(Calendar c){
//        c.set(Calendar.HOUR, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//    }
//
//    private String sDateFormat(String sDate) {
//        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);
//
//        return result;
//    }

}
