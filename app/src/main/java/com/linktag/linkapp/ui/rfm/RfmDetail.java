package com.linktag.linkapp.ui.rfm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CDS_Model;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.RfmVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RfmDetail extends BaseActivity {

    private BaseHeader header;

    private EditText ed_name;
    private EditText ed_memo;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private Button bt_save;

    private RfmVO rfmVO;


    private CtdVO intentVO;

    private String scanCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rfm);


        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
            scanCode = getIntent().getStringExtra("scanCode");
        }

    }

    public void requestRFM_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(RfmDetail.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<RFMModel> call = Http.rfm(HttpBaseService.TYPE.POST).RFM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                rfmVO.RFM_ID,
                rfmVO.RFM_01,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                rfmVO.RFM_97,
                rfmVO.RFM_98
        );


        call.enqueue(new Callback<RFMModel>() {
            @Override
            public void onResponse(Call<RFMModel> call, Response<RFMModel> response) {

                if(ed_name.getText().equals("")){
                    Toast.makeText(mContext,getString(R.string.validation_check1),Toast.LENGTH_LONG).show();
                    return;
                }

                if(GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")){
//                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, rfmVO.RFM_01);
//                    ctds_control.requestCTDS_CONTROL();

                    RfmMain.RFM_01 = rfmVO.RFM_01;
                    Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + "냉장고정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }

            @Override
            public void onFailure(Call<RFMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    private void requestCDS_CONTROL(String GUBUN, String CTD_07, String scanCode, String CDS_03, String CTD_01, String CTD_02, String CTD_09, String OCM_01){
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<CDS_Model> call = Http.cds(HttpBaseService.TYPE.POST).CDS_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CTD_07,
                scanCode,
                CDS_03,
                CTD_01,
                CTD_02,
                CTD_09,
                OCM_01
        );

        call.enqueue(new Callback<CDS_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CDS_Model> call, Response<CDS_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            Response<CDS_Model> response = (Response<CDS_Model>) msg.obj;

                            if(GUBUN.equals("INSERT")){
                                rfmVO.RFM_01 = response.body().Data.get(0).CDS_03;
                                requestRFM_CONTROL("INSERT");
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CDS_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                Toast.makeText(mContext, R.string.common_exception, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void initLayout() {

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_memo = (EditText) findViewById(R.id.ed_memo);
        bt_save = (Button) findViewById(R.id.bt_save);

        rfmVO = (RfmVO) getIntent().getSerializableExtra("RfmVO");



        ed_name.setText(rfmVO.getRFM_02());

        ed_memo.setText(rfmVO.getRFM_03());

    }

    @Override
    protected void initialize() {

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (scanCode != null) {
                    requestCDS_CONTROL(
                            "INSERT",
                            intentVO.CTD_07,
                            scanCode,
                            "",
                            intentVO.CTD_01,
                            intentVO.CTD_02,
                            intentVO.CTD_09,
                            mUser.Value.OCM_01);
                } else {
                    requestRFM_CONTROL("UPDATE");
                }


            }
        });

        header.btnHeaderRight1.setVisibility((View.VISIBLE));
        header.btnHeaderRight1.setMaxWidth(50);
        header.btnHeaderRight1.setMaxHeight(50);
        header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
        header.btnHeaderRight1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setMessage("해당 정보를 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestRFM_CONTROL("DELETE");
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
        });

    }

}
