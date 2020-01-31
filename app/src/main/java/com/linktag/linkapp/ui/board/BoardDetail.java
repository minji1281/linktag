package com.linktag.linkapp.ui.board;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.settings.SettingsKey;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;
import com.linktag.base.util.ClsImage;
import com.linktag.base_resource.broadcast_action.ClsBroadCast;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.BRDModel;
import com.linktag.linkapp.model.NOTModel;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.BRD_VO;
import com.linktag.linkapp.value_object.NOT_VO;
import com.linktag.linkapp.value_object.OcmVO;

import java.io.File;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetail extends BaseActivity {

    public static final String WORK_STATE = "WORK_STATE";


    //======================
    // Layout
    //======================
    private BaseHeader header;

    private Button btnCancel;
    private Button btnUpdate;
    private EditText etSubjecet;
    private EditText etContent;

    String DSH_GB="";
    String DB_GB="";
    String getDSH_04 = "";
    String getDSH_05 = "";

    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        if(getIntent().getStringExtra("DSH_GB").equals("BRD")){ DSH_GB = "BRD";}
        else if(getIntent().getStringExtra("DSH_GB").equals("NOT")){ DSH_GB = "NOT";}

        if(getIntent().getStringExtra("DSH_01").equals("")){ DB_GB = "INSERT";}
        else { DB_GB = "UPDATE";}

        if(getIntent().getStringExtra("DSH_04").equals("")){ getDSH_04 = "";}else{getDSH_04 = getIntent().getStringExtra("DSH_04");}
        if(getIntent().getStringExtra("DSH_05").equals("")){ getDSH_05 = "";}else{getDSH_05 = getIntent().getStringExtra("DSH_05");}

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
    //    header.btnHeaderText.setOnClickListener(v -> requestOCM_CONTROL("UPDATE"));

        if(DSH_GB.equals("BRD")){ header.tvHeaderTitle.setText(R.string.dash_01);}
        else if(DSH_GB.equals("NOT")){ header.tvHeaderTitle.setText(R.string.dash_02);}

        Toast.makeText(mContext,getIntent().getStringExtra("DSH_01"), Toast.LENGTH_SHORT).show();

        etSubjecet = findViewById(R.id.etSubjecet);
        etSubjecet.setText(getDSH_04);

        etContent = findViewById(R.id.etContent);
        etContent.setText(getDSH_05);

        btnCancel = findViewById(R.id.btnCancel);
        if(DB_GB.equals("INSERT")){ btnCancel.setText(R.string.dash_05); btnCancel.setOnClickListener(v -> finish()); }
        else if (DB_GB.equals("UPDATE")) { btnCancel.setText(R.string.dash_12); btnCancel.setOnClickListener(v -> requestBRD_CONTROL("DELETE"));}


        btnUpdate = findViewById(R.id.btnUpdate);
        if(DSH_GB.equals("BRD")){ btnUpdate.setOnClickListener(v -> requestBRD_CONTROL(DB_GB));}
        else if(DSH_GB.equals("NOT")){ btnUpdate.setOnClickListener(v -> requestNOT_CONTROL(DB_GB));}

    }

    private boolean validationCheck(){
        if(etSubjecet.getText().toString().equals("")){
            System.out.println("############################111111");
            etSubjecet.requestFocus();
         //   Toast.makeText(mActivity, R.string.dash_10, Toast.LENGTH_LONG).show();
            BaseAlert.show(getString(R.string.dash_10));
            return false;
        }
        if(etContent.getText().toString().equals("")){
            System.out.println("############################222222");
            etContent.requestFocus();
         //  Toast.makeText(mActivity, R.string.dash_11, Toast.LENGTH_LONG).show();
            BaseAlert.show(getString(R.string.dash_11));
            return false;
        }
        return true;
    }

    private void requestBRD_CONTROL(String GUB){
        if(!validationCheck())
            return;

        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }
        String strToday = ClsDateTime.getNow("yyyyMMdd");

        openLoadingBar();
        String BRD_01 = "";

        String GUBUN = GUB;
        String BRD_ID = mUser.Value.CTM_01;
        if(GUB.equals("INSERT")){  BRD_01 = "";} else { BRD_01 = getIntent().getStringExtra("DSH_01");}
        String BRD_02 = "";
        String BRD_03 = "";
        String BRD_04 = etSubjecet.getText().toString();
        String BRD_05 = etContent.getText().toString();
        String BRD_06 = strToday;
        String BRD_98 = mUser.Value.OCM_01;
        Call<BRDModel> call = Http.commute(HttpBaseService.TYPE.POST).BRD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                BRD_ID,
                BRD_01,
                BRD_02,
                BRD_03,
                BRD_04,
                BRD_05,
                BRD_06,
                BRD_98
        );

        call.enqueue(new Callback<BRDModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<BRDModel> call, Response<BRDModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if (msg.what == 100){
                            closeLoadingBar();

                            Response<BRDModel> response = (Response<BRDModel>) msg.obj;

                            callBack_BRD(GUB, response.body().Data.get(0));
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<BRDModel> call, Throwable t){
                Log.d("Test", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void callBack_BRD(String GUB, BRD_VO data){
        System.out.println("############################5555555555");

            switch(GUB){
                case "INSERT":
                    finish();
                    //setUserPwd();
                    break;
                case "UPDATE":
                    finish();
                    //setUserPwd();
                    break;
                case "DELETE":
                    finish();
                    //setUserPwd();
                    break;
            }


    }

    private void goBoard() {
        Intent intent = new Intent(mContext, BoardMain.class);
        intent.putExtra("DSH_GB", DSH_GB);
        finish();
        mContext.startActivity(intent);

        // Log.d("***********************",String.valueOf(BRDList.get(position).RUTC_01));
    }


    private void requestNOT_CONTROL(String GUB){
        if(!validationCheck())
            return;

        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        String strToday = ClsDateTime.getNow("yyyyMMdd");

        openLoadingBar();
        String NOT_01="";

        String GUBUN = GUB;
        String NOT_ID = mUser.Value.CTM_01;
        if(GUB.equals("INSERT")){  NOT_01 = "";} else { NOT_01 = getIntent().getStringExtra("DSH_01");}
        String NOT_02 = "";
        String NOT_03 = "";
        String NOT_04 = etSubjecet.getText().toString();
        String NOT_05 = etContent.getText().toString();
        String NOT_06 = strToday;
        String NOT_98 = mUser.Value.OCM_01;
        Call<NOTModel> call = Http.commute(HttpBaseService.TYPE.POST).NOT_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                NOT_ID,
                NOT_01,
                NOT_02,
                NOT_03,
                NOT_04,
                NOT_05,
                NOT_06,
                NOT_98
        );

        call.enqueue(new Callback<NOTModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<NOTModel> call, Response<NOTModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if (msg.what == 100){
                            closeLoadingBar();

                            Response<NOTModel> response = (Response<NOTModel>) msg.obj;

                            callBack_NOT(GUB, response.body().Data.get(0));
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<NOTModel> call, Throwable t){
                Log.d("Test", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void callBack_NOT(String GUB, NOT_VO data){
        switch(GUB){
            case "INSERT":
                finish();
                //setUserPwd();
                break;
            case "UPDATE":
                finish();
                //setUserPwd();
                break;
            case "DELETE":
                finish();
                //setUserPwd();
                break;
        }

    }


    @Override
    protected void initialize() {
//        String mUserImage = "";
//        String setPwd = getIntent().getExtras().getString("setPwd", "");
//
//        // 프로필 이미지 설정
//        ClsImage.setUserPhoto(mContext, imgProfilePhoto, mUserImage, R.drawable.main_profile_no_image);
//
//        if(setPwd.equals("1")){
//            setUserPwd();
//            etOldPwd.requestFocus();
//        }
    }

//    private boolean validationCheck(String GUB){
//        if(GUB.equals("PASS"))
//        {
//            if(!mUser.Value.OCM_03.equals(etOldPwd.getText().toString())){
//                etOldPwd.requestFocus();
//                Toast.makeText(mActivity, "현재 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if(etNewPwd1.getText().toString().length() == 0){
//                etNewPwd1.requestFocus();
//                Toast.makeText(mActivity, "수정 할 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if(!etNewPwd1.getText().toString().equals(etNewPwd2.getText().toString())){
//                etNewPwd1.requestFocus();
//                Toast.makeText(mActivity, "입력된 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        } else if (GUB.equals("UPDATE")) {
//
//        }
//
//        return true;
//    }
//
//
//    private void requestOCM_CONTROL(String GUB){
//        if(!validationCheck(GUB))
//            return;
//
//        //인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        openLoadingBar();
//
//        String GUBUN = GUB;
//        String OCM_01 = mUser.Value.OCM_01;
//        String OCM_02 = etUserName.getText().toString();
//        String OCM_03 = etNewPwd1.getText().toString();
//        String OCM_31 = etDep.getText().toString();
//        String OCM_32 = etRank.getText().toString();
//        String OCM_51 = etPhoneNumber.getText().toString();
//        String OCM_98 = mUser.Value.OCM_01;
//
//        Call<OCM_Model> call = Http.ocm(HttpBaseService.TYPE.POST).OCM_CONTROL(
//                BaseConst.URL_HOST,
//                GUBUN,
//                OCM_01,
//                OCM_02,
//                OCM_03,
//                OCM_31,
//                OCM_32,
//                OCM_51,
//                OCM_98
//        );
//
//        call.enqueue(new Callback<OCM_Model>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response){
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
//                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;
//
//                            callBack(GUB, response.body().Data.get(0));
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<OCM_Model> call, Throwable t){
//                Log.d("OCM_CONTROL", t.getMessage());
//                closeLoadingBar();
//            }
//        });
//
//    }
//
//    private void callBack(String GUB, OcmVO data){
//        if(data.Validation){
//            switch(GUB){
//                case "PASS":
//                    logout();
//                    //setUserPwd();
//                    break;
//                case "UPDATE":
//                 //   setUserData(data);
//                    break;
//            }
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 사진
//        if (requestCode == REQUEST_CODE_CROP && resultCode == RESULT_OK) {
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPhoto);
//                imgProfilePhoto.setImageBitmap(bitmap);
//                mBase64 = ClsImage.getBase64ImageString(bitmap);
//                mUserImage = uriPhoto.getPath();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (requestCode == REQUEST_CODE_CROP_ALBUM && resultCode == RESULT_OK) {
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriAlbum);
//                imgProfilePhoto.setImageURI(uriAlbum);
//                mBase64 = ClsImage.getBase64ImageString(bitmap);
//                mUserImage = uriAlbum.getPath();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            if (resultCode == RESULT_OK) {
//                switch (requestCode) {
//                    case REQUEST_CODE_ALBUM_PHOTO:
//                        if (data.getData() != null) {
//                            try {
//                                uriPhoto = data.getData();
//                                cropImage();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        break;
//                    case REQUEST_CODE_PHOTO_TAKE_PHOTO:
//                        if (!fileTakePhoto.exists()) {
//                            return;
//                        }
//
//                        cropImage();
//                        break;
//                    case REQUEST_CODE_CROP:
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPhoto);
//                            imgProfilePhoto.setImageBitmap(bitmap);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//            }
//        }

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


}
