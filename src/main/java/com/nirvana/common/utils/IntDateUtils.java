package com.nirvana.common.utils;

import java.util.Date;

public class IntDateUtils {

    private static final String FORMAT_INT_PATTERN = "yyyyMMdd";

    private IntDateUtils() {
    }

    public static int toInt(Date date) {
        return Integer.parseInt(DateUtils.format(date, FORMAT_INT_PATTERN));
    }

    public static Date fromInt(int date) {
        return DateUtils.fromString(date + "", FORMAT_INT_PATTERN);
    }

    public static long getTimestamp(int date) {
        return DateUtils.fromString(date + "", FORMAT_INT_PATTERN).getTime();
    }

    public static int addDay(int date, int day) {
        return toInt(DateUtils.addDay(fromInt(date), day));
    }

    public static int addMonth(int date, int month) {
        return toInt(DateUtils.addMonth(fromInt(date), month));
    }

    public static int addYear(int date, int year) {
        return toInt(DateUtils.addYear(fromInt(date), year));
    }

    public static int daysBetween(int date1, int date2) {
        Date d1 = fromInt(date1);
        Date d2 = fromInt(date2);
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MILLIS_PER_DAY);
    }

    public static int getYear(int date) {
        return date / 10000;
    }

    public static int getMonth(int date) {
        return (date - getYear(date) * 10000) / 100;
    }

    public static int getDay(int date) {
        return date - getYear(date) * 10000 - getMonth(date) * 100;
    }

    public static boolean check(int date) {
        int year = getYear(date);
        if (year > 9999 || year < 1970) {
            return false;
        }

        int month = getMonth(date);
        if (month < 1 || month > 12) {
            return false;
        }

        int day = getDay(date);
        if (day < 1 || day > 31) {
            return false;
        }
        return true;
    }
}
