package com.nirvana.common.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;

/**
 * Created by Nirvana on 2017/11/9.
 * util to get an sun.misc.Unsafe instance.
 */
public class UnsafeUtils {

    public static Unsafe getUnsafe() {
        //try Unsafe.getUnsafe()
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException ignored) {
        }

        //try reflection
        try {
            return java.security.AccessController.doPrivileged
                    ((PrivilegedExceptionAction<Unsafe>) () -> {
                        Class<Unsafe> k = Unsafe.class;
                        for (Field f : k.getDeclaredFields()) {
                            f.setAccessible(true);
                            Object x = f.get(null);
                            if (k.isInstance(x))
                                return k.cast(x);
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    });
        } catch (java.security.PrivilegedActionException e) {
            throw new RuntimeException("Could not initialize intrinsics",
                    e.getCause());
        }
    }

}
