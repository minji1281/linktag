package com.linktag.linkapp.ui.master_log;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.LogVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterLog extends BaseActivity {

    //======================
    // Layout
    //======================
    private BaseHeader header;
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;

    //======================
    // Variable
    //======================
    private LogAdapter mAdapter;
    private ArrayList<LogVO> mList;

    //======================
    // Initialize
    //======================
    private LogVO LOG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_list);

        LOG = (LogVO) getIntent().getSerializableExtra("LOG");

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        listView = findViewById(R.id.listView);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestLOG_CONTROL();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new LogAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestLOG_CONTROL();
    }

    private void requestLOG_CONTROL(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, getResources().getString(R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = "INSERT_ARM";
        String LOG_ID = LOG.LOG_ID; //컨테이너
        String LOG_01 = LOG.LOG_01; //코드번호
        String LOG_02 = ""; //일련번호
        String LOG_03 = ""; //구분
        String LOG_04 = getResources().getString(com.linktag.base.R.string.common_alarm_send); //내역
        String LOG_05 = ""; //수정일시
        String LOG_98 = LOG.LOG_98; //최종수정자
        String SP_NAME = LOG.SP_NAME;

        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                LOG_ID,
                LOG_01,
                LOG_02,
                LOG_03,

                LOG_04,
                LOG_05,
                LOG_98,
                SP_NAME
        );

        call.enqueue(new Callback<LOG_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOG_Model> call, Response<LOG_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<LOG_Model> response = (Response<LOG_Model>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null) mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<LOG_Model> call, Throwable t){
                Log.d("LOG_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

}
