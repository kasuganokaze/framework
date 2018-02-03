package org.kaze.framework.aop.proxy;

/**
 * 动态代理接口
 *
 * @author kaze
 * @since 2017/09/04
 */
public interface Proxy {

    /**
     * 执行链式代理
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;

}
