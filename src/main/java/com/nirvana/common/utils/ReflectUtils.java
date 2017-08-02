package com.nirvana.common.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by InThEnd on 2017/3/1.
 * 反射工具类。
 */
public class ReflectUtils {

    /**
     * 获取泛型的类型。
     */
    public static Class<?> getGenericsType(final Class<?> clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return null;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return null;
        }
        if (!(params[index] instanceof Class)) {
            return null;
        }

        return (Class) params[index];
    }

}
