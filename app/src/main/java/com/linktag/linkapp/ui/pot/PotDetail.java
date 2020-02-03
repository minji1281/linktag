package com.linktag.linkapp.ui.pot;

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
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.R;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotDetail extends BaseActivity {

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private TextView tvName;
    private TextView tvPreWaterDay;
    private EditText etMemo;

    private NumberPicker npCycle;
    private NumberPicker npCycle2;

    private Switch swAlarm;

    private TimePicker tpAlarmTime;

    private Button btnWaterUpdate;
    private Button btnSave;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_detail);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        //작성자면 삭제버튼 보임
        if(getIntent().getExtras().getString("POT_97").equals(mUser.Value.OCM_01)){
            header.btnHeaderRight1.setVisibility((View.VISIBLE));
            header.btnHeaderRight1.setMaxWidth(50);
            header.btnHeaderRight1.setMaxHeight(50);
            header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
            header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mActivity)
                            .setMessage("삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPOT_CONTROL("DELETE");
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

        tvName = (TextView) findViewById(R.id.tvName);
        tvPreWaterDay = (TextView) findViewById(R.id.tvPreWaterDay);
        etMemo = (EditText) findViewById(R.id.etMemo);

        npCycle = (NumberPicker) findViewById(R.id.npCycle);
        npCycle.setMinValue(0);
        npCycle.setMaxValue(60);
        npCycle2 = (NumberPicker) findViewById(R.id.npCycle2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[] {"일", "개월"});

        swAlarm = (Switch) findViewById(R.id.swAlarm);

        tpAlarmTime = (TimePicker) findViewById(R.id.tpAlarmTime);

        btnWaterUpdate = (Button) findViewById(R.id.btnWaterUpdate);
        btnWaterUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(mActivity)
                        .setMessage("물주기를 업데이트 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public  void onClick(DialogInterface dialog, int which){
                                requestPOT_CONTROL("WATER");
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                            @Override
                            public  void onClick(DialogInterface dialog, int which){
                                return;
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                requestPOT_CONTROL("UPDATE");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initialize() {
        getDetail();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDetail() {
        /* 최초 로딩시 intent 가져옴 */
        tvName.setText(getIntent().getExtras().getString("POT_02"));
        tvPreWaterDay.setText(getIntent().getExtras().getString("POT_03_T"));
        etMemo.setText(getIntent().getExtras().getString("POT_06"));

        npCycle.setValue(getIntent().getExtras().getInt("POT_04"));
        if(getIntent().getExtras().getString("POT_05").equals("D")){
            npCycle2.setValue(0); //일
        }
        else{ //M
            npCycle2.setValue(1); //개월
        }

        Boolean alarm = false;
        if(getIntent().getExtras().getString("ARM_03").equals("Y")){
            alarm = true;
        }
        swAlarm.setChecked(alarm);

        tpAlarmTime.setHour(Integer.parseInt(getIntent().getExtras().getString("POT_96").substring(8, 10)));
        tpAlarmTime.setMinute(Integer.parseInt(getIntent().getExtras().getString("POT_96").substring(10)));
    }

    private void setOnClickDelete(){
        //Toast.makeText(mContext, "삭제 준비중입니다!!!", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPOT_CONTROL(String GUB){

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String POT_ID = "1"; //컨테이너 수정해야돼!!!
        String POT_01 = getIntent().getExtras().getString("POT_01"); //코드번호
        String POT_02 = getIntent().getExtras().getString("POT_02"); //명칭
        int POT_04 = npCycle.getValue(); //주기
        String POT_05 = "M";
        if(npCycle2.getValue() == 0){ //주기구분
            POT_05 = "D"; //일
        }
        String POT_06 = etMemo.getText().toString(); //메모
        String POT_81 = ""; //이미지
        String POT_96 = (tpAlarmTime.getHour()<10 ? "0" + String.valueOf(tpAlarmTime.getHour()) : String.valueOf(tpAlarmTime.getHour())) + (tpAlarmTime.getMinute()<10 ? "0" + String.valueOf(tpAlarmTime.getMinute()) : String.valueOf(tpAlarmTime.getMinute())); //알림시간
        String POT_98 = "M191100001"; //사용자코드 mUser.Value.OCM_01 수정해야돼!!!
        String ARM_03 = "N";
        if(swAlarm.isChecked()){ //알림여부
            ARM_03 = "Y";
        }

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                POT_02,
                POT_04,

                POT_05,
                POT_06,
                POT_81,
                POT_96,
                POT_98,

                ARM_03
        );

        call.enqueue(new Callback<POT_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            if(GUB.equals("DELETE")){
                                callBack(GUB, new PotVO());
                            }
                            else{
                                callBack(GUB, response.body().Data.get(0));
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void callBack(String GUB, PotVO data){
//        if(data.Validation){ //vs 서버올리면 수정해주석풀어!!
            switch(GUB){
                case "UPDATE":
                    break;
                case "WATER":
                    setUserData(data);
                    break;
                case "DELETE":
                    finish();
                    break;
            }
//        }

    }

    private void setUserData(PotVO potVO) {
        tvPreWaterDay.setText(potVO.POT_03_T);
    }

}
