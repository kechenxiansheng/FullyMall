package com.cm.fm.mall.common.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    /**
     * 时间格式
     * @param format    转换的格式
     * @param date      需要转换的时间对象
     * @return          指定格式的时间字符串
     */
    public static String convertDate(String format, Date date){
        if(TextUtils.isEmpty(format)){
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    /**
     * 时间格式
     * @param format    转换的格式
     * @param time      需要转换的时间戳，long类型（13位）
     * @return          指定格式的时间字符串
     */
    public static String convertDate(String format,long time){
        if(TextUtils.isEmpty(format)){
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    /**
     * 计算 某天的前n天或者后n天
     * @param time  yyyy-MM-dd 格式的日期 2021-03-28
     * @param step  间隔天数，前n天传负数，后n天传正数
     * @return 时间戳
     */
    public static long stepDay(String time,int step){
//        System.out.println("startTime : " + time);
        long stepDay = step*24*60*60*1000;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        long stepDayTime = 0;
        try {
            Date date = sf.parse(time);
            long curDay = date.getTime();
            stepDayTime = curDay + stepDay;
//            System.out.println("resultTime : " +stepDayTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stepDayTime;
    }

    /**
     * 计算 某天的前n天或者后n天
     * @param time  yyyy-MM-dd 格式的日期 2021-03-28
     * @param step  间隔天数，前n天传负数，后n天传正数
     * @return yyyy-MM-dd 格式的日期
     */
    public static String stepDayFormatted(String time,int step){
//        System.out.println("startTime : " + time);
        long stepDay = step*24*60*60*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String resultTime = "";
        try {
            Date date = sdf.parse(time);
            long curDay = date.getTime();
            long stepDayTime = curDay + stepDay;
            resultTime = sdf.format(stepDayTime);
//            System.out.println("resultTime : " +stepDayTime);
//            System.out.println("resultTime : " +resultTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultTime;
    }



    /**
     * 计算某月的前n月或者后n月
     * @param startMonth  yyyyMM 格式的日期 202103
     * @param step  间隔月数
     * @return
     */
    public static String stepMonth(String startMonth,int step){
//        System.out.println("startMonth : " + startMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String resultMonth = "";
        try {
            Date date = sdf.parse(startMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH,step); //日期加减n个月
            Date addDate = calendar.getTime();
            resultMonth = sdf.format(addDate);
//            System.out.println("resultMonth : " + resultMonth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMonth;
    }
}
