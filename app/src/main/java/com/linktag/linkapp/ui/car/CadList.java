package com.linktag.linkapp.ui.car;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CADModel;
import com.linktag.linkapp.model.CARModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.ui.menu.Member;
import com.linktag.linkapp.value_object.CAD_VO;
import com.linktag.linkapp.value_object.CAR_VO;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CadList extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private BaseFooter footer;
    private ListView listView;
    private TextView emptyText;
    private ImageView imgNew;
    private Button btnCarEdit;
    @BindView(R.id.spCar)
    Spinner spCar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cad_list);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        CAR = new CAR_VO();
        if (getIntent().hasExtra("scanCode")) {
            CAR.CAR_01 = getIntent().getExtras().getString("scanCode");
            requestCAR_SELECT();
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
        imgNew.setOnClickListener(v -> goCadNew());

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
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, CAR_01);
                    ctds_control.requestCTDS_CONTROL();
                    CAR.CAR_01 = CAR_01;
                }

                if(GUBUN.equals("DELETE")){
                    CAR.CAR_01 = ""; //굳이 할 필요 없을라나...
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
            btnDelete.setText("취소");
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
                requestCAR_CONTROL(GUBUN, etCarNum.getText().toString(), etCarYear.getText().toString(), etMemo.getText().toString());

                dialog.dismiss();
            }
        });
        if(GUBUN.equals("UPDATE")){
            btnDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(mActivity)
                            .setMessage("해당 차량의 모든 일지가 함께 삭제됩니다.\n삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface delete_dialog, int which) {
                                    requestCAR_CONTROL("DELETE", "", "", "");

                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
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

}
