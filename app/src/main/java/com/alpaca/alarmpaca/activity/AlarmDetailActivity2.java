package com.alpaca.alarmpaca.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alpaca.alarmpaca.AlarmReceiver;
import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.AlarmMgrUtil;
import com.alpaca.alarmpaca.util.RealmUtil;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmList;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class AlarmDetailActivity2 extends AppCompatActivity {

    FloatingTextButton cancelBtn, saveBtn;
    TextView textClock;
    private int mHour, mMinute;
    TimePickerDialog timePickerDialog;
    static int hourAttr, minuteAttr;


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


        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        textClock = findViewById(R.id.textClock);
        textClock.setOnClickListener(v -> {

            if (v == textClock) {

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(
                        AlarmDetailActivity2.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay,
                                                  int minute) {
                                hourAttr = hourOfDay;
                                minuteAttr = minute;
//                                textClock.setText(hourOfDay + ":" + minute);
                                textClock.setText(getTimeString(hourOfDay,minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        cancelBtn.setOnClickListener(cancelBtnClickListener);
        saveBtn.setOnClickListener(saveBtnClickListener);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setOnClickListener(this);
            }
        });

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
        Intent intent = new Intent(AlarmDetailActivity2.this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(AlarmDetailActivity2.this, 0, intent, 0);

        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
            Log.wtf("AlarmManager", "Cancel all alarm");
        }
        finish();
    };

    private final View.OnClickListener saveBtnClickListener = view -> {

        int hour = hourAttr;
        int minute = minuteAttr;
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

        AlarmMgrUtil.setAlarm(AlarmDetailActivity2.this, alarm);

        finish();
    };

    public String getTimeString(int hour, int minute){
        return (((""+hour).length() == 1)? ("0" + hour): "" + hour) + ":" + (((""+minute).length() == 1) ? ("0" + minute): "" + minute);
    }
}
