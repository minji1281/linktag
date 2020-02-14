package com.linktag.linkapp.ui.air;

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
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.AIRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.air.AirAdapter;
import com.linktag.linkapp.value_object.AIR_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AirList extends BaseActivity implements AirAdapter.AlarmClickListener {
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
    private AirAdapter mAdapter;
    private ArrayList<AIR_VO> mList;


    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_air_list);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility((View.GONE));

        //신규등록 test
        imgNew = findViewById(R.id.imgNew);
        imgNew.setOnClickListener(v -> goAirNew());

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, AirDetail.class);
                AIR_VO AIR = mList.get(position);
                intent.putExtra("AIR", AIR);
                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new AirAdapter(mContext, mList, this);
        listView.setAdapter(mAdapter);

        //requestAIR_SELECT();
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestAIR_SELECT();
    }

    private void requestAIR_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "LIST";
        String AIR_ID = "1"; //컨테이너
        String AIR_01 = " ";
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<AIRModel> call = Http.air(HttpBaseService.TYPE.POST).AIR_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                AIR_ID,
                AIR_01,
                OCM_01
        );

        call.enqueue(new Callback<AIRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<AIRModel> call, Response<AIRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<AIRModel> response = (Response<AIRModel>) msg.obj;

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
            public void onFailure(Call<AIRModel> call, Throwable t){
                Log.d("AIR_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    @Override
    public void onListAlarmClick(int position) {
//        AIR_VO data = mList.get(position);
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
    private void goAirNew(){
//        Toast.makeText(mContext, "신규등록", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(mContext, AirDetail.class);
        mContext.startActivity(intent);
    }

}
