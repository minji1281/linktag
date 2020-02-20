package com.linktag.linkapp.ui.car;

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
import android.widget.ArrayAdapter;
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
import com.linktag.linkapp.model.CADModel;
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.CAD_VO;
import com.linktag.linkapp.value_object.COD_VO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private EditText etDay;
    private EditText etName;
    private EditText etPrice;
    private EditText etKm;
    private EditText etMemo;

    private Spinner spGub1;
    private Spinner spGub2;

    private Button btnSave;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================
    private EditText etFocusDay;
    private CAD_VO CAD;
    private String gubun;
    private String CAD_04;
    private String CAD_06;

    Calendar CAD_03_Calendar = Calendar.getInstance(); //개봉일자
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    DatePickerDialog.OnDateSetListener CAD_03_DatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            CAD_03_Calendar.set(Calendar.YEAR, year);
            CAD_03_Calendar.set(Calendar.MONTH, month);
            CAD_03_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            etFocusDay.setText(sdf.format(CAD_03_Calendar.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_detail);

        if(getIntent().hasExtra("CAD")){
            CAD = (CAD_VO) getIntent().getSerializableExtra("CAD");
            gubun = "UPDATE";
        }
        else{
            CAD = new CAD_VO();
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
            if(CAD.CAD_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setMaxWidth(50);
                header.btnHeaderRight1.setMaxHeight(50);
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mActivity)
                                .setMessage("해당 내역을 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestCAD_CONTROL("DELETE");
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

        spGub1 = (Spinner) findViewById(R.id.spGub1);
        ArrayList arrayList1 = new ArrayList();
        arrayList1.add("교체");
        arrayList1.add("점검");
        ArrayAdapter arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item2, arrayList1);
        spGub1.setAdapter(arrayAdapter1);
        spGub1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CAD_04 = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spGub2 = (Spinner) findViewById(R.id.spGub2);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add("셀프");
        arrayList2.add("카센터");
        ArrayAdapter arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item2, arrayList2);
        spGub2.setAdapter(arrayAdapter2);
        spGub2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CAD_06 = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etDay = (EditText) findViewById(R.id.etDay);
        etDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFocusDay = (EditText) findViewById(R.id.etDay);
                new DatePickerDialog(mContext, CAD_03_DatePicker, CAD_03_Calendar.get(Calendar.YEAR), CAD_03_Calendar.get(Calendar.MONTH), CAD_03_Calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etKm = (EditText) findViewById(R.id.etKm);
        etMemo = (EditText) findViewById(R.id.etMemo);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                requestCAD_CONTROL(gubun);
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
        etDay.setText(sDateFormat(CAD.CAD_03));
        CAD_03_Calendar.set(Integer.parseInt(CAD.CAD_03.substring(0,4)), Integer.parseInt(CAD.CAD_03.substring(4,6))-1, Integer.parseInt(CAD.CAD_03.substring(6,8)));
        etName.setText(CAD.CAD_05);
        etPrice.setText(String.valueOf(Math.round(CAD.CAD_07)));
        etKm.setText(String.valueOf(Math.round(CAD.CAD_08)));
        etMemo.setText(CAD.CAD_09);

        spGub1.setSelection(Integer.parseInt(CAD.CAD_04) - 1);
        spGub2.setSelection(Integer.parseInt(CAD.CAD_06) - 1);
    }

    private void requestCAD_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String CAD_ID = getIntent().getStringExtra("CTN_02"); //컨테이너
        String CAD_01 = getIntent().getStringExtra("CAD_01"); //코드번호
        String CAD_02 = ""; //일련번호
        if(gubun.equals("UPDATE")){
            CAD_02 = CAD.CAD_02;
        }
        String CAD_03 = etDay.getText().toString().replace("-", ""); //일자
        String CAD_05 = etName.getText().toString(); //내역
        double CAD_07 = 0; //비용
        if(!etPrice.getText().toString().equals("")){
            CAD_07 = Double.parseDouble(etPrice.getText().toString().replace(",", ""));
        }
        int CAD_08 = 0; //주행거리
        if(!etKm.getText().toString().equals("")){
            CAD_08 = Integer.parseInt(etKm.getText().toString().replace(",", ""));
        }
        String CAD_09 = etMemo.getText().toString(); //메모
        String CAD_98 = mUser.Value.OCM_01; //최종수정자

        Call<CADModel> call = Http.cad(HttpBaseService.TYPE.POST).CAD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CAD_ID,
                CAD_01,
                CAD_02,
                CAD_03,

                CAD_04,
                CAD_05,
                CAD_06,
                CAD_07,
                CAD_08,

                CAD_09,
                CAD_98
        );

        call.enqueue(new Callback<CADModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CADModel> call, Response<CADModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<CADModel> response = (Response<CADModel>) msg.obj;

                            finish();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CADModel> call, Throwable t){
                Log.d("CAD_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "-" + sDate.substring(4,6) + "-" + sDate.substring(6,8);

        return result;
    }

}
