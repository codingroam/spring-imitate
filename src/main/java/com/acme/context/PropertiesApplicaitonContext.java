package com.acme.context;

import com.acme.bean.parse.PropertiesConfigParse;

/**
 * @author ：wk
 * @date ：Created in 2022/10/11 10:38 下午
 * @description：
 */
public class PropertiesApplicaitonContext implements ApplicationContext{

    private BeanFactory beanFactory;

    public PropertiesApplicaitonContext(String propertiesConfig) {
        beanFactory = new BeanFactory(this, new PropertiesConfigParse(), propertiesConfig);
        beanFactory.loadMvcHandleMapping();
    }

    @Override
    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return (T) beanFactory.getBean(requiredType);
    }
}
