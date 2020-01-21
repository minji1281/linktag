package com.linktag.base.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

import com.linktag.base.R;
import com.linktag.base.base_activity.BaseActivity;

public class BaseLoadingBar {
    private ProgressDialog progressDialog;

    private String mMessage;

    private int mTimerCount = 0;

    private void initialize() {
        try {
            mTimerCount = 0;
            handlerClose.sendEmptyMessage(240);

            if (mMessage == null || mMessage.isEmpty())
                progressDialog = ProgressDialog.show(BaseActivity.BaseContext, "", BaseActivity.BaseContext.getString(R.string.common_loading));
            else {
                try {
                    progressDialog = ProgressDialog.show(BaseActivity.BaseContext, "", mMessage);
                } catch (Exception e) {
                    progressDialog = ProgressDialog.show(BaseActivity.BaseContext, "", BaseActivity.BaseContext.getString(R.string.common_loading));
                    e.printStackTrace();
                }
            }

            progressDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 다이얼로그생성
    public void show() {
        initialize();
    }

    // 다이얼로그종료
    public void dismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    // 메지시 변경
    public void setMessage(String message) {
        this.mMessage = message;
    }

    /**
     * 타이머 10초가 지나면 자동으로 꺼지기
     */
    @SuppressLint("HandlerLeak")
    private Handler handlerClose = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mTimerCount > 10) {
                dismiss();
                mTimerCount = 0;
            } else {
                mTimerCount++;
                handlerClose.sendEmptyMessageDelayed(240, 1000);
            }
        }
    };
}
