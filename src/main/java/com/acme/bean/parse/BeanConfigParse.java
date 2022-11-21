package com.acme.bean.parse;

import com.acme.bean.BeanDefinition;

import java.io.InputStream;
import java.util.List;

/**
 * @author ：wk
 * @date ：Created in 2022/10/5 8:53 下午
 * @description：Bean配置解析器
 */
public interface BeanConfigParse {

    /**
     * 将文件输入流转换成bean
     * @param inputStream 文件输入流
     * @return bean对象定义
     */
    List<BeanDefinition> parse(InputStream inputStream);

    /** todo 暂时先写到这里，后面优化成后置处理器操作
     * 是否启动注解扫包
     * @param clazz
     */
    void enableAnnotationScan(Class<?> clazz);
}
