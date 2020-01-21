package com.linktag.base.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

public class ClsNotificationChannel {

    public static final String CHANNEL_ID_DEFAULT = "10000000000";
    public static final String CHANNEL_ID_PUSH = "10000000001";

    public static final String CHANNEL_NAME_DEFAULT = "CHANNEL_NAME_DEFAULT";
    public static final String CHANNEL_NAME_PUSH = "CHANNEL_NAME_PUSH";

    public static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //일반 로컬 NotificationChannel 설정
            NotificationChannel notificationChannelDefault = new NotificationChannel(CHANNEL_ID_DEFAULT, CHANNEL_NAME_DEFAULT, NotificationManager.IMPORTANCE_HIGH);
            notificationChannelDefault.setDescription("Default Notification Channel");
            notificationChannelDefault.enableLights(true);
            notificationChannelDefault.setLightColor(Color.GREEN);
            notificationChannelDefault.enableVibration(true);
            notificationChannelDefault.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannelDefault.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannelDefault);

            //PUSH NotificationChannel 설정
            NotificationChannel notificationChannelPUSH = new NotificationChannel(CHANNEL_ID_PUSH, CHANNEL_NAME_PUSH, NotificationManager.IMPORTANCE_HIGH);
            notificationChannelPUSH.setDescription("PUSH Notification Channel");
            notificationChannelPUSH.enableLights(true);
            notificationChannelPUSH.setLightColor(Color.GREEN);
            notificationChannelPUSH.enableVibration(true);
            notificationChannelPUSH.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannelPUSH.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannelPUSH);
        }

    }

    public static void deleteNotificationChannel(Context context, String channelID) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //로컬 NotificationChannel 삭제
            notificationManager.deleteNotificationChannel(channelID);
        }
    }
}
