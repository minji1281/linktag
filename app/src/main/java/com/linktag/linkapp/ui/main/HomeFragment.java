package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CMTModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.dash_state.CommentStateAdapter;
import com.linktag.linkapp.value_object.CMT_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {
    //===================================
    // Layout//===================================
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    //===================================
    // Variable
    //===================================
    private CommentStateAdapter mAdapter;
    private ArrayList<CMT_VO> mList;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

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
       // listView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestCMT_SELECT());
    }
    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new CommentStateAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }


    public void requestCMT_SELECT() {
        System.out.println("$$$$$$$$$$$$$$$됐다 됐다 잘 됐다.");
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

    //    openLoadingBar();



        Call<CMTModel> call = Http.commute(HttpBaseService.TYPE.POST).CMT_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                mUser.Value.CTM_01,
                "",
                "",
                "",
                ""
        );

        call.enqueue(new Callback<CMTModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CMTModel> call, Response<CMTModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<CMTModel> response = (Response<CMTModel>) msg.obj;

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
            public void onFailure(Call<CMTModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


}
