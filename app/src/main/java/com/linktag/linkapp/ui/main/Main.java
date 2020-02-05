package com.linktag.linkapp.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.base_view_pager.BaseViewPager;
import com.linktag.base.base_view_pager.ViewPagerAdapter;
import com.linktag.base_resource.broadcast_action.ClsBroadCast;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.login.Login;
import com.linktag.linkapp.ui.menu.Menu;
import com.linktag.linkapp.ui.scanner.ScanBarcode;
import com.linktag.linkapp.ui.settings_main.SettingFragment;
import com.linktag.linkapp.ui.work_place_search.FindWorkPlace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main extends BaseActivity {
    private final int TAB_PAGE_HOME = 0;
    private final int TAB_PAGE_COMMENT = 1;
    private final int TAB_PAGE_APPLY = 2;
    private final int TAB_PAGE_SETTING = 3;

    // Variable
    private CommuteFragment fragmentHome;
    private WorkFragment fragmentWork;
    private ApplyFragment fragmentApply;
    private SettingFragment fragmentSetting;



    private BaseViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mListFragment = new ArrayList<>();

    // Layout
    private BaseHeader header;
    private TextView tvMainHome;
    private TextView tvMainWork;
    private TextView tvMainScan;
    private TextView tvMainSetting;
    private  TextView tvMainJdm;  //장독 테스트

    private BroadcastReceiver mBroadcastLogout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ClsBroadCast.BROAD_CAST_ACTION_LOGOUT)) {
                goLogin();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        initLayout();

        initialize();

        checkPwd();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderRight3.setOnClickListener(v -> goMenu());

        //header.btnHeaderRight1.setOnClickListener(v -> goScan());

        tvMainHome = findViewById(R.id.tvMainHome);
        tvMainHome.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_HOME));
        tvMainWork = findViewById(R.id.tvMainWork);
        tvMainWork.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_COMMENT));
        tvMainScan = findViewById(R.id.tvMainScan);
        //  tvMainScan.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_APPLY));
        tvMainScan.setOnClickListener(v -> goScan());
        tvMainSetting = findViewById(R.id.tvMainSetting);
        tvMainSetting.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SETTING));

        //장독테스트
        tvMainJdm = findViewById(R.id.tvMainJdm);

        tvMainJdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, JDMMain.class);
                startActivity(intent);
            }
        });

        initViewPager();
    }

    @Override
    protected void initialize() {
        registerReceiver();
        setTag();

    }

    private void checkPwd(){
//        if(mUser.Value.EMP_15_CHK.equals("SAME")){
//            new AlertDialog.Builder(mActivity)
//                    .setMessage("아이디와 비밀번호가 같습니다.\n비밀번호 변경 페이지로 이동하시겠습니까?")
//                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(mContext, ProfileMain.class);
//
//                            intent.putExtra("setPwd", "1");
//
//                            mContext.startActivity(intent);
//                        }
//                    })
//                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            return;
//                        }
//                    })
//                    .setCancelable(false)
//                    .show();
//        }

    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastLogout,
                new IntentFilter(ClsBroadCast.BROAD_CAST_ACTION_LOGOUT));
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastLogout);
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.viewPagerMain);

        fragmentHome = new CommuteFragment();
        fragmentWork = new WorkFragment();
        fragmentApply = new ApplyFragment();
        fragmentSetting = new SettingFragment();

        fragmentHome.setOnLoadingDialog(callLoadingBar);
        fragmentWork.setOnLoadingDialog(callLoadingBar);
        fragmentApply.setOnLoadingDialog(callLoadingBar);
        fragmentSetting.setOnLoadingDialog(callLoadingBar);

        mListFragment.add(fragmentHome);
        mListFragment.add(fragmentWork);
        mListFragment.add(fragmentApply);
        mListFragment.add(fragmentSetting);

        mViewPagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), mListFragment);
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setOffscreenPageLimit(mListFragment.size() - 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTag();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 화면 탭 설정
     */
    private void setTag() {
        tvMainHome.setSelected(false);
        tvMainWork.setSelected(false);
        tvMainScan.setSelected(false);
        tvMainSetting.setSelected(false);

        switch (viewPager.getCurrentItem()) {
            case TAB_PAGE_HOME:
                tvMainHome.setSelected(true);
                header.tvHeaderTitle.setText(R.string.home_01);
                header.btnHeaderRight1.setVisibility(View.GONE);
                header.btnHeaderRight3.setVisibility(View.VISIBLE);
                header.btnHeaderText.setVisibility(View.GONE);
                break;
            case TAB_PAGE_COMMENT:
                tvMainWork.setSelected(true);
                header.tvHeaderTitle.setText(R.string.home_02);
                header.btnHeaderRight1.setVisibility(View.GONE);
                header.btnHeaderRight3.setVisibility(View.VISIBLE);
                header.btnHeaderText.setVisibility(View.GONE);

//                if(fragmentWork != null && fragmentWork.getContext() != null)
//                    fragmentWork.requestCMT_SELECT();
                break;
            case TAB_PAGE_APPLY:
                tvMainScan.setSelected(true);
                header.tvHeaderTitle.setText(R.string.home_03);
                header.btnHeaderRight1.setVisibility(View.GONE);
                header.btnHeaderRight3.setVisibility(View.VISIBLE);
                header.btnHeaderText.setText("신청");
                header.btnHeaderText.setVisibility(View.GONE);
                break;
            case TAB_PAGE_SETTING:
                tvMainSetting.setSelected(true);
                header.tvHeaderTitle.setText(R.string.home_04);
                header.btnHeaderRight1.setVisibility(View.GONE);
                header.btnHeaderRight3.setVisibility(View.VISIBLE);
                header.btnHeaderText.setVisibility(View.GONE);
                break;

        }
    }

    /**
     * 로그인으로 이동한다.
     */
    private void goLogin() {
        Intent intent = new Intent(mContext, Login.class);
        mContext.startActivity(intent);
    }

    /**
     * 바코드를 스캔한다.
     */
    private void goScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanBarcode.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    /**
     * 메뉴 화면 이동
     */
    private void goMenu(){
        Intent intent = new Intent(mContext, Menu.class);
        mContext.startActivity(intent);
    }

    /**
     * ViewPager 이동시킨다.
     *
     * @param page
     */
    private void setCurrentViewPager(int page) {
        viewPager.setCurrentItem(page);
    }

    //================================
    // Event
    //================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FindWorkPlace.REQUEST_CODE:
                if (fragmentHome != null)
                    fragmentHome.onActivityResult(requestCode, resultCode, data);
                break;
            case IntentIntegrator.REQUEST_CODE:
                // QR 코드/ 바코드를 스캔한 결과
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (result.getFormatName() != null)
                {
//                    String qrData = result.getContents();
//                    fragmentHome.setScan(qrData);

                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }


        }
    }

    public void clearAppCache(File dir){
        if (dir == null)
            dir = getCacheDir();
        if (dir == null)
            return;

        File[] caches = dir.listFiles();

        for (File aCache : caches){
            if(aCache.isDirectory()){
                clearAppCache(aCache);
            } else {
                aCache.delete();
            }
        }
    }


    protected long IBackPressedTime;

    @Override
    public void onBackPressed(){
        long now = System.currentTimeMillis();

        if((now - IBackPressedTime) < 2 * 1000){
            unregisterReceiver();

            clearAppCache(null);

            this.finishAffinity();

            this.overridePendingTransition(0, 0);
        } else {
            IBackPressedTime = now;
            Toast.makeText(getBaseContext(), (String) getText(R.string.main_back), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }


}
