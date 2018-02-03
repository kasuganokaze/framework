package org.kaze.framework.mvc;

import org.apache.commons.lang3.ArrayUtils;
import org.kaze.framework.bean.BeanHelper;
import org.kaze.framework.bean.stereotype.Controller;
import org.kaze.framework.mvc.annotation.RequestMapping;
import org.kaze.framework.mvc.bean.Handler;
import org.kaze.framework.mvc.bean.RequestMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制器助手类
 *
 * @author kaze
 * @since 2017/08/31
 */
public final class ControllerHelper {

    /**
     * 用于存放请求与处理器的映射关系
     */
    private static final Map<String, Handler> actionMap = new HashMap<>();
    private static final Map<String, String> urlMappingMap = new HashMap<>();

    static {
        //获取所有Controller类
        Map<Class<?>, Object> controllerClassSet = BeanHelper.getBeanMap();
        for (Map.Entry<Class<?>, Object> entry : controllerClassSet.entrySet()) {
            Class<?> controllerClass = entry.getKey();
            if (!controllerClass.isAnnotationPresent(Controller.class)) {
                continue;
            }
            //先看类上是否有该注解
            String controllerRequestPath = "";
            if (controllerClass.isAnnotationPresent(RequestMapping.class)) {
                controllerRequestPath = controllerClass.getAnnotation(RequestMapping.class).value();
            }
            Method[] methods = controllerClass.getDeclaredMethods();
            for (Method method : methods) {
                //判断当前方法是否带有RequestMapping注解
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    String requestPath = controllerRequestPath + requestMapping.value();
                    //验证URL映射规则
                    if (requestPath.matches("(/\\{*\\w*\\}*)+")) {
                        // 获取方法类型
                        RequestMethod[] requestMethods = requestMapping.method();
                        // 未设置方法类型则默认匹配所有类型
                        if (ArrayUtils.isEmpty(requestMethods)) {
                            requestMethods = RequestMethod.values();
                        }
                        for (RequestMethod requestMethod : requestMethods) {
                            Handler handler = new Handler(controllerClass, method);
                            //初始化Action Map
                            actionMap.put(requestMethod.name() + requestPath, handler);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        String requestUrl = requestMethod + requestPath;
        Handler handler = actionMap.get(requestUrl);
        if (handler != null)
            return handler;
        String[] requestUrls = requestUrl.split("/");
        for (Map.Entry<String, Handler> entry : actionMap.entrySet()) {
            String[] actionUrls = entry.getKey().split("/");
            if (requestUrls.length == actionUrls.length) {
                handler = entry.getValue();
                for (int i = 0; i < requestUrls.length; i++) {
                    urlMappingMap.put(actionUrls[i], requestUrls[i]);
                    if (actionUrls[i].startsWith("{") && actionUrls[i].endsWith("}")) {
                        continue;
                    } else if (!requestUrls[i].equals(actionUrls[i])) {
                        handler = null;
                        urlMappingMap.clear();
                        break;
                    }
                }
                if (handler != null) {
                    break;
                }
            }
        }
        return handler;
    }

    public static Map<String, String> getUrlMappingMap() {
        return urlMappingMap;
    }

}
