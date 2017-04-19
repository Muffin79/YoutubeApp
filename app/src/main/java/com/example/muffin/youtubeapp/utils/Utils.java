package com.example.muffin.youtubeapp.utils;


import android.app.Activity;
import android.content.Context;

import com.example.muffin.youtubeapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String formatPublishedDate(Context context, String publishedDate){
        Date result = new Date();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            result = df1.parse(publishedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getTimeAgo(result, context);
    }

    // Method to get current date
    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    // Method to get how many time ago
    public static String getTimeAgo(Date date, Context ctx) {

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " +
                    ctx.getResources().getString(R.string.date_util_term_a) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+
                    ctx.getResources().getString(R.string.date_util_term_an)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                    (Math.round(dim / 60)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+
                    ctx.getResources().getString(R.string.date_util_term_a)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+
                    ctx.getResources().getString(R.string.date_util_term_a)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " "+
                    ctx.getResources().getString(R.string.date_util_term_a)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " +
                    ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                    (Math.round(dim / 525600)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    // Method to get time distance in minute
    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    // Method to convert ISO 8601 time to string
    public static String getTimeFromString(String duration) {
        // TODO Auto-generated method stub
        String time = "";
        boolean hourexists = false, minutesexists = false, secondsexists = false;
        if (duration.contains("H"))
            hourexists = true;
        if (duration.contains("M"))
            minutesexists = true;
        if (duration.contains("S"))
            secondsexists = true;
        if (hourexists) {
            String hour;
            hour = duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("H"));
            if (hour.length() == 1)
                hour = "0" + hour;
            time += hour + ":";
        }
        if (minutesexists) {
            String minutes;
            if (hourexists)
                minutes = duration.substring(duration.indexOf("H") + 1,
                        duration.indexOf("M"));
            else
                minutes = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("M"));
            if (minutes.length() == 1)
                minutes = "0" + minutes;
            time += minutes + ":";
        } else {
            time += "00:";
        }
        if (secondsexists) {
            String seconds;
            if (hourexists) {
                if (minutesexists)
                    seconds = duration.substring(duration.indexOf("M") + 1,
                            duration.indexOf("S"));
                else
                    seconds = duration.substring(duration.indexOf("H") + 1,
                            duration.indexOf("S"));
            } else if (minutesexists)
                seconds = duration.substring(duration.indexOf("M") + 1,
                        duration.indexOf("S"));
            else
                seconds = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("S"));
            if (seconds.length() == 1)
                seconds = "0" + seconds;
            time += seconds;
        }else {
            time += "00";
        }
        return time;
    }
}
