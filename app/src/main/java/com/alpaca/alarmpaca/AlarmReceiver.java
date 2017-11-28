package com.alpaca.alarmpaca;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alpaca.alarmpaca.activity.AlarmAlertActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //perform your task
        Toast.makeText(context, "Alarm alert", Toast.LENGTH_SHORT).show();

        Intent alarmAlertIntent = new Intent(context, AlarmAlertActivity.class);
        alarmAlertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmAlertIntent);
    }
}