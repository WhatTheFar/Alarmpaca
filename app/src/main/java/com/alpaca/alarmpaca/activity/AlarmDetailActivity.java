package com.alpaca.alarmpaca.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alpaca.alarmpaca.R;
import com.alpaca.alarmpaca.model.Alarm;
import com.alpaca.alarmpaca.util.AlarmMgrUtil;
import com.alpaca.alarmpaca.util.RealmUtil;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmList;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class AlarmDetailActivity extends AppCompatActivity implements WeekdaysDataSource.Callback {

    FloatingTextButton cancelBtn;
    FloatingTextButton saveBtn;
    TextView clockTv;

    private int mHour, mMinute;
    private Alarm intentAlarm;

    public static final String ALARM_ID_EXTRA = "alarm_id_extra";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);

        initInstances();

        WeekdaysDataSource wds = new WeekdaysDataSource(this, R.id.weekdays_stub);
        wds.start(this);
    }

    private void initInstances() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        clockTv = findViewById(R.id.clock_tv);

        cancelBtn.setOnClickListener(cancelBtnClickListener);
        saveBtn.setOnClickListener(saveBtnClickListener);
        clockTv.setOnClickListener(clockTvClickListener);

        int alarmId = getIntent().getIntExtra(ALARM_ID_EXTRA, -1);
        if (alarmId != -1) {
            Realm realm = RealmUtil.getRealmInstance();
            intentAlarm = realm.where(Alarm.class).equalTo("id", alarmId).findFirst();
            clockTv.setText(intentAlarm.getTime());

            mHour = intentAlarm.getHour();
            mMinute = intentAlarm.getMinute();
        } else {
            Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
            clockTv.setText(Alarm.getTime(mHour, mMinute));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWeekdaysItemClicked(int i, WeekdaysDataItem weekdaysDataItem) {

    }

    @Override
    public void onWeekdaysSelected(int i, ArrayList<WeekdaysDataItem> arrayList) {

    }

    private final View.OnClickListener cancelBtnClickListener = view -> finish();

    private final View.OnClickListener saveBtnClickListener = view -> {

        int hour = mHour;
        int minute = mMinute;
        RealmList<Integer> period = new RealmList<>();
        for (int i = 0; i < 7; i++) {
            period.add(0);
        }

        final Alarm alarm = new Alarm(hour, minute, period);
        alarm.setActivated(true);

        Realm realm = RealmUtil.getRealmInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(alarm));
        realm.close();

//        Log.wtf("Alarm", "New alarm : " + alarm.getId());

        //Set alarm
        AlarmMgrUtil.setAlarm(AlarmDetailActivity.this, alarm);

        if (intentAlarm != null) {
            Intent intent = new Intent();
            intent.putExtra(ALARM_ID_EXTRA, intentAlarm.getId());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    };

    private final View.OnClickListener clockTvClickListener = view -> {

        TimePickerDialog timePicker = new TimePickerDialog(
                AlarmDetailActivity.this,
                (timePicker1, hourOfDay, minute) -> {
                    mHour = hourOfDay;
                    mMinute = minute;

                    clockTv.setText(Alarm.getTime(hourOfDay, minute));
                },
                mHour,
                mMinute,
                false);

        timePicker.show();

    };


}
