package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_footer.BaseFooter;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.base_view_pager.BaseViewPager;
import com.linktag.base.base_view_pager.ViewPagerAdapter;
import com.linktag.base_resource.broadcast_action.ClsBroadCast;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.arclayout.arclayoutMain;
import com.linktag.linkapp.ui.login.Login;
import com.linktag.linkapp.ui.menu.ChangeActivityCls;
import com.linktag.linkapp.ui.menu.ChooseOne;
import com.linktag.linkapp.ui.menu.ChooseScan;
import com.linktag.linkapp.ui.scanner.ScanResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main extends BaseActivity {
    private final int TAB_PAGE_HOME = 0;
    private final int TAB_PAGE_CALENDAR = 1;

    // Variable
    private HomeFragment fragmentHome;
    private CalendarFragment fragmentCalendar;

    private BaseViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mListFragment = new ArrayList<>();

    // Layout
    private BaseHeader header;
    private BaseFooter footer;

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

        if (getIntent().hasExtra("toActivity")) {
            requestCTD_SELECT();
        }
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.layoutHeader.setBackgroundColor(getResources().getColor(R.color.header_background));
        header.ivHeaderTitle.setVisibility(View.VISIBLE);

//        header.tvHeaderTitle.setTextColor(Color.WHITE);

        footer = findViewById(R.id.footer);
        footer.btnFooterHome.setSelected(true);

        footer.btnFooterMember.setVisibility(View.GONE);
        footer.btnFooterSetting.setVisibility(View.VISIBLE);

        //footer.btnFooterMember.setOnClickListener(v -> goMember());

        //     footer.btnFooterScan.setOnClickListener(v -> goScan());
        footer.btnFooterScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, arclayoutMain.class);
//                intent.putExtra("DSH_01", "");
//                intent.putExtra("DSH_04", "");
//                intent.putExtra("DSH_05", "");
//                intent.putExtra("DSH_09", "0");
//                intent.putExtra("DSH_97","");
//                intent.putExtra("DSH_GB", "NOT");

                mContext.startActivity(intent);
            }
        });
        //footer.btnFooterMenu.setOnClickListener(v -> goMenu());
        //footer.btnFooterSetting.setOnClickListener(v -> goSettingMain());

        footer.btnFooterHome.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_HOME));
        footer.btnFooterCalendar.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_CALENDAR));

        initViewPager();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        //액티비티 애니메이션 x
    }

    @Override
    protected void initialize() {
        registerReceiver();
        setTag();


        if (getIntent().hasExtra("startFragment")) {
            String g = getIntent().getStringExtra("startFragment");

            if (g.equals("home"))
                setCurrentViewPager(TAB_PAGE_HOME);
            else if (g.equals("calendar"))
                setCurrentViewPager(TAB_PAGE_CALENDAR);
        }

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

        fragmentHome = new HomeFragment();
        fragmentCalendar = new CalendarFragment();

        //fragmentHome.setOnLoadingDialog(callLoadingBar);
        //fragmentCalendar.setOnLoadingDialog(callLoadingBar);

        mListFragment.add(fragmentHome);
        mListFragment.add(fragmentCalendar);

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
        footer.btnFooterHome.setSelected(false);
        footer.btnFooterCalendar.setSelected(false);

        switch (viewPager.getCurrentItem()) {
            case TAB_PAGE_HOME:
                footer.btnFooterHome.setSelected(true);
//                header.tvHeaderTitle.setText(R.string.home_01);
//                header.tvHeaderTitle.setText("LINK TAG");
                break;
            case TAB_PAGE_CALENDAR:
                footer.btnFooterCalendar.setSelected(true);
//                header.tvHeaderTitle.setText(R.string.home_02);
//                header.tvHeaderTitle.setText("LINK TAG");
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


    private void requestCTD_SELECT() {
        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "PUSH_DETAIL",
                getIntent().getStringExtra("CTD_01"),
                getIntent().getStringExtra("CTD_02"),
                mUser.Value.OCM_01,
                ""
        );

        call.enqueue(new Callback<CTD_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTD_Model> call, Response<CTD_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

                            callBack(response.body());

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CTD_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                //closeLoadingBar();
            }
        });
    }

    private void callBack(CTD_Model model) {
        if (model.Total > 1) {
            // 결과 여러건
            Intent intent = new Intent(mContext, ChooseScan.class);
            intent.putExtra("mList", model.Data);
            mContext.startActivity(intent);
        } else {
            // 결과 1개
            // Detail
            // Detail 조회 페이지 이동
            ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, model.Data.get(0));
            changeActivityCls.changeServiceWithScan(getIntent().getStringExtra("scanCode"));
        }

    }


    /**
     * 바코드를 스캔한다.
     */
//    private void goScan() {
//        IntentIntegrator integrator = new IntentIntegrator(this);
//        integrator.setCaptureActivity(ScanBarcode.class);
//        integrator.setOrientationLocked(false);
//        integrator.initiateScan();
//    }

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
    protected void scanResult(String str) {
        ScanResult scanResult = new ScanResult(mContext, str, null);
        scanResult.run();
    }

    public void clearAppCache(File dir) {
        if (dir == null)
            dir = getCacheDir();
        if (dir == null)
            return;

        File[] caches = dir.listFiles();

        for (File aCache : caches) {
            if (aCache.isDirectory()) {
                clearAppCache(aCache);
            } else {
                aCache.delete();
            }
        }
    }


    protected long IBackPressedTime;

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();

        if ((now - IBackPressedTime) < 2 * 1000) {
            unregisterReceiver();

            //clearAppCache(null);

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
