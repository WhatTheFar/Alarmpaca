package com.alpaca.alarmpaca.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alpaca.alarmpaca.activity.AlarmAlertActivity;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.RealmUtil;

import io.realm.Realm;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //perform your task
        int alarmId = intent.getExtras().getInt("id");

        Realm realm = RealmUtil.getRealmInstance();
        final Alarm alarm = realm.where(Alarm.class).equalTo("id", alarmId).findFirst();

        if (!alarm.isRepeat()) {
            realm.executeTransaction(realm1 -> alarm.setActivated(false));
            Log.wtf("Realm", "Update Success alarmId : " + alarm.getId() + ", isActivated : " + alarm.isActivated());

        } else {
            //TODO repeat alarm
        }


        Intent alarmAlertIntent = new Intent(context, AlarmAlertActivity.class);
//        alarmAlertIntent.putExtra("id", intent.getExtras().getInt("id"));
        alarmAlertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmAlertIntent);
    }
}