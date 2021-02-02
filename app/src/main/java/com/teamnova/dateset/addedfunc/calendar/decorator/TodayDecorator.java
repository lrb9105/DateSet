package com.teamnova.dateset.addedfunc.calendar.decorator;

import android.content.Context;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.teamnova.dateset.R;

import java.util.Calendar;

public class TodayDecorator implements DayViewDecorator {
    CalendarDay today = CalendarDay.today();
    Context context;

    public TodayDecorator(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return today.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(context.getDrawable(R.drawable.background_date));
    }
}
