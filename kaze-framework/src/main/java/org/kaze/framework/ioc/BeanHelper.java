package org.kaze.framework.ioc;

import org.kaze.framework.core.support.ClassScanner;
import org.kaze.framework.ioc.stereotype.Component;
import org.kaze.framework.ioc.stereotype.Controller;
import org.kaze.framework.ioc.stereotype.Repository;
import org.kaze.framework.ioc.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bean 助手类
 *
 * @author kaze
 * @since 2017/08/30
 */
public final class BeanHelper {

    /**
     * 定义Bean映射（Bean类,Bean实例）
     */
    private static final Map<Class<?>, Object> beanMap = new HashMap<>();

    static {
        try {
            Set<Class<?>> beanClassSet = ClassScanner.getClassSet();
            for (Class<?> beanClass : beanClassSet) {
                if (beanClass.isAnnotationPresent(Controller.class) ||
                        beanClass.isAnnotationPresent(Service.class) ||
                        beanClass.isAnnotationPresent(Repository.class) ||
                        beanClass.isAnnotationPresent(Component.class)) {
                    Object beanInstance = beanClass.newInstance();
                    beanMap.put(beanClass, beanInstance);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("init BeanHelper failure", e);
        }
    }

    /**
     * 获取Bean映射
     */
    public static Map<Class<?>, Object> getBeanMap() {
        return beanMap;
    }

    /**
     * 获取Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> cls) {
        if (!beanMap.containsKey(cls)) {
            throw new RuntimeException("can not get bean by class:" + cls);
        }
        return (T) beanMap.get(cls);
    }

    /**
     * 设置Bean实例
     */
    public static void setBean(Class<?> cls, Object obj) {
        beanMap.put(cls, obj);
    }

}
