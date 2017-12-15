package com.nirvana.common.utils.scanner;

/**
 * Created by Nirvana on 2017/12/15.
 */
public class TrueFilter implements ClassFilter {

    @Override
    public boolean leave(Class<?> clazz) {
        return true;
    }
}
