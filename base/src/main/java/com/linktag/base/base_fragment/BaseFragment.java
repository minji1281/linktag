package com.linktag.base.base_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.linktag.base.settings.InterfaceSettings;
import com.linktag.base.user_interface.InterfaceUser;

public class BaseFragment extends Fragment {
    protected Context mContext;
    protected Activity mActivity;
    protected InterfaceSettings mSettings;
    protected InterfaceUser mUser;

    public BaseFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();

        mActivity = getActivity();

        mSettings = InterfaceSettings.getInstance(mContext);

        mUser = InterfaceUser.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mContext = context;
            mActivity = (Activity) context;
            mSettings = InterfaceSettings.getInstance(mContext);
            mUser = InterfaceUser.getInstance();
        }
    }

    //=========================
    // 로딩바 구현
    //=========================
    public interface CallLoadingBar {
        void callOpenLoadingBar();

        void callCloseLoadingBar();
    }

    // Loading Callback
    protected CallLoadingBar callLoadingBar;

    public void setOnLoadingDialog(CallLoadingBar callLoadingBar) {
        this.callLoadingBar = callLoadingBar;
    }

    protected void openLoadingBar() {
        if (callLoadingBar != null)
            callLoadingBar.callOpenLoadingBar();
    }

    protected void closeLoadingBar() {
        if (callLoadingBar != null)
            callLoadingBar.callCloseLoadingBar();
    }
}
