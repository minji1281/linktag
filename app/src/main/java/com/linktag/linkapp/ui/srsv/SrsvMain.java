package com.linktag.linkapp.ui.srsv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.base_view_pager.BaseViewPager;
import com.linktag.base.base_view_pager.ViewPagerAdapter;
import com.linktag.linkapp.R;

import java.util.ArrayList;
import java.util.List;


public class SrsvMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    private LinearLayout layoutMenu;
    private LinearLayout layoutMenuTab;
    private TextView tvMenu1;
    private TextView tvMenu2;
    private TextView tvMenu3;
    private LinearLayout layoutShopInsert;
    private BaseViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mListFragment = new ArrayList<>();

    //======================
    // Variable
    //======================
//    private CtdVO intentVO;

    private final int TAB_PAGE_SERVICE1 = 0;
    private final int TAB_PAGE_SERVICE2 = 1;
    private final int TAB_PAGE_SERVICE3 = 2;

    private SrsvMenuFragment fragService1;
    private SrsvMenuFragment fragService2;
    private SrsvMenuFragment fragService3;

    //======================
    // Initialize
    //======================

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_srsv);

//        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        layoutMenu = findViewById(R.id.layoutMenu);
        layoutMenuTab = findViewById(R.id.layoutMenuTab);
        tvMenu1 = findViewById(R.id.tvMenu1);
        tvMenu1.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SERVICE1));
        tvMenu2 = findViewById(R.id.tvMenu2);
        tvMenu2.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SERVICE2));
        tvMenu3 = findViewById(R.id.tvMenu3);
        tvMenu3.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SERVICE3));
        layoutShopInsert = findViewById(R.id.layoutShopInsert);

        initViewPager();

    }

    @Override
    protected void initialize() {
        setTag();
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    private void initViewPager(){
        viewPager = findViewById(R.id.viewPagerMenu);

        fragService1 = new SrsvMenuFragment();
        fragService1.setGUBUN("LIST_RBM");
        fragService2 = new SrsvMenuFragment();
        fragService2.setGUBUN("RECENT_RSV");
        fragService3 = new SrsvMenuFragment();
        fragService3.setGUBUN("MY_LIST");

        fragService1.setOnLoadingDialog(callLoadingBar);
        fragService2.setOnLoadingDialog(callLoadingBar);
        fragService3.setOnLoadingDialog(callLoadingBar);

        mListFragment.add(fragService1);
        mListFragment.add(fragService2);
        mListFragment.add(fragService3);

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

    private void setTag(){
        tvMenu1.setSelected(false);
        tvMenu2.setSelected(false);
        tvMenu3.setSelected(false);

        switch (viewPager.getCurrentItem()) {
            case TAB_PAGE_SERVICE1:
                tvMenu1.setSelected(true);
                tvMenu1.setElevation(20);
                tvMenu2.setElevation(0);
                tvMenu3.setElevation(0);
                break;
            case TAB_PAGE_SERVICE2:
                tvMenu2.setSelected(true);
                tvMenu1.setElevation(0);
                tvMenu2.setElevation(20);
                tvMenu3.setElevation(0);
                break;
            case TAB_PAGE_SERVICE3:
                tvMenu3.setSelected(true);
                tvMenu1.setElevation(0);
                tvMenu2.setElevation(0);
                tvMenu3.setElevation(20);
                break;
        }
    }

    /**
     * ViewPager 이동시킨다.
     *
     * @param page
     */
    private void setCurrentViewPager(int page) {
        viewPager.setCurrentItem(page);
    }

}
