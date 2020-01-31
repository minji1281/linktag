package com.linktag.linkapp.ui.work_record;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.work_state.WorkDetailAdapter;
import com.linktag.linkapp.value_object.LED_VO;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkRecordActivity extends BaseActivity {
    public static final String WORK_STATE = "WORK_STATE";

    BaseHeader header;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefresh;
    ImageView imgUserPhoto;
    TextView tvUserName;
    TextView tvWorkType;
    TextView tvWorkTime;
    TextView tvWorkDate;
    TextView tvWorkState;
    Button btnMove;


    private LED_VO mCMTV0;

    private WorkDetailAdapter mAdapter;
    private ArrayList<LED_VO> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_record);

        mContext = this;

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout(){
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        tvUserName = findViewById(R.id.tvUserName);
        tvWorkType = findViewById(R.id.tvWorkType);
        tvWorkTime = findViewById(R.id.tvWorkTime);
        tvWorkTime.setVisibility(View.GONE);
        tvWorkDate = findViewById(R.id.tvWorkDate);
        tvWorkDate.setVisibility(View.GONE);
        tvWorkState = findViewById(R.id.tvWorkState);
        tvWorkState.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestATDVIEW());
    }

    @Override
    protected void initialize(){
        mCMTV0 = (LED_VO) getIntent().getSerializableExtra(WORK_STATE);

        tvUserName.setText(mCMTV0.LED_04_NM);
        tvWorkType.setText(mCMTV0.CDO_06_NM + " " + mCMTV0.CDO_07_NM);
        tvWorkTime.setText(ClsDateTime.ConvertStringBuffer(mCMTV0.LED_07, ":", 2) + " - " +
                            ClsDateTime.ConvertStringBuffer(mCMTV0.LED_08, ":", 2));
        tvWorkState.setText(mCMTV0.STAT);

        mList = new ArrayList<>();
        mAdapter = new WorkDetailAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);

        requestATDVIEW();

    }

    private void requestATDVIEW() {
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(getString(R.string.common_network_error));
//            return;
//        }
//
//        openLoadingBar();
//
//        String strToday = ClsDateTime.getNow("yyyyMMdd");
//
//        Call<LEDModel> call = Http.commute(HttpBaseService.TYPE.POST).CMT_SELECT(
//                BaseConst.URL_HOST,
//                "LIST2",
//                mCMTV0.LED_ID,
//                "CMT",
//                "",
//                mCMTV0.LED_04,
//                "",
//                ClsDateTime.getNow("yyyyMMdd"),
//                ""
//        );
//
//        call.enqueue(new Callback<LEDModel>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<LEDModel> all, Response<LEDModel> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if(msg.what == 100){
//                            closeLoadingBar();
//
//                            Response<LEDModel> response = (Response<LEDModel>) msg.obj;
//
//                            mList = response.body().Data;
//                            if (mList == null)
//                                mList = new ArrayList<>();
//
//                            mAdapter.updateData(mList);
//                            mAdapter.notifyDataSetChanged();
//                            swipeRefresh.setRefreshing(false);
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<LEDModel> call, Throwable t){ closeLoadingBar(); }
//        });

    }

}
