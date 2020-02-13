package com.linktag.linkapp.ui.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.linkapp.ui.pot.PotList;
import com.linktag.linkapp.ui.sign_up.SignUp;
import com.linktag.linkapp.model.LOGIN_Model;
import com.linktag.linkapp.value_object.LoginVO;
import com.linktag.base.linkapp;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.settings.SettingsKey;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.main.Main;
import com.linktag.linkapp.ui.scanner.ScanBarcode;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends BaseActivity {
    //========================================
    // Layout
    //========================================
    private EditText etAccount;
    private EditText etPassword;
    private TextView tvSignUp;
    private TextView tvFindIdPassword;
    private CheckBox chkAutoAccount;
    private CheckBox chkAutoLogin;
    private Button btnLogin;

    //========================================
    // Initialize
    //========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initLayout();

        initialize();
    }

    @Override
    protected void initialize() {
        if(mSettings.Value.AutoID){
            etAccount.setText(mSettings.Value.LoginID);
        }
        if(mSettings.Value.AutoLogin){
            etAccount.setText(mSettings.Value.LoginID);
            etPassword.setText(mSettings.Value.LoginPW);
            btnLogin.performClick();
        }

    }

    @Override
    protected void initLayout() {
        etAccount = findViewById(R.id.etAccount);

        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkLoginButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword = findViewById(R.id.etPassword);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkLoginButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> goSignUp());

        tvFindIdPassword = findViewById(R.id.tvFindIdPassword);
        tvFindIdPassword.setOnClickListener(v -> goScan());

        chkAutoAccount = findViewById(R.id.chkAutoAccount);
        chkAutoAccount.setChecked(mSettings.Value.AutoID);

        chkAutoLogin = findViewById(R.id.chkAutoLogin);
        chkAutoLogin.setChecked(mSettings.Value.AutoLogin);

        btnLogin = findViewById(R.id.btnLogin);
        //btnLogin.setEnabled(false);
        btnLogin.setOnClickListener(v -> requestEMPVIEW());
        //btnLogin.setOnClickListener(v -> alertTest());
    }

    private void alertTest(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//
//        builder.setTitle("매장 선택");
//        builder.setMessage("최근에 선택한 가나다 매장으로 진행하시겠습니까?");
//        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(mContext, "Yes clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(mContext, "No clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//        builder.setNeutralButton("예(한달간 표시안함)", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(mContext, "test", Toast.LENGTH_LONG).show();
//            }
//        });
//        builder.create().show();
    }

    /**
     * 로그인 버튼 활성화 체크
     */
    private void checkLoginButton() {
        if (etAccount.getText().toString().trim().length() > 0 &&
                etPassword.getText().toString().trim().length() > 0) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    /**
     * 로그인
     */
    private void requestEMPVIEW() {
        if (!validateEmail()) {
            etAccount.requestFocus();
            BaseAlert.show(getString(R.string.login_0));
            return;
        }

        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        String strEmail = etAccount.getText().toString();
        String strPassword = etPassword.getText().toString();

        if(chkAutoLogin.isChecked()){
            mSettings.Value.AutoLogin = true;
            mSettings.putBooleanItem(SettingsKey.AutoLogin, mSettings.Value.AutoLogin);

            mSettings.Value.AutoID = true;
            mSettings.putBooleanItem(SettingsKey.AutoID, mSettings.Value.AutoID);

            mSettings.Value.LoginID = strEmail;
            mSettings.putStringItem(SettingsKey.LoginID, mSettings.Value.LoginID);

            mSettings.Value.LoginPW = strPassword;
            mSettings.putStringItem(SettingsKey.LoginPW, mSettings.Value.LoginPW);
        } else {
            mSettings.Value.AutoLogin = false;
            mSettings.putBooleanItem(SettingsKey.AutoLogin, mSettings.Value.AutoLogin);

            if(chkAutoAccount.isChecked()) {
                mSettings.Value.AutoID = true;
                mSettings.putBooleanItem(SettingsKey.AutoID, mSettings.Value.AutoID);

                mSettings.Value.LoginID = strEmail;
                mSettings.putStringItem(SettingsKey.LoginID, mSettings.Value.LoginID);
            } else {
                mSettings.Value.AutoID = false;
                mSettings.putBooleanItem(SettingsKey.AutoID, mSettings.Value.AutoID);

                mSettings.Value.LoginID = "";
                mSettings.putStringItem(SettingsKey.LoginID, mSettings.Value.LoginID);
            }
        }


        // API 호출 시 로딩바
        openLoadingBar();

        // API URL및 Param 설정
        // 여기서 <SampleModel> 은 받을 데이터의 형태(Json format)
        Call<LOGIN_Model> call = Http.login(HttpBaseService.TYPE.POST).login(
                BaseConst.URL_HOST,
                "LOGIN",
                strPassword,
                strEmail,
                "",     // BUS -> AUB1, SHOP -> AUS1
                ""
        );

//        Call<LOGIN_Model> call = Http.login(HttpBaseService.TYPE.POST).login(
//                BaseConst.URL_HOST,
//                "LOGIN",
//                strPassword,
//                strEmail,
//                "",     // BUS -> AUB1, SHOP -> AUS1
//                ""
//        );

        // Api 호출 후 response 받을 위치
        call.enqueue(new Callback<LOGIN_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOGIN_Model> call, Response<LOGIN_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                //=====================
                // response callback
                //=====================
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            // 로딩바 닫음
                            closeLoadingBar();

                            Response<LOGIN_Model> response = (Response<LOGIN_Model>) msg.obj;

                            if (response.body().Data.get(0).Validation) {

                                setSession(response.body().Data.get(0));

                            } else {
                                if(response.body().Data.get(0).ErrorCode.equals("001")){
                                    etAccount.requestFocus();
                                    BaseAlert.show(getString(R.string.login_3));
                                }
                                else if(response.body().Data.get(0).ErrorCode.equals("002")){
                                    etPassword.requestFocus();
                                    BaseAlert.show(getString(R.string.login_1));
                                }
                                else{
                                    // ErrorMsg
                                    etAccount.requestFocus();
                                    BaseAlert.show(getString(R.string.login_2));
                                }
                            }
                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<LOGIN_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    /**
     * @param loginVO
     */
    private void setSession(LoginVO loginVO) {
        mUser.Value.CTM_01 = loginVO.CTM_01;
        mUser.Value.CTM_17 = loginVO.CTM_17;
        mUser.Value.CTM_19 = loginVO.CTM_19;

        mUser.Value.OCP_ID = loginVO.OCP_ID;
        mUser.Value.OCP_01 = loginVO.OCP_01;

        mUser.Value.OCM_01 = loginVO.OCM_01;
        mUser.Value.OCM_02 = loginVO.OCM_02;
        mUser.Value.OCM_03 = loginVO.OCM_03;
        mUser.Value.OCM_03_CHK = loginVO.OCM_03_CHK;
        mUser.Value.OCM_21 = loginVO.OCM_21;
        mUser.Value.OCM_28 = loginVO.OCM_28;
        mUser.Value.OCM_29 = loginVO.OCM_29;
        mUser.Value.OCM_31 = loginVO.OCM_31;
        mUser.Value.OCM_32 = loginVO.OCM_32;
        mUser.Value.OCM_51 = loginVO.OCM_51;

        goMain();

    }

    /**
     * 이메일 체크
     *
     * @return
     */
    private boolean validateEmail() {
        String email = etAccount.getText().toString();

        boolean isValid = Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+", email.trim());

        return isValid;
    }

    /**
     *
     */
    private void goMain(){
        Intent intent = new Intent(mContext, Main.class);
        mContext.startActivity(intent);
        finish();
    }

    /**
     * 메인으로 이동한다.
     */
    private void goStore() {
        Intent intent = new Intent(mContext, Main.class);
        mContext.startActivity(intent);

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        mUser.Value.RUTC_ST = formatDate;


        finish();

    }


    //========================
    // Methods
    //========================

    /**
     * 스캔화면으로 이동한다.
     */
    private void goScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanBarcode.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    /**
     * 회원가입 화면으로 이동한다.
     */
    private void goSignUp() {

        Intent intent = new Intent(mActivity, SignUp.class);
        mActivity.startActivity(intent);

    }

    //========================
    // Events
    //========================
    /**
     * 앱종료 시
     */
    private long finalBackTime;

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();

        if ((now - finalBackTime) < 2000) {
            this.finishAffinity();
        } else {
            finalBackTime = now;
            Toast.makeText(this, "이전 버튼을 한번더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            // QR 코드/ 바코드를 스캔한 결과
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            // result.getFormatName() : 바코드 종류
            // result.getContents() : 바코드 값
            Log.d("Test", "Scan Type : " + result.getFormatName() + " / Data : " + result.getContents());
        }
    }
}
