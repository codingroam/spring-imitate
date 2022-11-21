package com.acme.bean.parse;

import com.acme.Contants.ContextConstants;
import com.acme.annotation.Component;
import com.acme.bean.BeanDefinition;
import com.acme.utils.GenericBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ：wk
 * @date ：Created in 2022/10/11 10:58 下午
 * @description：注解解析器
 */
public class BaseParse {

    /**
     * 1、读取解析xml，读取xml中的bean信息，通过反射技术实例化bean对象，然后放入map待用
     * 2、提供接口方法根据id从map中获取bean（静态方法）
     */
    public static ConcurrentHashMap<String, BeanDefinition> initBeanDefinition = new ConcurrentHashMap<>();

    protected List<BeanDefinition> buildBeanDefinitionFromComponentScan(List<String> packageNameList) {
        return Optional.of(packageNameList.stream().map(className-> {
            Class<?> aClass;
            try {
                aClass = Class.forName(className);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("注解解析异常");
            }
            //过滤掉注解
            if (aClass.isAnnotation()) {
                return null;
            }
            Component component = getComponent(aClass, Component.class);
            if (null == component) {
                return null;
            }
            String beanId;
            if ("".equals(component.value())) {
                //将类名的第一个字母转为小写
                beanId = getLowerFirst(className);
            } else {
                beanId = component.value();
            }
            //bean初始化
            beforeInitBeanDefinition(beanId);
            BeanDefinition beanDefinition = initBeanDefinition(className, beanId, aClass);
            afterInitBeanDefinition(beanDefinition);
            return beanDefinition;
        }).filter(Objects::nonNull).collect(Collectors.toList())).orElse(new ArrayList<>(0));
    }

    private String getLowerFirst(String className) {
        return className.substring(className.lastIndexOf(".") +1, className.lastIndexOf(".") +2).toLowerCase() + className.substring(className.lastIndexOf(".") +2);
    }

    private Component getComponent(Class<?> clazz, Class<Component> serviceClass) {
        Component annotation = clazz.getAnnotation(serviceClass);
        if (null != annotation) {
            return annotation;
        }
        for (Annotation anno : clazz.getAnnotations()) {
            Component component = anno.annotationType().getAnnotation(serviceClass);
            if (component != null) {
                try {
                    Method method = anno.annotationType().getMethod(ContextConstants.VALUE);
                    Object invoke = method.invoke(anno);
                    if (null == invoke || "".equals(invoke)) {
                        return component;
                    }
                    InvocationHandler invocationHandler = Proxy.getInvocationHandler(component);
                    Field membervalues = invocationHandler.getClass().getDeclaredField("membervalues");
                    membervalues.setAccessible(true);
                    Map<String, Object> map = (Map<String, Object>)membervalues.get(invocationHandler);
                    //将打了component注解打注解上打value值赋值到component上
                    map.put("value", String.valueOf(invoke));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("获取注解异常");
                }
            }
            return component;
        }
        return annotation;
    }

    protected void afterInitBeanDefinition(BeanDefinition beanDefinition) {
        initBeanDefinition.put(beanDefinition.getId(), beanDefinition);
    }

    protected void beforeInitBeanDefinition(String beanId) {
        if (null != initBeanDefinition.get(beanId)) {
            throw new RuntimeException(String.format("存在重复的bean,beanId为:%s", beanId));
        }
    }

    private BeanDefinition initBeanDefinition(String path, String beanId, Class<?> aClass) {
        return doInitBeanDefinition(path, beanId, aClass);
    }

    private BeanDefinition doInitBeanDefinition(String className, String beanId, Class<?> aClass) {
        return GenericBuilder.of(BeanDefinition::new)
                .with(BeanDefinition::setId, beanId)
                .with(BeanDefinition::setBeanClass, aClass)
                .with(BeanDefinition::setClassName, className)
                .build();
    }
}
