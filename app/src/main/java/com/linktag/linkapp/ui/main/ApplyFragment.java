package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.apply_class.ApplyStateAdapter;
import com.linktag.linkapp.ui.apply_class.ApplyUpdateActivity;
import com.linktag.linkapp.value_object.LED_VO;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyFragment extends BaseFragment implements ApplyStateAdapter.ApplyBtnClickListener {
    //===================================
    // Layout//===================================
    private BaseHeader header;
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    //===================================
    // Variable
    //===================================
    private ApplyStateAdapter mAdapter;
    private ArrayList<LED_VO> mList;

    public ApplyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_apply, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        requestBVA_SELECT();

    }

    private void initLayout() {
        listView = view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ApplyUpdateActivity.class);
                intent.putExtra("LED_02", mList.get(position).LED_02);
                mContext.startActivity(intent);
            }
        });


        header = mActivity.findViewById(R.id.header);
        header.btnHeaderText.setOnClickListener(v -> goApply());

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestBVA_SELECT());
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new ApplyStateAdapter(mContext, mList, "", this);
        listView.setAdapter(mAdapter);
    }

    public void goApply(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("생성할 항목을 선택해 주세요.");
        builder.setCancelable(true);
        builder.setPositiveButton("연장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(mContext, ApplyUpdateActivity.class);
                intent.putExtra("LED_02", "");
                intent.putExtra("LED_05", "2");
                mActivity.startActivity(intent);
                //mActivity.startActivityForResult(intent, ApplyUpdateActivity.REQUEST_CODE);
            }
        });
        builder.setNegativeButton("연차", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(mContext, ApplyUpdateActivity.class);
                intent.putExtra("LED_02", "");
                intent.putExtra("LED_05", "1");
                mActivity.startActivity(intent);
                //mActivity.startActivityForResult(intent, ApplyUpdateActivity.REQUEST_CODE);
            }
        });
        builder.create().show();

    }

    public void requestBVA_SELECT() {
        // 인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(getString(R.string.common_network_error));
//            return;
//        }
//
//        //openLoadingBar();
//
//        String strToday = ClsDateTime.getNow("yyyyMMdd");
//
//        Call<LEDModel> call = Http.apply(HttpBaseService.TYPE.POST).BVA_SELECT(
//                BaseConst.URL_HOST,
//                "LIST",
//                mUser.Value.OCP_ID,
//                "BVA",
//                "",
//                "",
//                mUser.Value.OCM_01,
//                "",
//                "",
//
//                "N",
//                strToday,
//                strToday,
//                ""
//        );
//
//        call.enqueue(new Callback<LEDModel>() {
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<LEDModel> call, Response<LEDModel> response) {
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
//                            if(mList == null)
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
//            public void onFailure(Call<LEDModel> call, Throwable t) {
//                Log.d("Test", t.getMessage());
//                closeLoadingBar();
//
//            }
//        });

    }

    private void requestBVA_CONTROL(String GUB, String LED_02s, String LED_19s) {
        // 인터넷 연결 여부 확인
//        if (!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(mContext.getString(R.string.common_network_error));
//            return;
//        }
//
//        String GUBUN = GUB;
//        String LED_ID = mUser.Value.OCP_ID;
//        String LED_01 = "BVA";
//        String LED_02 = LED_02s;
//        String LED_03 = "";
//        String LED_04 = "";
//        String LED_05 = "";
//        String LED_06 = "";
//        double LED_11 = 0;
//        double LED_12 = 0;
//        String LED_19 = LED_19s;
//        String LED_23 = "";
//        String LED_24 = "";
//        String LED_97 = "";
//        String LED_98 = mUser.Value.OCM_01;
//
//        Call<LEDModel> call = Http.apply(HttpBaseService.TYPE.POST).BVA_CONTROL(
//                BaseConst.URL_HOST,
//                GUBUN,
//                LED_ID,
//                LED_01,
//                LED_02,
//                LED_03,
//                LED_04,
//                LED_05,
//                LED_06,
//                LED_11,
//                LED_12,
//                LED_19,
//                LED_23,
//                LED_24,
//                LED_97,
//                LED_98
//        );
//
//        call.enqueue(new Callback<LEDModel>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<LEDModel> call, Response<LEDModel> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if (msg.what == 100){
//
//                            Response<LEDModel> response = (Response<LEDModel>) msg.obj;
//
//                            if(response.body().Data.get(0).Validation){
//                                onResume();
//                            } else {
//                                Toast.makeText(mContext, R.string.login_err, Toast.LENGTH_LONG);
//                            }
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<LEDModel> call, Throwable t){
//                Log.d("Test", t.getMessage());
//
//            }
//        });

    }

    @Override
    public void onListBtnClick(String btnName, int position) {
        String btn1 = "APPROVE";
        String btn2 = "RETURN";
        LED_VO data = mList.get(position);
        String nm = data.LED_05.equals("1") ? "연차" : "연장";

        if (btnName.equals(btn1)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage("해당 " + nm + "을/를 승인하시겠습니까?");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            requestBVA_CONTROL("UPDATE19", data.LED_02, "Y");
                        }
                    });
            builder.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();

        } else if (btnName.equals(btn2)){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage("해당 " + nm + "을/를 반려하시겠습니까?");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            requestBVA_CONTROL("UPDATE19", data.LED_02, "R");
                        }
                    });
            builder.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }

    }
}
