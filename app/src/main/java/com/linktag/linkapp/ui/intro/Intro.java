package com.linktag.linkapp.ui.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.util.ClsNotificationChannel;
import com.linktag.base.util.ClsPermission;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.login.Login;
import com.linktag.linkapp.ui.permission_info.PermissionInfo;

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
