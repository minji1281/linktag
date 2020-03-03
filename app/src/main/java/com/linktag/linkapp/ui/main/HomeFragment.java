package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.base.util.ExpandableHeightGridView;
import com.linktag.linkapp.ui.menu.ChangeActivityCls;
import com.linktag.linkapp.ui.menu.ServiceAdapter;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {
    private int BMK_STATE_NORMAL = 0;
    private int BMK_STATE_DELETE = 1;

    //===================================
    // Layout//===================================
    private View view;

    private ExpandableHeightGridView gridBMK;
    private TextView btnBmkAdd;
    private TextView btnBmkDelete;
    private TextView btnBmkCancel;
    private TextView BmkSpacer;

    private ServiceAdapter mAdapter;
    private ArrayList<CtdVO> mList;

    //===================================
    // Variable
    //===================================
    private int BMK_STATE;

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

        BMK_STATE = BMK_STATE_NORMAL;

        requestCTD_SELECT();
    }

    private void initLayout() {
        gridBMK = (ExpandableHeightGridView) view.findViewById(R.id.gridBMK);
        gridBMK.setExpanded(true);
        gridBMK.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onBmkGridClick(position);
            }
        });

        btnBmkAdd = view.findViewById(R.id.btnBmkAdd);
        //btnBmkAdd.setOnClickListener(v -> go);
        btnBmkDelete = view.findViewById(R.id.btnBmkDelete);
        btnBmkDelete.setOnClickListener(v -> setBmkState(BMK_STATE_DELETE));
        btnBmkCancel = view.findViewById(R.id.btnBmkCancel);
        btnBmkCancel.setOnClickListener(v -> setBmkState(BMK_STATE_NORMAL));
        BmkSpacer = view.findViewById(R.id.BmkSpacer);
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new ServiceAdapter(mContext, mList);
        gridBMK.setAdapter(mAdapter);
    }


    public void requestCTD_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "LIST_BOOKMARK",
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

    private void setBmkState(int setBmkState){
        if(mList.size() > 0){
            BMK_STATE = setBmkState;

           if(setBmkState == BMK_STATE_NORMAL){
               mAdapter.setDelete(false);

               btnBmkCancel.setVisibility(View.GONE);
               btnBmkDelete.setVisibility(View.VISIBLE);
               btnBmkAdd.setVisibility(View.VISIBLE);
               BmkSpacer.setVisibility(View.VISIBLE);

           } else if(setBmkState == BMK_STATE_DELETE) {
               mAdapter.setDelete(true);

               btnBmkCancel.setVisibility(View.VISIBLE);
               btnBmkDelete.setVisibility(View.GONE);
               btnBmkAdd.setVisibility(View.GONE);
               BmkSpacer.setVisibility(View.GONE);

           }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onBmkGridClick(int position) {
        if(BMK_STATE == BMK_STATE_NORMAL){
            ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, mList.get(position));
            changeActivityCls.changeService();
        } else if (BMK_STATE == BMK_STATE_DELETE){
            System.out.println("DELETE :" + position);
        }
    }

}
