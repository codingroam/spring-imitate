package com.acme.mvc.pojo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author ：wk
 * @date ：Created in 2022/10/10 10:04 下午
 * @description： 封装handler方法相关信息
 */
public class Handler {

    /**
     * 打了注解的Controller
     */
    private Object Controller;

    /**
     * 打了RequestMapping注解打方法
     */
    private Method method;

    /**
     * 支持正则表达式打url
     */
    private Pattern pattern;

    /**
     * 参数顺序，为了绑定参数，key是参数名称，value是参数位置
     */
    private Map<String, Integer> paramIndexMapping = new HashMap<>();

    public Object getController() {
        return Controller;
    }

    public void setController(Object controller) {
        Controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
        this.paramIndexMapping = paramIndexMapping;
    }
}
