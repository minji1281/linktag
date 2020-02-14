package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.SVC_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.SvcVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberInvite extends BaseActivity {
    //===================================
    // Layout
    //===================================
    private BaseHeader header;
    private EditText etSearch;
    private GridView gridView;
    private ImageView btnSearch;

    //===================================
    // Variable
    //===================================
    private AddServiceAdapter mAdapter;
    private ArrayList<SvcVO> mList;

    private String GUBUN;
    private String CTM_01;
    private String contractType;

    //===================================
    // Initialize
    //===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> requestSVC_SELECT());
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    requestSVC_SELECT();
                    etSearch.requestFocus();
                    return true;
                }
                return false;
            }
        });

        gridView = findViewById(R.id.gridView);
        gridView.setOnItemClickListener((parent, view, position, id) -> onSelect(position));
    }

    @Override
    protected void initialize() {
        contractType = getIntent().getStringExtra("contractType");

        header.tvHeaderTitle.setText(contractType.equals("P") ? "서비스 추가" : "공유 서비스 추가");

        String CTM_01s = getIntent().getStringExtra("CTM_01");

        CTM_01 = CTM_01s == null || CTM_01s.equals("") ? mUser.Value.CTM_01 : CTM_01s;

        mList = new ArrayList<>();
        mAdapter = new AddServiceAdapter(mContext, mList);
        gridView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestSVC_SELECT();
    }

    private void onSelect(int position) {
        if(mList.get(position).SVC_CHK.equals("N")){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(mList.get(position).SVC_03 + " 을/를 추가하시겠습니까?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    GUBUN = contractType.equals("P") ? "INSERT" : "INSERT_SHARED";
                    requestCTD_CONTROL(mList.get(position).SVC_02);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.create().show();
        }

    }

    public void requestSVC_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        String GUB = contractType.equals("P") ? "LIST_SERVICE" : "LIST";
        String SVC_03 = etSearch.getText().toString();

        Call<SVC_Model> call = Http.svc(HttpBaseService.TYPE.POST).SVC_SELECT(
                BaseConst.URL_HOST,
                GUB,
                "",
                CTM_01,
                SVC_03,
                "",
                "",
                "",
                "Y"
        );

        call.enqueue(new Callback<SVC_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<SVC_Model> call, Response<SVC_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            //closeLoadingBar();

                            Response<SVC_Model> response = (Response<SVC_Model>) msg.obj;

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
            public void onFailure(Call<SVC_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                //closeLoadingBar();

            }
        });

    }

    public void requestCTD_CONTROL(String SVC_02) {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CTM_01,
                SVC_02,
                "1",
                "1",
                "3",
                0,
                mUser.Value.OCM_01,
                "",
                "",
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
                            //closeLoadingBar();

                            //Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

                            if(contractType.equals("P"))
                                requestSVC_SELECT();
                            else
                                mActivity.finish();
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
