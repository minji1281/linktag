package com.linktag.linkapp.ui.car;

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
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CADModel;
import com.linktag.linkapp.model.CARModel;
import com.linktag.linkapp.model.CDS_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.AddSharedDetail;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CAD_VO;
import com.linktag.linkapp.value_object.CAR_VO;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CadMain extends BaseActivity {
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
    private Button btnCarEdit;
    @BindView(R.id.spCar)
    Spinner spCar;
    private EditText etSearch;

    //======================
    // Variable
    //======================
    private CadAdapter mAdapter;
    private ArrayList<CAD_VO> mList;
    private ArrayList<CAR_VO> mCarList;
    private CtdVO intentVO;

    //======================
    // Initialize
    //======================
    ArrayList<CarSpinnerList> carList = new ArrayList<>();
    public static CAR_VO CAR;
    private String scanGubun = "N";
    private String scanCode;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cad_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        CAR = new CAR_VO();

        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            CAR.CAR_01 = getIntent().getExtras().getString("scanCode");
            scanCode = getIntent().getExtras().getString("scanCode");
            requestCAR_SELECT();
        }
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        //요거
        initLayoutByContractType();

        imgNew = findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goCadNew());

        layoutSpinner = (LinearLayout) findViewById(R.id.layoutSpinner);
        layoutSpinnerEmpty = (LinearLayout) findViewById(R.id.layoutSpinnerEmpty);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CAD_VO CAD = mList.get(position);

                Intent intent = new Intent(mContext, CadDetail.class);
                intent.putExtra("CAD", CAD);
                intent.putExtra("intentVO", intentVO);

                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
        btnCarEdit = (Button) findViewById(R.id.btnCarEdit);
        btnCarEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                carDialog("UPDATE");
            }
        });
        spCar = (Spinner) findViewById(R.id.spCar);
        etSearch = findViewById(R.id.etSearch);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestCAD_SELECT();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new CadAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        carInitial();

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

    private void requestCAD_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = "LIST";
        String CAD_ID = intentVO.CTN_02; //컨테이너
        String CAD_01 = CAR.CAR_01; //차량코드
        String CAD_02 = ""; //일련번호
        String CAD_03 = ""; //정비일자
        String CAD_04 = ""; //내역

        Call<CADModel> call = Http.cad(HttpBaseService.TYPE.POST).CAD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                CAD_ID,
                CAD_01,
                CAD_02,
                CAD_03,

                CAD_04
        );

        call.enqueue(new Callback<CADModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CADModel> call, Response<CADModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<CADModel> response = (Response<CADModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null) mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                            mAdapter.getFilter().filter(etSearch.getText());

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CADModel> call, Throwable t){
                Log.d("CAD_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestCAR_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = "DETAIL";
        String CAR_ID = intentVO.CTN_02; //컨테이너
        String CAD_01 = CAR.CAR_01; //코드번호

        Call<CARModel> call = Http.car(HttpBaseService.TYPE.POST).CAR_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                CAR_ID,
                CAD_01
        );

        call.enqueue(new Callback<CARModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CARModel> call, Response<CARModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<CARModel> response = (Response<CARModel>) msg.obj;

                            mCarList = response.body().Data;
                            if (mCarList == null) mCarList = new ArrayList<>();

                            if (mCarList.size() == 0){
                                scanGubun = "Y";
                                carDialog("INSERT"); //CAR NEW
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CARModel> call, Throwable t){
                Log.d("CAR_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestCAR_CONTROL(String GUBUN, String CAR_02, String CAR_03, String CAR_04) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String CAR_ID = intentVO.CTN_02; //컨테이너
        String CAR_01 = CAR.CAR_01; //일련번호
        String CAR_98 = mUser.Value.OCM_01; //최종수정자

        Call<CARModel> call = Http.car(HttpBaseService.TYPE.POST).CAR_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CAR_ID,
                CAR_01,
                CAR_02,
                CAR_03,

                CAR_04,
                CAR_98
        );

        call.enqueue(new Callback<CARModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CARModel> call, Response<CARModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                if (GUBUN.equals("INSERT")) {
                    CAR.CAR_01 = CAR_01;
                    CAR.CAR_02 = CAR_02;
                    CAR.CAR_03 = CAR_03;
                    CAR.CAR_04 = CAR_04;
                }

                if(GUBUN.equals("DELETE")){
                    CAR.CAR_01 = "";
                    mList.clear();
                    mAdapter.updateData(mList);
                    mAdapter.notifyDataSetChanged();
                }

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<CARModel> response = (Response<CARModel>) msg.obj;

                            carInitial();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CARModel> call, Throwable t){
                Log.d("CAR_CONTROL", t.getMessage());

                if(GUBUN.equals("INSERT")){
                    requestCDS_CONTROL(
                            "DELETE",
                            intentVO.CTD_07,
                            scanCode,
                            CAR.CAR_01,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "");
                }

//                closeLoadingBar();
            }
        });

    }

    private void goCadNew(){
        Intent intent = new Intent(mContext, CadDetail.class);
        intent.putExtra("intentVO", intentVO);
        intent.putExtra("CAD_01", CAR.CAR_01); //차량코드

        mContext.startActivity(intent);
    }

    private void carInitial(){
        CarInfo carinfo = new CarInfo(carList, mActivity, "spCar", intentVO.CTN_02, CAR.CAR_01);
        carinfo.execute();
        spCar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(scanGubun.equals("Y")){
                    scanGubun = "N";
                }
                else{
                    CAR.CAR_01 = carList.get(position).getCode();
                    CAR.CAR_02 = carList.get(position).getCarNum();
                    CAR.CAR_03 = carList.get(position).getCarYear();
                    CAR.CAR_04 = carList.get(position).getMemo();
                }
                requestCAD_SELECT();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void carDialog(String GUBUN){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_car, null);
        builder.setView(view);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        if(GUBUN.equals("INSERT")){
            btnDelete.setText(R.string.onCancel);
            btnDelete.setTextColor(Color.GRAY);
        }

        EditText etCarNum = (EditText) view.findViewById(R.id.etCarNum);
        EditText etCarYear = (EditText) view.findViewById(R.id.etCarYear);
        EditText etMemo = (EditText) view.findViewById(R.id.etMemo);
        if(GUBUN.equals("UPDATE")){
            etCarNum.setText(CAR.CAR_02);
            etCarYear.setText(CAR.CAR_03);
            etMemo.setText(CAR.CAR_04);
        }

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (GUBUN.equals("INSERT")) {
                    requestCDS_CONTROL(
                            "INSERT",
                            intentVO.CTD_07,
                            scanCode,
                            "",
                            intentVO.CTD_01,
                            intentVO.CTD_02,
                            intentVO.CTD_09,
                            mUser.Value.OCM_01,
                            etCarNum.getText().toString(),
                            etCarYear.getText().toString(),
                            etMemo.getText().toString());
                } else {
                    requestCAR_CONTROL(GUBUN, etCarNum.getText().toString(), etCarYear.getText().toString(), etMemo.getText().toString());
                }

                dialog.dismiss();
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

        tvDeleteText.setText(R.string.car_delete_text);
        etDeleteName.setHint(R.string.car_carnum_hint);

        AlertDialog dialogDelete = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etDeleteName.getText().toString().equals(CAR.CAR_02)){
                    dialogDelete.dismiss();
                    requestCAR_CONTROL("DELETE", "", "", "");
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(mActivity, R.string.car_delete_name_check, Toast.LENGTH_SHORT).show();
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
    private void goMember() {
        Intent intent = new Intent(mContext, Member.class);
        intent.putExtra("intentVO", intentVO);
        mContext.startActivity(intent);
    }

    @Override
    protected void scanResult(String str){
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }

    private void requestCDS_CONTROL(String GUBUN, String CTD_07, String scanCode, String CDS_03, String CTD_01, String CTD_02, String CTD_09, String OCM_01, String etCarNum, String etCarYear, String Memo){
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
                                CAR.CAR_01 = response.body().Data.get(0).CDS_03;
                                requestCAR_CONTROL(GUBUN, etCarNum, etCarYear, Memo);
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

}
