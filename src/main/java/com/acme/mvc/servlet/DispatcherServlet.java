package com.acme.mvc.servlet;

import com.acme.Contants.ConfigConstants;
import com.acme.context.BeanFactory;
import com.acme.context.PropertiesApplicaitonContext;
import com.acme.mvc.pojo.Handler;
import com.acme.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author ：wk
 * @date ：Created in 2022/10/10 8:48 上午
 * @description：
 */
public class DispatcherServlet extends HttpServlet {

    // 存储url和Method之间的映射关系
    private List<Handler> handlerMapping = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化容器
        String initParameter = config.getInitParameter(ConfigConstants.CONTEXTCONFIGLOCATION);
        PropertiesApplicaitonContext propertiesApplicaitonContext = new PropertiesApplicaitonContext(initParameter);
        BeanFactory beanFactory = (BeanFactory) propertiesApplicaitonContext.getBean("beanFactory");
        handlerMapping = beanFactory.getHandlers();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 根据uri获取到能够处理当前请求的hanlder（从handlermapping中（list））
        Handler handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 not found");
        }
        // 参数绑定
        // 获取所有参数类型数组，这个数组的长度就是我们最后要传入的args数组的长度
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        // 根据上述数组长度创建一个新的数组（参数数组，是要传入反射调用的）
        Object[] paramValues = new Object[parameterTypes.length];
        // 以下就是为了向参数数组中塞值，而且还得保证参数的顺序和方法中形参顺序一致
        Map<String, String[]> parameterMap = req.getParameterMap();
        parameterMap.forEach((key, value) -> {
            String paramValue = StringUtils.join(value, ",");
            if (!handler.getParamIndexMapping().containsKey(key)) {
                return;
            }
            // 方法形参确实有该参数，找到它的索引位置，对应的把参数值放入paraValues
            Integer index = handler.getParamIndexMapping().get(key);
            paramValues[index] = paramValue;
        });
        Integer requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());
        paramValues[requestIndex] = req;
        Integer responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());
        paramValues[responseIndex] = resp;
        try {
            handler.getMethod().invoke(handler.getController(), paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler getHandler(HttpServletRequest req) {
        if (CollectionUtils.isEmpty(handlerMapping)) {
            return null;
        }
        String url = req.getRequestURI();
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }
}
