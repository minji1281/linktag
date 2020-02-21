package com.linktag.linkapp.ui.car;

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
import com.linktag.linkapp.model.CADModel;
import com.linktag.linkapp.model.CARModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.CAD_VO;
import com.linktag.linkapp.value_object.CAR_VO;

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
    private ListView listView;
    private TextView emptyText;
    private ImageView imgNew;

    //======================
    // Variable
    //======================
    private CadAdapter mAdapter;
    private ArrayList<CAD_VO> mList;
    private ArrayList<CAR_VO> mCarList;


    //======================
    // Initialize
    //======================
    ArrayList<SpinnerList> carList = new ArrayList<>();
    @BindView(R.id.spHeaderRight)
    Spinner spCar;
    public static String CAD_01 = "";
    private String CTM_01;
    private String CTN_02;
    private String scancode;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cad_list);

        CTM_01 = getIntent().getStringExtra("CTM_01");
        CTN_02 = getIntent().getStringExtra("CTN_02");
        if (getIntent().hasExtra("scanCode")) {
            scancode = getIntent().getExtras().getString("scanCode");
            requestCAR_SELECT();
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility(View.GONE);
        header.btnHeaderLeftText.setVisibility(View.VISIBLE);
        header.btnHeaderLeftText.setText("차량 소모품 교체/점검");
        header.btnHeaderLeftText.setOnClickListener(null);

        header.spHeaderRight.setVisibility(View.VISIBLE);
//        carInitial();

        imgNew = findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goCadNew());

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, CadDetail.class);
                CAD_VO CAD = mList.get(position);
                intent.putExtra("CAD", CAD);
                intent.putExtra("CTN_02", CTN_02); //컨테이너
                intent.putExtra("CAD_01", CAD_01); //차량코드
                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new CadAdapter(mContext, mList);
        listView.setAdapter(mAdapter);

        //requestCAD_SELECT();
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

        openLoadingBar();

        String GUBUN = "LIST";
        String CAD_ID = CTN_02; //컨테이너
        String CAD_02 = ""; //일련번호

        Call<CADModel> call = Http.cad(HttpBaseService.TYPE.POST).CAD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                CAD_ID,
                CAD_01,
                CAD_02
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
                            closeLoadingBar();

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
                closeLoadingBar();
            }
        });
    }

    private void requestCAR_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "DETAIL";
        String CAR_ID = CTN_02; //컨테이너
        CAD_01 = scancode; //코드번호

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
                            closeLoadingBar();

                            Response<CARModel> response = (Response<CARModel>) msg.obj;

                            mCarList = response.body().Data;
                            if (mCarList == null) mCarList = new ArrayList<>();

                            if (mCarList.size() == 0){
                                goCarNew();
                            }
                            else{
                                CAD_01 = mCarList.get(0).CAR_01;
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CARModel> call, Throwable t){
                Log.d("CAR_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void goCadNew(){
        Intent intent = new Intent(mContext, CadDetail.class);
        intent.putExtra("CTN_02", CTN_02); //컨테이너
        intent.putExtra("CAD_01", CAD_01); //차량코드
        mContext.startActivity(intent);
    }

    private void goCarNew(){
        Intent intent = new Intent(mContext, CarDetail.class);
        intent.putExtra("CTN_02", getIntent().getStringExtra("CTN_02"));
        intent.putExtra("CAR_01", scancode); //차량코드
        intent.putExtra("CTM_01", getIntent().getStringExtra("CTM_01"));
        intent.putExtra("CTD_02", getIntent().getStringExtra("CTD_02"));
        mContext.startActivity(intent);
    }

    private void carInitial(){
        CarInfo carinfo = new CarInfo(carList, mActivity, "spHeaderRight", CTN_02, CAD_01);
        carinfo.execute();
        header.spHeaderRight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CAD_01 = carList.get(position).getCode();
                requestCAD_SELECT();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
