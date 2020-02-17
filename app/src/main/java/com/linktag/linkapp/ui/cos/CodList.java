package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.cos.CodAdapter;
//import com.linktag.linkapp.ui.air.CodDetail;
import com.linktag.linkapp.value_object.COD_VO;

import java.sql.Array;
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


    //======================
    // Initialize
    //======================
    ArrayList<CosList> cosList = new ArrayList<>();
    @BindView(R.id.spHeaderRight)
    Spinner spCos;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cod_list);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility((View.GONE));

//        header.spHeaderRight.setVisibility((View.VISIBLE));
        header.spHeaderRight.setVisibility(View.VISIBLE);
        CosInfo cosinfo = new CosInfo(cosList, mActivity);
        cosinfo.execute();
        header.spHeaderRight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestCOD_SELECT(cosList.get(position).getCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //신규등록 test
        imgNew = findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goCodNew());

        listView = findViewById(R.id.listView);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(mContext, CodDetail.class);
//                COD_VO AIR = mList.get(position);
//                intent.putExtra("COD", COD);
//                mContext.startActivity(intent);
//            }
//        });
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

//        requestCOD_SELECT("");
    }

    private void requestCOD_SELECT(String COD_95){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "LIST";
        String COD_ID = "1"; //컨테이너
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

    @Override
    public void onListAlarmClick(int position) {
//        COD_VO data = mList.get(position);
//
//        if(data.ARM_03.equals("Y")){
//            data.ARM_03 = "N";
//        }
//        else{ //N
//            data.ARM_03 = "Y";
//        }

        Toast.makeText(mContext, "준비중 입니다.", Toast.LENGTH_LONG).show();
    }

    //신규등록 test
    private void goCodNew(){
        Toast.makeText(mContext, "신규등록", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(mContext, CodDetail.class);
//        mContext.startActivity(intent);
    }

}
