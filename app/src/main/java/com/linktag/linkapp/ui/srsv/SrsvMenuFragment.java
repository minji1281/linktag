package com.linktag.linkapp.ui.srsv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.SIFModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.SIF_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SrsvMenuFragment extends BaseFragment {

    private String GUBUN;

    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView tvEmpty;

    private SifRecycleAdapter_horizontal mAdapter;
    private ArrayList<SIF_VO> mList;


    public SrsvMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_srsv_menu, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        requestSIF_SELECT();
    }

    private void initLayout() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);

    }

    protected void initialize(){
        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SifRecycleAdapter_horizontal(mContext, mList);
        recyclerView.setAdapter(mAdapter);
    }

    public void setGUBUN(String GUB){
        this.GUBUN = GUB;
    }

    private void requestSIF_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String SIF_02 = mUser.Value.OCM_01; //사용자코드

        Call<SIFModel> call = Http.sif(HttpBaseService.TYPE.POST).SIF_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                "",
                SIF_02,
                "",
                "",

                0,
                0,
                "",
                "",
                ""
        );

        call.enqueue(new Callback<SIFModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<SIFModel> call, Response<SIFModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<SIFModel> response = (Response<SIFModel>) msg.obj;

                            mList = response.body().Data;
                            if(mList == null)
                                mList = new ArrayList<>();

                            if(mList.size() > 0){
                                tvEmpty.setVisibility(View.GONE);
                            }
                            else{
                                tvEmpty.setVisibility(View.VISIBLE);
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<SIFModel> call, Throwable t){
                Log.d("SIF_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

}
