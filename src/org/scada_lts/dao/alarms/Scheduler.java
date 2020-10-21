package org.scada_lts.dao.alarms;

import java.util.Date;

public class Scheduler {

    private Long id;
    private int per_mail;
    private int per_sms;
    private int hour_start;
    private int hour_stop;
    private String description;
    private String mtime;

    public Scheduler() {
    }

    public Scheduler(Long id, int per_mail, int per_sms, int hour_start, int hour_stop, String description, String mtime) {
        this.id = id;
        this.per_mail = per_mail;
        this.per_sms = per_sms;
        this.hour_start = hour_start;
        this.hour_stop = hour_stop;
        this.description = description;
        this.mtime = mtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPer_mail() {
        return per_mail;
    }

    public void setPer_mail(int per_mail) {
        this.per_mail = per_mail;
    }

    public int getPer_sms() {
        return per_sms;
    }

    public void setPer_sms(int per_sms) {
        this.per_sms = per_sms;
    }

    public int getHour_start() {
        return hour_start;
    }

    public void setHour_start(int hour_start) {
        this.hour_start = hour_start;
    }

    public int getHour_stop() {
        return hour_stop;
    }

    public void setHour_stop(int hour_stop) {
        this.hour_stop = hour_stop;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }
}
