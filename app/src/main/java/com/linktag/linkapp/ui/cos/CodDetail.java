package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.COD_VO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

//    private Spinner spCos;

    private EditText etName;
    private EditText etBrand;
    private EditText etPrice;
    private EditText etOpenDay;
    private EditText etEndDay;
    private EditText etUseEndDay;
    private EditText etMemo;

    private Switch swAlarm;

    private TimePicker tpAlarmTime;

    private Button btnSave;
    private Button btnUseDelete;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    private String ARM_03 = "N";
    private EditText etFocusDay;
    private COD_VO COD;
    private String gubun;
    ArrayList<SpinnerList> cosList = new ArrayList<>();
    @BindView(R.id.spCos)
    Spinner spCos;
    private String COD_95;

    Calendar COD_05_Calendar = Calendar.getInstance(); //개봉일자
    Calendar COD_06_Calendar = Calendar.getInstance(); //유통기한
    Calendar COD_07_Calendar = Calendar.getInstance(); //사용종료일자
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    DatePickerDialog.OnDateSetListener COD_05_DatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            COD_05_Calendar.set(Calendar.YEAR, year);
            COD_05_Calendar.set(Calendar.MONTH, month);
            COD_05_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etFocusDay.setText(sdf.format(COD_05_Calendar.getTime()));
        }
    };
    DatePickerDialog.OnDateSetListener COD_06_DatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            COD_06_Calendar.set(Calendar.YEAR, year);
            COD_06_Calendar.set(Calendar.MONTH, month);
            COD_06_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etFocusDay.setText(sdf.format(COD_06_Calendar.getTime()));
        }
    };
    DatePickerDialog.OnDateSetListener COD_07_DatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            COD_07_Calendar.set(Calendar.YEAR, year);
            COD_07_Calendar.set(Calendar.MONTH, month);
            COD_07_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etFocusDay.setText(sdf.format(COD_07_Calendar.getTime()));
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_detail);

        if(getIntent().hasExtra("COD")){
            COD = (COD_VO) getIntent().getSerializableExtra("COD");
            gubun = "UPDATE";
        }
        else{
            COD = new COD_VO();
            COD.COD_95 = getIntent().getStringExtra("COD_95");
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
            if(COD.COD_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setMaxWidth(50);
                header.btnHeaderRight1.setMaxHeight(50);
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage("해당 화장품을 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestCOD_CONTROL("DELETE");
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

            if(!COD.COD_07.equals("")){
                btnUseDelete = (Button) findViewById(R.id.btnUseDelete);
                btnUseDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        etUseEndDay.setText("");
                    }
                });
                btnUseDelete.setVisibility(View.VISIBLE);
            }
        }

        spCos = (Spinner) findViewById(R.id.spCos);
        CosInfo cosinfo = new CosInfo(cosList, mActivity, "spCos", "LIST2", getIntent().getStringExtra("CTN_02"), COD.COD_95);
        cosinfo.execute();
        spCos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                COD_95 = cosList.get(position).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etBrand = (EditText) findViewById(R.id.etBrand);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etOpenDay = (EditText) findViewById(R.id.etOpenDay);
        etOpenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFocusDay = (EditText) findViewById(R.id.etOpenDay);
                new DatePickerDialog(mContext, COD_05_DatePicker, COD_05_Calendar.get(Calendar.YEAR), COD_05_Calendar.get(Calendar.MONTH), COD_05_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etEndDay = (EditText) findViewById(R.id.etEndDay);
        etEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFocusDay = (EditText) findViewById(R.id.etEndDay);
                new DatePickerDialog(mContext, COD_06_DatePicker, COD_06_Calendar.get(Calendar.YEAR), COD_06_Calendar.get(Calendar.MONTH), COD_06_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etUseEndDay = (EditText) findViewById(R.id.etUseEndDay);
        etUseEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFocusDay = (EditText) findViewById(R.id.etUseEndDay);
                new DatePickerDialog(mContext, COD_07_DatePicker, COD_07_Calendar.get(Calendar.YEAR), COD_07_Calendar.get(Calendar.MONTH), COD_07_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etMemo = (EditText) findViewById(R.id.etMemo);

        swAlarm = (Switch) findViewById(R.id.swAlarm);

        tpAlarmTime = (TimePicker) findViewById(R.id.tpAlarmTime);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                requestCOD_CONTROL(gubun);
            }
        });

        if(gubun.equals("INSERT")){
            etUseEndDay.setVisibility((View.GONE));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initialize() {
        if(gubun.equals("UPDATE")){
            getDetail();
        }
        else{ //INSERT부분 추가할거있으면 추가!!
//            etBuyDay.setText(sdf.format(AIR_03_Calendar.getTime()));
//            etFilterDay.setText(sdf.format(AIR_04_Calendar.getTime()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDetail() {
        etName.setText(COD.COD_02);
        etBrand.setText(COD.COD_03);
        etPrice.setText(String.valueOf(Math.round(COD.COD_04)));
        etMemo.setText(COD.COD_08);

        Boolean alarm = false;
        if(COD.ARM_03.equals("Y")){
            alarm = true;
        }
        swAlarm.setChecked(alarm);

        tpAlarmTime.setHour(Integer.parseInt(COD.COD_96.substring(8, 10)));
        tpAlarmTime.setMinute(Integer.parseInt(COD.COD_96.substring(10)));

        if(!COD.COD_05.equals("")){
            etOpenDay.setText(sDateFormat(COD.COD_05));
            COD_05_Calendar.set(Integer.parseInt(COD.COD_05.substring(0,4)), Integer.parseInt(COD.COD_05.substring(4,6))-1, Integer.parseInt(COD.COD_05.substring(6,8)));
        }
        if(!COD.COD_06.equals("")){
            etEndDay.setText(sDateFormat(COD.COD_06));
            COD_06_Calendar.set(Integer.parseInt(COD.COD_06.substring(0,4)), Integer.parseInt(COD.COD_06.substring(4,6))-1, Integer.parseInt(COD.COD_06.substring(6,8)));
        }
        if(!COD.COD_07.equals("")){
            etUseEndDay.setText(sDateFormat(COD.COD_07));
            COD_07_Calendar.set(Integer.parseInt(COD.COD_07.substring(0,4)), Integer.parseInt(COD.COD_07.substring(4,6))-1, Integer.parseInt(COD.COD_07.substring(6,8)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCOD_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String COD_ID = getIntent().getStringExtra("CTN_02"); //컨테이너
        String COD_01 = ""; //코드번호
        if(gubun.equals("UPDATE")){
            COD_01 = COD.COD_01;
        }
        String COD_02 = etName.getText().toString(); //명칭
        String COD_03 = etBrand.getText().toString(); //브랜드명
        double COD_04 = 0; //가격
        if(!etPrice.getText().toString().equals("")){
            COD_04 = Double.parseDouble(etPrice.getText().toString().replace(",", ""));
        }
        String COD_05 = etOpenDay.getText().toString().replace("-", ""); //개봉일자
        String COD_06 = etEndDay.getText().toString().replace("-", ""); //유통기한일자
        String COD_07 = etUseEndDay.getText().toString().replace("-", ""); //사용종료일자
        String COD_08 = etMemo.getText().toString(); //메모
        String COD_96 = (tpAlarmTime.getHour()<10 ? "0" + String.valueOf(tpAlarmTime.getHour()) : String.valueOf(tpAlarmTime.getHour())) + (tpAlarmTime.getMinute()<10 ? "0" + String.valueOf(tpAlarmTime.getMinute()) : String.valueOf(tpAlarmTime.getMinute())); //알림시간
        String COD_98 = mUser.Value.OCM_01; //최종수정자
        if(swAlarm.isChecked()){ //알림여부
            ARM_03 = "Y";
        }

        Call<CODModel> call = Http.cod(HttpBaseService.TYPE.POST).COD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                COD_ID,
                COD_01,
                COD_02,
                COD_03,

                COD_04,
                COD_05,
                COD_06,
                COD_07,
                COD_08,

                COD_95,
                COD_96,
                COD_98,
                ARM_03
        );

        call.enqueue(new Callback<CODModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CODModel> call, Response<CODModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<CODModel> response = (Response<CODModel>) msg.obj;
                            if(GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")){
                                CodList.COD_95 = COD_95;
                            }

                            finish();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CODModel> call, Throwable t){
                Log.d("COD_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "-" + sDate.substring(4,6) + "-" + sDate.substring(6,8);

        return result;
    }

}
