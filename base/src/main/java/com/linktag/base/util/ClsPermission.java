package com.linktag.base.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.linktag.base.R;

public class ClsPermission {
    public static int REQUEST_CODE = 4332;
    public static String[] ALL_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    /**
     * 권한 설정 리스너
     *
     * @author 정현
     */
    public interface OnPermissionListener {
        void permissionGranted();

        void permissionDenied();
    }

    /**
     * 퍼미션 전체 체크
     *
     * @param context
     * @return
     */
    public static boolean checkPermissionAll(Context context) {
        boolean isGranted = true;

        for (int i = 0; i < ALL_PERMISSION.length; i++) {
            if (ActivityCompat.checkSelfPermission(context, ALL_PERMISSION[i]) == PackageManager.PERMISSION_DENIED) {
                isGranted = false;
                break;
            }
        }

        return isGranted;
    }

    /**
     * 퍼미션 요청
     * @param activity
     */
    public static void requestPermissionAll(Activity activity) {
        ActivityCompat.requestPermissions(activity, ALL_PERMISSION, REQUEST_CODE);
    }

    /**
     * 퍼미션 요청
     * @param activity
     * @param permission
     */
    public static void requestPermission(Activity activity, String[] permission) {
        ActivityCompat.requestPermissions(activity, permission, REQUEST_CODE);
    }

    /**
     * 퍼미션을 체크한다.
     *
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean isGranted = false;

        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            isGranted = true;
        }

        return isGranted;
    }

    /**
     * 퍼미션 접근 시 다시 묻지 않기 인 경우엔 설정으로 보내버린다.
     *
     * @param context
     * @param permission
     */
    public static void checkPermissionResult(final Context context, String[] permission, int[] grantResult,
                                             OnPermissionListener l) {

        if (context == null) {
            return;
        }

        // 하나라도 거부된 경우에는 Denied로 처리
        boolean isGranted = true;

        for (int i = 0; i < permission.length; i++) {
            if (PackageManager.PERMISSION_DENIED == grantResult[i]) {
                isGranted = false;
                break;
            }
        }

        if (isGranted) {
            l.permissionGranted();
        } else {
            l.permissionDenied();

            // 국문일때는 무조건 메시지가 나오도록
            for (int i = 0; i < permission.length; i++) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission[i])) {
                    BaseAlert.show("접근 권한 필요",
                            "해당 기능을 사용하려면 접근 권한이 필요합니다. 설정에서 접근 권한을 허용해주세요.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goAppSettings(context);
                                }
                            }, null, "설정",
                            context.getString(R.string.common_close));
                    break;
                }
            }
        }
    }


    /**
     * 앱 설정으로 간다.
     *
     * @param context
     */
    public static void goAppSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            context.startActivity(intent);
        }
    }
}
