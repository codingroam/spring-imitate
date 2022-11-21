package com.acme.bean.parse;

import com.acme.Contants.ContextConstants;
import com.acme.Contants.JdbcConstants;
import com.acme.bean.BeanDefinition;
import com.acme.jdbc.DatasourceProperty;
import com.acme.utils.ClassUtils;
import com.acme.utils.GenericBuilder;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ：wk
 * @date ：Created in 2022/10/11 10:21 下午
 * @description：解析properties文件
 */
public class PropertiesConfigParse implements BeanConfigParse{

    private BaseParse baseParse;

    private Properties properties = new Properties();

    public PropertiesConfigParse() {
        baseParse = new BaseParse();
    }

    @Override
    public List<BeanDefinition> parse(InputStream inputStream) {
        //加载properties配置文件
        doload(inputStream);
        //扫描包路径
        List<String> packageNameList = doScan();
        List<BeanDefinition> beanDefinitionList = baseParse.buildBeanDefinitionFromComponentScan(packageNameList);
        Map<String, BeanDefinition> beanDefinitionMap = beanDefinitionList.stream().collect(Collectors.toMap(BeanDefinition::getId, Function.identity(), (old, now) -> old));
        if (null == beanDefinitionMap.get(JdbcConstants.DATASOURCEPROPERTY)) {
            //构建数据源配置
            List<BeanDefinition.PropertyArg> propertyArgList = buildDataSourcePropertyArg();
            beanDefinitionList.add(GenericBuilder.of(BeanDefinition::new)
                    .with(BeanDefinition::setId, JdbcConstants.DATASOURCEPROPERTY)
                    .with(BeanDefinition::setClassName, DatasourceProperty.class.getName())
                    .with(BeanDefinition::setPropertyArgList, propertyArgList)
                    .with(BeanDefinition::setBeanClass, DatasourceProperty.class)
                    .build());
        }
        return beanDefinitionList;
    }

    private List<BeanDefinition.PropertyArg> buildDataSourcePropertyArg() {
        List<BeanDefinition.PropertyArg> propertyArgList = new LinkedList<>();
        //driverClassName
        String driverClassName = properties.getProperty(JdbcConstants.DRIVERCLASSNAME);
        propertyArgList.add(GenericBuilder.of(BeanDefinition.PropertyArg::new)
                .with(BeanDefinition.PropertyArg::setName, JdbcConstants.DRIVERCLASSNAME)
                .with(BeanDefinition.PropertyArg::setValue, driverClassName)
                .with(BeanDefinition.PropertyArg::setParentId, JdbcConstants.DATASOURCEPROPERTY)
                .build());
        //password
        String password = properties.getProperty(JdbcConstants.PASSWORD);
        propertyArgList.add(GenericBuilder.of(BeanDefinition.PropertyArg::new)
                .with(BeanDefinition.PropertyArg::setName, JdbcConstants.PASSWORD)
                .with(BeanDefinition.PropertyArg::setValue, password)
                .with(BeanDefinition.PropertyArg::setParentId, JdbcConstants.DATASOURCEPROPERTY)
                .build());
        //url
        String url = properties.getProperty(JdbcConstants.URL);
        propertyArgList.add(GenericBuilder.of(BeanDefinition.PropertyArg::new)
                .with(BeanDefinition.PropertyArg::setName, JdbcConstants.URL)
                .with(BeanDefinition.PropertyArg::setValue, url)
                .with(BeanDefinition.PropertyArg::setParentId, JdbcConstants.DATASOURCEPROPERTY)
                .build());
        //username
        String userName = properties.getProperty(JdbcConstants.USERNAME);
        propertyArgList.add(GenericBuilder.of(BeanDefinition.PropertyArg::new)
                .with(BeanDefinition.PropertyArg::setName, JdbcConstants.USERNAME)
                .with(BeanDefinition.PropertyArg::setValue, userName)
                .with(BeanDefinition.PropertyArg::setParentId, JdbcConstants.DATASOURCEPROPERTY)
                .build());
        return propertyArgList;
    }

    private List<String> doScan() {
        return ClassUtils.getClasses(properties.getProperty(ContextConstants.BASE_PACKAGE));
    }

    private void doload(InputStream inputStream) {
        try {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enableAnnotationScan(Class<?> clazz) {

    }


}
