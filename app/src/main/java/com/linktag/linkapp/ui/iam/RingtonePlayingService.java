package com.linktag.linkapp.ui.iam;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.jdm.JdmDetail;

public class RingtonePlayingService extends Service {
    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;

    private Ringtone r;
    Vibrator vibrator;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

//        if (Build.VERSION.SDK_INT >= 26) {
//            String CHANNEL_ID = "default";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
//
//            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("알람시작")
//                    .setContentText("알람음이 재생됩니다.")
//                    .setSmallIcon(R.drawable.ic_launcher)
//
//                    .build();
//
//            startForeground(0, notification);
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction().equals("startForeground")) {
            //test(intent);
            startForgroundService(intent);

        }


//        Toast.makeText(getApplicationContext(), "외않됌", Toast.LENGTH_LONG).show();
//        Intent showIntent = new Intent(context, IamDetail.class);
//        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(showIntent);


//        String getState = intent.getExtras().getString("state");
//
//        assert getState != null;
//        switch (getState) {
//            case "alarm on":
//                startId = 1;
//                break;
//            case "alarm off":
//                startId = 0;
//                break;
//            default:
//                startId = 0;
//                break;
//        }
//
//        // 알람음 재생 X , 알람음 시작 클릭
//        if (!this.isRunning && startId == 1) {
//
//            vibrator.vibrate(5000);
//
//        }


        return START_STICKY;

    }

//    private void test(Intent intent){
//        Context mContext = getApplicationContext();
//        String toActivity = intent.getExtras().getString("toActivity");
//
//        intent.setClassName(mContext, getPackageName() + toActivity);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP
//                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }

    private void startForgroundService(Intent intent) {




        Context mContext = getApplicationContext();
        String alarmTitle = intent.getExtras().getString("alarmTitle");
        String alarmText = intent.getExtras().getString("alarmText");
        int mRoutCode = intent.getIntExtra("mRoutCode",0);
        String goActivity = intent.getExtras().getString("goActivity");

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        intent.setClassName(mContext, mContext.getPackageName() + goActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, mRoutCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "linktag alarm");


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
        } else
            builder.setSmallIcon(R.mipmap.sym_icon_foreground); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker(alarmText) //알림발생시 잠깐 나오는 텍스트
                .setContentTitle(alarmTitle)
                .setContentText(alarmText)
                .setContentInfo("INFO")
                .setFullScreenIntent(pendingIntent, true);
//                .setContentIntent(pendingIntent);

        startForeground(mRoutCode, builder.build());

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build();
        r.setAudioAttributes(audioAttributes);
        r.play();


        InfinityAlarm();
    }

    public void InfinityAlarm() {

        long[] pattern = {1000, 3000, 1000, 3000};
        vibrator.vibrate(pattern, 0);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
        r.stop();
        Log.d("onDestory() 실행", "서비스 파괴");

    }
}
