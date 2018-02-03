package org.kaze.framework.aop.bean;

import org.kaze.framework.aop.proxy.ProxyChain;

import java.lang.reflect.Method;

/**
 * 执行前，执行后,切面类
 *
 * @author kaze
 * @since 2017/09/06
 */
public class JoinPoint {

    private ProxyChain proxyChain;

    public JoinPoint(ProxyChain proxyChain) {
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

}
