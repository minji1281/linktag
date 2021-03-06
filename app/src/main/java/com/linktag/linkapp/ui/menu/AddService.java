package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

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

public class AddService extends BaseActivity {
    //===================================
    // Layout
    //===================================
    private BaseHeader header;
    private EditText etSearch;
    private ListView listview;
    private ImageView btnSearch;

    //===================================
    // Variable
    //===================================
    private AddServiceAdapter mAdapter;
    private ArrayList<SvcVO> mList;

    private String GUBUN;
    private String CTM_01;
    //private String contractType;

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
        header.layoutHeader.setBackgroundColor(getResources().getColor(R.color.header_background));
        header.headerSpacer.setVisibility(View.INVISIBLE);
        header.tvHeaderTitle.setTextColor(Color.WHITE);
        header.btnHeaderLeft.setSelected(true);

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

        listview = findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSelect(position);
            }
        });
    }

    @Override
    protected void initialize() {
        //contractType = getIntent().getStringExtra("contractType");

        //header.tvHeaderTitle.setText(contractType.equals("P") ? "서비스 추가" : "공유 서비스 추가");

        if(getIntent().hasExtra("CTM_01"))
            CTM_01 = getIntent().getStringExtra("CTM_01");
        else
            CTM_01 = mUser.Value.CTM_01;

        mList = new ArrayList<>();
        mAdapter = new AddServiceAdapter(mContext, mList);
        listview.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestSVC_SELECT();
    }

    private void onSelect(int position) {
        if(mList.get(position).SVC_CHK.equals("N")){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage("[" + mList.get(position).SVC_03 + "] " + getString(R.string.alert_service_add));
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.common_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //GUBUN = contractType.equals("P") ? "INSERT" : "INSERT_SHARED";
                    GUBUN = "INSERT";
                    requestCTD_CONTROL(mList.get(position).SVC_02);
                }
            });
            builder.setNegativeButton(R.string.common_no, new DialogInterface.OnClickListener() {
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

        //String GUB = contractType.equals("P") ? "LIST_SERVICE" : "LIST";
        String GUB = "LIST_SERVICE";
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
                "P",
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

//                            if(contractType.equals("P"))
//                                requestSVC_SELECT();
//                            else
//                                mActivity.finish();

                            requestSVC_SELECT();
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
