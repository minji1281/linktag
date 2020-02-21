package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.COSModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.COS_VO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CosDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private EditText etName;
    private EditText etMemo;

    private Button btnSave;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    private COS_VO COS;
    private String gubun;
    private String COS_01;
    private String CTM_01;
    private String CTD_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cos_detail);

        if (getIntent().hasExtra("COS_01")) {
            COS_01 = getIntent().getStringExtra("COS_01");
            CTM_01 = getIntent().getStringExtra("CTM_01");
            CTD_02 = getIntent().getStringExtra("CTD_02");
        }

        if(getIntent().hasExtra("COS")){
            COS = (COS_VO) getIntent().getSerializableExtra("COS");
            gubun = "UPDATE";
        }
        else{
            COS = new COS_VO();
            gubun = "INSERT";
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        if(gubun.equals("UPDATE")){
            if(COS.COS_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setMaxWidth(50);
                header.btnHeaderRight1.setMaxHeight(50);
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage("해당 화장대의 모든 화장품이 함께 삭제됩니다.\n삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestCOS_CONTROL("DELETE");
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

        etName = (EditText) findViewById(R.id.etName);
        etMemo = (EditText) findViewById(R.id.etMemo);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                requestCOS_CONTROL(gubun);
            }
        });

    }

    @Override
    protected void initialize() {
        if(gubun.equals("UPDATE")){
            getDetail();
        }
        else{ //INSERT부분 추가할거있으면 추가!!

        }
    }

    private void getDetail() {
        etName.setText(COS.COS_02);
        etMemo.setText(COS.COS_03);
    }

    private void requestCOS_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String COS_ID = getIntent().getStringExtra("CTN_02"); //컨테이너
        if(gubun.equals("UPDATE")){
            COS_01 = COS.COS_01;
        }
        String COS_02 = etName.getText().toString(); //명칭
        String COS_03 = etMemo.getText().toString(); //메모
        String COS_98 = mUser.Value.OCM_01; //최종수정자

        Call<COSModel> call = Http.cos(HttpBaseService.TYPE.POST).COS_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                COS_ID,
                COS_01,
                COS_02,
                COS_03,

                COS_98
        );

        call.enqueue(new Callback<COSModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<COSModel> call, Response<COSModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                if (gubun.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, CTM_01, CTD_02, COS_01);
                    ctds_control.requestCTDS_CONTROL();
                    CodList.COD_95 = COS_01;
                }

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<COSModel> response = (Response<COSModel>) msg.obj;

                            finish();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<COSModel> call, Throwable t){
                Log.d("COS_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

}
