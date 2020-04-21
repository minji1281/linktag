package com.linktag.linkapp.ui.icm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ICMModel;
import com.linktag.linkapp.model.ICRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.ICM_VO;
import com.linktag.linkapp.value_object.ICR_VO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IcrMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private BaseFooter footer;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public static TextView empty;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView_ICI;
    private LinearLayoutManager linearLayoutManager_ICI;

    private TextView tvMinute1;
    private TextView tvMinute2;
    private TextView tvMinute3;
    private Button btnBabyNote;
    private ImageView imgDay;
    private TextView tvDay;
    private TextView tvBabyDay;

    //======================
    // Variable
    //======================
    public static IcrRecycleAdapter mAdapter;
    public static ArrayList<ICR_VO> mList;
    private CtdVO intentVO;
    private ICM_VO ICM;
    private IciRecycleAdapter mAdapter_ICI;
    private ArrayList<String> mList_ICI;

    //======================
    // Initialize
    //======================
    private String ICR_03 = ""; //일자

    private Calendar TODAY = Calendar.getInstance();
    private Calendar ICR_03_C = Calendar.getInstance(); //일자

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_icr_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        clearCalTime(TODAY);
        clearCalTime(ICR_03_C);

        // 요거
        initLayoutByContractType();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView_ICI = (RecyclerView) findViewById(R.id.recyclerView_ICI);

        empty = findViewById(R.id.empty);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestICM_SELECT("DETAIL");
                requestICR_SELECT("LIST");
                swipeRefresh.setRefreshing(false);
            }
        });

        tvMinute1 = findViewById(R.id.tvMinute1);
        tvMinute2 = findViewById(R.id.tvMinute2);
        tvMinute3 = findViewById(R.id.tvMinute3);

        btnBabyNote = findViewById(R.id.btnBabyNote);
        btnBabyNote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setBabyNoteDialog();
            }
        });
        imgDay = findViewById(R.id.imgDay);
        imgDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dayDialog();
            }
        });
        tvDay = findViewById(R.id.tvDay);
        tvDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dayDialog();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        ICR_03 = sdf.format(ICR_03_C.getTime());
        tvDay.setText(sDateFormat(ICR_03));

        tvBabyDay = findViewById(R.id.tvBabyDay);

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
//        setRmdAdapter();
        mAdapter = new IcrRecycleAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);

        mList_ICI = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ici)));
        linearLayoutManager_ICI = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_ICI.setLayoutManager(linearLayoutManager_ICI);
