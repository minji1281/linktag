package com.linktag.base.util;

import android.app.Activity;
import android.content.Intent;

public class ClsUtil {
    public static void forceRestartAppforActivity(final Activity activity) {
        try {
            Intent i = activity.getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);

            // 현재 활성화 되어있는 모든 Activity를 종료한다.
            activity.finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
