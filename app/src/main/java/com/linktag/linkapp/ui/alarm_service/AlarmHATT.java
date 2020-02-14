package com.linktag.linkapp.ui.alarm_service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.linktag.linkapp.ui.main.JDMMain;

import java.util.Calendar;

public class AlarmHATT {

    private Context context;

    public AlarmHATT(Context context) {
        this.context = context;
    }

    public void Alarm(String datetime) {


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, Alarm_Receiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(datetime.substring(0,4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(datetime.substring(4,6))-1);
        calendar.set(Calendar.DATE, Integer.parseInt(datetime.substring(6,8)));
        calendar.set(Calendar.HOUR, Integer.parseInt(datetime.substring(8,10)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(datetime.substring(10,12)));
        calendar.set(Calendar.SECOND, 0);



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //API 19 이상 API 23미만
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                Toast.makeText(context, "19이상 23미만", Toast.LENGTH_LONG).show();
            } else {
                //API 19미만
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

                Toast.makeText(context, "19미만", Toast.LENGTH_LONG).show();
            }
        } else {
            //API 23 이상
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        }
    }
}
