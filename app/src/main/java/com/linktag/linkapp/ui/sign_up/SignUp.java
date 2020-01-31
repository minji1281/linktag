package com.linktag.linkapp.ui.sign_up;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.login.Login;
import com.linktag.linkapp.ui.main.Main;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends BaseActivity {
    //========================================
    // Layout
    //========================================
    private BaseHeader header;

    private EditText etName;
    private EditText etMobile;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    private ImageView imgShowPassword;

    private CheckBox ckbAll;
    private CheckBox ckbTerms;
    private CheckBox ckbPrivacy;
    private CheckBox ckbMarketing;

    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initLayout();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> {
            finish();
        });


        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etMobile.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etEmail = findViewById(R.id.etEmail);
        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(50);
        etEmail.setFilters(filter);

        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);

        imgShowPassword = findViewById(R.id.imgShowPassword);
        imgShowPassword.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    etPassword.setInputType(InputType.TYPE_NULL);
                    imgShowPassword.setSelected(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    imgShowPassword.setSelected(false);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    break;
            }

            return false;
        });

        ckbAll = findViewById(R.id.ckbAll);
        ckbAll.setOnClickListener(v -> setAllCheck(ckbAll.isChecked()));
        ckbTerms = findViewById(R.id.ckbTerms);
        ckbTerms.setOnClickListener(v -> checkAllCheck());
        ckbPrivacy = findViewById(R.id.ckbPrivacy);
        ckbPrivacy.setOnClickListener(v -> checkAllCheck());
        ckbMarketing = findViewById(R.id.ckbMarketing);
        ckbMarketing.setOnClickListener(v -> checkAllCheck());

        btnNext = findViewById(R.id.btnNext);
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(v -> validate());
    }

    /**
     * 휴대폰 유효성 체크
     */
    private void validate() {
        // 이메일 주소 체크
        if (!isEmptyItem(etEmail, "이메일 주소를 입력하세요."))
            return;

        if (!validateEmail()) {
            BaseAlert.show("이메일 주소를 확인하세요.");
            return;
        }

        // 이름 체크
        if (!isEmptyItem(etName, "이름을 입력하세요."))
            return;

        // 휴대폰 번호 체크
        if (!isEmptyItem(etMobile, "휴대폰 번호를 입력하세요."))
            return;

        String strMobile = etMobile.getText().toString().trim();

        if (strMobile.indexOf("010") != 0) {
            etMobile.requestFocus();
            BaseAlert.show("휴대폰 번호를 확인하세요.");
            return;
        }

        // 비밀 번호 체크
        if (!isEmptyItem(etPassword, "비밀 번호를 입력하세요."))
            return;

        String strPassword = etPassword.getText().toString().trim();

        if (!isValidPassword(strPassword)) {
            etPassword.requestFocus();
            BaseAlert.show("영문, 숫자를 포함한 7-30자 입력");
            return;
        }

        // 비밀 번호 확인
        if (!isEmptyItem(etPassword, "비밀 번호 확인을 입력하세요."))
            return;

        if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
            etPasswordConfirm.requestFocus();
            BaseAlert.show("입력된 비밀번호가 다릅니다. 비밀번호를 확인해주세요.");
            return;
        }

        requestSignUp();
    }

    /**
     * 이메일 체크
     *
     * @return
     */
    private boolean validateEmail() {
        String email = etEmail.getText().toString();

        boolean isValid = Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+", email.trim());

        return isValid;
    }

    /**
     * 비밀번호 유효성 체크
     *
     * @param password
     * @return
     */
    private boolean isValidPassword(String password) {
        int nPasswordPatternCount = 0;

        // 정규 표현식 소문자 영어가 사용되었는지
        boolean bUseAlpha = Pattern.matches(".*[a-zA-Z].*", password);

        // 정규 표현식 숫자가 사용되었는지
        boolean bUseNumber = Pattern.matches(".*[0-9].*", password);

        if (bUseAlpha)
            nPasswordPatternCount++;

        if (bUseNumber)
            nPasswordPatternCount++;

        if (nPasswordPatternCount < 2 || password.length() < 7)
            return false;

        return true;
    }

    /**
     * 회원가입 요청
     */
    private void requestSignUp() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        String strEmail = etEmail.getText().toString().trim();
        String strName = etName.getText().toString().trim();
        String strPhone = etMobile.getText().toString().trim();
        String strPassword = etPassword.getText().toString().trim();

        openLoadingBar();

        Call<OCM_Model> call = Http.member(HttpBaseService.TYPE.POST).signUp(
                BaseConst.URL_HOST,
                "INSERT",
                "",
                "",
                strName, // 이름
                strPassword, // 비밀번호
                strEmail, // 이메일
                strPhone,  // 휴대전화번호,
                ""
        );

        // Api 호출 후 response 받을 위치
        call.enqueue(new Callback<OCM_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response) {
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

                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;

                            if (response.body().Data.get(0).Validation) {
                                // UserInterface 연결
                            //    BaseAlert.show("가입을 완료하였습니다.", (dialog, which) -> goMain());
                             //   BaseAlert.show(getString(R.string.sign_up_2));
                                Toast.makeText(getApplicationContext(), getString(R.string.sign_up_2), Toast.LENGTH_SHORT).show();
                                goLogin();
                            } else {
                                // ErrorMsg
                                BaseAlert.show("가입에 실패했습니다.");
                            }
                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<OCM_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();
            }
        });
    }

    /**
     * 메인화면으로 이동한다.
     */
    private void goMain() {
        Intent intent = new Intent(mContext, Main.class);
        mContext.startActivity(intent);
        finish();
    }

    private void goLogin() {
        Intent intent = new Intent(mContext, Login.class);
        mContext.startActivity(intent);
        finish();
    }

    /**
     * 체크 에디트박스
     *
     * @param editText
     * @param message
     */
    private boolean isEmptyItem(EditText editText, String message) {
        String strEmpty = editText.getText().toString().trim();

        if (strEmpty.isEmpty()) {
            BaseAlert.show(message);
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            return false;
        }

        return true;
    }

    /**
     * 체크박스 확인
     */
    private void checkAllCheck() {
        if (ckbTerms.isChecked() && ckbPrivacy.isChecked() && ckbMarketing.isChecked())
            ckbAll.setChecked(true);
        else
            ckbAll.setChecked(false);

        if (ckbTerms.isChecked() && ckbPrivacy.isChecked())
            btnNext.setEnabled(true);
        else
            btnNext.setEnabled(false);
    }

    /**
     * 전체 체크
     *
     * @param checked
     */
    private void setAllCheck(boolean checked) {
        ckbTerms.setChecked(checked);
        ckbPrivacy.setChecked(checked);
        ckbMarketing.setChecked(checked);

        checkAllCheck();
    }

    @Override
    protected void initialize() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (etEmail.isFocused()) {
                mgr.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
                etEmail.clearFocus();
            } else if (etName.isFocused()) {
                mgr.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                etName.clearFocus();
            } else if (etMobile.isFocused()) {
                mgr.hideSoftInputFromWindow(etMobile.getWindowToken(), 0);
                etMobile.clearFocus();
            } else if (etPassword.isFocused()) {
                mgr.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
                etPassword.clearFocus();
            } else if (etPasswordConfirm.isFocused()) {
                mgr.hideSoftInputFromWindow(etPasswordConfirm.getWindowToken(), 0);
                etPasswordConfirm.clearFocus();
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
