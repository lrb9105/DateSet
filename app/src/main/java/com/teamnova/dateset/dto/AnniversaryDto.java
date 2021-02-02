package com.teamnova.dateset.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AnniversaryDto implements Serializable, Cloneable, DateInterface{
    String title = null;         // 제목
    String date = null;     // 시작날짜(YYYYMMDD)
    String dateWithDay;     // 시작날짜(일 포함)
    String repeatType = null;    // 반복종류
    String repeatEndDate = null; // 반복종료날짜(YYYYMMDD)
    String repeatEndDateWithDay = null; // 반복종료날짜(일 포함)
    String alarmType = null;     // 알람종류
    String memo = null;          // 메모 null가능

    public AnniversaryDto(){}

    public AnniversaryDto(String title, String date, String dateWithDay, String repeatType, String repeatEndDate, String repeatEndDateWithDay, String alarmType, String memo) {
        this.title = title;
        this.date = date;
        this.dateWithDay = dateWithDay;
        this.repeatType = repeatType;
        this.repeatEndDate = repeatEndDate;
        this.repeatEndDateWithDay = repeatEndDateWithDay;
        this.alarmType = alarmType;
        this.memo = memo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateWithDay() {
        return dateWithDay;
    }

    public void setDateWithDay(String dateWithDay) {
        this.dateWithDay = dateWithDay;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    public String getRepeatEndDateWithDay() {
        return repeatEndDateWithDay;
    }

    public void setRepeatEndDateWithDay(String repeatEndDateWithDay) {
        this.repeatEndDateWithDay = repeatEndDateWithDay;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String startTime() {
        return null;
    }

    @Override
    public String endTime() {
        return null;
    }

    @Override
    public String title() {
        return this.getTitle();
    }

    @Override
    public String startDate() {
        return this.getDate();
    }
}
