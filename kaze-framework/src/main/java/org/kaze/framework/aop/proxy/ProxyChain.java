package org.kaze.framework.aop.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 代理链
 *
 * @author kaze
 * @since 2017/09/09
 */
public class ProxyChain {

    private final Object targetObject;
    private final Method targetMethod;
    private final Object[] methodParams;
    private final MethodProxy methodProxy;
    private final Class<?> targetClass;
    private List<Object[]> proxyList = new ArrayList<>();
    private int proxyIndex = 0;
    private Class<?> aspectClass;

    public ProxyChain(Object targetObject, Method targetMethod, Object[] methodParams, MethodProxy methodProxy, Class<?> targetClass, List<Object[]> proxyList) {
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodParams = methodParams;
        this.methodProxy = methodProxy;
        this.targetClass = targetClass;
        this.proxyList = proxyList;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Class<?> getAspectClass() {
        return aspectClass;
    }

    public Object doProxyChain() throws Throwable {
        Object methodResult;
        if (proxyIndex < proxyList.size()) {
            Object[] objects = proxyList.get(proxyIndex++);
            aspectClass = (Class<?>) objects[0];
            methodResult = ((Proxy) objects[1]).doProxy(this);
        } else {
            methodResult = methodProxy.invokeSuper(targetObject, methodParams);
        }
        return methodResult;
    }

}
