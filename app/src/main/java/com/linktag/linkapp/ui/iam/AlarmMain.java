package com.linktag.linkapp.ui.iam;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmMain {

    public void setAlarm(Context context, Intent intent) {

        String alarmTime = intent.getExtras().getString("alarmTime");
//        PotVO ppptest = (PotVO) intent.getExtras().getSerializable("ppptest");
//        Intent alarmintent = new Intent(context, AlarmReceiver.class);
//        alarmintent.putExtra("ppptest", (PotVO) intent.getExtras().getSerializable("ppptest"));

        // 알림 시간 설정
        int AlarmYear = Integer.parseInt(alarmTime.substring(0, 4));
        int AlarmMonth = Integer.parseInt(alarmTime.substring(4,6));
        int AlarmDay = Integer.parseInt(alarmTime.substring(6,8));
        int AlarmHour = Integer.parseInt(alarmTime.substring(8,10));
        int AlarmMinute = Integer.parseInt(alarmTime.substring(10,12));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, AlarmMinute);
        calendar.set(Calendar.YEAR, AlarmYear);
        calendar.set(Calendar.MONTH, AlarmMonth - 1);
        calendar.set(Calendar.DATE, AlarmDay);
        calendar.set(Calendar.HOUR_OF_DAY, AlarmHour);
        calendar.set(Calendar.MINUTE, AlarmMinute);
        calendar.set(Calendar.SECOND, 0);

//        if (calendar.before(Calendar.getInstance())) { // 알림시간이 지났으면 토스트메세지 띄우기
//            Toast.makeText(context, "알림시간이 지났습니다!\r\n일자 및 시간을 업데이트 해주세요.", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(calendar.getTime());
//            Toast.makeText(context,"알람이 " + date_text + "으로 설정되었습니다!", Toast.LENGTH_SHORT).show();
//
//            alarmNotification(context, intent, calendar);
//        }
        alarmNotification(context, intent, calendar);

    }

    void alarmNotification(Context context, Intent intent, Calendar calendar)
    {
        int mRoutCode = intent.getExtras().getInt("mRoutCode");

//        PackageManager pm = context.getPackageManager();
        intent.setClass(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mRoutCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }

        // 부팅 후 실행되는 리시버 사용가능하게 설정

//        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);

    }

    public void deleteAlarm(Context context, int ID){
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_NO_CREATE);
        if (sender != null) {
            // 이미 설정된 알람이 있는 경우

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            sender = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (sender != null) {
                am.cancel(sender);
                sender.cancel();
            }
            //Toast.makeText(context,"있어", Toast.LENGTH_SHORT).show();
        }
//        else{
//            Toast.makeText(context,"없어", Toast.LENGTH_SHORT).show();
//        }

        Toast.makeText(context,"알람이 해제되었습니다.", Toast.LENGTH_SHORT).show();
    }

}
