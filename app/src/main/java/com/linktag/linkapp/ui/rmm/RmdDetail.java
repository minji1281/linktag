package com.linktag.linkapp.ui.rmm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CDS_Model;
import com.linktag.linkapp.model.RMDModel;
import com.linktag.linkapp.model.RMRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.RMD_VO;
import com.linktag.linkapp.value_object.RMR_VO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RmdDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private EditText etName;
    private EditText etEquip;

    private ImageView imgReserveDay;

    private TextView tvReserveDay;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefresh;

    private LinearLayout layoutReserve;
    private LinearLayout layoutReserve2;

    private Button btnSave;

    //======================
    // Variable
    //======================
    private DetailRmrRecycleAdapter mAdapter;
    private ArrayList<RMR_VO> mList;
    private ArrayList<RMR_VO> mList_tmp;

    //======================
    // Initialize
    //======================
    private CtdVO intentVO;
    private RMD_VO RMD;
    private String GUBUN;
    private String scanCode;
    private String RMR_03 = "";

    Calendar RMR_03_C = Calendar.getInstance(); //예약일자
    Calendar TODAY = Calendar.getInstance();
    public static ArrayList<ReserveList> RMR_04_list = new ArrayList<>(); //예약

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmd_detail);

//        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(getIntent().hasExtra("RMD")){
            RMD = (RMD_VO) getIntent().getSerializableExtra("RMD");
            GUBUN = "UPDATE";
        }
        else{
            intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
            RMD = new RMD_VO();
            RMD.RMD_ID = intentVO.CTN_02;
            RMD.RMD_01 = intentVO.CTM_01;
            RMD.RMD_02 = getIntent().getStringExtra("scancode");
            RMD.RMM_98 = getIntent().getStringExtra("RMM_98");
            scanCode = getIntent().getStringExtra("scancode");
            GUBUN = "INSERT";

            if(!RMD.RMM_98.equals(mUser.Value.OCM_01)){ //INSERT는 마스터만 할 수 있다.
                Toast.makeText(mActivity, R.string.rmd_detail_no_master, Toast.LENGTH_SHORT).show();
                fnFinish();
            }
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> fnFinish());

        clearCalTime(RMR_03_C);
        clearCalTime(TODAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        RMR_03 = sdf.format(RMR_03_C.getTime());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etEquip = (EditText) findViewById(R.id.etEquip);

        tvReserveDay = (TextView) findViewById(R.id.tvReserveDay);
        tvReserveDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reserveDayDialog();
            }
        });
        tvReserveDay.setText(sDateFormat(RMR_03));
        imgReserveDay = (ImageView) findViewById(R.id.imgReserveDay);
        imgReserveDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reserveDayDialog();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_DetailRmr);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestRMR_SELECT("LIST_DETAIL");
                swipeRefresh.setRefreshing(false);
            }
        });

        layoutReserve = (LinearLayout) findViewById(R.id.layoutReserve);
        layoutReserve2 = (LinearLayout) findViewById(R.id.layoutReserve2);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(RMD.RMM_98.equals(mUser.Value.OCM_01)){ //마스터일때
                    if(validationCheck()){
                        if (GUBUN.equals("INSERT")) {
                            requestCDS_CONTROL(
                                    "INSERT",
                                    intentVO.CTD_07,
                                    scanCode,
                                    "",
                                    intentVO.CTD_01,
                                    intentVO.CTD_02,
                                    intentVO.CTD_09,
                                    mUser.Value.OCM_01);
                        } else {
                            requestRMD_CONTROL(GUBUN);
                        }
                    }
                }
                else{ //유저일때
                    if(RMR_03_C.compareTo(TODAY) < 0){ //오늘 이전일자로 예약할 수 없다.
                        Toast.makeText(mActivity, R.string.rmd_detail_day_text, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(RMR_04_list.size()>0){
                            for(int i=0; i<RMR_04_list.size(); i++){
                                String EndGub = "N";
                                if(i == RMR_04_list.size()-1){
                                    EndGub = "Y";
                                }
                                requestRMR_CONTROL(RMD.RMD_ID, RMD.RMD_01, RMD.RMD_02, RMR_04_list.get(i).getTime(), EndGub);
                            }
                            RMR_04_list.clear();

                            Toast.makeText(mActivity, R.string.rmd_detail_save_text, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        if(GUBUN.equals("UPDATE")){
            if(RMD.RMD_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        deleteDialog();
                    }
                });
            }
            else{ //유저는 내용을 수정할 수 없다.
                etName.setFocusable(false);
                etEquip.setFocusable(false);
            }
        }
        else{ //INSERT
            layoutReserve.setVisibility(View.GONE);
            layoutReserve2.setVisibility(View.GONE);

//            getNewData();
        }
    }

    @Override
    protected void initialize() {
        if(GUBUN.equals("UPDATE")){
            getDetail();

            mList = new ArrayList<>();
            linearLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new DetailRmrRecycleAdapter(mContext, mList, RMD.RMD_97);
            recyclerView.setAdapter(mAdapter);

            requestRMR_SELECT("LIST_DETAIL");
        }
    }

    private void getDetail() {
        etName.setText(RMD.RMD_03);
        etEquip.setText(RMD.RMD_04);
    }

    private void getNewData(){
        //초기값

    }

    private void requestRMD_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String RMD_ID = RMD.RMD_ID; //컨테이너
        String RMD_01 = RMD.RMD_01; //Master일련번호(RMM_01)
        String RMD_02 = RMD.RMD_02; //일련번호(스캔코드)
        String RMD_03 = etName.getText().toString(); //명칭

        String RMD_04 = etEquip.getText().toString(); //장비
        String RMD_98 = mUser.Value.OCM_01; //최종수정자

        Call<RMDModel> call = Http.rmd(HttpBaseService.TYPE.POST).RMD_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                RMD_ID,
                RMD_01,
                RMD_02,
                RMD_03,

                RMD_04,
                RMD_98
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

                            fnFinish();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMDModel> call, Throwable t){
                Log.d("RMD_CONTROL", t.getMessage());

                if(GUB.equals("INSERT")){
                    requestCDS_CONTROL(
                            "DELETE",
                            intentVO.CTD_07,
                            scanCode,
                            RMD.RMD_02,
                            "",
                            "",
                            "",
                            "");
                }

//                closeLoadingBar();
            }
        });

    }

    private void requestRMR_SELECT(String GUBUN){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String RMR_ID = RMD.RMD_ID; //컨테이너
        String RMR_01 = RMD.RMD_01; //Master일련번호
        String RMR_02 = RMD.RMD_02; //연습실일련번호

        Call<RMRModel> call = Http.rmr(HttpBaseService.TYPE.POST).RMR_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                RMR_ID,
                RMR_01,
                RMR_02,
                RMR_03,

                "",
                "",
                ""
        );

        call.enqueue(new Callback<RMRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RMRModel> call, Response<RMRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<RMRModel> response = (Response<RMRModel>) msg.obj;

                            mList.clear();
                            mList_tmp = response.body().Data;
                            if (mList_tmp == null)
                                mList_tmp = new ArrayList<>();

                            int i = 0;
                            Calendar time_c = Calendar.getInstance();
                            time_c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(RMD.RMM_03.substring(0,2)));
                            time_c.set(Calendar.MINUTE, Integer.parseInt(RMD.RMM_03.substring(2)));
                            time_c.set(Calendar.SECOND, 0);
                            time_c.set(Calendar.MILLISECOND, 0);

                            Calendar time_c_ed = Calendar.getInstance();
                            time_c_ed.set(Calendar.HOUR_OF_DAY, Integer.parseInt(RMD.RMM_04.substring(0,2)));
                            time_c_ed.set(Calendar.MINUTE, Integer.parseInt(RMD.RMM_04.substring(2)));
                            time_c_ed.set(Calendar.SECOND, 0);
                            time_c_ed.set(Calendar.MILLISECOND, 0);

