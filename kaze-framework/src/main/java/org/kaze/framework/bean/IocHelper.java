package org.kaze.framework.bean;

import org.kaze.framework.core.util.ClassUtil;
import org.kaze.framework.core.util.ConfigUtil;
import org.kaze.framework.bean.annotation.Autowired;
import org.kaze.framework.bean.annotation.Value;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入助手类
 *
 * @author kaze
 * @since 2017/08/30
 */
public final class IocHelper {

    static {
        try {
            // 获取所有的Bean类与Bean实例之间的映射关系
            Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
            //遍历Bean Map
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                //从BeanMap中获取Bean类与Bean实例
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                //获取Bean类定义的所有成员变量（简称Bean Field）
                Field[] fields = beanClass.getDeclaredFields();
                //遍历Bean Field
                for (Field beanField : fields) {
                    //判断当前Bean Filed是否带有Autowired注解
                    if (beanField.isAnnotationPresent(Autowired.class)) {
                        //在Bean Map中获取Bean Field对应的实例
                        Object beanFieldInstance = beanMap.get(beanField.getType());
                        if (beanFieldInstance != null) {
                            beanField.setAccessible(true);
                            beanField.set(beanInstance, beanFieldInstance);
                        }
                    } else if (beanField.isAnnotationPresent(Value.class)) {
                        String key = beanField.getAnnotation(Value.class).value();
                        if (key.length() > 3) {
                            key = key.substring(2, key.length() - 1);
                            beanField.setAccessible(true);
                            if (ClassUtil.isInt(beanField.getType())) {
                                beanField.set(beanInstance, ConfigUtil.getInt(key));
                            } else if (ClassUtil.isBoolean(beanField.getType())) {
                                beanField.set(beanInstance, ConfigUtil.getBoolean(key));
                            } else if (ClassUtil.isString(beanField.getType())) {
                                beanField.set(beanInstance, ConfigUtil.getString(key));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("init IocHelper failure", e);
        }
    }

}
