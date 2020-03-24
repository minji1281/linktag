package com.linktag.linkapp.ui.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.linktag.linkapp.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int ID = intent.getExtras().getInt("ID");
        String alarmTitle = intent.getExtras().getString("alarmTitle");
        String alarmText = intent.getExtras().getString("alarmText");

        //잠금화면일때도 알림
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "app:alarm");
        wl.acquire();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String className = intent.getExtras().getString("className");
        intent.setClassName(context, context.getPackageName() + className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingI = PendingIntent.getActivity(context, ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "linktag alarm");
//        builder.setContentIntent(resultPendingIntent);

////        Intent resultIntent = new Intent(context, PotMain.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(intent.getComponent());
////        intent.setClassName(context.getPackageName(), context.getPackageName() + ".ui.login.Login");
//        stackBuilder.addNextIntent(intent);
////        stackBuilder.addNextIntentWithParentStack(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(ID, PendingIntent.FLAG_UPDATE_CURRENT);


        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("linktag alarm", alarmTitle, importance);
            channel.setDescription(alarmText);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }
        else builder.setSmallIcon(R.mipmap.sym_icon_foreground); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker(alarmText) //알림발생시 잠깐 나오는 텍스트
                .setContentTitle(alarmTitle)
                .setContentText(alarmText)
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        if (notificationManager != null) {
            // 노티피케이션 동작시킴
            notificationManager.notify(ID, builder.build()); //id값은 알림채널과 연결용
        }

        wl.release();
    }

}
