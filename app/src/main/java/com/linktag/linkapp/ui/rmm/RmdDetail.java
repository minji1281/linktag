package com.linktag.linkapp.ui.rmm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CDS_Model;
import com.linktag.linkapp.model.RMDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.RMD_VO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RmdDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private EditText etName;
    private EditText etEquip;

    private ImageView imgReserveDay;

    private TextView tvReserveDay;

    private Button btnSave;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    private CtdVO intentVO;
    private RMD_VO RMD;
    private String GUBUN;
    private String scanCode;
    private String RMR_03 = "";

    Calendar RMR_03_C = Calendar.getInstance(); //예약일자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmd_detail);

//        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(getIntent().hasExtra("RMD")){
            RMD = (RMD_VO) getIntent().getSerializableExtra("RMD");
            GUBUN = "UPDATE";
        }
        else{
            intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
            RMD = new RMD_VO();
            RMD.RMD_ID = intentVO.CTN_02;
            RMD.RMD_01 = intentVO.CTM_01;
            RMD.RMD_02 = getIntent().getStringExtra("scancode");
            scanCode = getIntent().getStringExtra("scancode");
            GUBUN = "INSERT";
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        clearCalTime(RMR_03_C);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        RMR_03 = sdf.format(RMR_03_C.getTime());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etEquip = (EditText) findViewById(R.id.etEquip);

        tvReserveDay = (TextView) findViewById(R.id.tvReserveDay);
        tvReserveDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reserveDayDialog();
            }
        });
        tvReserveDay.setText(sDateFormat(RMR_03));
        imgReserveDay = (ImageView) findViewById(R.id.imgReserveDay);
        imgReserveDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                reserveDayDialog();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(validationCheck()){
                    if (GUBUN.equals("INSERT")) {
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
                        requestRMD_CONTROL(GUBUN);
                    }
                }
            }
        });

        if(GUBUN.equals("UPDATE")){
            if(RMD.RMD_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        deleteDialog();
                    }
                });
            }
        }
        else{ //INSERT
//            imgFilter.setVisibility(View.GONE);
//            lbFilter.setVisibility(View.GONE);
//
//            tvLog.setVisibility(View.GONE);

            getNewData();
        }
    }

    @Override
    protected void initialize() {

        if(GUBUN.equals("UPDATE")){
            getDetail();
        }
    }

    private void getDetail() {
//        RMR_03_C.set(Integer.parseInt(FRM.FRM_03.substring(0,4)), Integer.parseInt(FRM.FRM_03.substring(4,6)) - 1, Integer.parseInt(FRM.FRM_03.substring(6)));

        etName.setText(RMD.RMD_03);
        etEquip.setText(RMD.RMD_04);

    }

    private void getNewData(){
        //초기값

//        int year = FRM_03_C.get(Calendar.YEAR);
//        int month = FRM_03_C.get(Calendar.MONTH) + 1;
//        int day = FRM_03_C.get(Calendar.DATE);
//        FRM.FRM_03 = String.valueOf(year) + (month<10 ? "0" + String.valueOf(month) : String.valueOf(month)) + (day<10 ? "0" + String.valueOf(day) : String.valueOf(day));
    }

    private void requestRMD_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String RMD_ID = RMD.RMD_ID; //컨테이너
        String RMD_01 = RMD.RMD_01; //Master일련번호(RMM_01)
        String RMD_02 = RMD.RMD_02; //일련번호(스캔코드)
        String RMD_03 = etName.getText().toString(); //명칭

        String RMD_04 = etEquip.getText().toString(); //장비
        String RMD_98 = mUser.Value.OCM_01; //최종수정자

        Call<RMDModel> call = Http.rmd(HttpBaseService.TYPE.POST).RMD_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                RMD_ID,
                RMD_01,
                RMD_02,
                RMD_03,

                RMD_04,
                RMD_98
        );

        call.enqueue(new Callback<RMDModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RMDModel> call, Response<RMDModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

//                            if(GUB.equals("INSERT")){
//                                CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, RMD.RMD_02);
//                                ctds_control.requestCTDS_CONTROL();
//                            }

                            Response<RMDModel> response = (Response<RMDModel>) msg.obj;

                            finish();

//                            if(!GUB.equals("DELETE")){
//                                if(FRM.ARM_03.equals("Y")){
//                                    if(GUB.equals("WATER")){
//                                        FRM.FRM_96 = response.body().Data.get(0).FRM_96;
//                                    }
//                                    String NextDay = FRM.FRM_96;
//                                    Toast.makeText(mContext,mContext.getString(R.string.dialog_alarm_toast_text) + " " + NextDay.substring(0,4)+"." + NextDay.substring(4,6)+"."+ NextDay.substring(6,8)+" " +
//                                            NextDay.substring(8,10)+":" + NextDay.substring(10,12), Toast.LENGTH_LONG ).show();
//                                }
//                            }
//
//                            if(GUB.equals("FILTER")){
//                                setUserData(response.body().Data.get(0));
//                            }
//                            else{
//                                finish();
//                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMDModel> call, Throwable t){
                Log.d("RMD_CONTROL", t.getMessage());

                if(GUB.equals("INSERT")){
                    requestCDS_CONTROL(
                            "DELETE",
                            intentVO.CTD_07,
                            scanCode,
                            RMD.RMD_02,
                            "",
                            "",
                            "",
                            "");
                }

//                closeLoadingBar();
            }
        });

    }

