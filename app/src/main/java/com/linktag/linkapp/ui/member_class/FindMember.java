package com.linktag.linkapp.ui.member_class;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CDOModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CDO_VO;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindMember extends BaseActivity {
    public static final int REQUEST_CODE = 1555;

    //===================================
    // Layout
    //===================================
    private BaseHeader header;
    private EditText etSearch;
    private ListView listView;
    private ImageView btnSearch;

    //===================================
    // Variable
    //===================================
    private FindMemberAdapter mAdapter;
    private ArrayList<CDO_VO> mList;

    //===================================
    // Initialize
    //===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_member);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> requestEMP_SELECT());
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
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view, position, id) -> onClose(position));

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();

        mAdapter = new FindMemberAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestEMP_SELECT();
    }

    private void onClose(int position) {
        Intent intent = new Intent();
        intent.putExtra("CDO_02", mList.get(position).CDO_02);
        intent.putExtra("CDO_04", mList.get(position).CDO_04);
        setResult(RESULT_OK, intent);

        this.finish();
    }

    public void requestEMP_SELECT() {
        // 인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(getString(R.string.common_network_error));
//            return;
//        }
//
//        openLoadingBar();
//
//        String CDO_03 = etSearch.getText().toString();
//
//        Call<CDOModel> call = Http.member(HttpBaseService.TYPE.POST).EMP_SELECT(
//                BaseConst.URL_HOST,
//                "LIST",
//                mUser.Value.OCP_ID,
//                "EMP",
//                "",
//                CDO_03,
//                "",
//                "",
//                "",
//                ""
//        );
//
//        call.enqueue(new Callback<CDOModel>() {
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<CDOModel> call, Response<CDOModel> response) {
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
//                            Response<CDOModel> response = (Response<CDOModel>) msg.obj;
//
//                            mList = response.body().Data;
//                            if(mList == null)
//                                mList = new ArrayList<>();
//
//                            mAdapter.updateData(mList);
//                            mAdapter.notifyDataSetChanged();
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<CDOModel> call, Throwable t) {
//                Log.d("Test", t.getMessage());
//                closeLoadingBar();
//
//            }
//        });

    }
}
