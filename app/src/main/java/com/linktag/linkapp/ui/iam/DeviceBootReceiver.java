package com.linktag.linkapp.ui.iam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences sharedPreferences = context.getSharedPreferences("daily alarm", MODE_PRIVATE);

            int mRoutCode = sharedPreferences.getInt("mRoutCode", 1);
            String pattern = sharedPreferences.getString("alarmCycle", "");
            String array_pattern[] = pattern.split("");

            // on device boot complete, reset the alarm
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mRoutCode, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//

            long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());


            Calendar nextNotifyTime = new GregorianCalendar();
            nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime", millis));


            // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
            if (nextNotifyTime.before(Calendar.getInstance())) {

                //요일별 더하기
                int nowWeek = nextNotifyTime.get(Calendar.DAY_OF_WEEK);
                int dateAdd = 1;
                for (int i = 1; i < array_pattern.length; i++) {
                    if (nowWeek + i >= 8) {
                        nowWeek = 0;
                        i = 1;
                    }
                    if (array_pattern[nowWeek + i].equals("Y")) {
                        nextNotifyTime.add(Calendar.DATE, dateAdd);
                        break;
                    }
                    dateAdd++;
                }

            }
            Date currentDateTime = nextNotifyTime.getTime();
            String date_text = new SimpleDateFormat("yyyy.MM.dd (EE) a hh : mm ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(),"[재부팅후] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();


            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}