//    private void setUserData(RMD_VO rmdVO) {
//        FRM.FRM_03 = frmVO.FRM_03;
//        FRM.FRM_96 = frmVO.FRM_96;
//        FRM_03_C.set(Integer.parseInt(FRM.FRM_03.substring(0,4)), Integer.parseInt(FRM.FRM_03.substring(4,6)) - 1, Integer.parseInt(FRM.FRM_03.substring(6)));
//        FRM_96_C.set(Integer.parseInt(FRM.FRM_96.substring(0,4)), Integer.parseInt(FRM.FRM_96.substring(4,6)) - 1, Integer.parseInt(FRM.FRM_96.substring(6,8)));
//        setImgFilter();
//        setPreFilterDay();
//        setNextFilterDay();
//        setDDAY();
//    }

//    private void setReserveDay(){
////        tvReserveDay.setText(RMR.RMR_03.substring(0,4)+"."+FRM.FRM_03.substring(4,6)+"."+FRM.FRM_03.substring(6));
//    }

    private void reserveDayDialog(){
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                RMR_03_C.set(year, month, date);
                String tmp = String.valueOf(year);

                month++;
                if(month<10){
                    tmp += "0" + String.valueOf(month);
                }
                else{
                    tmp += String.valueOf(month);
                }

                if(date<10){
                    tmp += "0" + String.valueOf(date);
                }
                else{
                    tmp += String.valueOf(date);
                }

                RMR_03 = tmp;
                tvReserveDay.setText(sDateFormat(RMR_03));
            }
        }, RMR_03_C.get(Calendar.YEAR), RMR_03_C.get(Calendar.MONTH), RMR_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        EditText etDeleteName = (EditText) view.findViewById(R.id.etDeleteName);

        AlertDialog dialog = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etDeleteName.getText().toString().equals(RMD.RMD_03)){
                    dialog.dismiss();
                    requestRMD_CONTROL("DELETE");
                }
                else{
                    Toast.makeText(mActivity, R.string.dialog_delete_check_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean validationCheck(){
        boolean check = true;
        if(etName.getText().toString().equals("")){
            check = false;
            Toast.makeText(mActivity, R.string.frm_validation_check1, Toast.LENGTH_SHORT).show();
        }
//        else if(FRM.FRM_05.equals("")){
//            check = false;
//            Toast.makeText(mActivity, R.string.frm_validation_check2, Toast.LENGTH_SHORT).show();
//        }
//        else if(TODAY.compareTo(FRM_03_C) < 0){
//            check = false;
//            Toast.makeText(mActivity, R.string.frm_validation_check3, Toast.LENGTH_SHORT).show();
//        }
        return check;
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
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
                                RMD.RMD_02 = response.body().Data.get(0).CDS_03;
                                requestRMD_CONTROL("INSERT");
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

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);

        return result;
    }

}
