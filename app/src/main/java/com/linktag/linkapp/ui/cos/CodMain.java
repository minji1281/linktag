package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.model.COSModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.COD_VO;
import com.linktag.linkapp.value_object.COS_VO;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CodMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private BaseFooter footer;
    private SwipeRefreshLayout swipeRefresh;
    public static LinearLayout layoutSpinner;
    public static LinearLayout layoutSpinnerEmpty;
    private ListView listView;
    private TextView emptyText;
    public static ImageView imgNew;
    private Button btnEdit;
    @BindView(R.id.spCos)
    Spinner spCos;
    private EditText etSearch;

    //======================
    // Variable
    //======================
    private CodAdapter mAdapter;
    private ArrayList<COD_VO> mList;
    private ArrayList<COS_VO> mCosList;
    private CtdVO intentVO;

    //======================
    // Initialize
    //======================
    ArrayList<SpinnerList> cosList = new ArrayList<>();
    public static COS_VO COS;
    private String scanGubun = "N";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cod_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        COS = new COS_VO();
        if (getIntent().hasExtra("scanCode")) {
            COS.COS_01 = getIntent().getExtras().getString("scanCode");
            requestCOS_SELECT();
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

        imgNew = findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goCodNew());

        layoutSpinner = (LinearLayout) findViewById(R.id.layoutSpinner);
        layoutSpinnerEmpty = (LinearLayout) findViewById(R.id.layoutSpinnerEmpty);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                COD_VO COD = mList.get(position);

                Intent intent = new Intent(mContext, CodDetail.class);
                intent.putExtra("COD", COD);
                intent.putExtra("intentVO", intentVO);

                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cosDialog("UPDATE");
            }
        });
        spCos = (Spinner) findViewById(R.id.spCos);
        etSearch = findViewById(R.id.etSearch);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestCOD_SELECT();
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new CodAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        cosInitial();

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

    private void requestCOD_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = "LIST";
        String COD_ID = intentVO.CTN_02; //컨테이너
        String COD_01 = ""; //일련번호
        String COD_95 = COS.COS_01;
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<CODModel> call = Http.cod(HttpBaseService.TYPE.POST).COD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                COD_ID,
                COD_01,
                COD_95,
                OCM_01
        );

        call.enqueue(new Callback<CODModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CODModel> call, Response<CODModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<CODModel> response = (Response<CODModel>) msg.obj;

                            mList = response.body().Data;

                            if(mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                            mAdapter.getFilter().filter(etSearch.getText());

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CODModel> call, Throwable t){
                Log.d("COD_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestCOS_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "DETAIL";
        String COS_ID = intentVO.CTN_02; //컨테이너
        String COS_01 = COS.COS_01; //일련번호

        Call<COSModel> call = Http.cos(HttpBaseService.TYPE.POST).COS_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                COS_ID,
                COS_01
        );

        call.enqueue(new Callback<COSModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<COSModel> call, Response<COSModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<COSModel> response = (Response<COSModel>) msg.obj;

                            mCosList = response.body().Data;

                            if(mCosList == null)
                                mCosList = new ArrayList<>();

                            if (mCosList.size() == 0){
                                scanGubun = "Y";
                                cosDialog("INSERT"); //COS NEW
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<COSModel> call, Throwable t){
                Log.d("COS_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void requestCOS_CONTROL(String GUBUN, String COS_02, String COS_03) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String COS_ID = intentVO.CTN_02; //컨테이너
        String COS_01 = COS.COS_01; //일련번호
        String COS_98 = mUser.Value.OCM_01; //최종수정자

        Call<COSModel> call = Http.cos(HttpBaseService.TYPE.POST).COS_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                COS_ID,
                COS_01,
                COS_02,
                COS_03,

                COS_98
        );

        call.enqueue(new Callback<COSModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<COSModel> call, Response<COSModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                if (GUBUN.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, COS_01);
                    ctds_control.requestCTDS_CONTROL();
                    COS.COS_01 = COS_01;
                    COS.COS_02 = COS_02;
                    COS.COS_03 = COS_03;
                }

                if(GUBUN.equals("DELETE")){
                    COS.COS_01 = "";
                    mList.clear();
                    mAdapter.updateData(mList);
                    mAdapter.notifyDataSetChanged();
                }

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<COSModel> response = (Response<COSModel>) msg.obj;

                            cosInitial();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<COSModel> call, Throwable t){
                Log.d("COS_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private void goCodNew(){
        Intent intent = new Intent(mContext, CodDetail.class);
        intent.putExtra("intentVO", intentVO);
        intent.putExtra("COD_95", COS.COS_01); //화장대코드

        mContext.startActivity(intent);
    }

    private void cosInitial(){
        CosInfo cosinfo = new CosInfo(cosList, mActivity, "spCos", "LIST2", intentVO.CTN_02, COS.COS_01, "L");
        cosinfo.execute();
        spCos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(scanGubun.equals("Y")){
                    scanGubun = "N";
                }
                else{
                    COS.COS_01 = cosList.get(position).getCode();
                    COS.COS_02 = cosList.get(position).getName();
                    COS.COS_03 = cosList.get(position).getMemo();
                }
                requestCOD_SELECT();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cosDialog(String GUBUN){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cos, null);
        builder.setView(view);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        if(GUBUN.equals("INSERT")){
            btnDelete.setText(R.string.onCancel);
            btnDelete.setTextColor(Color.GRAY);
        }

        EditText etName = (EditText) view.findViewById(R.id.etName);
        EditText etMemo = (EditText) view.findViewById(R.id.etMemo);
        if(GUBUN.equals("UPDATE")){
            etName.setText(COS.COS_02);
            etMemo.setText(COS.COS_03);
        }

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etName.getText().toString().equals("")){
                    Toast.makeText(mActivity, R.string.pot_validation_check1, Toast.LENGTH_SHORT).show();
                }
                else{
                    requestCOS_CONTROL(GUBUN, etName.getText().toString(), etMemo.getText().toString());

                    dialog.dismiss();
                }
            }
        });
        if(GUBUN.equals("UPDATE")){
            btnDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    deleteDialog(dialog);
                }
            });
        }
        else{ //INSERT
            btnDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    private void deleteDialog(AlertDialog dialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        TextView tvDeleteText = (TextView) view.findViewById(R.id.tvDeleteText);
        EditText etDeleteName = (EditText) view.findViewById(R.id.etDeleteName);

        tvDeleteText.setText(R.string.cos_delete_text);

        AlertDialog dialogDelete = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etDeleteName.getText().toString().equals(COS.COS_02)){
                    dialogDelete.dismiss();
                    requestCOS_CONTROL("DELETE", "", "");
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(mActivity, R.string.dialog_delete_check_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogDelete.dismiss();
            }
        });

        dialogDelete.show();
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
