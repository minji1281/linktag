package com.linktag.linkapp.ui.permission_info;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsPermission;
import com.linktag.linkapp.R;

public class PermissionInfo extends BaseActivity {
    // ----------------------
    // final
    // ----------------------
    public static final int REQUEST_CODE = 7576;

    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_info);

        initLayout();

        initialize();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }

    @Override
    protected void initLayout() {
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> requestPermission());

    }

    @Override
    protected void initialize() {

    }

    private void requestPermission() {
        ClsPermission.requestPermissionAll(mActivity);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ClsPermission.REQUEST_CODE) {
            // 퍼미션 여부를 찾는다.
            ClsPermission.checkPermissionResult(mActivity, permissions, grantResults, new ClsPermission.OnPermissionListener() {
                @Override
                public void permissionGranted() {
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void permissionDenied() {
                    BaseAlert.show("권한을 허용하지 않으면 앱을 사용할 수 없습니다.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClsPermission.goAppSettings(mContext);
                            finish();
                        }
                    });
                }
            });
        }
    }
}
