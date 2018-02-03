package org.kaze.framework.mvc;

import org.kaze.framework.mvc.bean.Handler;
import org.kaze.framework.mvc.support.HandlerInvoker;
import org.kaze.framework.mvc.support.WebServletContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求转发器
 *
 * @author kaze
 * @since 2017/08/31
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置请求编码方式
        request.setCharacterEncoding("utf-8");
        //获取请求方法与请求路径
        String requestMethod = request.getMethod();
        String requestPath = request.getPathInfo();
        //默认到index.jsp
        if (requestPath.equals("/")) {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
        //去掉末尾的"/"
        if (requestPath.endsWith("/")) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }
        //获取Handler
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
        if (handler == null) {
            return;
        }
        //将Servlet绑定到当前线程
        WebServletContext.init(request, response);
        try {
            //执行请求
            HandlerInvoker.invoke(request, response, handler);
        } catch (Exception e) {
            System.out.println("service执行出错");
        } finally {
            //销毁当前线程的Servlet
            WebServletContext.destroy();
        }
    }

}
