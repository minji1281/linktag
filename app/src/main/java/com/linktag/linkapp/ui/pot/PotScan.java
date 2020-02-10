package com.linktag.linkapp.ui.pot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.Html;
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
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotScan extends BaseActivity {

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private TextView tvPOT_02;
    private TextView tvDDAY;
    private TextView tvPOT_03_T;
//
//    private NumberPicker npCycle;
//    private NumberPicker npCycle2;
//
//    private Switch swAlarm;
//
//    private TimePicker tpAlarmTime;
//
    private Button btnDetail;
    private Button btnWater;

    //======================
    // Variable
    //======================

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_scan);

        initLayout();

//        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility(View.GONE);

        tvPOT_02 = (TextView) findViewById(R.id.tvPOT_02);
        tvDDAY = (TextView) findViewById(R.id.tvDDAY);
        tvPOT_03_T = (TextView) findViewById(R.id.tvPOT_03_T);

        btnDetail = (Button) findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                moveDetail();
            }
        });

        btnWater = (Button) findViewById(R.id.btnWater);
        btnWater.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                requestPOT_CONTROL("WATER");
            }
        });

    }

    @Override
    protected void initialize() {
        String POT_96 = getIntent().getExtras().getString("POT_96");

        Calendar today = Calendar.getInstance();
        Calendar alarmday = Calendar.getInstance();
        alarmday.set(Integer.parseInt(POT_96.substring(0,4)), Integer.parseInt(POT_96.substring(4,6)) - 1, Integer.parseInt(POT_96.substring(6,8)));

        long dday = (alarmday.getTimeInMillis() - today.getTimeInMillis()) / (24 * 60 * 60 * 1000);

        tvPOT_02.setText(getIntent().getExtras().getString("POT_02"));
        if(dday>0){
            tvDDAY.setText(Html.fromHtml("다음 주기까지 <font color=red>" + Long.toString(dday)  + "일</font> 남았습니다."));
        }
        else if(dday==0){
            tvDDAY.setText(Html.fromHtml("<font color=red>D-Day</font> 오늘은 물주기 날입니다!"));
        }
        else{
            tvDDAY.setText(Html.fromHtml("주기일에서 <font color=red>" + Long.toString(dday*-1)  + "일</font> 지났습니다!"));
        }
        tvPOT_03_T.setText("최근 물주기 시각\r\n" + getIntent().getExtras().getString("POT_03_T"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        initialize();

//        Toast.makeText(mActivity, getIntent().getExtras().getString("POT_02"), Toast.LENGTH_SHORT).show();
    }

    public void moveDetail(){
        finish();
        getIntent().setClass(mContext, PotDetail.class);
        mContext.startActivity(getIntent());
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
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
        String POT_98 = mUser.Value.OCM_01; //사용자코드

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                "",
                0,

                "",
                "",
                "",
                "",
                POT_98,

                ""
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

                            getIntent().putExtra("POT_03_T", response.body().Data.get(0).POT_03_T);
                            callBack(GUB, response.body().Data.get(0));
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
        if(data.Validation){
            switch(GUB){
                case "WATER":
                    moveDetail();
                    break;
            }
        }

    }
//
//    private void setUserData(PotVO potVO) {
//        tvPreWaterDay.setText(potVO.POT_03_T);
//    }

}
