package org.kaze.framework.mvc.bean;

import java.lang.reflect.Method;

/**
 * 封装请求信息
 *
 * @author kaze
 * @since 2017/08/31
 */
public class Handler {

    private Class<?> controllerClass;

    private Method actionMethod;

    public Handler(Class<?> controllerClass, Method actionMethod) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

}
