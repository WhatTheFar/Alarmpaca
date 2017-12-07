package com.alpaca.alarmpaca.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.alpaca.alarmpaca.util.AlarmMgrUtil;
import com.alpaca.alarmpaca.util.RealmUtil;

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

    private final View.OnClickListener cancelBtnClickListener = view -> {

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
    };

    private final View.OnClickListener saveBtnClickListener = view -> {

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        RealmList<Integer> period = new RealmList<>();
        for (int i = 0; i < 7; i++) {
            period.add(0);
        }

        final Alarm alarm = new Alarm(hour, minute, period);
        alarm.setActivated(true);

        Realm realm = RealmUtil.getRealmInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(alarm));
        realm.close();

        Log.wtf("Alarm", "New alarm : " + alarm.getId());

        AlarmMgrUtil.setAlarm(AlarmDetailActivity.this, alarm);

        finish();
    };
}
