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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.settings.SettingsKey;
import com.linktag.base.util.ClsDateTime;
import com.linktag.base_resource.broadcast_action.ClsBroadCast;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.board.BoardMain;
import com.linktag.linkapp.ui.settings_profile.ProfileMain;

public class SettingMain extends BaseActivity {
    //===========================
    // Layout
    //===========================

    private BaseHeader header;

    private RelativeLayout layProfile;

    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserSignDate;

    private TextView btnNotice;
    private TextView btnGuide;
    private TextView btnHelp;
    private TextView btnPreferences;
    private TextView btnLogout;



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

        layProfile = findViewById(R.id.layProfile);
        layProfile.setOnClickListener(v -> goProfileMain());

        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(mUser.Value.OCM_02);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(mUser.Value.OCM_21);
        tvUserSignDate = findViewById(R.id.tvUserSignDate);
        tvUserSignDate.setText(ClsDateTime.ConvertDateFormat("yyyyMMdd", "yyyy. MM. dd", mUser.Value.OCM_28) + " 가입");

        btnNotice = findViewById(R.id.btnNotice);
        btnNotice.setOnClickListener(v -> goNotice());

        btnGuide = findViewById(R.id.btnGuide);
        btnGuide.setOnClickListener(v -> goHomepage("guide"));

        btnHelp = findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(v -> goHomepage("help"));

        btnPreferences = findViewById(R.id.btnPreferences);
        btnPreferences.setOnClickListener(v -> goPreference());

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void initialize() {

    }

    private void goNotice(){
        Intent intent = new Intent(mContext, BoardMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("DSH_GB", "NOT");
        mContext.startActivity(intent);
    }

    /**
     * 홈페이지로 이동
     */
    private void goHomepage(String gub) {
        if(gub.equals("guide")){
            // 서비스 소개
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://linktag.io/Home/Service"));
            mContext.startActivity(intent);
        } else if (gub.equals("help")){
            // 고객센터
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://linktag.io/Home/Custom"));
            mContext.startActivity(intent);
        }
    }

    /**
     * 프로필 메인
     */
    private void goProfileMain() {
        Intent intent = new Intent(mContext, ProfileMain.class);
        //intent.putExtra("setPwd", gub);
        mContext.startActivity(intent);
    }

    /**
     * 환경설정
     */
    private void goPreference() {
        Intent intent = new Intent(mContext, Preference.class);
        mContext.startActivity(intent);
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
