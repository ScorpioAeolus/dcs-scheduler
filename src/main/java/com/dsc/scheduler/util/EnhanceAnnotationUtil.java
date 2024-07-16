package com.dsc.scheduler.util;

import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Enhance low version AnnotationUtils
 *
 * @author typhoon
 **/
public abstract class EnhanceAnnotationUtil {


    /**
     * Determine whether the given class is a candidate for carrying one of the specified
     * annotations (at type, method or field level).
     * @param clazz the class to introspect
     * @param annotationTypes the searchable annotation types
     * @return {@code false} if the class is known to have no such annotations at any level;
     * {@code true} otherwise. Callers will usually perform full method/field introspection
     * if {@code true} is being returned here.
     * @since 5.2
     * @see #isCandidateClass(Class, Class)
     * @see #isCandidateClass(Class, String)
     */
    public static boolean isCandidateClass(Class<?> clazz, Collection<Class<? extends Annotation>> annotationTypes) {
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (isCandidateClass(clazz, annotationType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether the given class is a candidate for carrying the specified annotation
     * (at type, method or field level).
     * @param clazz the class to introspect
     * @param annotationType the searchable annotation type
     * @return {@code false} if the class is known to have no such annotations at any level;
     * {@code true} otherwise. Callers will usually perform full method/field introspection
     * if {@code true} is being returned here.
     * @since 5.2
     * @see #isCandidateClass(Class, String)
     */
    public static boolean isCandidateClass(Class<?> clazz, Class<? extends Annotation> annotationType) {
        return isCandidateClass(clazz, annotationType.getName());
    }

    /**
     * Determine whether the given class is a candidate for carrying the specified annotation
     * (at type, method or field level).
     * @param clazz the class to introspect
     * @param annotationName the fully-qualified name of the searchable annotation type
     * @return {@code false} if the class is known to have no such annotations at any level;
     * {@code true} otherwise. Callers will usually perform full method/field introspection
     * if {@code true} is being returned here.
     * @since 5.2
     * @see #isCandidateClass(Class, Class)
     */
    public static boolean isCandidateClass(Class<?> clazz, String annotationName) {
        if (annotationName.startsWith("java.")) {
            return true;
        }
        if (hasPlainJavaAnnotationsOnly(clazz)) {
            return false;
        }
        return true;
    }


    static boolean hasPlainJavaAnnotationsOnly(Class<?> type) {
        return (type.getName().startsWith("java.") || type == Ordered.class);
    }
}
