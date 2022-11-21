package com.acme.context;

/**
 * @author ：wk
 * @date ：Created in 2022/10/4 11:37 下午
 * @description：
 */
public interface ApplicationContext {

    /**
     * 根据bean名称获取bean实例
     * @param name
     * @return
     * @throws Exception
     */
    Object getBean(String name);

    /**
     * 根据beanClass类型获取bean实例
     * @param requiredType
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T getBean(Class<T> requiredType);


}
