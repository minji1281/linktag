package com.linktag.linkapp.ui.vot;

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
import android.widget.CheckBox;
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
import com.linktag.linkapp.model.VICModel;
import com.linktag.linkapp.model.VITModel;
import com.linktag.linkapp.model.VOTModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.VIT_VO;
import com.linktag.linkapp.value_object.VOT_VO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VotDetail extends BaseActivity {
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
    private EditText etVitName;

    private CheckBox cbGub1;
    private CheckBox cbGub2;

    private Button btnVitAdd;

    private RecyclerView recyclerView_DetailVit;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefresh;

    private TextView tvDay;
    private TextView tvWriter;
    private TextView tvCount;
    private TextView tvGub1;
    private TextView tvGub2;

    private Button btnSave;

    //======================
    // Variable
    //======================
    public static DetailVitRecycleAdapter mAdapter_VIT;
    public static ArrayList<VIT_VO> mList_VIT;
    private ArrayList<VOT_VO> mList_VOT;

    //======================
    // Initialize
    //======================
    private CtdVO intentVO;
    private VOT_VO VOT;
    private String GUBUN;
    public static ArrayList<String> voteItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vot_detail);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(getIntent().hasExtra("VOT")){
            VOT = (VOT_VO) getIntent().getSerializableExtra("VOT");
            GUBUN = "UPDATE";
        }
        else{
            VOT = new VOT_VO();
            VOT.VOT_ID = intentVO.CTN_02;
            VOT.VOT_01 = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            VOT.VOT_03 = sdf.format(Calendar.getInstance().getTime());
            VOT.VOT_05 = "N";
            VOT.VOT_06 = "N";
            GUBUN = "INSERT";
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etVitName = (EditText) findViewById(R.id.etVitName);

        cbGub1 = (CheckBox) findViewById(R.id.cbGub1);
        cbGub2 = (CheckBox) findViewById(R.id.cbGub2);

        btnVitAdd = (Button) findViewById(R.id.btnVitAdd);
        btnVitAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(etVitName.getText().toString().equals("")){
                    Toast.makeText(mActivity, R.string.vot_detail_vit_name_re, Toast.LENGTH_SHORT).show();
                }
                else{
                    VIT_VO tmp = new VIT_VO();
                    tmp.VIT_03 = etVitName.getText().toString();
                    mList_VIT.add(tmp);
                    etVitName.setText("");

                    mAdapter_VIT.updateData(mList_VIT);
                    mAdapter_VIT.notifyDataSetChanged();
                }
            }
        });

        recyclerView_DetailVit = (RecyclerView) findViewById(R.id.recyclerView_DetailVit);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter_VIT.updateData(mList_VIT);
                mAdapter_VIT.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        tvDay = (TextView) findViewById(R.id.tvDay);
        tvWriter = (TextView) findViewById(R.id.tvWriter);
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvGub1 = (TextView) findViewById(R.id.tvGub1);
        tvGub2 = (TextView) findViewById(R.id.tvGub2);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(GUBUN.equals("INSERT")){
                    if(validationCheck()){
                        requestVOT_CONTROL(GUBUN);
                    }
                }
                else{ //UPDATE
                    if(voteItemList.size() > 0){
                        String EndGub = "N";
                        for(int i=0; i<voteItemList.size(); i++){
                            if(i == voteItemList.size()-1){
                                EndGub = "Y";
                            }
                            requestVIC_CONTROL(voteItemList.get(i), EndGub);
                        }
                    }
                    else{ //투표항목을 선택하지 않았을때
                        Toast.makeText(mActivity, R.string.vot_detail_validation3, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(GUBUN.equals("UPDATE")){
            if(VOT.VOT_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        deleteDialog();
                    }
                });
            }

            cbGub1.setVisibility(View.GONE);
            cbGub2.setVisibility(View.GONE);
            etName.setFocusable(false);
            etVitName.setVisibility(View.GONE);
            btnVitAdd.setVisibility(View.GONE);
            btnSave.setText(R.string.vot_detail_voting);
        }
        else{ //INSERT
            tvGub1.setVisibility(View.GONE);
            tvGub2.setVisibility(View.GONE);
            btnSave.setText(R.string.detail_save);

            getNewData();
        }
    }

    @Override
    protected void initialize() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        tvDay.setText(sDateFormat(VOT.VOT_03));
        tvWriter.setText(mUser.Value.OCM_02);
        tvCount.setText("0");

        mList_VIT = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView_DetailVit.setLayoutManager(linearLayoutManager);
        mAdapter_VIT = new DetailVitRecycleAdapter(mContext, mList_VIT, GUBUN);
        recyclerView_DetailVit.setAdapter(mAdapter_VIT);

        if(GUBUN.equals("UPDATE")){
            getDetail();

            requestVIT_SELECT("LIST_DETAIL");
        }
        else{ //INSERT

        }
    }

    private void getDetail() {
        etName.setText(VOT.VOT_02);

        if(VOT.VOT_05.equals("Y")){
            tvGub1.setText("O");
        }
        else{
            tvGub1.setText("X");
        }
        if(VOT.VOT_06.equals("Y")){
            tvGub2.setText("O");
        }
        else{
            tvGub2.setText("X");
        }
    }

    private void getNewData(){
        //초기값

    }

    private void requestVOT_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String VOT_ID = VOT.VOT_ID; //컨테이너
        String VOT_01 = VOT.VOT_01; //일련번호(자동생성)
        String VOT_02 = etName.getText().toString(); //명칭
        String VOT_03 = VOT.VOT_03; //작성일자(YYYYMMDD)

        String VOT_04 = ""; //마감일자(YYYYMMDD)
        String VOT_05 = VOT.VOT_05; //복수여부(Y/N)
        String VOT_06 = VOT.VOT_06; //익명여부(Y/N)
        if(GUB.equals("INSERT")){
            if(cbGub1.isChecked()){
                VOT_05 = "Y";
            }
            if(cbGub2.isChecked()){
                VOT_06 = "Y";
            }
        }
        String VOT_98 = mUser.Value.OCM_01; //최종수정자

        Call<VOTModel> call = Http.vot(HttpBaseService.TYPE.POST).VOT_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                VOT_ID,
                VOT_01,
                VOT_02,
                VOT_03,

                VOT_04,
                VOT_05,
                VOT_06,
                VOT_98
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

                            if(GUB.equals("DELETE")){
                                finish();
                            }
                            else{
                                mList_VOT = response.body().Data;

                                if(mList_VOT == null)
                                    mList_VOT = new ArrayList<>();

                                if(mList_VOT.size()>0){
                                    VOT.VOT_01 = mList_VOT.get(0).VOT_01;

                                    String EndGub = "N";
                                    for(int i=0; i<mList_VIT.size(); i++){
                                        if(i == mList_VIT.size()-1){
                                            EndGub = "Y";
                                        }
                                        requestVIT_CONTROL(mList_VIT.get(i).VIT_03, EndGub);
                                    }
                                }
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VOTModel> call, Throwable t){
                Log.d("VOT_CONTROL", t.getMessage());

//                closeLoadingBar();
            }
        });

    }

    private void requestVIT_SELECT(String GUB){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String VIT_ID = VOT.VOT_ID; //컨테이너
        String VIT_01 = VOT.VOT_01; //투표코드(VOT_01)

        Call<VITModel> call = Http.vit(HttpBaseService.TYPE.POST).VIT_SELECT(
                BaseConst.URL_HOST,
                GUB,
                VIT_ID,
                VIT_01
        );

        call.enqueue(new Callback<VITModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VITModel> call, Response<VITModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<VITModel> response = (Response<VITModel>) msg.obj;

                            mList_VIT = response.body().Data;
                            if(mList_VIT == null)
                                mList_VIT = new ArrayList<>();
//
                            mAdapter_VIT.updateData(mList_VIT);
                            mAdapter_VIT.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VITModel> call, Throwable t){
                Log.d("VIT_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestVIT_CONTROL(String VIT_03, String EndGub) {

        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String GUB = "INSERT";
        String VIT_ID = VOT.VOT_ID; //컨테이너
        String VIT_01 = VOT.VOT_01; //투표코드(VOT_01)
        String VIT_02 = ""; //일련번호(자동생성)

        String VIT_98 = mUser.Value.OCM_01; //최종수정자

        Call<VITModel> call = Http.vit(HttpBaseService.TYPE.POST).VIT_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                VIT_ID,
                VIT_01,
                VIT_02,
                VIT_03,

                VIT_98
        );

        call.enqueue(new Callback<VITModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VITModel> call, Response<VITModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<VITModel> response = (Response<VITModel>) msg.obj;

                            if(EndGub.equals("Y")){ //마지막 CONTROL
                                finish();
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VITModel> call, Throwable t){
                Log.d("VIT_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private void requestVIC_CONTROL(String VIC_02, String EndGub) {

        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String GUB = "INSERT";
        String VIC_ID = VOT.VOT_ID; //컨테이너
        String VIC_01 = VOT.VOT_01; //투표코드(VOT_01)
        String VIC_03 = mUser.Value.OCM_01; //사용자(OCM_01)
        String VIC_98 = mUser.Value.OCM_01; //최종수정자

        Call<VICModel> call = Http.vic(HttpBaseService.TYPE.POST).VIC_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                VIC_ID,
                VIC_01,
                VIC_02,
                VIC_03,

                VIC_98
        );

        call.enqueue(new Callback<VICModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VICModel> call, Response<VICModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<VICModel> response = (Response<VICModel>) msg.obj;

                            if(EndGub.equals("Y")){ //마지막 CONTROL
                                finish();
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VICModel> call, Throwable t){
                Log.d("VIC_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

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
                if(etDeleteName.getText().toString().equals(VOT.VOT_02)){
                    dialog.dismiss();
                    requestVOT_CONTROL("DELETE");
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
        if(mList_VIT.size() < 2){
            check = false;
            Toast.makeText(mActivity, R.string.vot_detail_validation2, Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);

        return result;
    }

}
