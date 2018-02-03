package org.kaze.framework.mvc.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet助手类
 *
 * @author kaze
 * @since 2017.9.2
 */
public final class WebServletContext {

    /**
     * 使每个线程独自拥有一份ServletHelper实例
     */
    private static final ThreadLocal<WebServletContext> servletContextHolder = new ThreadLocal<>();
    private HttpServletRequest request;
    private HttpServletResponse response;

    public WebServletContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * 初始化
     */
    public static void init(HttpServletRequest request, HttpServletResponse response) {
        servletContextHolder.set(new WebServletContext(request, response));
    }

    /**
     * 获取Request对象
     */
    public static HttpServletRequest getRequest() {
        return servletContextHolder.get().request;
    }

    /**
     * 获取Response对象
     */
    public static HttpServletResponse getResponse() {
        return servletContextHolder.get().response;
    }

    /**
     * 获取Session对象
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 从页面获取属性
     */
    public static String getParameter(String key) {
        return getRequest().getParameter(key);
    }

    /**
     * 销毁
     */
    public static void destroy() {
        servletContextHolder.remove();
    }

}
