package com.rajeman.myjournal.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private Calendar cal;
    private Date date;
    private SimpleDateFormat simpleDateFormat;

    public DateUtils(long timeStamp) {

        cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        date = cal.getTime();

    }

    public String getWeekDay() {
        simpleDateFormat = new SimpleDateFormat("EEE");
        String weekDay = simpleDateFormat.format(date);
        return weekDay;
    }


    public String getDay() {
        return Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
    }

    public String getTime() {
        simpleDateFormat = new SimpleDateFormat("hh:mm aaa");
        String time = simpleDateFormat.format(date);
        return time;
    }

    public String getMonthYear() {
        simpleDateFormat = new SimpleDateFormat("MMM, yyyy");
        String monthYear = simpleDateFormat.format(date);
        return monthYear;
    }

}
