package com.acme.bean.parse;

import com.acme.Contants.ContextConstants;
import com.acme.annotation.ComponentScan;
import com.acme.bean.BeanDefinition;
import com.acme.utils.ClassUtils;
import com.acme.utils.CollectionUtils;
import com.acme.utils.GenericBuilder;
import com.acme.utils.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：wk
 * @date ：Created in 2022/10/5 8:53 下午
 * @description：
 */
public class XmlBeanConfigParse implements BeanConfigParse{

    /**
     * 是否使用@ComponentScan注解
     */
    private static boolean enableComponentScan = Boolean.FALSE;

    /**
     * 注解扫包路径
     */
    private static String componentScanPackage;

    private BaseParse baseParse;

    public XmlBeanConfigParse() {
        baseParse = new BaseParse();
    }


    @Override
    public void enableAnnotationScan(Class<?> clazz) {
        ComponentScan annotation = clazz.getAnnotation(ComponentScan.class);
        if (null == annotation) {
            return;
        }
        String basePackageValue;
        try {
            Method method = annotation.annotationType().getMethod(ContextConstants.BASE_PACKAGE);
            basePackageValue = (String) method.invoke(annotation);
            if (StringUtils.isEmpty(basePackageValue)) {
                basePackageValue = clazz.getPackage().getName();
            }
            componentScanPackage = basePackageValue;
            enableComponentScan = Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析注解参数异常");
        }

    }


    @Override
    public List<BeanDefinition> parse(InputStream inputStream) {
        try {
            Document document = new SAXReader().read(inputStream);
            Element rootElement = document.getRootElement();
            //获取配置所有的bean标签
            List<BeanDefinition> beanDefinitionList = beanParse(rootElement);
            //获取配置文件的component-scan标签下的扫包范围，以及打上@ComponentScan注解的扫包范围，
            // 并且将@component,@service,@Repository注解的上的类加载进bean里面,在配置文件配置packageName,所以这里packageName为空
            List<BeanDefinition> configurationList;
            if (enableComponentScan) {
                configurationList = ConfigurationParse(rootElement, componentScanPackage);
            } else {
                configurationList = ConfigurationParse(rootElement, null);
            }
            if (CollectionUtils.isNotEmpty(beanDefinitionList)) {
                beanDefinitionList.addAll(configurationList);
                return beanDefinitionList;
            }
            return configurationList;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<BeanDefinition> ConfigurationParse(Element rootElement, String packageName) {
        List<Element> elements = rootElement.selectNodes("//component-scan");
        //扫描包下返回的class类路径集合
        List<String> packagePath;
        packagePath = Optional.ofNullable(elements).orElse(new ArrayList<>()).stream().flatMap(element -> {
            String basePackage = element.attributeValue(ContextConstants.BASE_PACKAGE);
            if (StringUtils.isEmpty(basePackage)) {
                throw new RuntimeException("解析文件异常");
            }
            return ClassUtils.getClasses(basePackage).stream();
        }).collect(Collectors.toList());
        //如果配置文件没有配置，就去获取传进来的启动类上的配置，如果找不到，直接返回空集合，如果两边都配置了扫包，则以配置文件为主
        if (CollectionUtils.isEmpty(packagePath) && StringUtils.isNotEmpty(packageName)) {
            packagePath = ClassUtils.getClasses(packageName);
        }
        if (CollectionUtils.isEmpty(packagePath)) {
            return new ArrayList<>();
        }
        return baseParse.buildBeanDefinitionFromComponentScan(packagePath);
    }

    /**
     * 根据properties里面的扫包路径扫描所有注入容器的bean
     * @param basePackage
     * @return
     */
    private List<BeanDefinition> PropertiesParse(String basePackage) {
        if (StringUtils.isEmpty(basePackage)) {
            return new ArrayList<>();
        }
        return baseParse.buildBeanDefinitionFromComponentScan(ClassUtils.getClasses(basePackage));
    }

    private List<BeanDefinition> beanParse(Element rootElement) {
        BeanDefinition beanDefinition = new BeanDefinition();
        //获取beans跟节点下的bean节点
        List<Element> beanList = rootElement.selectNodes("//bean");
        if (CollectionUtils.isEmpty(beanList)) {
            return new ArrayList<>(0);
        }
        //获取全部property节点
        List<Element> propertyList = rootElement.selectNodes("//property");
        //封装property参数 key为property的parentId，即beanId, value为List<Property>
        Map<String, List<BeanDefinition.PropertyArg>> propertyMap = getPropertyMap(propertyList);
        return beanList.stream().map(element -> buildBeanDefinition(element, propertyMap)).collect(Collectors.toList());
    }

    public Map<String, List<BeanDefinition.PropertyArg>> getPropertyMap(List<Element> elementList) {
        return Optional.ofNullable(elementList).map(pro -> pro.stream().map(this::buildPropertyArg).collect(Collectors.groupingBy(BeanDefinition.PropertyArg::getParentId))).orElse(new HashMap<>(0));
    }

    public BeanDefinition.PropertyArg buildPropertyArg(Element element) {
        BeanDefinition.PropertyArg propertyArg = new BeanDefinition.PropertyArg();
        String name = element.attributeValue(ContextConstants.NAME);
        String ref = element.attributeValue(ContextConstants.REF);
        String value = element.attributeValue(ContextConstants.VALUE);
        Element parent = element.getParent();
        String parentId = parent.attributeValue(ContextConstants.ID);
        propertyArg.setName(name);
        propertyArg.setRef(ref);
        propertyArg.setValue(value);
        propertyArg.setParentId(parentId);
        return propertyArg;
    }

    public BeanDefinition buildBeanDefinition(Element element, Map<String, List<BeanDefinition.PropertyArg>> propertyMap) {
        //通过bean节点获取里面的id属性和class属性
        String id = element.attributeValue(ContextConstants.ID);
        String className = element.attributeValue(ContextConstants.CLAZZ);
        Class<?> beanClass;
        try {
            //通过反射获取class属性对应的对象
            beanClass = Class.forName(className);
            //todo 先不处理bean的参数
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析类型错误或者类型为空！");
        }
        BeanDefinition beanDefinition = GenericBuilder.of(BeanDefinition::new)
                .with(BeanDefinition::setId, id)
                .with(BeanDefinition::setClassName, className)
                .with(BeanDefinition::setBeanClass, beanClass)
                .build();
        String lazyInit = element.attributeValue(ContextConstants.LAZY_INIT);
        if (StringUtils.isNotEmpty(lazyInit)) {
            beanDefinition.setLazyInit(Boolean.getBoolean(lazyInit));
        }
        String scope = element.attributeValue(ContextConstants.SCOPE);
        if (StringUtils.isNotEmpty(scope)) {
            beanDefinition.setScope(BeanDefinition.Scope.valueOf(scope));
        }
        if (!propertyMap.isEmpty()) {
            beanDefinition.setPropertyArgList(propertyMap.get(id));
        }
        baseParse.beforeInitBeanDefinition(beanDefinition.getId());
        baseParse.afterInitBeanDefinition(beanDefinition);
        return beanDefinition;
    }
}
