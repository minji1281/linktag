package com.linktag.linkapp.ui.car;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CARModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.CAR_VO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private EditText etName;
    private EditText etCarNum;
    private EditText etModel;
    private EditText etBuyDay;
    private EditText etFuel;
    private EditText etMemo;

    private Button btnSave;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    private EditText etFocusDay;
    private CAR_VO CAR;
    private String gubun;
    private String CAR_01;
    private String CTM_01;
    private String CTD_02;


    Calendar CAR_05_Calendar = Calendar.getInstance(); //구매일자
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    DatePickerDialog.OnDateSetListener CAR_05_DatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            CAR_05_Calendar.set(Calendar.YEAR, year);
            CAR_05_Calendar.set(Calendar.MONTH, month);
            CAR_05_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etFocusDay.setText(sdf.format(CAR_05_Calendar.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        if (getIntent().hasExtra("CAR_01")) {
            CAR_01 = getIntent().getStringExtra("CAR_01");
            CTM_01 = getIntent().getStringExtra("CTM_01");
            CTD_02 = getIntent().getStringExtra("CTD_02");
        }

        if(getIntent().hasExtra("CAR")){
            CAR = (CAR_VO) getIntent().getSerializableExtra("CAR");
            gubun = "UPDATE";
        }
        else{
            CAR = new CAR_VO();
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
            if(CAR.CAR_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setMaxWidth(50);
                header.btnHeaderRight1.setMaxHeight(50);
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage("해당 차량의 모든 내역이 함께 삭제됩니다.\n삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestCAR_CONTROL("DELETE");
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
        etCarNum = (EditText) findViewById(R.id.etCarNum);
        etModel = (EditText) findViewById(R.id.etModel);
        etBuyDay = (EditText) findViewById(R.id.etBuyDay);
        etBuyDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFocusDay = (EditText) findViewById(R.id.etBuyDay);
                new DatePickerDialog(mContext, CAR_05_DatePicker, CAR_05_Calendar.get(Calendar.YEAR), CAR_05_Calendar.get(Calendar.MONTH), CAR_05_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etFuel = (EditText) findViewById(R.id.etFuel);
        etMemo = (EditText) findViewById(R.id.etMemo);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                requestCAR_CONTROL(gubun);
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
        etName.setText(CAR.CAR_02);
        etCarNum.setText(CAR.CAR_03);
        etModel.setText(CAR.CAR_04);
        if(!CAR.CAR_05.equals("")){
            etBuyDay.setText(sDateFormat(CAR.CAR_05));
            CAR_05_Calendar.set(Integer.parseInt(CAR.CAR_05.substring(0,4)), Integer.parseInt(CAR.CAR_05.substring(4,6))-1, Integer.parseInt(CAR.CAR_05.substring(6,8)));
        }
        etFuel.setText(CAR.CAR_06);
        etMemo.setText(CAR.CAR_07);
    }

    private void requestCAR_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String CAR_ID = getIntent().getStringExtra("CTN_02"); //컨테이너
        if(gubun.equals("UPDATE")){
            CAR_01 = CAR.CAR_01;
        }
        String CAR_02 = etName.getText().toString(); //명칭
        String CAR_03 = etCarNum.getText().toString(); //차량번호
        String CAR_04 = etModel.getText().toString(); //연식
        String CAR_05 = etBuyDay.getText().toString().replace("-", ""); //구매일자
        String CAR_06 = etFuel.getText().toString(); //연료
        String CAR_07 = etMemo.getText().toString(); //메모
        String CAR_98 = mUser.Value.OCM_01; //최종수정자

        Call<CARModel> call = Http.car(HttpBaseService.TYPE.POST).CAR_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CAR_ID,
                CAR_01,
                CAR_02,
                CAR_03,

                CAR_04,
                CAR_05,
                CAR_06,
                CAR_07,
                CAR_98
        );

        call.enqueue(new Callback<CARModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CARModel> call, Response<CARModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                if (gubun.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, CTM_01, CTD_02, CAR_01);
                    ctds_control.requestCTDS_CONTROL();
                    CadList.CAD_01 = CAR_01;
                }

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<CARModel> response = (Response<CARModel>) msg.obj;

                            finish();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CARModel> call, Throwable t){
                Log.d("CAR_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "-" + sDate.substring(4,6) + "-" + sDate.substring(6,8);

        return result;
    }

}
