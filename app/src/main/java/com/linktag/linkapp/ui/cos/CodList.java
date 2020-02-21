package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.model.COSModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.COD_VO;
import com.linktag.linkapp.value_object.COS_VO;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CodList extends BaseActivity implements CodAdapter.AlarmClickListener {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private ListView listView;
    private TextView emptyText;
    private ImageView imgNew;

    //======================
    // Variable
    //======================
    private CodAdapter mAdapter;
    private ArrayList<COD_VO> mList;
    private ArrayList<COS_VO> mCosList;

    //======================
    // Initialize
    //======================
    ArrayList<SpinnerList> cosList = new ArrayList<>();
    @BindView(R.id.spHeaderRight)
    Spinner spCos;
    public static String COD_95;
    private String CTM_01;
    private String CTN_02;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cod_list);

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTN_02 = getIntent().getStringExtra("CTN_02");
        if (getIntent().hasExtra("scanCode")) {
            COD_95 = getIntent().getExtras().getString("scanCode");
            requestCOS_SELECT();
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility(View.GONE);
        header.btnHeaderLeftText.setVisibility(View.VISIBLE);
        header.btnHeaderLeftText.setText("화장품 목록");
        header.btnHeaderLeftText.setOnClickListener(null);

        header.spHeaderRight.setVisibility(View.VISIBLE);

        imgNew = findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goCodNew());

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, CodDetail.class);
                COD_VO COD = mList.get(position);
                intent.putExtra("COD", COD);
                intent.putExtra("CTN_02", getIntent().getStringExtra("CTN_02"));
                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new CodAdapter(mContext, mList, this);
        listView.setAdapter(mAdapter);

        //requestCOD_SELECT();
    }

    @Override
    protected void onResume(){
        super.onResume();

//        requestCOD_SELECT(COD_95);
        cosInitial();
    }

    private void requestCOD_SELECT(String COD_95){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "LIST";
        String COD_ID = getIntent().getStringExtra("CTN_02"); //컨테이너
        String COD_01 = ""; //일련번호
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
                            closeLoadingBar();

                            Response<CODModel> response = (Response<CODModel>) msg.obj;

                            mList = response.body().Data;

                            if(mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CODModel> call, Throwable t){
                Log.d("COD_SELECT", t.getMessage());
                closeLoadingBar();
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
        String COS_ID = CTN_02; //컨테이너
        String COS_01 = COD_95; //일련번호

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
                                goCosNew();
                            }
                            else{
                                COD_95 = mCosList.get(0).COS_01;
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

    private void requestCOD_CONTROL(String GUB, COD_VO COD) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String GUBUN = GUB;
        String COD_ID = COD.COD_ID; //컨테이너
        String COD_01 = COD.COD_01; //코드번호
        String COD_02 = "";
        String COD_03 = "";
        double COD_04 = 0;
        String COD_05 = "";
        String COD_06 = "";
        String COD_07 = "";
        String COD_08 = "";
        String COD_96 = "";
        String COD_98 = mUser.Value.OCM_01; //최종수정자
        String ARM_03 = COD.ARM_03;

        Call<CODModel> call = Http.cod(HttpBaseService.TYPE.POST).COD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                COD_ID,
                COD_01,
                COD_02,
                COD_03,

                COD_04,
                COD_05,
                COD_06,
                COD_07,
                COD_08,

                COD.COD_95,
                COD_96,
                COD_98,
                ARM_03
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
                        if (msg.what == 100){

                            Response<CODModel> response = (Response<CODModel>) msg.obj;

                            onResume();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CODModel> call, Throwable t){
                Log.d("Test", t.getMessage());

            }
        });

    }

    @Override
    public void onListAlarmClick(int position) {
        COD_VO data = mList.get(position);

        if(data.ARM_03.equals("Y")){
            data.ARM_03 = "N";
        }
        else{ //N
            data.ARM_03 = "Y";
        }

        requestCOD_CONTROL("ALARM_UPDATE", data);
    }

    private void goCodNew(){
        Intent intent = new Intent(mContext, CodDetail.class);
        intent.putExtra("CTN_02", getIntent().getStringExtra("CTN_02"));
        intent.putExtra("COD_95", COD_95); //화장대코드
        mContext.startActivity(intent);
    }

    private void goCosNew(){
        Intent intent = new Intent(mContext, CosDetail.class);
        intent.putExtra("CTN_02", getIntent().getStringExtra("CTN_02"));
        intent.putExtra("COS_01", COD_95); //화장대코드
        intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
        intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
        mContext.startActivity(intent);
    }

    private void cosInitial(){
        CosInfo cosinfo = new CosInfo(cosList, mActivity, "spHeaderRight", "LIST", getIntent().getStringExtra("CTN_02"), COD_95);
        cosinfo.execute();
        header.spHeaderRight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                COD_95 = cosList.get(position).getCode();
                requestCOD_SELECT(COD_95);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
