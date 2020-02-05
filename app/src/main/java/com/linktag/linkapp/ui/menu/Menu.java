package com.linktag.linkapp.ui.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_view_pager.BaseViewPager;
import com.linktag.base.base_view_pager.ViewPagerAdapter;
import com.linktag.linkapp.R;

import java.util.ArrayList;
import java.util.List;

public class Menu extends BaseActivity {
    private final int TAB_PAGE_SERVICE = 0;
    private final int TAB_PAGE_SHARED = 1;
    private final int TAB_PAGE_MEMBER = 2;
    private final int TAB_PAGE_SETTING = 3;


    private ServiceFragment fragService;
    private SharedFragment fragShared;
    private MemberFragment fragMember;

        /*
    private SettingFragment fragSetting;
*/

    private BaseViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mListFragment = new ArrayList<>();


    private ImageView ivClose;
    private TextView tvMenuService;
    private TextView tvMenuShared;
    private TextView tvMenuMember;
    private TextView tvMenuSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {

        ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> finish());
        tvMenuService = findViewById(R.id.tvMenuService);
        tvMenuService.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SERVICE));
        tvMenuShared = findViewById(R.id.tvMenuShared);
        tvMenuShared.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SHARED));

        tvMenuMember = findViewById(R.id.tvMenuMember);
        tvMenuMember.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_MEMBER));
        /*
        tvMenuSetting = findViewById(R.id.tvMenuSetting);
        tvMenuSetting.setOnClickListener(v -> setCurrentViewPager(TAB_PAGE_SETTING));
*/
        initViewPager();
    }

    @Override
    protected void initialize() {
        setTag();
    }

    private void initViewPager(){
        viewPager = findViewById(R.id.viewPagerMenu);

        fragService = new ServiceFragment();
        fragShared = new SharedFragment();
        fragMember = new MemberFragment();

        fragService.setOnLoadingDialog(callLoadingBar);
        fragShared.setOnLoadingDialog(callLoadingBar);

        mListFragment.add(fragService);
        mListFragment.add(fragShared);
        mListFragment.add(fragMember);
        /*
        fragSetting = new SettingFragment();
        */

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
        tvMenuService.setSelected(false);
        tvMenuShared.setSelected(false);
        tvMenuMember.setSelected(false);

                /*
        tvMenuSetting.setSelected(false);
*/
        switch (viewPager.getCurrentItem()) {
            case TAB_PAGE_SERVICE:
                tvMenuService.setSelected(true);
                break;
            case TAB_PAGE_SHARED:
                tvMenuShared.setSelected(true);
                break;
            case TAB_PAGE_MEMBER:
                tvMenuMember.setSelected(true);
                break;

                                /*
            case TAB_PAGE_SETTING:
                tvMenuSetting.setSelected(true);
                break;
                */

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


    @Override
    public void onBackPressed() {
        finish();
    }

}
