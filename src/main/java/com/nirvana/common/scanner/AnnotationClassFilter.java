package com.nirvana.common.scanner;

import java.lang.annotation.Annotation;

/**
 * Created by Nirvana on 2017/12/15.
 */
public class AnnotationClassFilter implements ClassFilter {

    private Class<? extends Annotation> annotationClass;

    public AnnotationClassFilter(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public boolean leave(Class<?> clazz) {
        return clazz.isAnnotationPresent(annotationClass);
    }
}
