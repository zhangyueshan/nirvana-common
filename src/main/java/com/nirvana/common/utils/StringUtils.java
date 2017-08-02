package com.nirvana.common.utils;

/**
 * Created by InThEnd on 2016/4/20.
 * 字符串工具类。
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 如果不会空，返回原字符串。
     * 如果为空，返回null
     */
    public static String nullOrNotBlank(String string) {
        if (isBlank(string))
            return null;
        else
            return string;
    }

    /**
     * 删除字符串最尾部字符。
     */
    public static String deleteTail(String string) {
        StringBuilder builder = new StringBuilder(string);
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * 在左边补全字符串到指定大小。
     */
    public static String fillAtLeft(String string, char prefix, int number) {
        int length = string.length();
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < number - length; i++) {
            stringBuilder.insert(0, prefix);
        }
        return stringBuilder.toString();
    }

    /**
     * 在右边补全字符串到指定大小。
     */
    public static String fillAtRight(String string, char suffix, int number) {
        int length = string.length();
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < number - length; i++) {
            stringBuilder.append(suffix);
        }
        return stringBuilder.toString();
    }
}
