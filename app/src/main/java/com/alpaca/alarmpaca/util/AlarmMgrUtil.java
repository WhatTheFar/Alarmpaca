package com.alpaca.alarmpaca.util;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.alpaca.alarmpaca.AlarmReceiver;
import com.alpaca.alarmpaca.model.Alarm;

import java.util.Calendar;

import io.realm.Realm;

public class AlarmMgrUtil {

    @SuppressLint("ObsoleteSdkInt")
    public static void setAlarm(Context context, Alarm alarm) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", alarm.getId());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, 0);

        Calendar calendar = alarm.getCalendarInstance();
        calendar.getTime();

        if (System.currentTimeMillis() >= calendar.getTimeInMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            Log.wtf("AlarmManager", "Alarm setExact at " + calendar.getTime() + ",Millis : " + calendar.getTimeInMillis());

        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            Log.wtf("AlarmManager", "Alarm set at " + calendar.getTime() + ",Millis : " + calendar.getTimeInMillis());
        }
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        cancelAlarm(context, alarm.getId());
    }

    public static void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        alarmMgr.cancel(alarmIntent);
        Log.wtf("AlarmManger", "Cancel alarm, requestCode : " + alarmId);

    }
}
