package com.alpaca.alarmpaca.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.alpaca.alarmpaca.R;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;


import java.text.DateFormat;
import java.util.Date;

/**
 * Created by cartoon on 11/27/2017 AD.
 */

public class AlarmAlertActivity extends AppCompatActivity {

    TextClock textClock;
    ImageView doubleAlpacaImageView,shakingImageView;
    Animation shakeAnim,fadeInAnim;
    TextView tvDateAlert;

    CustomAnalogClock customAnalogClock;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_alert);

        initInstances();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initInstances() {
        customAnalogClock = findViewById(R.id.analog_clock);
        customAnalogClock.setAutoUpdate(true);

        textClock = findViewById(R.id.textClock);
        textClock.setFormat12Hour("hh:mm a");
        textClock.setFormat24Hour(null);

        tvDateAlert = findViewById(R.id.tvDateAlert);

        String dateAlert = "Mon , November 28";
        Date date = new Date();
        dateAlert = DateFormat.getDateInstance().format(date);
        tvDateAlert.setText(dateAlert);

        doubleAlpacaImageView = findViewById(R.id.doubleAlpacaImageView);
        shakingImageView = findViewById(R.id.shakingImageView);
        shakeAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shaking);
        fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_repeat);
        textClock.startAnimation(fadeInAnim);
        shakingImageView.startAnimation(shakeAnim);

    }


}
