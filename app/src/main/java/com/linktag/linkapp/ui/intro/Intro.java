package com.linktag.linkapp.ui.intro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;
import com.linktag.base.util.ClsNotificationChannel;
import com.linktag.base.util.ClsPermission;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.BRDModel;
import com.linktag.linkapp.model.TKNModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.login.Login;
import com.linktag.linkapp.ui.permission_info.PermissionInfo;
import com.linktag.linkapp.value_object.TKN_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Intro extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        initialize();
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initialize() {
        ClsNotificationChannel.createNotificationChannel(mContext);

        new Handler().postDelayed(() -> {
            if (!ClsPermission.checkPermissionAll(mContext))
                goPermissionInfo();
            else
                goLogin();
        }, 2000);
    }

    /**
     * 퍼미션 설정으로 이동
     */
    private void goPermissionInfo() {
        Intent intent = new Intent(mActivity, PermissionInfo.class);
        mActivity.startActivityForResult(intent, PermissionInfo.REQUEST_CODE);
    }


    /**
     * 로그인화면으로 이동
     */
    private void goLogin() {
        Intent intent = new Intent(mContext, Login.class);
        mContext.startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PermissionInfo.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            goLogin();
        }
    }
    
}
