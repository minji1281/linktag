package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharedFragment extends BaseFragment implements SharedAdapter.SharedBtnClickListener {

    private View view;
    private GridView gridView;
    private TextView tvAddShared;


    private SharedAdapter mAdapter;
    private ArrayList<CtdVO> mList;

    public SharedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_shared, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        requestCTD_SELECT();
    }

    private void initLayout() {
        gridView = view.findViewById(R.id.gridView);
        tvAddShared = view.findViewById(R.id.tvAddShared);
        tvAddShared.setOnClickListener(v -> goAddService());
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new SharedAdapter(mContext, mList, this);
        gridView.setAdapter(mAdapter);
    }

    private void goAddService(){
        Intent intent = new Intent(mContext, AddService.class);
        mContext.startActivity(intent);
    }

    public void requestCTD_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext )){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "LIST_SHARED",
                "",
                "",
                mUser.Value.OCM_01
        );

        call.enqueue(new Callback<CTD_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTD_Model> call, Response<CTD_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

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
            public void onFailure(Call<CTD_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    @Override
    public void onGridBtnClick(int position) {
        //mActivity.finish();

        // 누르면 해당 서비스로 이동하게 구현
        // mList.get(position)
        String SVC_01 = mList.get(position).SVC_01;
        String CTN_02 = mList.get(position).CTN_02;

        ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, SVC_01, CTN_02);
        changeActivityCls.changeService();

        mActivity.finish();
    }
}
