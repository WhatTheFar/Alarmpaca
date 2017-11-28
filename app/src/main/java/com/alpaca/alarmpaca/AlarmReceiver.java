package com.alpaca.alarmpaca;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.alpaca.alarmpaca.activity.MainActivity;

/**
 * Created by Far on 11/26/2017 AD.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //perform your task
        Toast.makeText(context, "Alarm alert", Toast.LENGTH_SHORT).show();

        Intent alarmIntent = new Intent(context, MainActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }
}