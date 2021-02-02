package com.teamnova.dateset.addedfunc.calendar.schedule;

import android.util.Log;

import com.teamnova.dateset.dto.ScheduleDto;

import java.util.Comparator;

public class CompareByDate implements Comparator<ScheduleDto> {

    @Override
    public int compare(ScheduleDto o1, ScheduleDto o2) {
        int ret = o1.getStartDate().compareTo(o2.getStartDate());

        if(ret == 0){
            ret = o1.getStartTimeWithoutAmPm().compareTo(o2.getStartTimeWithoutAmPm());
        }
        return ret;
    }
}