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
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.cos.CodAdapter;
import com.linktag.linkapp.ui.cos.CodDetail;
import com.linktag.linkapp.ui.cos.CosDetail;
import com.linktag.linkapp.ui.cos.CosInfo;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.CAD_VO;
import com.linktag.linkapp.value_object.COD_VO;

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


    //======================
    // Initialize
    //======================
    ArrayList<SpinnerList> carList = new ArrayList<>();
    @BindView(R.id.spHeaderRight)
    Spinner spCar;
    private String CAD_01;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cad_list);

//        initLayout();
//
//        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility(View.GONE);
        header.btnHeaderLeftText.setVisibility(View.VISIBLE);
        header.btnHeaderLeftText.setText("차량 소모품 교체/점검");
        header.btnHeaderLeftText.setOnClickListener(null);
        header.btnHeaderRight1.setVisibility(View.VISIBLE);
        header.btnHeaderRight1.setImageResource(android.R.drawable.ic_menu_add);
        header.btnHeaderRight1.setOnClickListener(v -> goCarNew()); //신규등록 test

        header.spHeaderRight.setVisibility(View.VISIBLE);
        CarInfo carinfo = new CarInfo(carList, mActivity, "spHeaderRight", getIntent().getStringExtra("CTN_02"), "");
        carinfo.execute();
        header.spHeaderRight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CAD_01 = carList.get(position).getCode();
                requestCAD_SELECT(CAD_01);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        imgNew = findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goCadNew());

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, CadDetail.class);
                CAD_VO CAD = mList.get(position);
                intent.putExtra("CAD", CAD);
                intent.putExtra("CTN_02", getIntent().getStringExtra("CTN_02")); //컨테이너
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

        requestCAD_SELECT(CAD_01);

        initLayout();

        initialize();
    }

    private void requestCAD_SELECT(String CAD_01){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "LIST";
        String CAD_ID = getIntent().getStringExtra("CTN_02"); //컨테이너
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

                            if(mList == null)
                                mList = new ArrayList<>();

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

    private void goCadNew(){
        Intent intent = new Intent(mContext, CadDetail.class);
        intent.putExtra("CTN_02", getIntent().getStringExtra("CTN_02")); //컨테이너
        intent.putExtra("CAD_01", CAD_01); //차량코드
        mContext.startActivity(intent);
    }

    //신규등록 test
    private void goCarNew(){
        Intent intent = new Intent(mContext, CarDetail.class);
        intent.putExtra("CTN_02", getIntent().getStringExtra("CTN_02"));
        mContext.startActivity(intent);
    }

}
