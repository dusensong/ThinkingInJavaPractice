package com.example.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dss on 2017/6/22.
 */

public class TimeTest {

    public static Date DateOfString(String str, String formatString) {
        if (formatString == null) {
            formatString = "yyyy-MM-dd HH:mm:ss";
        }

        if (str == null) {
            return null;
        } else {
            SimpleDateFormat df = new SimpleDateFormat(formatString);

            try {
                return df.parse(str);
            } catch (ParseException var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public static Date DateAddByDay(Date date, int diffByDay) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(5, c.get(5) + diffByDay);
        Date t = c.getTime();
        return t;
    }


    public static String StringOfDate(Date date, String formatString) {
        if (formatString == null) {
            formatString = "yyyy-MM-dd HH:mm:ss";
        }

        if (date == null) {
            return "";
        } else {
            SimpleDateFormat df = new SimpleDateFormat(formatString);
            return df.format(date);
        }
    }

    public static void calcSiteTime(String beginTime, String endTime) throws ParseException {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTime(start.getTime());
        // 判断当前时间是上午还是下午
        if(start.get(Calendar.AM_PM) == Calendar.AM){
            // 开始日期是前一天，结束日期是今天
            start.add(Calendar.DAY_OF_MONTH, -1);
        }else{
            end.add(Calendar.DAY_OF_MONTH, 1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startT = sdf.parse(beginTime);
        Date endT = sdf.parse(endTime);

        start.set(Calendar.HOUR_OF_DAY, startT.getHours());


        String today = StringOfDate(new Date(), "yyyy-MM-dd");
        String sFromTime = String.format("%s %s", today, beginTime);
        String sToTime = String.format("%s %s", today, endTime);
        Date fromTime = DateOfString(sFromTime, "yyyy-MM-dd HH:mm");
        Date toTime = DateOfString(sToTime, "yyyy-MM-dd HH:mm");

        if (fromTime.compareTo(new Date()) > 0) {
            fromTime = DateAddByDay(fromTime, -1);
        }
        if (toTime.compareTo(fromTime) <= 0) {
            toTime = DateAddByDay(toTime, 1);
        }
        System.out.println("fromTime=" + fromTime);
        System.out.println("toTime=" + toTime);
    }

    public static void main(String[] args){
        try {
            calcSiteTime("12:00", "12:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
