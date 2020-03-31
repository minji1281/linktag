package com.linktag.linkapp.ui.alarm_service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.jdm.JdmMain;
import com.linktag.linkapp.ui.login.Login;
import com.linktag.linkapp.ui.main.Main;
import com.linktag.linkapp.value_object.JdmVO;

public class Alarm_Receiver extends BroadcastReceiver {


    @SuppressLint("StaticFieldLeak")
    @Override
    public void onReceive(Context context, Intent intent) {

        int notify_id = intent.getExtras().getInt("notify_id");
        String contentTitle = intent.getExtras().getString("contentTitle");
        String contentText = intent.getExtras().getString("contentText");
        String className = intent.getExtras().getString("className");
        String gotoActivity = intent.getExtras().getString("gotoActivity");
        String gotoLogin = intent.getExtras().getString("gotoLogin");
        String gotoMain = intent.getExtras().getString("gotoMain");

        JdmVO jdmVO = (JdmVO) intent.getSerializableExtra("JdmVO");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("alarm_channel", "알람 테스트", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("알림테스트");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        intent.setClassName(context, context.getPackageName() + gotoMain);


        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent login = new Intent(context, Login.class);
        Intent intro = new Intent(context, Main.class);
//        Intent intro = intent;
//        intro.setClassName(context, context.getPackageName()+"JdmMain");
//        intro.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notify_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivities(context, notify_id, new Intent[]{intro, intent}, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, "alarm_channel");
        } else {
            builder = new Notification.Builder(context);
        }
        Intent intent2 = new Intent(context, JdmMain.class);


        builder.setSmallIcon(R.drawable.alarm_state_on)
                .setTicker("HETT")
                .setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(contentTitle)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationManager.notify(notify_id, builder.build());
    }


}
