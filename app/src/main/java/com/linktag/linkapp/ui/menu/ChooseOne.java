package com.linktag.linkapp.ui.menu;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_view_pager.BaseViewPager;
import com.linktag.base.base_view_pager.ViewPagerAdapter;
import com.linktag.base.util.BaseLoadingBar;
import com.linktag.linkapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseOne extends BaseActivity {
    private final int CHOOSE_PAGE_SERVICE = 0;
    private final int CHOOSE_PAGE_SHARED = 1;

    private ServiceFragment fragService;
    private SharedFragment fragShared;

    private BaseViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mListFragment = new ArrayList<>();

    private TextView tvChooseService;
    private TextView tvChooseShared;

    private String scanCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chooseone);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        tvChooseService = findViewById(R.id.tvChooseService);
        tvChooseService.setOnClickListener(v -> setCurrentViewPager(CHOOSE_PAGE_SERVICE));
        tvChooseShared = findViewById(R.id.tvChooseShared);
        tvChooseShared.setOnClickListener(v -> setCurrentViewPager(CHOOSE_PAGE_SHARED));

        initViewPager();
    }

    @Override
    protected void initialize() {
        setTag();
    }

    private void initViewPager(){
        viewPager = findViewById(R.id.viewPagerChoose);

        fragService = new ServiceFragment();
        fragShared = new SharedFragment();

        //fragService.setOnLoadingDialog(callLoadingBar);
        //fragShared.setOnLoadingDialog(callLoadingBar);

        mListFragment.add(fragService);
        mListFragment.add(fragShared);

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

        scanCode = getIntent().getStringExtra("scanCode");
        Bundle bundle = new Bundle(1);
        bundle.putString("scanCode", scanCode);

        fragShared.setArguments(bundle);
        fragService.setArguments(bundle);

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        // 바깥 레이어 클릭시 안닫히게
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        else
            return true;
    }


    private void setTag(){
        tvChooseService.setSelected(false);
        tvChooseShared.setSelected(false);

        switch (viewPager.getCurrentItem()) {
            case CHOOSE_PAGE_SERVICE:
                tvChooseService.setSelected(true);
                break;
            case CHOOSE_PAGE_SHARED:
                tvChooseShared.setSelected(true);
                break;
        }
    }


    private void setCurrentViewPager(int page) {
        viewPager.setCurrentItem(page);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void closePop(View v){
        finish();
    }
}