//        setRmdAdapter();
        mAdapter_ICI = new IciRecycleAdapter(mContext, mList_ICI, intentVO);
        recyclerView_ICI.setAdapter(mAdapter_ICI);
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestICM_SELECT("DETAIL");

        requestICR_SELECT("LIST");
    }

    private void requestICR_SELECT(String GUB){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String ICR_ID = intentVO.CTN_02; //컨테이너
        String ICR_01 = intentVO.CTD_01; //Master일련번호(RMM_01)
        String ICR_02 = ""; //일련번호

        Call<ICRModel> call = Http.icr(HttpBaseService.TYPE.POST).ICR_SELECT(
                BaseConst.URL_HOST,
                GUB,
                ICR_ID,
                ICR_01,
                ICR_02,
                ICR_03
        );

        call.enqueue(new Callback<ICRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ICRModel> call, Response<ICRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<ICRModel> response = (Response<ICRModel>) msg.obj;

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

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ICRModel> call, Throwable t){
                Log.d("ICR_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void requestICM_SELECT(String GUB){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String ICM_ID = intentVO.CTN_02; //컨테이너
        String ICM_01 = intentVO.CTD_01; //Master일련번호(ICM_01)

        Call<ICMModel> call = Http.icm(HttpBaseService.TYPE.POST).ICM_SELECT(
                BaseConst.URL_HOST,
                GUB,
                ICM_ID,
                ICM_01
        );

        call.enqueue(new Callback<ICMModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ICMModel> call, Response<ICMModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<ICMModel> response = (Response<ICMModel>) msg.obj;
//
                            if(response.body().Data.size()>0){
                                ICM = response.body().Data.get(0);

                                String tmp = "";
                                if(ICM.MM_CNT>0){
                                    tmp += String.valueOf(ICM.MM_CNT) + "개월 ";
                                }
                                tmp += String.valueOf(ICM.DD_CNT) + "일 (" + String.valueOf(ICM.ALL_DD_CNT) + "일)";
                                tvBabyDay.setText(tmp);

                                tvMinute1.setText(String.valueOf(ICM.MINUTE1) + "분전");
                                tvMinute2.setText(String.valueOf(ICM.MINUTE2) + "분전");
                                tvMinute3.setText(String.valueOf(ICM.MINUTE3) + "분전");
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ICMModel> call, Throwable t){
                Log.d("ICM_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestICM_CONTROL(String GUB, String ICM_02, String ICM_03, AlertDialog dialog) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String ICM_ID = intentVO.CTN_02; //컨테이너
        String ICM_01 = intentVO.CTD_01; //일련번호(CTD_01)

        String ICM_98 = mUser.Value.OCM_01; //최종수정자

        Call<ICMModel> call = Http.icm(HttpBaseService.TYPE.POST).ICM_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                ICM_ID,
                ICM_01,
                ICM_02,
                ICM_03,

                ICM_98
        );

        call.enqueue(new Callback<ICMModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ICMModel> call, Response<ICMModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<ICMModel> response = (Response<ICMModel>) msg.obj;

                            if(response.body().Data.size()>0){
                                ICM = response.body().Data.get(0);

                                String tmp = "";
                                if(ICM.MM_CNT>0){
                                    tmp += String.valueOf(ICM.MM_CNT) + "개월 ";
                                }
                                tmp += String.valueOf(ICM.DD_CNT) + "일 (" + String.valueOf(ICM.ALL_DD_CNT) + "일)";
                                tvBabyDay.setText(tmp);

                                dialog.dismiss();
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ICMModel> call, Throwable t){
                Log.d("ICM_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private void dayDialog(){
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                ICR_03_C.set(year, month, date);
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

                ICR_03 = tmp;
                tvDay.setText(sDateFormat(ICR_03));

                requestICR_SELECT("LIST");
            }
        }, ICR_03_C.get(Calendar.YEAR), ICR_03_C.get(Calendar.MONTH), ICR_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private String fnTime(int hour, int minute){
        String tmp;

        if(hour<10){
            tmp = "0" + String.valueOf(hour);
        }
        else{
            tmp = String.valueOf(hour);
        }

        if(minute<10){
            tmp += "0" + String.valueOf(minute);
        }
        else{
            tmp += String.valueOf(minute);
        }

        return tmp;
    }

    private void setBabyNoteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_icm, null);
        builder.setView(view);

        EditText etName = (EditText) view.findViewById(R.id.etName);
        etName.setText(ICM.ICM_02);
        DatePicker dpBirthDay = (DatePicker) view.findViewById(R.id.dpBirthDay);
        if(ICM.ICM_03.length() == 8){
            dpBirthDay.updateDate(Integer.parseInt(ICM.ICM_03.substring(0,4)), Integer.parseInt(ICM.ICM_03.substring(4,6)) - 1, Integer.parseInt(ICM.ICM_03.substring(6)));
        }

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String yyyy = String.valueOf(dpBirthDay.getYear());
                String mm = dpBirthDay.getMonth()+1 < 10 ? "0" + String.valueOf(dpBirthDay.getMonth()+1) : String.valueOf(dpBirthDay.getMonth()+1);
                String dd = dpBirthDay.getDayOfMonth() < 10 ? "0" + String.valueOf(dpBirthDay.getDayOfMonth()) : String.valueOf(dpBirthDay.getDayOfMonth());

                requestICM_CONTROL("UPDATE", etName.getText().toString(), yyyy+mm+dd, dialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);

        return result;
    }

}
