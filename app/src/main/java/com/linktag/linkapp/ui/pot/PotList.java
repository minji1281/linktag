package com.linktag.linkapp.ui.pot;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm.AlarmMain;
import com.linktag.linkapp.ui.alarm.AlarmReceiver;
import com.linktag.linkapp.ui.alarm.DeviceBootReceiver;
import com.linktag.linkapp.value_object.PotVO;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;


public class PotList extends BaseActivity implements PotAdapter.AlarmClickListener {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    //private Spinner spinnerCity;
    //private Spinner spinnerStreet;
    //private EditText etSearch;
    private ListView listView;
    //private ImageView btnSearch;
    private TextView emptyText;

//    AlarmManager alarm_manager;
//    private Calendar calendar;
//    PendingIntent pendingIntent;
//    private Intent my_intent;
//    private static final int REQUEST_CODE = 3333;
//    public long calculateTime = 0;

    //======================
    // Variable
    //======================
    private PotAdapter mAdapter;
    private ArrayList<PotVO> mList;


    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_list);

        initLayout();

        initialize();

        if(getIntent().getExtras() != null){
            Intent intent = getIntent();
            intent.setClass(mContext, PotScan.class);
            mContext.startActivity(intent);
        }
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility((View.GONE));

        //spinnerCity = findViewById(R.id.spinnerCity);
        //spinnerStreet = findViewById(R.id.spinnerStreet);
//        etSearch = findViewById(R.id.etSearch);
//        etSearch.setOnKeyListener(new View.OnKeyListener(){
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
//                    requestPOT_SELECT();
//                    return true;
//                }
//                return false;
//            }
//        });
//        btnSearch = findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(v -> requestPOT_SELECT());

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, PotDetail.class);
                intent.putExtra("POT_81", mList.get(position).POT_81);
                intent.putExtra("POT_02", mList.get(position).POT_02);
                intent.putExtra("POT_03_T", mList.get(position).POT_03_T);
                intent.putExtra("POT_04", mList.get(position).POT_04);
                intent.putExtra("POT_05", mList.get(position).POT_05);
                intent.putExtra("ARM_03", mList.get(position).ARM_03);
                intent.putExtra("POT_96", mList.get(position).POT_96);
                intent.putExtra("POT_06", mList.get(position).POT_06);
                intent.putExtra("POT_01", mList.get(position).POT_01);
                intent.putExtra("POT_97", mList.get(position).POT_97);
                intent.putExtra("ARM_04", mList.get(position).ARM_04);
                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new PotAdapter(mContext, mList, this); //, this
        listView.setAdapter(mAdapter);

        //requestPOT_SELECT();
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestPOT_SELECT();
    }

    private void requestPOT_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "LIST";
        String POT_ID = "1"; //컨테이너
        String POT_01 = " ";
        String OCM_01 = mUser.Value.OCM_01; //사용자 아이디

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                OCM_01
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

                            mList = response.body().Data;

                            if(mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    private void requestPOT_CONTROL(String GUB, PotVO pot) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        String GUBUN = GUB;
        String POT_ID = pot.POT_ID; //컨테이너
        String POT_01 = pot.POT_01; //코드번호
        String POT_02 = "";
        int POT_04 = 0;

        String POT_05 = "";
        String POT_06 = "";
        String POT_81 = "";
        String POT_96 = "";
        String POT_98 = mUser.Value.OCM_01; //사용자 아이디

        String ARM_03 = pot.ARM_03; //알림여부

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
                        if (msg.what == 100){

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            if(response.body().Data.get(0).Validation){
                                AlarmMain alarmMain = new AlarmMain();
                                int ID = response.body().Data.get(0).ARM_04;

                                if(ARM_03.equals("Y")){
                                    String alarmTitle = "물주기 - " + response.body().Data.get(0).POT_02;
                                    String alarmText = "식물에게 물을 주세요~";
                                    String className = ".ui.pot.PotScan";

                                    Intent intent = new Intent();
                                    intent.putExtra("POT_81", response.body().Data.get(0).POT_81);
                                    intent.putExtra("POT_02", response.body().Data.get(0).POT_02);
                                    intent.putExtra("POT_03_T", response.body().Data.get(0).POT_03_T);
                                    intent.putExtra("POT_04", response.body().Data.get(0).POT_04);
                                    intent.putExtra("POT_05", response.body().Data.get(0).POT_05);
                                    intent.putExtra("ARM_03", response.body().Data.get(0).ARM_03);
                                    intent.putExtra("POT_96", response.body().Data.get(0).POT_96);
                                    intent.putExtra("POT_06", response.body().Data.get(0).POT_06);
                                    intent.putExtra("POT_01", response.body().Data.get(0).POT_01);
                                    intent.putExtra("POT_97", response.body().Data.get(0).POT_97);
                                    intent.putExtra("className", className);

                                    intent.putExtra("ID", ID);
                                    intent.putExtra("alarmTitle", alarmTitle);
                                    intent.putExtra("alarmText", alarmText);

                                    alarmMain.setAlarm(getApplicationContext(), intent); //푸시알람 설정
                                }
                                else{
                                    alarmMain.deleteAlarm(getApplicationContext(), pot.ARM_04); //푸시알람 해제
                                }

                                onResume();
                            } else {
                                Toast.makeText(mContext, R.string.login_err, Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("Test", t.getMessage());

            }
        });

    }

    @Override
    public void onListAlarmClick(int position) {
        PotVO data = mList.get(position);

        if(data.ARM_03.equals("Y")){
            data.ARM_03 = "N";
        }
        else{ //N
            data.ARM_03 = "Y";
        }

        requestPOT_CONTROL("ALARM_UPDATE", data);
    }

}
