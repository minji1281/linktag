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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.CTU_Model;
import com.linktag.linkapp.model.INV_Model;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.INV_VO;
import com.linktag.linkapp.value_object.OcmVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteList extends BaseActivity implements InviteListAdapter.InviteBtnClickListener {
    //===================================
    // Layout
    //===================================
    private BaseHeader header;

    private ListView listview;
    private TextView emptyText;

    //===================================
    // Variable
    //===================================
    private ArrayList<INV_VO> mList;
    private InviteListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitelist);

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

        listview = findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        emptyText = findViewById(R.id.empty);
        listview.setEmptyView(emptyText);
    }

    @Override
    protected void initialize(){
        mList = new ArrayList<>();
        mAdapter = new InviteListAdapter(mContext, mList, this);
        listview.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestINV_SELECT();
    }

    public void requestINV_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<INV_Model> call = Http.inv(HttpBaseService.TYPE.POST).INV_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                "",
                "",
                mUser.Value.OCM_01
        );

        call.enqueue(new Callback<INV_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<INV_Model> call, Response<INV_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<INV_Model> response = (Response<INV_Model>) msg.obj;

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
            public void onFailure(Call<INV_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    @Override
    public void onListBtnClick(String btnName, int position) {
        String btn1 = "ACCEPT";
        String btn2 = "DENY";
        INV_VO data = mList.get(position);

        if(btnName.equals(btn1)){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(R.string.alert_accept);
            builder.setPositiveButton(R.string.common_yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // CTU_CONTROL INSERT
                            requestCTU_CONTROL(data);
                        }
                    });
            builder.setNegativeButton(R.string.common_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        } else if (btnName.equals(btn2)){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(R.string.alert_deny);
            builder.setPositiveButton(R.string.common_yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // INV_CONTROL DELETE
                            requestINV_CONTROL(data);
                        }
                    });
            builder.setNegativeButton(R.string.common_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }

    }

    private void requestCTU_CONTROL(INV_VO inv_vo){
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTU_Model> call = Http.ctu(HttpBaseService.TYPE.POST).CTU_CONTROL(
                BaseConst.URL_HOST,
                "INSERT_SHARED",
                inv_vo.INV_01,
                inv_vo.INV_02,
                inv_vo.INV_03,
                "N",
                "1",

                "", "", "", 0, 0, 0, 0,
                "",
                mUser.Value.OCM_01
        );

        call.enqueue(new Callback<CTU_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTU_Model> call, Response<CTU_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            Response<CTU_Model> response = (Response<CTU_Model>) msg.obj;

                            if(response.body().Data.get(0).Validation)
                                requestINV_CONTROL(inv_vo);
                            else
                                Toast.makeText(mContext, R.string.alert_member_callback2, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CTU_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });
    }

    private void requestINV_CONTROL(INV_VO inv_vo){
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<INV_Model> call = Http.inv(HttpBaseService.TYPE.POST).INV_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                inv_vo.INV_01,
                inv_vo.INV_02,
                inv_vo.INV_03,
                "",
                "",
                ""
        );

        call.enqueue(new Callback<INV_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<INV_Model> call, Response<INV_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            //Response<INV_Model> response = (Response<INV_Model>) msg.obj;

                            requestINV_SELECT();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<INV_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });
    }
}
