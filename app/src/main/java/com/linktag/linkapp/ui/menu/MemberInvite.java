package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
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
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.model.SVC_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.CtuVO;
import com.linktag.linkapp.value_object.OcmVO;
import com.linktag.linkapp.value_object.SvcVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberInvite extends BaseActivity {
    public static int REQUEST_CODE = 5331;

    private int STATE = 0;
    private int STATE_NONE = 0;
    private int STATE_EMAIL = 1;
    private int STATE_SHARED = 2;
    //===================================
    // Layout
    //===================================
    private BaseHeader header;

    private LinearLayout selectTypeEmail;
    private LinearLayout selectTypeShared;

    private LinearLayout laySelectType;
    private LinearLayout layEmail;
    private LinearLayout layShared;

    private Spinner spinnerShared;

    private ArrayList<ClsShared> sharedList;
    private String[] ar;

    private EditText etSearch1;
    private EditText etSearch2;

    private TextView tvShared;
    private ImageView btnSearch;
    private ListView listview;
    private ArrayList<OcmVO> mList;
    private MemberAdapter mAdapter;
    private TextView emptyText;


    //===================================
    // Initialize
    //===================================
    private CtdVO intentVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_member_invite);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        spinnerShared = findViewById(R.id.spinnerShared);

        laySelectType = findViewById(R.id.laySelectType);
        layEmail = findViewById(R.id.layEmail);
        layShared = findViewById(R.id.layShared);

        selectTypeEmail = findViewById(R.id.selectTypeEmail);
        selectTypeEmail.setOnClickListener(v -> changeState(STATE_EMAIL));
        selectTypeShared = findViewById(R.id.selectTypeShared);
        selectTypeShared.setOnClickListener(v -> changeState(STATE_SHARED));

        etSearch1 = findViewById(R.id.etSearch1);

        etSearch2 = findViewById(R.id.etSearch2);
        etSearch2.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    requestOCM_SELECT();
                    return true;
                }
                return false;
            }
        });
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> requestOCM_SELECT());

        listview = findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int spinnerPosition = spinnerShared.getSelectedItemPosition();
                if(spinnerPosition == 0){
                    Toast.makeText(mContext, "사용자를 추가 할 공유를 선택해 주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    new AlertDialog.Builder(mActivity)
                            .setMessage("해당 유저를 등록 하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestCTU_CONTROL(mList.get(position));

                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                }
            }
        });

        tvShared = findViewById(R.id.tvShared);

        emptyText = findViewById(R.id.empty);
        listview.setEmptyView(emptyText);
    }

    @Override
    protected void initialize() {
        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        tvShared.setText(intentVO.CTM_17);

        sharedList = new ArrayList<>();

        mList = new ArrayList<>();
        mAdapter = new MemberAdapter(mContext, mList);
        listview.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void changeState(int state){
        STATE = state;

        if(state == STATE_NONE){
            laySelectType.setVisibility(View.VISIBLE);
            layEmail.setVisibility(View.GONE);
            layShared.setVisibility(View.GONE);

        } else if(state == STATE_EMAIL) {
            laySelectType.setVisibility(View.GONE);
            layEmail.setVisibility(View.VISIBLE);
            layShared.setVisibility(View.GONE);

        } else if(state == STATE_SHARED) {
            requestCTD_SELECT();

            laySelectType.setVisibility(View.GONE);
            layEmail.setVisibility(View.GONE);
            layShared.setVisibility(View.VISIBLE);

        }

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
                "LIST_SHARED",
                "",
                "",
                mUser.Value.OCM_01,
                ""
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

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

                            setSpinner(response.body());
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

    private void setSpinner(CTD_Model model){
        sharedList.clear();

        ar = new String[model.Total + 1];
        ar[0] = "공유 선택";

        sharedList.add(new ClsShared("공유 선택", "", ""));

        if(model.Total != 0){
            if(model.Total > 0){
                for (int i=1; i<model.Total + 1; i++){
                    sharedList.add(new ClsShared(model.Data.get(i - 1).CTD_02_NM, model.Data.get(i - 1).CTD_01, model.Data.get(i - 1).CTD_02));

                    ar[i] = model.Data.get(i - 1).CTD_02_NM + "[" +  model.Data.get(i - 1).CTM_17 + "]";
                }
            }
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, ar);

        spinnerShared.setAdapter(adapter);

        spinnerShared.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                requestOCM_SELECT();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void requestOCM_SELECT(){
        int position = spinnerShared.getSelectedItemPosition();
        String CTM_01s = sharedList.get(position).getContract();
        String OCM_02 = etSearch2.getText().toString();

        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<OCM_Model> call = Http.ocm(HttpBaseService.TYPE.POST).OCM_SELECT(
                BaseConst.URL_HOST,
                "LIST_INVITE",
                mUser.Value.OCM_01,
                OCM_02,
                "",
                CTM_01s
        );

        call.enqueue(new Callback<OCM_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){

                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;

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
            public void onFailure(Call<OCM_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });
    }

    private void requestCTU_CONTROL(OcmVO ocmVO){
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTU_Model> call = Http.ctu(HttpBaseService.TYPE.POST).CTU_CONTROL(
                BaseConst.URL_HOST,
                "INSERT_SHARED",
                intentVO.CTM_01,
                intentVO.CTD_02,
                ocmVO.OCM_01,
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

                            callBack(response.body().Data.get(0));

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

    private void callBack(CtuVO ctuVO){
        if(ctuVO.Validation){
            Toast.makeText(mContext, "등록 되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "이미 등록 된 사용자 입니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
