package com.teamnova.dateset.addedfunc.calendar.schedule;

import android.util.Log;

import com.teamnova.dateset.dto.ScheduleDto;

import java.util.Comparator;

class CompareByTime implements Comparator<ScheduleDto>{
    @Override
    public int compare(ScheduleDto o1, ScheduleDto o2) {
        Log.d("debug_compare_time",""+o1.getStartTimeWithoutAmPm().compareTo(o2.getStartTimeWithoutAmPm()));
        return o1.getStartTimeWithoutAmPm().compareTo(o2.getStartTimeWithoutAmPm());
    }
}