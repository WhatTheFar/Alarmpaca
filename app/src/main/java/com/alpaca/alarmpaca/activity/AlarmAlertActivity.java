package com.alpaca.alarmpaca.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.alpaca.alarmpaca.AlarmService;
import com.alpaca.alarmpaca.R;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import java.text.DateFormat;
import java.util.Date;

import static com.alpaca.alarmpaca.AlarmService.BROADCAST_ALARM_SERVICE_END;


public class AlarmAlertActivity extends AppCompatActivity {

    TextClock textClock;
    ImageView doubleAlpacaImageView, shakingImageView;
    Animation shakeAnim, fadeInAnim;
    TextView tvDateAlert;

    CustomAnalogClock customAnalogClock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_alert);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initInstances();

        LocalBroadcastManager.getInstance(AlarmAlertActivity.this)
                .registerReceiver(alarmBroadcastReceiver, new IntentFilter(BROADCAST_ALARM_SERVICE_END));

        Intent alertIntent = new Intent(this, AlarmService.class);
        startService(alertIntent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.wtf("AlarmAlertActivity", "onDestroy");

        LocalBroadcastManager.getInstance(AlarmAlertActivity.this)
                .unregisterReceiver(alarmBroadcastReceiver);
    }

    private void initInstances() {
        customAnalogClock = findViewById(R.id.analog_clock);
        customAnalogClock.setAutoUpdate(true);

        textClock = findViewById(R.id.textClock);
        textClock.setFormat12Hour("hh:mm a");
        textClock.setFormat24Hour(null);

        tvDateAlert = findViewById(R.id.tvDateAlert);

        Date date = new Date();
        String dateAlert = DateFormat.getDateInstance().format(date);
        tvDateAlert.setText(dateAlert);

        doubleAlpacaImageView = findViewById(R.id.doubleAlpacaImageView);
        shakingImageView = findViewById(R.id.shakingImageView);
        shakeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shaking);
        fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_repeat);
        textClock.startAnimation(fadeInAnim);
        shakingImageView.startAnimation(shakeAnim);

    }

    protected final BroadcastReceiver alarmBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.wtf("AlarmAlertActivity", "receive BROADCAST_ALARM_SERVICE_END");
            finish();
        }
    };

}
