package com.linktag.linkapp.ui.settings_main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.settings.SettingsKey;
import com.linktag.base_resource.broadcast_action.ClsBroadCast;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.settings_profile.ProfileMain;

public class SettingMain extends BaseActivity {
    //===========================
    // Layout
    //===========================

    private BaseHeader header;

    private Button btnProfile;
    private Button btnPreferences;
    private Button btnLogout;
    private Button btnGoHomepage;

    private TextView tvUserName;
    private TextView tvUserPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_setting);

        initLayout();

        initialize();
    }

    /**
     * 레이아웃 초기화
     */
    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(mUser.Value.OCM_02);

        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserPhone.setText(mUser.Value.OCM_51);

        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> goProfileMain(""));

        btnPreferences = findViewById(R.id.btnPreferences);
        btnPreferences.setOnClickListener(v -> goPreference());

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());

        btnGoHomepage = findViewById(R.id.btnGoHomepage);
        btnGoHomepage.setOnClickListener(v -> goHomepage());
    }

    @Override
    protected void initialize() {

    }
    /**
     * 홈페이지로 이동
     */
    private void goHomepage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://linktag.io"));
        mContext.startActivity(intent);
    }

    /**
     * 프로필 메인
     */
    private void goProfileMain(String gub) {
        Intent intent = new Intent(mContext, ProfileMain.class);
        intent.putExtra("setPwd", gub);
        mContext.startActivity(intent);
    }

    /**
     * 환경설정
     */
    private void goPreference() {
        //Intent intent = new Intent(mContext, ProfileMain.class);
        //mContext.startActivity(intent);
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
