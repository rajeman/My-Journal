package com.rajeman.myjournal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
   Calendar cal;
   Date date;
   SimpleDateFormat simpleDateFormat;

    public DateUtils(long timeStamp) {

        cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        date = cal.getTime();


    }

    public String getWeekDay(){
      simpleDateFormat = new SimpleDateFormat("EEE");
     String weekDay =  simpleDateFormat.format(date);
     return weekDay;

  /*      int day = cal.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                return "Sun";
            case Calendar.MONDAY:
                 return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thur";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";

                }
                */

    }


    public String getDay(){

        return Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
    }

    public String getTime(){
        simpleDateFormat = new SimpleDateFormat("hh:mm aaa");
        String time = simpleDateFormat.format(date);
        return time;
      //  return cal.get(Calendar.HOUR) +":" + cal.get(Calendar.MINUTE) + cal.get(Calendar.AM_PM);
    }

    public String getMonthYear(){
       simpleDateFormat = new SimpleDateFormat("MMM, yyyy");
       String monthYear = simpleDateFormat.format(date);
       return monthYear;
        //return theMonth(cal.get(Calendar.MONTH)).substring(0, 3);
    }
/*
    private  String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
*/
}
