package com.linktag.linkapp.ui.rmm;

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
import com.linktag.linkapp.model.RMDModel;
import com.linktag.linkapp.model.RMMModel;
import com.linktag.linkapp.model.RMRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.RMD_VO;
import com.linktag.linkapp.value_object.RMM_VO;
import com.linktag.linkapp.value_object.RMR_VO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RmdMain extends BaseActivity {
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
    private RmdRecycleAdapter mAdapter;
    private myRmrRecycleAdapter mAdapter_myRmr; //myReserveDialog에서 씀
    private ArrayList<RMD_VO> mList;
    private ArrayList<RMD_VO> mList2;
    private ArrayList<RMM_VO> mRmmList;
    private ArrayList<RMR_VO> mList_myRmr; //myReserveDialog에서 씀
    public static String scancode;
    private CtdVO intentVO;

    //======================
    // Initialize
    //======================
    private RMM_VO RMM = new RMM_VO(); //setReserveDialog에서 씀
    private String RMM_02_tmp = ""; //setReserveDialog에서 씀
    private String RMR_03 = ""; //dayDialog에서 씀
    private String RMR_04ST_FILTER = ""; //filterDialog에서 씀
    private String RMR_04ED_FILTER = ""; //filterDialog에서 씀
    private String RMR_FILTER_GUB = "1"; //filterDialog에서 씀
    private String RMR_FILTER_GUB_tmp = "1"; //filterDialog에서 씀

    public static Calendar RMR_03_C = Calendar.getInstance(); //일자

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rmd_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            scancode = getIntent().getExtras().getString("scanCode");
        }
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        clearCalTime(RMR_03_C);

        // 요거
        initLayoutByContractType();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        empty = findViewById(R.id.empty);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestRMD_SELECT("LIST", "");
                swipeRefresh.setRefreshing(false);
            }
        });

        tvNotice = findViewById(R.id.tvNotice);
        tvNotice.setMovementMethod(new ScrollingMovementMethod());
        btnReserve = findViewById(R.id.btnReserve);
        btnReserve.setOnClickListener(new View.OnClickListener(){ //user일때
            @Override
            public void onClick(View v){
                myReserveDialog();
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
//        tvDay.setText(sDateFormat(RMR_03));
        tvFilter = findViewById(R.id.tvFilter);
        tvFilter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                filterDialog();
            }
        });
        imgFilter = findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                filterDialog();
            }
        });

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        setRmdAdapter();
    }

    private void setRmdAdapter(){
        mAdapter = new RmdRecycleAdapter(mContext, mList, RMR_03, RMR_04ST_FILTER, RMR_04ED_FILTER, RMM.RMM_03, RMM.RMM_04, RMM.RMM_98);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        RMR_03 = sdf.format(RMR_03_C.getTime());
        tvDay.setText(sDateFormat(RMR_03));

        requestRMM_SELECT();

        requestRMD_SELECT("LIST", "");
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
        String RMM_01 = intentVO.CTD_01; //Master일련번호(RMM_01)

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

                            mRmmList = response.body().Data;
                            if(mRmmList == null)
                                mRmmList = new ArrayList<>();

                            if(mRmmList.size() > 0){
                                tvNotice.setText(mRmmList.get(0).RMM_05); //공지
                                RMM.RMM_02 = mRmmList.get(0).RMM_02;
                                RMM.RMM_03 = mRmmList.get(0).RMM_03;
                                RMM.RMM_04 = mRmmList.get(0).RMM_04;
                                RMM.RMM_98 = mRmmList.get(0).RMM_98;
                                RMR_04ST_FILTER = RMM.RMM_03;
                                RMR_04ED_FILTER = RMM.RMM_04;
                                setFilterText();
//                                fnFilter();

                                if(mRmmList.get(0).RMM_98.equals(mUser.Value.OCM_01)){
                                    setMaster();
                                }

                                setRmdAdapter();

                                if (scancode != null && !scancode.equals("")) {
                                    requestRMD_SELECT("DETAIL", scancode);
                                }

                            }

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
        String RMD_01 = intentVO.CTD_01; //Master일련번호(RMM_01)
        String LIST_GUB = RMR_FILTER_GUB; //LIST구분자
        String RMR_04ST = RMR_04ST_FILTER; //시작시간
        String RMR_04ED = RMR_04ED_FILTER; //종료일자

        Call<RMDModel> call = Http.rmd(HttpBaseService.TYPE.POST).RMD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                RMD_ID,
                RMD_01,
                RMD_02,
                LIST_GUB,

                RMR_03,
                RMR_04ST,
                RMR_04ED
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

                                if(mList.size() ==0){
                                    empty.setVisibility(View.VISIBLE);
                                }
                                else{
                                    empty.setVisibility(View.GONE);
                                }

                                setRmdAdapter();
                                swipeRefresh.setRefreshing(false);
                            }
                            else{ //DETAIL (스캔찍을때)
                                mList2 = response.body().Data;
                                if(mList2 == null)
                                    mList2 = new ArrayList<>();

                                if(mList2.size() == 0){ //등록된 정보가 없는경우
                                    goRmdNew();
                                }
                                else{ //등록된 정보가 있는경우
                                    RMD_VO RMD = mList2.get(0);
                                    RMD.RMM_03 = RMM.RMM_03;
                                    RMD.RMM_04 = RMM.RMM_04;
                                    RMD.RMM_98 = RMM.RMM_98;

                                    Intent intent = new Intent(mContext, RmdDetail.class);
                                    intent.putExtra("RMD", RMD);
                                    intent.putExtra("intentVO", intentVO);

                                    mContext.startActivity(intent);
                                }
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMDModel> call, Throwable t){
                Log.d("RMD_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestRMM_CONTROL(String GUB, String RMM_02, String RMM_03, String RMM_04, String RMM_05) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String RMM_ID = intentVO.CTN_02; //컨테이너
        String RMM_01 = intentVO.CTD_01; //일련번호(CTD_01)
        String RMM_98 = mUser.Value.OCM_01; //최종수정자

        Call<RMMModel> call = Http.rmm(HttpBaseService.TYPE.POST).RMM_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                RMM_ID,
                RMM_01,
                RMM_02,
                RMM_03,

                RMM_04,
                RMM_05,
                RMM_98
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

                            if(GUB.equals("UPDATE_NOTICE")){
                                tvNotice.setText(response.body().Data.get(0).RMM_05);
                            }
                            else if(GUB.equals("UPDATE_TIME")){
                                RMM.RMM_02 = response.body().Data.get(0).RMM_02;
                                RMM.RMM_03 = response.body().Data.get(0).RMM_03;
                                RMM.RMM_04 = response.body().Data.get(0).RMM_04;
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMMModel> call, Throwable t){
                Log.d("RMM_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private void requestRMR_SELECT(String GUBUN, String RMR_ID, String RMR_01, String RMR_05){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        Call<RMRModel> call = Http.rmr(HttpBaseService.TYPE.POST).RMR_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                RMR_ID,
                RMR_01,
                "",
                "",

                "",
                "",
                RMR_05
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

                            mList_myRmr = response.body().Data;
                            if (mList_myRmr == null)
                                mList_myRmr = new ArrayList<>();

                            if(mList_myRmr.size()>0){
                                for(int i=1; i<mList_myRmr.size(); i++){
                                    if(mList_myRmr.get(i-1).RMR_02.equals(mList_myRmr.get(i).RMR_02)){ //같은 연습실일때
                                        if(mList_myRmr.get(i-1).RMR_04ED.equals(mList_myRmr.get(i).RMR_04ST)){ //시간이 연달아일때
                                            mList_myRmr.get(i-1).RMR_04ED = mList_myRmr.get(i).RMR_04ED;
                                            mList_myRmr.remove(i);
                                            i--;
                                        }
                                    }
                                }
                            }

                            mAdapter_myRmr.updateData(mList_myRmr);
                            mAdapter_myRmr.notifyDataSetChanged();
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

    private void goRmdNew(){
        Intent intent = new Intent(mContext, RmdDetail.class);
        intent.putExtra("scancode", scancode);
        intent.putExtra("intentVO", intentVO);
        intent.putExtra("RMM_98", RMM.RMM_98);

        mContext.startActivity(intent);
    }

    private void noticeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rmm_notice, null);
        builder.setView(view);

        EditText etNotice = (EditText) view.findViewById(R.id.etNotice);
        etNotice.setText(tvNotice.getText());
        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                requestRMM_CONTROL("UPDATE_NOTICE", "", "", "", etNotice.getText().toString());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void myReserveDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rmm_my_reserve, null);
        builder.setView(view);

        BaseHeader nameHeader = view.findViewById(R.id.header);
        nameHeader.btnHeaderRight1.setImageResource(R.drawable.btn_cancel_gray);
        nameHeader.btnHeaderRight1.setVisibility(View.VISIBLE);
        RecyclerView recyclerView_myRmr = (RecyclerView) view.findViewById(R.id.recyclerView_myRmr);
        mList_myRmr = new ArrayList<>();
        LinearLayoutManager linearLayoutManager_myRmr = new LinearLayoutManager(mContext);
        recyclerView_myRmr.setLayoutManager(linearLayoutManager_myRmr);
        mAdapter_myRmr = new myRmrRecycleAdapter(mContext, mList_myRmr);
        recyclerView_myRmr.setAdapter(mAdapter_myRmr);

        requestRMR_SELECT("MYLIST", intentVO.CTN_02, intentVO.CTD_01, mUser.Value.OCM_01);

        AlertDialog dialog = builder.create();

        nameHeader.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setReserveDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rmm_set_reserve, null);
        builder.setView(view);

//        Spinner spTimeGub = (Spinner) view.findViewById(R.id.spTimeGub);
//        ArrayList TimeGubList = new ArrayList();
//        TimeGubList.add(mContext.getString(R.string.dialog_rmm_set_reserve_timegub_text1));
//        TimeGubList.add(mContext.getString(R.string.dialog_rmm_set_reserve_timegub_text2));
//        ArrayAdapter TimeGubAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_detail_item, TimeGubList);
//        spTimeGub.setAdapter(TimeGubAdapter);
//        spTimeGub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                RMM_02_tmp = String.valueOf(position + 1);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//        spTimeGub.setSelection(Integer.parseInt(RMM.RMM_02) - 1);

        TimePicker tpStartTime = (TimePicker) view.findViewById(R.id.tpStartTime);
        tpStartTime.setCurrentHour(Integer.parseInt(RMM.RMM_03.substring(0,2)));
        tpStartTime.setCurrentMinute(Integer.parseInt(RMM.RMM_03.substring(2)));

        TimePicker tpEndTime = (TimePicker) view.findViewById(R.id.tpEndTime);
        tpEndTime.setCurrentHour(Integer.parseInt(RMM.RMM_04.substring(0,2)));
        tpEndTime.setCurrentMinute(Integer.parseInt(RMM.RMM_04.substring(2)));

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String RMM_03_tmp = fnTime(tpStartTime.getCurrentHour(), tpStartTime.getCurrentMinute());
                String RMM_04_tmp = fnTime(tpEndTime.getCurrentHour(), tpEndTime.getCurrentMinute());
                if(Integer.valueOf(RMM_03_tmp) < Integer.valueOf(RMM_04_tmp)){
                    requestRMM_CONTROL("UPDATE_TIME", RMM_02_tmp, RMM_03_tmp, RMM_04_tmp, "");
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(mActivity, R.string.dialog_rmm_set_reserve_wrong_time, Toast.LENGTH_SHORT).show();
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

    private void filterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rmm_time_filter, null);
        builder.setView(view);

        TimePicker tpFilterStartTime = (TimePicker) view.findViewById(R.id.tpFilterStartTime);
        tpFilterStartTime.setCurrentHour(Integer.parseInt(RMR_04ST_FILTER.substring(0,2)));
        tpFilterStartTime.setCurrentMinute(Integer.parseInt(RMR_04ST_FILTER.substring(2)));

        TimePicker tpFilterEndTime = (TimePicker) view.findViewById(R.id.tpFilterEndTime);
        tpFilterEndTime.setCurrentHour(Integer.parseInt(RMR_04ED_FILTER.substring(0,2)));
        tpFilterEndTime.setCurrentMinute(Integer.parseInt(RMR_04ED_FILTER.substring(2)));

        RadioGroup rgFilterGub = (RadioGroup) view.findViewById(R.id.rgFilterGub);
        rgFilterGub.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.rbFilterGub1){ //모두보기
                    RMR_FILTER_GUB_tmp = "1";
                }
                else if(i == R.id.rbFilterGub2){ //예약가능한 연습실만
                    RMR_FILTER_GUB_tmp = "2";
                }
            }
        });
        if(RMR_FILTER_GUB.equals("1")){
            rgFilterGub.check(R.id.rbFilterGub1);
        }
        else{
            rgFilterGub.check(R.id.rbFilterGub2);
        }

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Integer.valueOf(RMM.RMM_03) <= Integer.valueOf(fnTime(tpFilterStartTime.getCurrentHour(), tpFilterStartTime.getCurrentMinute()))){
                    if(Integer.valueOf(RMM.RMM_04) >= Integer.valueOf(fnTime(tpFilterEndTime.getCurrentHour(), tpFilterEndTime.getCurrentMinute()))){
                        if(Integer.valueOf(fnTime(tpFilterStartTime.getCurrentHour(), tpFilterStartTime.getCurrentMinute())) < Integer.valueOf(fnTime(tpFilterEndTime.getCurrentHour(), tpFilterEndTime.getCurrentMinute()))){
                            RMR_FILTER_GUB = RMR_FILTER_GUB_tmp;
                            RMR_04ST_FILTER = fnTime(tpFilterStartTime.getCurrentHour(), tpFilterStartTime.getCurrentMinute());
                            RMR_04ED_FILTER = fnTime(tpFilterEndTime.getCurrentHour(), tpFilterEndTime.getCurrentMinute());
                            setFilterText();
                            requestRMD_SELECT("LIST", "");

                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(mActivity, R.string.dialog_rmm_set_reserve_wrong_time, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        String tmp = getString(R.string.dialog_rmm_time_filter_wrong_time) + " (" + RMM.RMM_03.substring(0,2) + ":" + RMM.RMM_03.substring(2) + "~" + RMM.RMM_04.substring(0,2) + ":" + RMM.RMM_04.substring(2) + ")";
                        Toast.makeText(mActivity, tmp, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    String tmp = getString(R.string.dialog_rmm_time_filter_wrong_time) + " (" + RMM.RMM_03.substring(0,2) + ":" + RMM.RMM_03.substring(2) + "~" + RMM.RMM_04.substring(0,2) + ":" + RMM.RMM_04.substring(2) + ")";
                    Toast.makeText(mActivity, tmp, Toast.LENGTH_SHORT).show();
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

    private void dayDialog(){
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                RMR_03_C.set(year, month, date);
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
                tvDay.setText(sDateFormat(RMR_03));

                RmdRecycleAdapter.RMR_04_list.clear();

                setRmdAdapter();
            }
        }, RMR_03_C.get(Calendar.YEAR), RMR_03_C.get(Calendar.MONTH), RMR_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private void setMaster(){
        tvNotice.setOnClickListener(new View.OnClickListener(){ //master만 공지수정 가능
            @Override
            public void onClick(View v){
                noticeDialog();
            }
        });

        btnReserve.setText(R.string.rmm_list_reserveset_text); //master는 예약설정
        btnReserve.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setReserveDialog();
            }
        });
    }

    private void setFilterText(){
        String tmp = "";

        tmp = RMR_04ST_FILTER.substring(0,2) + ":" + RMR_04ST_FILTER.substring(2) + "~";
        tmp += RMR_04ED_FILTER.substring(0,2) + ":" + RMR_04ED_FILTER.substring(2) + "\n";
        if(RMR_FILTER_GUB.equals("1")){
            tmp += getString(R.string.dialog_rmm_time_filter_gub1);
        }
        else{
            tmp += getString(R.string.dialog_rmm_time_filter_gub2);
        }

        tvFilter.setText(tmp);
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
