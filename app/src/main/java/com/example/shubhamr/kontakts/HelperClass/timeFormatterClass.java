package com.example.shubhamr.kontakts.HelperClass;

import java.text.SimpleDateFormat;
import java.util.Date;

public class timeFormatterClass {

    public static String getDate(String date){
        //Formatting Date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String dateFormat = formatter.format(new Date(Long.parseLong(date)));
        return dateFormat;
    }

    public static String getTime(String time){
        //Formatting Time
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm a");
        String timeFormat = formatter2.format(new Date(Long.parseLong(time)));
        return timeFormat;
    }

    public static String getDurationTime(String duration){
        int durationInt = Integer.parseInt(duration);
       int hours = durationInt / 3600;
       int minutes = (durationInt % 3600) / 60;
       int seconds = durationInt % 60;

       String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
       return timeString;
    }

}
