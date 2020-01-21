package com.linktag.base.util;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.linktag.base.R;
import com.linktag.base.base_activity.BaseActivity;


//--------------------------------------------------
// 알람창을 띄운다.
//--------------------------------------------------
public class BaseAlert {

    /**
     * 기본 알림창
     *
     * @param message 메시지
     */
    public static void show(String message) {
        show("", message, null);
    }

    /**
     * 제목있는 알림창
     *
     * @param title   제목
     * @param message 메시지
     */
    public static void show(String title, String message) {
        show(title, message, null);
    }

    /**
     * 제목없는 알림창 확인 버튼 클릭 시 이벤트를 발생시킨다.
     *
     * @param message       메시지
     * @param eventPositive 확인 이벤트
     */
    public static void show(String message, DialogInterface.OnClickListener eventPositive) {
        show("", message, eventPositive);
    }

    /**
     * 제목있는 알림창 확인 버튼 클릭 시 이벤트를 발생시킨다.
     *
     * @param title
     * @param message
     * @param eventPositive
     */
    public static void show(String title, String message, DialogInterface.OnClickListener eventPositive) {
        String alertMessage = message;
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(BaseActivity.BaseContext);
        AlertDialog dialog = customBuilder.setCancelable(false)
                .setMessage(alertMessage)
                .setTitle(title)
                .setPositiveButton(R.string.common_confirm, eventPositive)
                .create();
        dialog.show();
    }


    public static void show(String message, DialogInterface.OnClickListener eventPositive, DialogInterface.OnClickListener eventNegative) {
        show("", message, eventPositive, eventNegative);
    }


    /**
     * 알람창 기본형 확인과 취소버튼 클릭 시 이벤트를 발생시킨다.
     *
     * @param title         제목
     * @param message       메시지
     * @param eventPositive 확인 클릭 이벤트
     * @param eventNegative 취소 클릭 이벤트
     */
    public static void show(String title, String message, DialogInterface.OnClickListener eventPositive, DialogInterface.OnClickListener eventNegative) {
        String alertMessage = message;
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(BaseActivity.BaseContext);
        AlertDialog dialog = customBuilder.setCancelable(false)
                .setMessage(alertMessage)
                .setTitle(title)
                .setNegativeButton(R.string.common_cancel, eventNegative)
                .setPositiveButton(R.string.common_confirm, eventPositive)
                .create();
        dialog.show();
    }

    /**
     * * 알람창 기본형 확인과 취소버튼 클릭 시 이벤트를 발생시킨다.
     *
     * @param message       메시지
     * @param eventPositive 확인 클릭 이벤트
     * @param eventNegative 취소 클릭 이벤트
     * @param textPositive  ex) 예
     * @param textNegative  ex) 아니오
     */
    public static void show(String message, DialogInterface.OnClickListener eventPositive, DialogInterface.OnClickListener eventNegative, String textPositive, String textNegative) {
        String alertMessage = message;
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(BaseActivity.BaseContext);
        AlertDialog dialog = customBuilder.setCancelable(false)
                .setMessage(alertMessage)
                .setNegativeButton(textNegative, eventNegative)
                .setPositiveButton(textPositive, eventPositive)
                .create();
        dialog.show();
    }

    /**
     * * 알람창 기본형 확인과 취소버튼 클릭 시 이벤트를 발생시킨다.
     *
     * @param title         제목
     * @param message       메시지
     * @param eventPositive 확인 클릭 이벤트
     * @param eventNegative 취소 클릭 이벤트
     * @param textPositive  ex) 예
     * @param textNegative  ex) 아니오
     */
    public static void show(String title, String message, DialogInterface.OnClickListener eventPositive, DialogInterface.OnClickListener eventNegative, String textPositive, String textNegative) {
        String alertMessage = message;
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(BaseActivity.BaseContext);
        AlertDialog dialog = customBuilder.setCancelable(false)
                .setMessage(alertMessage)
                .setTitle(title)
                .setNegativeButton(textNegative, eventNegative)
                .setPositiveButton(textPositive, eventPositive)
                .create();
        dialog.show();
    }
}
