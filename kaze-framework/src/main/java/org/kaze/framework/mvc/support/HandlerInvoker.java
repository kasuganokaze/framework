package org.kaze.framework.mvc.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.kaze.framework.core.util.ConfigUtil;
import org.kaze.framework.ioc.BeanHelper;
import org.kaze.framework.mvc.annotation.ResponseBody;
import org.kaze.framework.mvc.bean.Handler;
import org.kaze.framework.mvc.bean.ModelAndView;
import org.kaze.framework.mvc.bean.Param;
import org.kaze.framework.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handler控制器
 *
 * @author kaze
 * @since 2017/09/05
 */
public class HandlerInvoker {

    /**
     * 执行请求
     */
    public static void invoke(HttpServletRequest request, HttpServletResponse response, Handler handler) throws Exception {
        //获取Controller类以及方法名
        Class<?> controllerClass = handler.getControllerClass();
        Method actionMethod = handler.getActionMethod();
        //获取Bean实例
        Object controllerBean = BeanHelper.getBean(controllerClass);
        //创建请求参数对象
        Param param = new Param(request, response, controllerClass, actionMethod);
        //调用方法获取结果
        actionMethod.setAccessible(true);
        Object result = actionMethod.invoke(controllerBean, param.getValues());
        //处理结果
        if (actionMethod.isAnnotationPresent(ResponseBody.class) || controllerClass.isAnnotationPresent(ResponseBody.class)) {
            handleBodyResult(result, response);
        } else {
            handleViewResult(result, request, response);
        }
    }

    /**
     * 返回页面
     */
    private static void handleViewResult(Object result, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view;
        Map<String, Object> modelMap = null;
        if (result instanceof ModelAndView) {
            ModelAndView mav = (ModelAndView) result;
            view = mav.getView();
            modelMap = mav.getModelMap();
        } else {
            view = result.toString();
        }
        if (StringUtil.isNotEmpty(view)) {
            if (view.startsWith("/")) {
                response.sendRedirect(request.getContextPath() + view);
            } else {
                if (MapUtils.isNotEmpty(modelMap)) {
                    for (Map.Entry<String, Object> entry : modelMap.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
                request.getRequestDispatcher(ConfigUtil.getViewPrefix() + view + ConfigUtil.getViewSuffix()).forward(request, response);
            }
        }
    }

    /**
     * 返回数据
     */
    private static void handleBodyResult(Object result, HttpServletResponse response) throws IOException {
        if (result != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(result);
            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            PrintWriter pw = response.getWriter();
            pw.write(json);
            pw.flush();
            pw.close();
        }
    }

}
