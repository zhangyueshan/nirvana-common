package com.nirvana.common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 有code码的枚举型包装类。
 * Created by Nirvana on 2017/8/1.
 */
@SuppressWarnings("JavaReflectionMemberAccess")
public class CodeEnumerator<T extends Enum> {

    private final Map<Integer, T> cache = new HashMap<>();

    public CodeEnumerator(Class<T> enumClass) {
        try {
            Field field = enumClass.getDeclaredField("code");
            field.setAccessible(true);
            for (T t : enumClass.getEnumConstants()) {
                cache.put(field.getInt(t), t);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("this Enum has no field names code.");
        } catch (IllegalAccessException ignore) {
        }
    }

    public T get(int code) {
        return cache.get(code);
    }

}
