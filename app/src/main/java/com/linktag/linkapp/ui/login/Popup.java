package com.linktag.linkapp.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.linktag.linkapp.R;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
//import com.linktag.linkapp.ui.bus.BusSelectAdapter;

import com.linktag.linkapp.value_object.OcmVO;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.settings.SettingsKey;

import com.linktag.base_resource.broadcast_action.ClsBroadCast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Popup extends BaseActivity {
//    TextView txtText;
//    EditT
      EditText txtText;
      Button btnCancel;
    TextView tvBusNm;
    TextView tvUserName;
    TextView tvUserEmail;
    ToggleButton toggle;
    ToggleButton toggleRutc;
    LinearLayout layoutChangePwd;
    LinearLayout layoutRutc;
    EditText etOldPwd;
    EditText etNewPwd1;
    EditText etNewPwd2;
    EditText etUserName;

    Spinner spinnerCity;
    Spinner spinnerStreet;
    EditText etSearch;
    ListView listrutcView;
    ImageView btnSearch;

//    BusSelectAdapter mAdapter;
//    ArrayList<RutcVO> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //UI 객체생성
//        txtText = (TextView)findViewById(R.id.txtText);

 //       txtText = (EditText)findViewById(R.id.txtText);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etUserName.setText(mUser.Value.OCM_02);
        btnCancel = (Button) findViewById(R.id.btnCancel);
//        tvBusNm = (TextView)findViewById(R.id.tvBusNm);
        tvUserName = (TextView)findViewById(R.id.tvUserName);
        tvUserEmail = (TextView)findViewById(R.id.tvUserEmail);
        toggle = (ToggleButton)findViewById(R.id.toggle);
        toggleRutc = (ToggleButton)findViewById(R.id.toggleRutc);
        layoutChangePwd = (LinearLayout) findViewById(R.id.layoutChangePwd);
        layoutRutc = (LinearLayout) findViewById(R.id.layoutRutc);
        etOldPwd = (EditText)findViewById(R.id.etOldPwd);
        etNewPwd1 = (EditText)findViewById(R.id.etNewPwd1);
        etNewPwd2 = (EditText)findViewById(R.id.etNewPwd2);


        //데이터 가져오기
//        Intent intent = getIntent();
//        String data = intent.getStringExtra("data");
//        txtText.setText(data);
        initLayout();
        initialize();

    }


    @Override
    protected void initialize() {
//        mList = new ArrayList<>();
//        mAdapter = new BusSelectAdapter(mContext, mList);
//        listrutcView.setAdapter(mAdapter);
    }

    @Override
    protected void initLayout() {
     //   tvBusNm.setText(mUser.Value.RUTC_03);
        tvUserName.setText(mUser.Value.OCM_02);
        tvUserEmail.setText(mUser.Value.OCM_21);

        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerStreet = findViewById(R.id.spinnerStreet);
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    //requestRUTC_SELECT();
                    return true;
                }
                return false;
            }
        });
        btnSearch = findViewById(R.id.btnSearch);
        //btnSearch.setOnClickListener(v -> requestRUTC_SELECT());

        listrutcView = findViewById(R.id.listrutcView);
        listrutcView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));


    }

    @Override
    protected void onResume(){
        super.onResume();

        //requestRUTC_SELECT();
    }


    //확인 버튼 클릭
    public void AllClose(View v){

            finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }


    //확인 버튼 클릭
    public void Onfinish(View v){
        finishAndRemoveTask();
        finishAffinity();
        System.runFinalization();
        System.exit(0);

    }

    public void Onsubmit(View v){
       // Toast.makeText(mActivity, "클릭", Toast.LENGTH_SHORT).show();
        requestOCM_CONTROL("PASS");

    }


    public void onToggleClicked(View v){   //비밀번호

        boolean on = ((ToggleButton) v).isChecked();

        if(on){
            layoutChangePwd.setVisibility(View.VISIBLE);
            layoutRutc.setVisibility(View.GONE);
            toggleRutc.setChecked(false);
        }  else {
            etOldPwd.setText("");
            etNewPwd1.setText("");
            etNewPwd2.setText("");
            layoutChangePwd.setVisibility(View.GONE);
            toggleRutc.setChecked(false);
            layoutRutc.setVisibility(View.GONE);
        }
    }

    public void onRutcToggleClicked(View v){  // 노선변경

        boolean on = ((ToggleButton) v).isChecked();

        if(on){
            layoutRutc.setVisibility(View.VISIBLE);
            layoutChangePwd.setVisibility(View.GONE);
            toggle.setChecked(false);

        }  else {

            layoutRutc.setVisibility(View.GONE);
            toggle.setChecked(false);
            layoutChangePwd.setVisibility(View.GONE);
        }
    }

    private boolean validationCheck(String GUB){
        if(GUB.equals("PASS"))
        {
            if(etOldPwd.getText().toString().length() == 0){
                etOldPwd.requestFocus();
                Toast.makeText(mActivity, "기존 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!mUser.Value.OCM_03.equals(etOldPwd.getText().toString())){
                etOldPwd.requestFocus();
                Toast.makeText(mActivity, "현재 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(etNewPwd1.getText().toString().length() == 0){
                etNewPwd1.requestFocus();
                Toast.makeText(mActivity, "수정 할 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!etNewPwd1.getText().toString().equals(etNewPwd2.getText().toString())){
                etNewPwd1.requestFocus();
                Toast.makeText(mActivity, "입력된 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (GUB.equals("UPDATE")) {

        }

        return true;
    }

    private void requestOCM_CONTROL(String GUB){
        if(!validationCheck(GUB))
            return;

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(this, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String OCM_01 = mUser.Value.OCM_01;
        String OCM_02 = etUserName.getText().toString();
        String OCM_03 = etNewPwd1.getText().toString();
        String OCM_24 = "";
        String OCM_51 = "";
        String OCM_52 = "";
        String OCM_98 = mUser.Value.OCM_01;

        Call<OCM_Model> call = Http.ocm(HttpBaseService.TYPE.POST).OCM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                OCM_01,
                OCM_02,
                OCM_03,
                OCM_24,
                OCM_51,
                OCM_52,
                OCM_98
        );

        call.enqueue(new Callback<OCM_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;

                            callBack(GUB, response.body().Data.get(0));
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<OCM_Model> call, Throwable t){
                Log.d("OCM_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void callBack(String GUB, OcmVO data){
        if(data.Validation){
            switch(GUB){
                case "PASS":
                    logout();
                    //setUserPwd();
                    break;
                case "UPDATE":
                    setUserData(data);
                    break;
            }
        }
    }

//    private void requestRUTC_SELECT(){
//        //인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        //openLoadingBar();
//
//        String GUBUN = "LIST";
//        String RUTC_ID = mUser.Value.CTM_01;
//        String RUTC_03 = etSearch.getText().toString();
//        String RUTC_07 = "";
//        String RUTC_08 = "";
//        Call<RUTC_Model> call = Http.rutc(HttpBaseService.TYPE.POST).RUTC_SELECT(
//                BaseConst.URL_HOST,
//                GUBUN,
//                RUTC_ID,
//                RUTC_03,
//                RUTC_07,
//                RUTC_08
//        );
//
//
//        call.enqueue(new Callback<RUTC_Model>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<RUTC_Model> call, Response<RUTC_Model> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//
//                        if(msg.what == 100){
//                            Response<RUTC_Model> response = (Response<RUTC_Model>) msg.obj;
//
//                            mList = response.body().Data;
//                            if(mList == null)
//                                mList = new ArrayList<>();
//
//                            mAdapter.updateData(mList);
//                            mAdapter.notifyDataSetChanged();
//
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<RUTC_Model> call, Throwable t){
//                Log.d("RUTC_SELECT", t.getMessage());
//                //closeLoadingBar();
//            }
//        });
//    }

    private void goWorkRecord(int position) {

//        intent.putExtra(WorkRecordActivity.WORK_STATE, mList.get(position));
//        mContext.startActivity(intent);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = sdfNow.format(date);

//        mUser.Value.RUTC_01 = String.valueOf(mList.get(position).RUTC_01);  //   #008  --노선번호
//        mUser.Value.RUTC_03 = String.valueOf(mList.get(position).RUTC_03);  //   #008  --노선번호
//        mUser.Value.RUTC_ST = formatDate;  //   #008  --운행시작시간
//
//        mUser.Value.RUTC_04 = String.valueOf(mList.get(position).RUTC_04);  //   #008  --기점
//        mUser.Value.RUTC_05 = String.valueOf(mList.get(position).RUTC_05);  //   #008  --종점

     //   tvBusNm.setText(String.valueOf(mList.get(position).RUTC_03));
        Toast.makeText(mActivity, "운행시간이 초기화 됩니다.", Toast.LENGTH_LONG).show();
      //  ((Main) Main.mContext).mapChanged();    // #999
//        ((Main) Main.mContext).onClick();
        finish();

        // Log.d("***********************",String.valueOf(mList.get(position).RUTC_01));
    }



    /**
     * 로그아웃
     */

    private void logout() {
        openLoadingBar();
        new Handler().postDelayed(() -> {
            closeLoadingBar();
            mSettings.Value.AutoLogin = false;
            mSettings.putBooleanItem(SettingsKey.AutoLogin, false);
            Intent intent = new Intent(ClsBroadCast.BROAD_CAST_ACTION_LOGOUT);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }, 500);
    }

    private void setUserData(OcmVO ocmVO) {
        mUser.Value.OCM_02 = ocmVO.OCM_02;
    }

}
