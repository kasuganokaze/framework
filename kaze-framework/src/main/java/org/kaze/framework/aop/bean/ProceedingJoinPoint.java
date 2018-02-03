package org.kaze.framework.aop.bean;

import org.kaze.framework.aop.proxy.ProxyChain;

import java.lang.reflect.Method;

/**
 * 执行时切面类
 *
 * @author kaze
 * @since 2017/09/06
 */
public class ProceedingJoinPoint {

    private ProxyChain proxyChain;

    public ProceedingJoinPoint(ProxyChain proxyChain) {
        this.proxyChain = proxyChain;
    }

    public Class<?> getTargetClass() {
        return proxyChain.getTargetClass();
    }

    public Method getTargetMethod() {
        return proxyChain.getTargetMethod();
    }

    public Object[] getMethodParams() {
        return proxyChain.getMethodParams();
    }

    public Object proceed() throws Throwable {
        return proxyChain.doProxyChain();
    }

}
