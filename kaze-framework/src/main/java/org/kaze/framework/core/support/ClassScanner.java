package org.kaze.framework.core.support;

import org.kaze.framework.core.util.ConfigUtil;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 类扫描
 *
 * @author kaze
 * @since 2017/09/15
 */
public final class ClassScanner {

    /**
     * 需要扫描的包
     */
    private static final String packages = ConfigUtil.getBasePackage();

    /**
     * 获取应用宝包下的所有类
     */
    public static Set<Class<?>> getClassSet() {
        return new ClassLoader() {
            public boolean checkAddClass(Class<?> cls) {
                return true;
            }
        }.getClassSet(packages);
    }

    /**
     * 获取应用包名下带有某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        return new ClassLoader() {
            public boolean checkAddClass(Class<?> cls) {
                return cls.isAnnotationPresent(annotationClass);
            }
        }.getClassSet(packages);
    }

}
