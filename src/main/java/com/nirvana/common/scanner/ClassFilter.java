package com.nirvana.common.scanner;

/**
 * Created by Nirvana on 2017/12/15.
 */
public interface ClassFilter {

    boolean leave(Class<?> clazz);

}
