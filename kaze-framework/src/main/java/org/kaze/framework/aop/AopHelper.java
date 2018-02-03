package org.kaze.framework.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.kaze.framework.aop.annotation.Aspect;
import org.kaze.framework.aop.proxy.AspectProxy;
import org.kaze.framework.aop.proxy.Proxy;
import org.kaze.framework.aop.proxy.ProxyChain;
import org.kaze.framework.core.support.ClassScanner;
import org.kaze.framework.ioc.BeanHelper;
import org.kaze.framework.ioc.stereotype.Service;
import org.kaze.framework.tx.proxy.TransactionProxy;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 动态代理助手类
 *
 * @author kaze
 * @since 2017/09/05
 */
public final class AopHelper {

    /**
     * 为相应的类生成代理
     */
    static {
        try {
            // 创建 Proxy Map
            Map<Class<?>, List<Class<?>>> proxyMap = getProxyMap();
            // 创建 Target Map
            Map<Class<?>, List<Object[]>> targetMap = createProxy(proxyMap);
            for (Map.Entry<Class<?>, List<Object[]>> entry : targetMap.entrySet()) {
                // 分别获取 map 中的 key 与 value
                final Class<?> targetClass = entry.getKey();
                final List<Object[]> proxyList = entry.getValue();
                // 创建代理实例
                Object proxyInstance = Enhancer.create(targetClass, new MethodInterceptor() {
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                        return new ProxyChain(o, method, objects, methodProxy, targetClass, proxyList).doProxyChain();
                    }
                });
                // 用代理实例覆盖目标实例，并放入 Bean 容器中
                BeanHelper.setBean(targetClass, proxyInstance);
            }
        } catch (Exception e) {
            throw new RuntimeException("init proxy failure", e);
        }
    }

    /**
     * 获取所有代理
     */
    private static Map<Class<?>, List<Class<?>>> getProxyMap() {
        Map<Class<?>, List<Class<?>>> proxyMap = new HashMap<>();
        // 切面代理
        List<Class<?>> aspectClassList = new ArrayList<>();
        // 事务代理
        List<Class<?>> serviceClassList = new ArrayList<>();
        // 获取所有Bean映射并遍历
        Map<Class<?>, Object> aspectClassMap = BeanHelper.getBeanMap();
        for (Map.Entry<Class<?>, Object> entry : aspectClassMap.entrySet()) {
            Class<?> cls = entry.getKey();
            if (cls.isAnnotationPresent(Aspect.class)) {
                aspectClassList.add(cls);
            }
            if (cls.isAnnotationPresent(Service.class)) {
                serviceClassList.add(cls);
            }
        }
        proxyMap.put(AspectProxy.class, aspectClassList);
        proxyMap.put(TransactionProxy.class, serviceClassList);
        return proxyMap;
    }

    /**
     * 根据代理目标获取代理链
     */
    private static Map<Class<?>, List<Object[]>> createProxy(Map<Class<?>, List<Class<?>>> proxyMap) throws Exception {
        Map<Class<?>, List<Object[]>> targetMap = new HashMap<>();
        // 遍历 Proxy Map
        for (Map.Entry<Class<?>, List<Class<?>>> entry : proxyMap.entrySet()) {
            // 分别获取 map 中的 key 与 value
            Class<?> proxyClass = entry.getKey();
            List<Class<?>> targetClassList = entry.getValue();
            // 遍历目标类列表
            for (Class<?> targetClass : targetClassList) {
                // 创建代理类（切面类）实例
                Proxy proxy = (Proxy) proxyClass.newInstance();
                if (!(proxy instanceof AspectProxy)) {
                    // 初始化 Target Map
                    addTargetMap(targetMap, targetClass, proxy, null);
                } else {
                    //如果是aspect，根据表达式找目标类
                    Set<Class<?>> classSet = ClassScanner.getClassSet();
                    String expression = targetClass.getAnnotation(Aspect.class).value();
                    for (Class<?> cls : classSet) {
                        if (cls.getName().split("\\.").length != expression.split("\\.").length) {
                            continue;
                        }
                        if (cls.getName().matches(expression.replace("*", ".+"))) {
                            // 初始化 Target Map
                            addTargetMap(targetMap, cls, proxy, targetClass);
                        }
                    }
                }
            }
        }
        return targetMap;
    }

    /**
     * 添加代理链
     */
    private static void addTargetMap(Map<Class<?>, List<Object[]>> targetMap, Class<?> cls, Proxy proxy, Class aspectClass) {
        if (targetMap.containsKey(cls)) {
            targetMap.get(cls).add(new Object[]{aspectClass, proxy});
        } else {
            List<Object[]> proxyList = new ArrayList<>();
            proxyList.add(new Object[]{aspectClass, proxy});
            targetMap.put(cls, proxyList);
        }
    }

}
