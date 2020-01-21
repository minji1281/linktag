package com.linktag.linkapp.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.model.RTSC_Model;
import com.linktag.linkapp.model.RUTC_Model;
import com.linktag.linkapp.ui.bus.BusMainAdapter;
import com.linktag.linkapp.API.ClovaFace;
import com.linktag.linkapp.API.WeatherAPI;
import com.linktag.linkapp.FaceCenterCircleView.FaceCenterCrop;
import com.linktag.linkapp.FaceDetectionUtil.FaceDetectionProcessor;
import com.linktag.linkapp.FaceDetectionUtil.FaceDetectionResultListener;
import com.linktag.linkapp.FaceDetectionUtil.common.CameraSource;
import com.linktag.linkapp.FaceDetectionUtil.common.CameraSourcePreview;
import com.linktag.linkapp.FaceDetectionUtil.common.FrameMetadata;
import com.linktag.linkapp.FaceDetectionUtil.common.GraphicOverlay;
import com.linktag.linkapp.Utils.Imageutils;
import com.linktag.linkapp.model.BHM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.login.Popup;
import com.linktag.linkapp.ui.work_place_search.FindWorkPlace;
import com.linktag.linkapp.value_object.BhmVO;
import com.linktag.linkapp.value_object.CfbVO;
import com.linktag.linkapp.value_object.RtscVO;
import com.linktag.base.linkapp;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_view_pager.BaseViewPager;
import com.linktag.base.base_view_pager.ViewPagerAdapter;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;
import com.linktag.base_resource.broadcast_action.ClsBroadCast;
import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.login.Login;
import com.linktag.linkapp.ui.scanner.ScanBarcode;
import com.linktag.linkapp.ui.settings_main.SettingFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolylineOverlay;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.linktag.linkapp.Utils.FaceDetectionScanner.Constants.KEY_CAMERA_PERMISSION_GRANTED;
import static com.linktag.linkapp.Utils.FaceDetectionScanner.Constants.PERMISSION_REQUEST_CAMERA;

public class Main extends BaseActivity {
    private final int TAB_PAGE_HOME = 0;
    private final int TAB_PAGE_WORK = 1;
  //  private final int TAB_PAGE_APPLY = 2;
    private final int TAB_PAGE_SETTING = 3;

    // Variable
    private CommuteFragment fragmentHome;
    private WorkFragment fragmentWork;
  //  private ApplyFragment fragmentApply;
    private SettingFragment fragmentSetting;

    private BaseViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mListFragment = new ArrayList<>();

    // Layout
    private BaseHeader header;
    private TextView tvMainHome;
    private TextView tvMainWork;
    private TextView tvMainApply;
    private TextView tvMainSetting;

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
        header.btnHeaderRight1.setOnClickListener(v -> goScan());

        tvMainHome = findViewById(R.id.tvMainHome);
        tvMainHome.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_HOME));
        tvMainWork = findViewById(R.id.tvMainWork);
        tvMainWork.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_WORK));
//        tvMainApply = findViewById(R.id.tvMainApply);
//        tvMainApply.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_APPLY));
        tvMainSetting = findViewById(R.id.tvMainSetting);
        tvMainSetting.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SETTING));

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
    //    fragmentApply = new ApplyFragment();
        fragmentSetting = new SettingFragment();

        fragmentHome.setOnLoadingDialog(callLoadingBar);
        fragmentWork.setOnLoadingDialog(callLoadingBar);
     //   fragmentApply.setOnLoadingDialog(callLoadingBar);
        fragmentSetting.setOnLoadingDialog(callLoadingBar);

        mListFragment.add(fragmentHome);
        mListFragment.add(fragmentWork);
    //    mListFragment.add(fragmentApply);
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
    //    tvMainApply.setSelected(false);
        tvMainSetting.setSelected(false);

        switch (viewPager.getCurrentItem()) {
            case TAB_PAGE_HOME:
                tvMainHome.setSelected(true);
                header.tvHeaderTitle.setText("스캔");
                header.btnHeaderRight1.setVisibility(View.VISIBLE);
                header.btnHeaderText.setVisibility(View.GONE);
                break;
            case TAB_PAGE_WORK:
                tvMainWork.setSelected(true);
                header.tvHeaderTitle.setText("대시보드");
                header.btnHeaderRight1.setVisibility(View.GONE);
                header.btnHeaderText.setVisibility(View.GONE);

                if(fragmentWork != null && fragmentWork.getContext() != null)
                    fragmentWork.requestCMT_SELECT();
                break;
//            case TAB_PAGE_APPLY:
//                tvMainApply.setSelected(true);
//                header.tvHeaderTitle.setText("연차/연장");
//                header.btnHeaderRight1.setVisibility(View.GONE);
//                header.btnHeaderText.setText("신청");
//                header.btnHeaderText.setVisibility(View.VISIBLE);

 //               break;
            case TAB_PAGE_SETTING:
                tvMainSetting.setSelected(true);
                header.tvHeaderTitle.setText("설정");
                header.btnHeaderRight1.setVisibility(View.GONE);
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
                    String qrData = result.getContents();
                    fragmentHome.setScan(qrData);

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
