package com.alpaca.alarmpaca.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Win10 M7 on 16/12/2560.
 */

public class RealmTasks extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private String selfLink;
    private String notes;
    private String status;
    private Date due;
    private String position;
    private boolean deleted;

    public RealmTasks() {

    }



    public RealmTasks(String id, String title, String notes, String status, Date due, boolean deleted) {
        this.id = id;
        this.title = title;
        this.notes = notes;
        this.status = status;
        this.due = due;
        this.deleted = deleted;
    }

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

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
