package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
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
    public static final int REQUEST_CODE = 1555;

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
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        gridView = findViewById(R.id.gridView);
        gridView.setOnItemClickListener((parent, view, position, id) -> onClose(position));

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();

        mAdapter = new AddServiceAdapter(mContext, mList);
        gridView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestSVC_SELECT();
    }

    private void onClose(int position) {

        System.out.println("onclose######" + position);

    }

    public void requestSVC_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        openLoadingBar();

        String SVC_03 = etSearch.getText().toString();

        Call<SVC_Model> call = Http.svc(HttpBaseService.TYPE.POST).SVC_SELECT(
                BaseConst.URL_HOST,
                "LIST_POP",
                "",
                "C200100001",
                SVC_03,
                "",
                "",
                "",
                ""
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
                            closeLoadingBar();

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
                closeLoadingBar();

            }
        });

    }
}
