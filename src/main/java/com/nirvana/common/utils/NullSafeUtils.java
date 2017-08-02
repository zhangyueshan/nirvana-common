package com.nirvana.common.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by InThEnd on 2017/3/1.
 */
public class NullSafeUtils {

    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isBlank(String string) {
        return string == null || string.equals("");
    }

}
