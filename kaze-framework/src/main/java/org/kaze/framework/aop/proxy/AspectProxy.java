package org.kaze.framework.aop.proxy;

import org.kaze.framework.aop.annotation.After;
import org.kaze.framework.aop.annotation.Around;
import org.kaze.framework.aop.annotation.Before;
import org.kaze.framework.aop.bean.JoinPoint;
import org.kaze.framework.aop.bean.ProceedingJoinPoint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 切面代理
 *
 * @author kaze
 * @since 2017/09/05
 */
public class AspectProxy implements Proxy {

    private Method beforeMethod;
    private Method aroundMethod;
    private Method afterMethod;

    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        //初始化需要代理的方法
        this.methodInit(proxyChain);
        //执行代理
        return this.invoke(proxyChain);
    }

    /**
     * 初始化需要代理的方法
     */
    private void methodInit(ProxyChain proxyChain) {
        Method[] methods = proxyChain.getAspectClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethod = method;
            }
            if (method.isAnnotationPresent(Around.class)) {
                aroundMethod = method;
            }
            if (method.isAnnotationPresent(After.class)) {
                afterMethod = method;
            }
        }
    }

    /**
     * 执行代理
     */
    private Object invoke(ProxyChain proxyChain) throws Throwable {
        //执行结果
        Object result;
        Object proxyObject = proxyChain.getAspectClass().newInstance();
        //如果有before注解，则注入参数,执行该代码块
        if (beforeMethod != null) {
            List<Object> args = new ArrayList<>();
            Class[] types = beforeMethod.getParameterTypes();
            for (Class<?> type : types) {
                if (type.equals(JoinPoint.class)) {
                    args.add(new JoinPoint(proxyChain));
                } else {
                    args.add(null);
                }
            }
            beforeMethod.invoke(proxyObject, args.toArray());
        }
        //如果有around注解，则注入参数,执行该代码块
        if (aroundMethod != null) {
            List<Object> args = new ArrayList<>();
            Class[] types = aroundMethod.getParameterTypes();
            for (Class<?> type : types) {
                if (type.equals(ProceedingJoinPoint.class)) {
                    args.add(new ProceedingJoinPoint(proxyChain));
                } else {
                    args.add(null);
                }
            }
            result = aroundMethod.invoke(proxyObject, args.toArray());
        } else {
            result = proxyChain.doProxyChain();
        }
        //如果有After注解，则注入参数,执行该代码块
        if (afterMethod != null) {
            List<Object> args = new ArrayList<>();
            Class[] types = afterMethod.getParameterTypes();
            for (Class<?> type : types) {
                if (JoinPoint.class.equals(type)) {
                    args.add(new JoinPoint(proxyChain));
                } else {
                    args.add(null);
                }
            }
            afterMethod.invoke(proxyObject, args.toArray());
        }
        return result;
    }

}
