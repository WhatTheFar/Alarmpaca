package com.alpaca.alarmpaca.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.AlarmMgrUtil;
import com.alpaca.alarmpaca.util.RealmUtil;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Realm realm = RealmUtil.getRealmInstance();
            RealmResults<Alarm> results = realm.where(Alarm.class).equalTo("isActivated", true).findAll();

            for (Alarm alarm : results
                    ) {
                AlarmMgrUtil.setAlarm(context, alarm);

//                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//                PendingIntent alarmPi = PendingIntent.getBroadcast(context, alarm.getId(), alarmIntent, 0);
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
//                calendar.set(Calendar.MINUTE, alarm.getMinute());
//                calendar.set(Calendar.SECOND, 0);
//                calendar.set(Calendar.MILLISECOND, 0);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPi);
//                }
//
//                Log.wtf("AlarmManager", "Alarm set at " + calendar.getTime() + ",Millis : " + calendar.getTimeInMillis());
            }
        }
    }
}
