package com.alpaca.alarmpaca.model;

import android.app.AlarmManager;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Alarm extends RealmObject {

    @PrimaryKey
    private int id;
    private int hour;
    private int minute;
    private String period;
    private String date;
    private RealmList<Integer> repeat;
    private boolean isActivated;

    public Alarm() {

    }

    public Alarm(int hour, int minute, RealmList<Integer> repeat) {
        String id;
        id = hour <= 9 ? "0" : "";
        id = id.concat(Integer.toString(hour))
                .concat(minute <= 9 ? "0" : "")
                .concat(Integer.toString(minute));
        String repeatId = "";
        for (Integer a : repeat
                ) {
            repeatId = repeatId.concat(a.toString());
        }
        int repeatInt = Integer.parseInt(repeatId, 2);

        this.id = Integer.parseInt(id + "" + repeatInt);
        this.hour = hour;
        this.minute = minute;
        this.period = hour < 12 ? "AM" : "PM";
        this.repeat = repeat;
    }

    public String getTime() {
        return getTime(hour, minute);
    }

    public static String getTime(int hour, int minute) {
        String time;
        int temp = hour % 12;
        if (temp == 0) {
            temp = 12;
        }
        time = temp <= 9 ? "0" : "";
        time = time.concat(Integer.toString(temp))
                .concat(":")
                .concat(minute <= 9 ? "0" : "")
                .concat(Integer.toString(minute));
        return time;
    }

    public String getDateString() {
        Calendar calendar = getCalendarInstance();

        if (System.currentTimeMillis() >= calendar.getTimeInMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY);
            return DateFormat.getDateInstance()
                    .format(getCalendarInstance().getTime());
        } else {
            return "Today";
        }
    }

    public Calendar getCalendarInstance() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public boolean isRepeat() {
        boolean isRepeat = false;
        for (Integer temp : repeat
                ) {
            isRepeat = (temp == 1);
        }
        return isRepeat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RealmList<Integer> getRepeat() {
        return repeat;
    }

    public void setRepeat(RealmList<Integer> repeat) {
        this.repeat = repeat;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

}
