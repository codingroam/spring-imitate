package com.acme.context;

import com.acme.bean.parse.XmlBeanConfigParse;

/**
 * @author ：wk
 * @date ：Created in 2022/10/4 11:17 下午
 * @description：
 */
public class ClassPathXmlApplicationContext implements ApplicationContext {

    /**
     * bean工厂
     */
    private BeanFactory beanFactory;

    /**
     * 实例化ClassPathXmlApplicationContext
     * @param configlocation 配置文件路径
     */
    public ClassPathXmlApplicationContext(String configlocation) {
        beanFactory = new BeanFactory(this, new XmlBeanConfigParse(), configlocation);
        beanFactory.loadMvcHandleMapping();
    }

    /**
     * 实例化ClassPathXmlApplicationContext
     * @param className 配置启动类
     * @param configlocation 配置问津路径
     * @param <T>
     */
    public <T> ClassPathXmlApplicationContext(Class<T> className, String configlocation) {
        beanFactory = new BeanFactory(this, new XmlBeanConfigParse());
        beanFactory.loadConfigurationDocument(className, configlocation);
    }

    @Override
    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType){
        return (T) beanFactory.getBean(requiredType);
    }
}
