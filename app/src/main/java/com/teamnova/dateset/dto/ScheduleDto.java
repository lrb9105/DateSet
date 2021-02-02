package com.teamnova.dateset.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public class ScheduleDto implements Comparable, Serializable, Cloneable,DateInterface {
    private String writer;        // 작성자 id
    private String schType;       // 스케줄 종류 0:공동, 1:본인, 2:상대방
    private String title;         // 제목
    private String place;         // 위치
    private String startDateWithDay;     // 시작날짜(일 포함)
    private String startDate;     // 시작날짜(YYYYMMDD)
    private String startTime;     // 시작시간
    private String endDate;       // 종료날짜(YYYYMMDD)
    private String endDateWithDay;       // 종료날짜(일 포함)
    private String endTime;       // 종료시간
    private String repeatType;    // 반복종류
    private String repeatEndDateWithDay; // 반복종료날짜(일 포함)
    private String repeatEndDate; // 반복종료날짜(YYYYMMDD)
    private String alarmType;     // 알람종류
    private String memo;          // 메모
    private int id;               // SP에서 사용할 id
    private String startTimeWithoutAmPm;     // 시작시간 hh:mm
    private String endTimeWithoutAmPm;       // 종료시간 hh:mm
    private String firstDate;    // 최초 시작일
    private String key;          // 파이어베이스 key값
    private String spKey;          // sp key값

    public ScheduleDto() {}

    public ScheduleDto(String writer, String schType, String title, String place, String startDateWithDay, String startDate, String startTime, String endDate, String endDateWithDay, String endTime, String repeatType, String repeatEndDateWithDay, String repeatEndDate, String alarmType, String memo, int id, String startTimeWithoutAmPm, String endTimeWithoutAmPm, String firstDate) {
        this.writer = writer;
        this.schType = schType;
        this.title = title;
        this.place = place;
        this.startDateWithDay = startDateWithDay;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endDateWithDay = endDateWithDay;
        this.endTime = endTime;
        this.repeatType = repeatType;
        this.repeatEndDateWithDay = repeatEndDateWithDay;
        this.repeatEndDate = repeatEndDate;
        this.alarmType = alarmType;
        this.memo = memo;
        this.id = id;
        this.startTimeWithoutAmPm = startTimeWithoutAmPm;
        this.endTimeWithoutAmPm = endTimeWithoutAmPm;
        this.firstDate = firstDate;
    }

    public ScheduleDto(String writer, String schType, String title, String place, String startDateWithDay, String startDate, String startTime, String endDate, String endDateWithDay, String endTime, String repeatType, String repeatEndDateWithDay, String repeatEndDate, String alarmType, String memo, String startTimeWithoutAmPm, String endTimeWithoutAmPm, String firstDate) {
        this.writer = writer;
        this.schType = schType;
        this.title = title;
        this.place = place;
        this.startDateWithDay = startDateWithDay;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endDateWithDay = endDateWithDay;
        this.endTime = endTime;
        this.repeatType = repeatType;
        this.repeatEndDateWithDay = repeatEndDateWithDay;
        this.repeatEndDate = repeatEndDate;
        this.alarmType = alarmType;
        this.memo = memo;
        this.startTimeWithoutAmPm = startTimeWithoutAmPm;
        this.endTimeWithoutAmPm = endTimeWithoutAmPm;
        this.firstDate = firstDate;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getSchType() {
        return schType;
    }

    public void setSchType(String schType) {
        this.schType = schType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStartDateWithDay() {
        return startDateWithDay;
    }

    public void setStartDateWithDay(String startDateWithDay) {
        this.startDateWithDay = startDateWithDay;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDateWithDay() {
        return endDateWithDay;
    }

    public void setEndDateWithDay(String endDateWithDay) {
        this.endDateWithDay = endDateWithDay;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getRepeatEndDateWithDay() {
        return repeatEndDateWithDay;
    }

    public void setRepeatEndDateWithDay(String repeatEndDateWithDay) {
        this.repeatEndDateWithDay = repeatEndDateWithDay;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTimeWithoutAmPm() {
        return startTimeWithoutAmPm;
    }

    public void setStartTimeWithoutAmPm(String startTimeWithoutAmPm) {
        this.startTimeWithoutAmPm = startTimeWithoutAmPm;
    }

    public String getEndTimeWithoutAmPm() {
        return endTimeWithoutAmPm;
    }

    public void setEndTimeWithoutAmPm(String endTimeWithoutAmPm) {
        this.endTimeWithoutAmPm = endTimeWithoutAmPm;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSpKey() {
        return spKey;
    }

    public void setSpKey(String spKey) {
        this.spKey = spKey;
    }

    @Override
    public int compareTo(Object o) {
        return this.startTimeWithoutAmPm.compareTo(((ScheduleDto)o).getStartTimeWithoutAmPm());
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String startTime() {
        return this.getStartTime();
    }

    @Override
    public String endTime() {
        return this.getEndTime();
    }

    @Override
    public String title() {
        return this.getTitle();
    }

    @Override
    public String startDate() {
        return this.getStartDate();
    }
}
