package com.nirvana.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by InThEnd on 2016/4/20.
 * 日期工具类。
 */
public class DateUtils {


    // 每秒的毫秒数
    public static final long MILLIS_PER_SECOND = 1000;
    //每分钟的毫秒数
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    // 每小时的毫秒数
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    // 每天的毫秒数
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    //默认格式
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    //DateFormat cache
    private static final Map<String, DateFormat> dateFormatMap = new ConcurrentHashMap<>();

    static {
        dateFormatMap.put("yyyy-MM-dd", new SimpleDateFormat("yyyy-MM-dd"));
        dateFormatMap.put("yyyy-MM-dd HH:mm:ss", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        dateFormatMap.put("yyyy-MM-dd hh:mm:ss", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        dateFormatMap.put("yyyy/MM/dd", new SimpleDateFormat("yyyy/MM/dd"));
        dateFormatMap.put("yyyy/MM/dd hh:mm:ss", new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"));
        dateFormatMap.put("yyyy/MM/dd HH:mm:ss", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
        dateFormatMap.put("yyyyMMdd", new SimpleDateFormat("yyyyMMdd"));
        dateFormatMap.put("yyyyMMddhhmmss", new SimpleDateFormat("yyyyMMddhhmmss"));
    }


    // 私有构造
    private DateUtils() {
    }

    /**
     * 获取此日期的同一天凌晨零点的Date
     */
    public static Date getMorning(Date date) {
        String dateStr = format(date, "yyyyMMdd");
        return fromString(dateStr, "yyyyMMdd");
    }

    /**
     * 获取此日期的同一天第二天凌晨零点的Date
     */
    public static Date getNight(Date date) {
        long time = getMorning(date).getTime();
        long night = time + MILLIS_PER_DAY;
        return new Date(night);
    }

    /**
     * 判断两个日期是否为同一天。
     */
    public static boolean isOneDay(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        return c1.get(Calendar.DATE) == c2.get(Calendar.DATE);
    }

    /**
     * 格式化时间。
     */
    public static String format(Date date, String pattern) {
        // 时间非空判断
        Assert.notNull(date);
        DateFormat dateFormat = getDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * 从字符串解析时间。
     */
    public static Date fromString(String dateString, String pattern) {
        Assert.hasLength(dateString);
        DateFormat dateFormat = getDateFormat(pattern);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期解析错误。");
        }
    }


    /**
     * 以秒为单位拨动时间。
     *
     * @param second 秒数，可以为负值
     */
    public static Date addSecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /**
     * 以分钟为单位拨动时间。
     *
     * @param minute 分钟数，可以为负值
     */
    public static Date addMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /**
     * 以小时为单位拨动时间。
     *
     * @param hour 小时数，可以为负值
     */
    public static Date addHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hour);
        return calendar.getTime();
    }

    /**
     * 以天为单位拨动时间。
     *
     * @param day 天数，可以为负值
     */
    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * 以月为单位拨动时间。
     *
     * @param month 月数，可以为负值
     */
    public static Date addMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /**
     * 以年为单位拨动时间。
     *
     * @param year 年数，可以为负值
     */
    public static Date addYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 是否是周末。
     */
    public static boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return dayOfWeek < 1 || dayOfWeek > 5;
    }

    private static DateFormat getDateFormat(String pattern) {
        DateFormat dateFormat;
        if (StringUtils.isBlank(pattern)) {
            dateFormat = dateFormatMap.get(DEFAULT_FORMAT);
        } else {
            dateFormat = dateFormatMap.get(pattern);
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat(pattern);
                dateFormatMap.put(pattern, dateFormat);
            }
        }
        return dateFormat;
    }

}
