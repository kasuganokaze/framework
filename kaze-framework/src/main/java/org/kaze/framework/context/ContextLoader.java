package org.kaze.framework.context;

import org.kaze.framework.aop.AopHelper;
import org.kaze.framework.core.util.ClassUtil;
import org.kaze.framework.core.util.ConfigUtil;
import org.kaze.framework.bean.BeanHelper;
import org.kaze.framework.bean.IocHelper;
import org.kaze.framework.mvc.ControllerHelper;
import org.kaze.framework.orm.EntityHelper;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * 框架加载
 *
 * @author: kasuganokaze
 * @date: 2018/2/3
 */
public class ContextLoader {

    protected void initWebApplicationContext(ServletContext servletContext){
        //注册Servlet
        registerServlet(servletContext);
        //初始化相关Helper类
        initHelperClass();
    }

    private void registerServlet(ServletContext servletContext) {
        //注册处理JSP的Servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping("/index.jsp");
        jspServlet.addMapping(ConfigUtil.getViewPrefix() + "*");
        //注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigUtil.getStaticPath() + "*");
        defaultServlet.addMapping("/favicon.ico");
    }

    private void initHelperClass() {
        Class<?>[] classList = {
                BeanHelper.class,
                AopHelper.class,
                EntityHelper.class,
                ControllerHelper.class,
                IocHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }

}
