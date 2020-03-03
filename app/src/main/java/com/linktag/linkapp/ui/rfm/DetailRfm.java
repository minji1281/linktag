package com.linktag.linkapp.ui.rfm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm.AlarmDialog;
import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.RfmVO;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRfm extends BaseActivity {

    private BaseHeader header;

    private EditText ed_name;
    private EditText ed_memo;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private Button bt_save;

    private RfmVO rfmVO;

    private String CTM_01;
    private String CTD_02;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rfm);


        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            CTM_01 = getIntent().getStringExtra("CTM_01");
            CTD_02 = getIntent().getStringExtra("CTD_02");
        }

    }

    public void requestRFM_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailRfm.this)) {
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

                if(GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")){
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, CTM_01, CTD_02, rfmVO.RFM_01);
                    ctds_control.requestCTDS_CONTROL();

                    RFMMain.RFM_01 = rfmVO.RFM_01;
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

        //명칭은 읽기전용으로 일단은...
        //ed_name.setEnabled(false);

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
                if (getIntent().hasExtra("scanCode")) {
                    requestRFM_CONTROL("INSERT");
                }else{
                    requestRFM_CONTROL("UPDATE");
                }

            }
        });

        header.btnHeaderRight1.setVisibility((View.VISIBLE));
        header.btnHeaderRight1.setMaxWidth(50);
        header.btnHeaderRight1.setMaxHeight(50);
        header.btnHeaderRight1.setImageResource(R.drawable.delete); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
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
