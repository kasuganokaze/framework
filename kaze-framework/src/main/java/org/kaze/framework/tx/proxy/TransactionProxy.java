package org.kaze.framework.tx.proxy;

import org.kaze.framework.aop.proxy.Proxy;
import org.kaze.framework.aop.proxy.ProxyChain;
import org.kaze.framework.tx.annotation.Transaction;
import org.kaze.framework.tx.util.ConnectionUtil;

/**
 * 事务代理
 *
 * @author kaze
 * @since 2017/09/04
 */
public class TransactionProxy implements Proxy {

    /**
     * 定义一个线程局部变量，用于保存当前线程中是否进行了事务处理，默认为 false（未处理）
     */
    private static final ThreadLocal<Boolean> flagContainer = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public Object doProxy(ProxyChain proxyChain) throws Throwable{
        Object result;
        // 获取当前事务开启标记；
        boolean flag = flagContainer.get();
        // 若当前线程未进行事务处理，而且方法带有Transaction注解，才进行事务处理
        if (!flag && proxyChain.getTargetMethod().isAnnotationPresent(Transaction.class)) {
            // 设置当前线程已进行事务处理
            flagContainer.set(true);
            try {
                // 开启事务
                ConnectionUtil.beginTransaction();
                // 执行目标方法
                result = proxyChain.doProxyChain();
                // 提交事务
                ConnectionUtil.commitTransaction();
            } catch (Exception e) {
                // 回滚事务
                ConnectionUtil.rollbackTransaction();
                throw e;
            } finally {
                flagContainer.remove();
            }
        } else {
            // 执行目标方法
            result = proxyChain.doProxyChain();
        }
        return result;
    }

}
