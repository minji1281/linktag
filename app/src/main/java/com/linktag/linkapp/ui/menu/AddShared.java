package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.SvcVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddShared extends BaseActivity {
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
    private AddSharedAdapter mAdapter;
    private ArrayList<SvcVO> mList;

    private String CTM_01;

    //===================================
    // Initialize
    //===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared);

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

        gridView = findViewById(R.id.gridView);
        gridView.setOnItemClickListener((parent, view, position, id) -> goDetail(position));
    }

    @Override
    protected void initialize() {
        if(getIntent().hasExtra("CTM_01"))
            CTM_01 = getIntent().getStringExtra("CTM_01");
        else
            CTM_01 = mUser.Value.CTM_01;

        mList = new ArrayList<>();
        mAdapter = new AddSharedAdapter(mContext, mList);
        gridView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestSVC_SELECT();
    }

    private void goDetail(int position) {
        CtdVO ctdVO = new CtdVO();
        ctdVO.CTD_01 = "";
        ctdVO.SVC_01 = mList.get(position).SVC_01;
        ctdVO.CTD_02 = mList.get(position).SVC_02;
        ctdVO.CTD_02_NM = mList.get(position).SVC_03;
        ctdVO.CTD_08 = "";

        Intent intent = new Intent(mContext, AddSharedDetail.class);
        intent.putExtra("type", "INSERT");
        intent.putExtra("intentVO", ctdVO);
        mContext.startActivity(intent);
    }

    public void requestSVC_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        String SVC_03 = etSearch.getText().toString();

        Call<SVC_Model> call = Http.svc(HttpBaseService.TYPE.POST).SVC_SELECT(
                BaseConst.URL_HOST,
                "LIST",
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

}
