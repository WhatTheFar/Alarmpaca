package com.alpaca.alarmpaca.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class RealmTasks extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String title;
    private String notes;
    private String status;
    private Date due;
    private String position;

    public static final String NEW_TASK_ID = "new_task";

    public RealmTasks() {

    }


    public RealmTasks(String id, String title, String notes, String status, Date due) {
        this.id = id;
        this.title = title;
        this.notes = notes;
        this.status = status;
        this.due = due;
    }

    protected RealmTasks(Parcel in) {
        id = in.readString();
        title = in.readString();
        notes = in.readString();
        status = in.readString();
        position = in.readString();
        long time = in.readLong();
        if (time != -1) {
            due = new Date(time);
        } else {
            due = null;
        }
    }

    public static final Creator<RealmTasks> CREATOR = new Creator<RealmTasks>() {
        @Override
        public RealmTasks createFromParcel(Parcel in) {
            return new RealmTasks(in);
        }

        @Override
        public RealmTasks[] newArray(int size) {
            return new RealmTasks[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(notes);
        parcel.writeString(status);
        parcel.writeString(position);

        if (due != null) {
            parcel.writeLong(due.getTime());
        } else {
            parcel.writeLong(-1);
        }
    }
}
