package com.alpaca.alarmpaca.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.alpaca.alarmpaca.AlarmReceiver;
import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.RealmUtil;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmList;

public class AlarmDetailActivity extends AppCompatActivity {

    TimePicker timePicker;
    Button cancelBtn;
    Button saveBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);

        initInstances();
    }

    private void initInstances() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        timePicker = findViewById(R.id.timePicker);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);

        cancelBtn.setOnClickListener(cancelBtnClickListener);
        saveBtn.setOnClickListener(saveBtnClickListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                Log.wtf("AlarmDetailActivity", "Home/Up btn clicked");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.wtf("AlarmDetailActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.wtf("AlarmDetailActivity", "onDestroy");
    }

    private final View.OnClickListener cancelBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(AlarmDetailActivity.this, AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(AlarmDetailActivity.this, 0, intent, 0);

            if (alarmMgr != null) {
                alarmMgr.cancel(alarmIntent);
                Log.wtf("AlarmManager", "Cancel all alarm");
            }
            finish();
        }
    };

    private final View.OnClickListener saveBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            RealmList<Integer> period = new RealmList<>();
            for (int i = 0; i < 7; i++) {
                period.add(0);
            }

            Alarm alarm = new Alarm(hour, minute, period);
            alarm.setActivated(true);

            Realm realm = RealmUtil.getRealmInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(alarm);
            realm.commitTransaction();
            realm.close();

            Log.wtf("Alarm", "New alarm : " + alarm.getId());


            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(AlarmDetailActivity.this, AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(AlarmDetailActivity.this, alarm.getId(), intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            calendar.set(Calendar.MINUTE, alarm.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (System.currentTimeMillis() >= calendar.getTimeInMillis()) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY);
                Log.wtf("AlarmManager", "Set Alarm at next day");
            }

            Log.wtf("AlarmManager", "Alarm set at " + calendar.getTime() + ",Millis : " + calendar.getTimeInMillis());

            if (alarmMgr != null) {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, alarmIntent);
                Log.wtf("AlarmManager", "Alarm set at " + calendar.getTime() + ",Millis : " + calendar.getTimeInMillis());
            }

            finish();
        }
    };
}
