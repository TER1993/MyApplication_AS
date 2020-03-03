package com.speedata.xu.myapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 * @date 2015/11/22
 */


public class MyDateAndTime {
    private int month;

    private void setDay() {
    }

    private void setHour() {
    }

    private void setMin() {
    }

    private int getMonth() {
        return month;
    }

    private void setMonth(int month) {
        this.month = month;
    }

    private void setSecond() {
    }

    private void setYear() {
    }

    private void setBeforAMonth() {
    }

    public static String getTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        return dateFormat.format(new Date());
    }

    private void setYearAndMonth() {
    }

    public static MyDateAndTime praseDateAndTime(String dateandtime) {
        MyDateAndTime result = new MyDateAndTime();
        String[] date; // = new String[3];
        String[] time; // = new String[3];
        date = dateandtime.substring(0, 10).split("-");
        time = dateandtime.substring(11).split(":");
        if (date.length < 3 || time.length < 3) {
            return null;
        }
        try {
            result.setYear();
            result.setMonth(Integer.parseInt(date[1]));
            result.setDay();

            result.setHour();
            result.setMin();
            result.setSecond();
            if (result.getMonth() < 0) {
                result.setYearAndMonth();
                result.setBeforAMonth();
            } else {
                result.setYearAndMonth();
                result.setBeforAMonth();
            }

        } catch (NumberFormatException e) {
            return null;
        }
        return result;
    }
}
