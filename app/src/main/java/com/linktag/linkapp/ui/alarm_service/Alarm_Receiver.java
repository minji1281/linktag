package com.linktag.linkapp.ui.alarm_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.main.JDMMain;

public class Alarm_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =new NotificationChannel("alarm_channel","알람 테스트", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("알림테스트");
            notificationManager.createNotificationChannel(notificationChannel);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, JDMMain.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, "alarm_channel");
        }
        else{
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(R.drawable.alarm_state_on)
                .setTicker("HETT")
                //.setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("푸쉬 제목")
                .setContentText("푸시내용")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}
