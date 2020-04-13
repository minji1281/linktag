package com.linktag.linkapp.ui.iam;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.linktag.linkapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String goService = intent.getExtras().getString("goService");

        intent.setClassName(context.getPackageName(), context.getPackageName() + goService);
        intent.setAction("startForeground");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }


//        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP
//                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(showIntent);


//        String toActivity = intent.getExtras().getString("toActivity");
//
//        intent.setClassName(context, context.getPackageName() + toActivity);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), mRoutCode,
//                intent, PendingIntent.FLAG_ONE_SHOT);
//        try {
//        Toast.makeText(context, "외않됌", Toast.LENGTH_LONG).show();
//            pendingIntent.send();
//        } catch (PendingIntent.CanceledException e) {
//            e.printStackTrace();
//        }

//         intent로부터 전달받은 string
//        String state = intent.getExtras().getString("state");
//
//        // RingtonePlayingService 서비스 intent 생성
//            intent.setClassName(context, context.getPackageName() + className);
//        // start the ringtone service
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
//            context.startForegroundService(intent);
//        }else{
//            context.startService(intent);
//        }

//        Intent intent1 = new Intent(context, IamDetail.class);
//        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent1);


//        //잠금화면일때도 알림
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(
//                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
//                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
//                        PowerManager.ON_AFTER_RELEASE, "app:alarm");
//        wl.acquire();

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        intent.setClassName(context, context.getPackageName() + className);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, mRoutCode,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "linktag alarm");
//
//
//        //OREO API 26 이상에서는 채널 필요
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            builder.setSmallIcon(R.drawable.ic_launcher); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
//
//            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌
//
//            NotificationChannel channel = new NotificationChannel("linktag alarm", alarmTitle, importance);
//            channel.setDescription(alarmText);
//
//            if (notificationManager != null) {
//                // 노티피케이션 채널을 시스템에 등록
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//        else builder.setSmallIcon(R.mipmap.sym_icon_foreground); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
//
//        builder.setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setTicker(alarmText) //알림발생시 잠깐 나오는 텍스트
//                .setContentTitle(alarmTitle)
//                .setContentText(alarmText)
//                .setContentInfo("INFO")
//                .setContentIntent(pendingIntent);
//
//        if (notificationManager != null) {
//            // 노티피케이션 동작시킴
//            notificationManager.notify(mRoutCode, builder.build()); //id값은 알림채널과 연결용
//
//            Calendar nextNotifyTime = Calendar.getInstance();
//
//            // 내일 같은 시간으로 알람시간 결정
//            nextNotifyTime.add(Calendar.DATE, 1);
//
//            //  Preference에 설정한 값 저장
//            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
//            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
//            editor.apply();
//
//            Date currentDateTime = nextNotifyTime.getTime();
//            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
//            Toast.makeText(context.getApplicationContext(),"다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
//
//        }

//        wl.release();
    }

    private void sendToActivity(Context context) {

        Intent intent = new Intent(context, IamDetail.class);

        //화면이 없는곳에서 화면을 띄울 때 사용되는 플래그
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }


}
