package com.linktag.base.base_view_pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BaseViewPager extends ViewPager {

    private boolean m_bIsEnabled;

    public BaseViewPager(Context context) {
        super(context);

        m_bIsEnabled = true;
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_bIsEnabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (m_bIsEnabled) {
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (m_bIsEnabled) {
            return super.onTouchEvent(arg0);
        } else {
            return false;
        }
    }

    public void setPagingEnabled(boolean enabled) {
        this.m_bIsEnabled = enabled;
    }

//    @Override
//    public void setCurrentItem(int item) {
//        super.setCurrentItem(item, false);
//    }
}
