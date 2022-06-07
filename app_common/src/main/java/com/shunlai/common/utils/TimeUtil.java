package com.shunlai.common.utils;

import android.content.Context;

import com.shunlai.common.BaseApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by enn on 2017/9/5.
 */

public class TimeUtil {
    public static String getDay(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM_dd", Locale.CHINA);
        Date currentTime = new Date(time);
        String resultTime = format1.format(currentTime);
        return resultTime;
    }
    public static String getDay2(long time){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date currentTime = new Date(time);
        String resultTime = format1.format(currentTime);
        return resultTime;
    }
    public static String getTime(long time){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date currentTime = new Date(time);
        String resultTime = format1.format(currentTime);
        return resultTime;
    }

    public static String getTimeFormat(String format, long time){
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        Date currentTime = new Date(time);
        String resultTime = format1.format(currentTime);
        return resultTime;
    }
    /**
     * 根据format 格式得到 date
     * @param dateStr
     * @param format
     * @return
     */
    public static Date getStringToData(String dateStr, String format){
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
        try {
            date=simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getWeek(Long time){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time));
        int dayForWeek;
        if(c.get(Calendar.DAY_OF_WEEK) == 1){
            dayForWeek = 7;
        }else{
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        switch (dayForWeek){
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
            default:
                return "星期一";

        }
    }


    public static String formatSecondsTo00(int timeSeconds) {
        int second = timeSeconds % 60;
        int minuteTemp = timeSeconds / 60;
        if (minuteTemp > 0) {
            int minute = minuteTemp % 60;
            int hour = minuteTemp / 60;
            if (hour > 0) {
                return (hour >= 10 ? (hour + "") : ("0" + hour)) + ":" + (minute >= 10 ? (minute + "") : ("0" + minute))
                    + ":" + (second >= 10 ? (second + "") : ("0" + second));
            } else {
                return (minute >= 10 ? (minute + "") : ("0" + minute)) + ":"
                    + (second >= 10 ? (second + "") : ("0" + second));
            }
        } else {
            return "00:" + (second >= 10 ? (second + "") : ("0" + second));
        }
    }
}
