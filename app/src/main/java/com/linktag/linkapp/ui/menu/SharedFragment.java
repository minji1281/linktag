package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.base_header.BaseHeader;
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

public class SharedFragment extends BaseFragment {
    private final String CHOOSE_TYPE_SCAN = "SCAN";
    private final String CHOOSE_TYPE_BMK = "BMK";

    private String GUBUN;

    private BaseHeader header;
    private View view;
    private GridView gridView;
    private LinearLayout layAdd;

    private SharedAdapter mAdapter;
    private ArrayList<CtdVO> mList;

    private String activity_name;

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
        header = mActivity.findViewById(R.id.header);

        layAdd = view.findViewById(R.id.layAdd);
        layAdd.setOnClickListener(v -> goAdd());

        gridView = view.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onGridClick(position);
            }
        });

        activity_name = mActivity.getClass().getSimpleName();

        if(activity_name.equals("ChooseOne")){
            header.btnHeaderText.setVisibility(View.GONE);
            layAdd.setVisibility(View.GONE);

            GUBUN = "LIST_BOOKMARK2";
        } else {
            GUBUN = "LIST_SHARED";
        }
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new SharedAdapter(mContext, mList);
        gridView.setAdapter(mAdapter);
    }

    public void goAdd(){
        Intent intent = new Intent(mContext, AddShared.class);
        //intent.putExtra("contractType", "S");
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
                GUBUN,
                "",
                "",
                mUser.Value.OCM_01,
                "S"
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

    private void onGridClick(int position) {
        // 누르면 해당 서비스로 이동하게 구현
        // mList.get(position)

        if(activity_name.equals("ChooseOne")){
            // 해당 서비스 NEW 이동
            String type;
            String scanCode;

            Bundle bundle = getArguments();

            if(bundle != null){
                type =  bundle.getString("type");
                scanCode = bundle.getString("scanCode");

                if(type.equals(CHOOSE_TYPE_SCAN)){
                    ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, mList.get(position));
                    changeActivityCls.changeServiceWithScan(scanCode);

                    closeLoadingBar();
                    mActivity.finish();
                } else {
                    // 북마크 등록
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("선택한 서비스를 즐겨찾기 하시겠습니까?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            requestBMK_CONTROL(mList.get(position));
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.create().show();
                }
            }
        } else {
            ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, mList.get(position));
            changeActivityCls.changeService();

            //mActivity.finish();
        }
    }

    private void requestBMK_CONTROL(CtdVO ctdVO) {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext )){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.bmk(HttpBaseService.TYPE.POST).BMK_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                ctdVO.CTD_01,
                ctdVO.CTD_02,
                mUser.Value.OCM_01,
                "1",
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
                            //closeLoadingBar();

                            requestCTD_SELECT();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CTD_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                //closeLoadingBar();

            }
        });

    }

}
