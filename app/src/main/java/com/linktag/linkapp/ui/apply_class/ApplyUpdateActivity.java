package com.linktag.linkapp.ui.apply_class;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.member_class.FindMember;
import com.linktag.linkapp.value_object.LED_VO;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyUpdateActivity extends BaseActivity {
    private BaseHeader header;

    RelativeLayout layoutLED06_OFF, layoutLED06_OVER, layoutLED11, layoutBtn;

    TextView tvTitleLED06, tvTitleLED12;
    TextView tvGUBUN, tvLED02, tvLED03, tvLED05, tvLED19;
    Spinner spLED06_OFF, spLED06_OVER;

    EditText etLED03NM, etLED11, etLED12, etLED24, etLED97;

    Button btnFindMem;
    ImageView btnDelete;

    private HashMap<String, String> map_LED_06_OFF = new HashMap<String, String>();
    private HashMap<String, String> map_LED_06_OVER = new HashMap<String, String>();

    Calendar calendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONDAY, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel(){
        String dFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dFormat, Locale.KOREA);

        etLED24.setText(sdf.format(calendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_update);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
        header.btnHeaderText.setVisibility(View.VISIBLE);
        header.btnHeaderText.setOnClickListener(v -> validationCheck());

        layoutLED06_OFF = findViewById(R.id.layoutLED06);
        layoutLED06_OVER = findViewById(R.id.layoutLED06_OVER);
        layoutLED11 = findViewById(R.id.layoutLED11);

        tvTitleLED06 = findViewById(R.id.tvTitleLED06);
        tvTitleLED12 = findViewById(R.id.tvTitleLED12);

        tvGUBUN = findViewById(R.id.tvGUBUN);
        tvLED02 = findViewById(R.id.tvLED02);
        tvLED03 = findViewById(R.id.tvLED03);
        tvLED05 = findViewById(R.id.tvLED05);
        tvLED19 = findViewById(R.id.tvLED19);

        etLED03NM = findViewById(R.id.etLED03NM);
        etLED11 = findViewById(R.id.etLED11);
        etLED12 = findViewById(R.id.etLED12);
        etLED97 = findViewById(R.id.etLED97);

        spLED06_OFF = findViewById(R.id.spLED06_OFF);
        spLED06_OVER = findViewById(R.id.spLED06_OVER);

        etLED24 = findViewById(R.id.etLED24);
        etLED24.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mActivity, datePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONDAY),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        btnFindMem = findViewById(R.id.btnFindMem);
        btnFindMem.setOnClickListener(v -> goFindMember());

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> goDelete());

    }

    @Override
    protected void initialize() {
        setMap();

        getDetail();

        setLayout();
    }

    private void setMap() {
        // string_array의 spinner값과 동기화

        // 연차
        map_LED_06_OFF.put("기타", "0");
        map_LED_06_OFF.put("개인사유", "1");
        map_LED_06_OFF.put("병가", "2");

        // 연장
        map_LED_06_OVER.put("기타", "0");

    }

    private void getDetail() {
        /* 최초 로딩시 intent 가져옴 */
        tvLED02.setText(getIntent().getExtras().getString("LED_02",""));
        tvLED05.setText(getIntent().getExtras().getString("LED_05","1"));

        requestBVA_SELECT();
    }

    private void setLayout(){
        final ArrayAdapter<String> adapter;
        String[] ar;
        String LED_05 = (String)tvLED05.getText();

        if(LED_05.equals("1")){
            // 연차
            layoutLED11.setVisibility(View.VISIBLE);
            layoutLED06_OFF.setVisibility(View.VISIBLE);
            layoutLED06_OVER.setVisibility(View.GONE);
            tvTitleLED12.setText("신청일수");
            ar = new String[]{"기타", "개인사유", "병가"};
            adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, ar);
            adapter.setDropDownViewResource(R.layout.spinner_item);
            spLED06_OFF.setAdapter(adapter);

        } else {
            // 연장
            layoutLED11.setVisibility(View.GONE);
            layoutLED06_OFF.setVisibility(View.GONE);
            layoutLED06_OVER.setVisibility(View.VISIBLE);
            tvTitleLED12.setText("연장시간");
            ar = new String[]{"기타"};
            adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, ar);
            adapter.setDropDownViewResource(R.layout.spinner_item);
            spLED06_OVER.setAdapter(adapter);
        }

    }

    public void requestBVA_SELECT() {
        // 인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(getString(R.string.common_network_error));
//            return;
//        }
//
//        //openLoadingBar();
//
//        String LED_02 = tvLED02.getText().toString();
//
//        Call<LEDModel> call = Http.apply(HttpBaseService.TYPE.POST).BVA_SELECT(
//                BaseConst.URL_HOST,
//                "DETAIL",
//                mUser.Value.OCP_ID,
//                "BVA",
//                LED_02,
//                "",
//                "",
//                "",
//                "",
//
//                "",
//                "",
//                "",
//                ""
//        );
//
//        call.enqueue(new Callback<LEDModel>() {
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<LEDModel> call, Response<LEDModel> response) {
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
//                            Response<LEDModel> response = (Response<LEDModel>) msg.obj;
//
//                            init(response.body());
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<LEDModel> call, Throwable t) {
//                Log.d("Test", t.getMessage());
//                closeLoadingBar();
//
//            }
//        });

    }

    private void init(LEDModel model){
        if(model.Total > 0){
            LED_VO data = model.Data.get(0);

            if(data.LED_19.equals("N")){
                btnDelete.setVisibility(View.VISIBLE);
                header.btnHeaderText.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
                header.btnHeaderText.setVisibility(View.GONE);
            }

            tvGUBUN.setText("UPDATE");
            tvLED02.setText(data.LED_02);
            tvLED03.setText(data.LED_03);
            etLED03NM.setText(data.LED_03_NM);

            tvLED05.setText(data.LED_05);

            etLED11.setText(String.valueOf(data.LED_11));
            etLED12.setText(String.valueOf(data.LED_12));

            tvLED19.setText(data.LED_19);

            etLED24.setText(ClsDateTime.ConvertDateFormat("yyyyMMdd", "yyyy-MM-dd", data.LED_24));

            etLED97.setText(data.LED_97);

            if(data.LED_05.equals("1"))
            {
                spLED06_OFF.setSelection(Integer.valueOf(data.LED_06));
                header.tvHeaderTitle.setText("연차");
            } else {
                etLED11.setText(0);
                spLED06_OVER.setSelection(Integer.valueOf(data.LED_06));
                header.tvHeaderTitle.setText("연장");
            }

        } else {
            btnDelete.setVisibility(View.GONE);

            tvGUBUN.setText("INSERT");
            tvLED19.setText("N");
            etLED24.setText(ClsDateTime.getNow("yyyy-MM-dd"));
        }

    }

    private void validationCheck(){
        String LED_03 = (String) tvLED03.getText();
        String LED_05 = (String) tvLED05.getText();
        // 신청일자
        //String LED_24 = etLED24.getText().toString().replaceAll("-","");
        String LED_24 = String.valueOf(etLED24.getText()).replaceAll("-", "");
        String LED_11 = String.valueOf(etLED11.getText());
        String LED_12 = String.valueOf(etLED12.getText());

        /**
         * BaseAlert이 Activity 베이스 에서 실행되는데
         * 팝업창에서 finish()한 후 BaseAlert가 나오게 되면 finish()가 액티비티 정보를 날려 버린 후
         * 기존 Activity를 찾지 못해서 에러 나는듯하여 Toast를 이용하여 에러 표시 하였음
         */

        if(LED_24.equals("") || LED_24.length() > 8){
            Toast.makeText(mContext, "신청일자를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            //BaseAlert.show("신청일자를 확인해 주세요.");
            return;
        }
        if(LED_03.equals("")) {
            etLED03NM.requestFocus();
            Toast.makeText(mContext, "승인자를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            //BaseAlert.show("승인자를 확인해 주세요.");
            return;
        }
        if(LED_05.equals("1")){
            if(LED_11.isEmpty() || LED_11.equals("")){
                etLED11.requestFocus();
                Toast.makeText(mContext, "남은일수를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                //BaseAlert.show("남은일수를 확인해 주세요.");
                return;
            }
            if(LED_12.isEmpty() || LED_12.equals("")){
                etLED12.requestFocus();
                Toast.makeText(mContext, "신청일수를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                //BaseAlert.show("신청일수를 확인해 주세요.");
                return;
            }
        } else if (LED_05.equals("2")){
            if(LED_12.equals("")){
                etLED12.requestFocus();
                Toast.makeText(mContext, "연장시간을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                //BaseAlert.show("연장시간을 확인해 주세요.");
                return;
            }
        }

        requestBVA_CONTROL();
    }

    private void requestBVA_CONTROL() {
        // 인터넷 연결 여부 확인
//        if (!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(getString(R.string.common_network_error));
//            return;
//        }
//
//        openLoadingBar();
//
//        String GUBUN = tvGUBUN.getText().toString();
//        String LED_ID = mUser.Value.CTM_01;
//        String LED_01 = "BVA";
//        String LED_02 = tvLED02.getText().toString();
//        String LED_03 = tvLED03.getText().toString();
//        String LED_04 = mUser.Value.OCM_01;
//        String LED_05 = tvLED05.getText().toString();
//        String LED_06 = getLED06V();
//        double LED_11 = Double.parseDouble(etLED11.getText().length() <= 0 ? "0" : etLED11.getText().toString());
//        double LED_12 = Double.parseDouble(etLED12.getText().length() <= 0 ? "0" : etLED12.getText().toString());
//        String LED_19 = tvLED19.getText().toString();
//        String LED_23 = ClsDateTime.getNow("yyyyMMdd");
//        String LED_24 = etLED24.getText().toString().replaceAll("-","");
//        String LED_97 = etLED97.getText().toString();
//        String LED_98 = mUser.Value.OCM_01;
//
//        Call<LEDModel> call = Http.apply(HttpBaseService.TYPE.POST).BVA_CONTROL(
//                BaseConst.URL_HOST,
//                GUBUN,
//                LED_ID,
//                LED_01,
//                LED_02,
//                LED_03,
//                LED_04,
//                LED_05,
//                LED_06,
//                LED_11,
//                LED_12,
//                LED_19,
//                LED_23,
//                LED_24,
//                LED_97,
//                LED_98
//        );
//
//        call.enqueue(new Callback<LEDModel>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<LEDModel> call, Response<LEDModel> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if (msg.what == 100){
//                            closeLoadingBar();
//
//                            Response<LEDModel> response = (Response<LEDModel>) msg.obj;
//
//                            finish();
//                            /*
//                            if(response.body().Data.get(0).Validation){
//                                finish();
//                            } else {
//                                Toast.makeText(mContext, R.string.login_err, Toast.LENGTH_LONG);
//                            }
//                            */
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<LEDModel> call, Throwable t){
//                Log.d("Test", t.getMessage());
//                closeLoadingBar();
//            }
//        });
    }


    private String getLED06V(){
        String str = "";
        String LED_05 = (String)tvLED05.getText();

        if(LED_05.equals("1")){
            str = map_LED_06_OFF.get(spLED06_OFF.getSelectedItem());
        } else if(LED_05.equals("2")){
            str = map_LED_06_OVER.get(spLED06_OVER.getSelectedItem());
        }

        return str;
    }

    /**
     * EMP 찾기 팝업
     */
    private void goFindMember(){
        Intent intent = new Intent(mContext, FindMember.class);
        intent.putExtra("CDO_04", etLED03NM.getText());
        this.startActivityForResult(intent, FindMember.REQUEST_CODE);
    }

    private void goDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("해당 항목을 삭제하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                tvGUBUN.setText("DELETE");
                requestBVA_CONTROL();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create().show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case FindMember.REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    tvLED03.setText(data.getStringExtra("CDO_02"));
                    etLED03NM.setText(data.getStringExtra("CDO_04"));
                }
                break;
        }
    }
}
