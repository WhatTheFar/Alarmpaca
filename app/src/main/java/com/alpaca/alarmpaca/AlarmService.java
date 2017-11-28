package com.alpaca.alarmpaca;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class AlarmService extends Service {

    ShakeDetector shakeDetector;
    Ringtone alarmRingtone;

    public static final String BROADCAST_ALARM_SERVICE_END = "alarm_service_end";

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.wtf("AlarmService", "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.wtf("AlarmService", "onStartCommand");

        initAlarmRingtone();
        initShakeDetector();

        alarmRingtone.play();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.wtf("AlarmService", "onDestroy");

        alarmRingtone.stop();
        shakeDetector.stopShakeDetector(AlarmService.this);
        shakeDetector.destroy(AlarmService.this);
        Log.wtf("ShakeDetector", "Destroy ShakeDetector");
    }

    private void initAlarmRingtone() {

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        alarmRingtone = RingtoneManager.getRingtone(AlarmService.this, ringtoneUri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
            attributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM);
            alarmRingtone.setAudioAttributes(attributesBuilder.build());

        } else {
            alarmRingtone.setStreamType(AudioManager.STREAM_ALARM);
        }
    }

    private void initShakeDetector() {

        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(5)
                .sensibility(4.0f);

        this.shakeDetector = new ShakeDetector(options);
        this.shakeDetector.start(AlarmService.this, shakeCallback);
        Log.wtf("ShakeDetector", "Start ShakeDetector");
    }

    final ShakeCallback shakeCallback = new ShakeCallback() {
        @Override
        public void onShake() {
            Log.wtf("ShakeDetector", "Event : onShake");
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(500);
                }
            }

            Intent broadcastIntent = new Intent(BROADCAST_ALARM_SERVICE_END);
            LocalBroadcastManager.getInstance(AlarmService.this)
                    .sendBroadcast(broadcastIntent);
            Log.wtf("AlarmService", "send BROADCAST_ALARM_SERVICE_END");

            alarmRingtone.stop();

            stopSelf();
        }
    };
}
