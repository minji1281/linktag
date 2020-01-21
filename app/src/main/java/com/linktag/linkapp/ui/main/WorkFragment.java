package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.work_record.WorkRecordActivity;
import com.linktag.linkapp.ui.work_state.WorkStateAdapter;
import com.linktag.linkapp.value_object.LED_VO;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkFragment extends BaseFragment {
    //===================================
    // Layout//===================================
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    //===================================
    // Variable
    //===================================
    private WorkStateAdapter mAdapter;
    private ArrayList<LED_VO> mList;


    public WorkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_work, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        requestCMT_SELECT();
    }

    private void initLayout() {
        listView = view.findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestCMT_SELECT());
    }

    private void goWorkRecord(int position) {
        Intent intent = new Intent(mContext, WorkRecordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(WorkRecordActivity.WORK_STATE, mList.get(position));
        mContext.startActivity(intent);
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new WorkStateAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }


    public void requestCMT_SELECT() {

        System.out.println("$$$$$$$$$$$$$$$됐다 됐다 잘 됐다.");
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        openLoadingBar();

        String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<LEDModel> call = Http.commute(HttpBaseService.TYPE.POST).CMT_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                mUser.Value.CTM_01,
                "CMT",
                "",
                mUser.Value.OCM_01,
                "",
                ClsDateTime.getNow("yyyyMMdd"),
                ""
        );

        call.enqueue(new Callback<LEDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LEDModel> call, Response<LEDModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<LEDModel> response = (Response<LEDModel>) msg.obj;

                            mList = response.body().Data;
                            if(mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<LEDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }



}