//                            mList.clear();
                            while(time_c.compareTo(time_c_ed) < 0){
                                String time_s = (time_c.get(Calendar.HOUR_OF_DAY)<10 ? "0" + String.valueOf(time_c.get(Calendar.HOUR_OF_DAY)) : String.valueOf(time_c.get(Calendar.HOUR_OF_DAY))) + (time_c.get(Calendar.MINUTE)<10 ? "0" + String.valueOf(time_c.get(Calendar.MINUTE)) : String.valueOf(time_c.get(Calendar.MINUTE)));
                                if(mList_tmp.size() > 0){
                                    if(time_s.equals(mList_tmp.get(i).RMR_04)){
                                        mList.add(mList_tmp.get(i));
                                        if(i < mList_tmp.size() - 1){
                                            i++;
                                        }
                                    }
                                    else{
                                        RMR_VO RMR_tmp = new RMR_VO();
                                        RMR_tmp.RMR_ID = RMR_ID;
                                        RMR_tmp.RMR_01 = RMR_01;
                                        RMR_tmp.RMR_02 = RMR_02;
                                        RMR_tmp.RMR_03 = RMR_03;
                                        RMR_tmp.RMR_04 = time_s;
                                        RMR_tmp.RMR_05 = "";
                                        RMR_tmp.RMR_05_NM = "";
                                        mList.add(RMR_tmp);
                                    }
                                }
                                else{
                                    RMR_VO RMR_tmp = new RMR_VO();
                                    RMR_tmp.RMR_ID = RMR_ID;
                                    RMR_tmp.RMR_01 = RMR_01;
                                    RMR_tmp.RMR_02 = RMR_02;
                                    RMR_tmp.RMR_03 = RMR_03;
                                    RMR_tmp.RMR_04 = time_s;
                                    RMR_tmp.RMR_05 = "";
                                    RMR_tmp.RMR_05_NM = "";
                                    mList.add(RMR_tmp);
                                }
                                time_c.add(Calendar.MINUTE, 30); //30분 고정
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMRModel> call, Throwable t){
                Log.d("RMD_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestRMR_CONTROL(String RMR_ID, String RMR_01, String RMR_02, String RMR_04, String EndGub) {

        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String GUB = "RESERVE";
        String RMR_05 = mUser.Value.OCM_01; //사용자
        String RMR_98 = mUser.Value.OCM_01; //최종수정자

        Call<RMRModel> call = Http.rmr(HttpBaseService.TYPE.POST).RMR_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                RMR_ID,
                RMR_01,
                RMR_02,
                RMR_03,

                RMR_04,
                RMR_05,
                RMR_98
        );

        call.enqueue(new Callback<RMRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RMRModel> call, Response<RMRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<RMRModel> response = (Response<RMRModel>) msg.obj;

                            if(EndGub.equals("Y")){ //마지막 CONTROL
                                requestRMR_SELECT("LIST_DETAIL");
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMRModel> call, Throwable t){
                Log.d("RMR_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private void reserveDayDialog(){
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                RMR_03_C.set(year, month, date);
                RmdMain.RMR_03_C.set(year, month, date);
                String tmp = String.valueOf(year);

                month++;
                if(month<10){
                    tmp += "0" + String.valueOf(month);
                }
                else{
                    tmp += String.valueOf(month);
                }

                if(date<10){
                    tmp += "0" + String.valueOf(date);
                }
                else{
                    tmp += String.valueOf(date);
                }

                RMR_03 = tmp;
                tvReserveDay.setText(sDateFormat(RMR_03));
                RMR_04_list.clear();

                requestRMR_SELECT("LIST_DETAIL");
            }
        }, RMR_03_C.get(Calendar.YEAR), RMR_03_C.get(Calendar.MONTH), RMR_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        EditText etDeleteName = (EditText) view.findViewById(R.id.etDeleteName);

        AlertDialog dialog = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etDeleteName.getText().toString().equals(RMD.RMD_03)){
                    dialog.dismiss();
                    requestRMD_CONTROL("DELETE");
                }
                else{
                    Toast.makeText(mActivity, R.string.dialog_delete_check_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean validationCheck(){
        boolean check = true;
        if(etName.getText().toString().equals("")){
            check = false;
            Toast.makeText(mActivity, R.string.frm_validation_check1, Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void requestCDS_CONTROL(String GUBUN, String CTD_07, String scanCode, String CDS_03, String CTD_01, String CTD_02, String CTD_09, String OCM_01){
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<CDS_Model> call = Http.cds(HttpBaseService.TYPE.POST).CDS_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CTD_07,
                scanCode,
                CDS_03,
                CTD_01,
                CTD_02,
                CTD_09,
                OCM_01
        );

        call.enqueue(new Callback<CDS_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CDS_Model> call, Response<CDS_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            Response<CDS_Model> response = (Response<CDS_Model>) msg.obj;

                            if(GUBUN.equals("INSERT")){
                                RMD.RMD_02 = response.body().Data.get(0).CDS_03;
                                requestRMD_CONTROL("INSERT");
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CDS_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                Toast.makeText(mContext, R.string.common_exception, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);

        return result;
    }

    private void fnFinish(){
        RmdMain.scancode = "";
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fnFinish();
    }

}
