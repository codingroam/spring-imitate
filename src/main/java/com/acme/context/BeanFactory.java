package com.acme.context;

import com.acme.annotation.Autowired;
import com.acme.annotation.Transactional;
import com.acme.bean.BeanDefinition;
import com.acme.bean.parse.BeanConfigParse;
import com.acme.enums.ProxyTypeEnum;
import com.acme.mvc.annotation.Controller;
import com.acme.mvc.annotation.RequestMapping;
import com.acme.mvc.pojo.Handler;
import com.acme.proxy.ProxyFactory;
import com.acme.transantion.TransactionProxyCreate;
import com.acme.utils.CollectionUtils;
import com.acme.utils.GenericBuilder;
import com.acme.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ：wk
 * @date ：Created in 2022/10/29 11:32 下午
 * @description：实例化bean的工厂
 */
public class BeanFactory {

    /**
     * 1、读取解析xml，读取xml中的bean信息，通过反射技术实例化bean对象，然后放入map待用
     * 2、提供接口方法根据id从map中获取bean（静态方法）
     */
    private BeanConfigParse beanConfigParse;

    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>(16);

    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>(16);

    private final List<Handler> handlers = new ArrayList<>();

    public BeanFactory(ApplicationContext applicationContext, BeanConfigParse beanConfigParse) {
        registerContext(applicationContext);
        this.beanConfigParse = beanConfigParse;
    }

    public BeanFactory(ApplicationContext applicationContext, BeanConfigParse beanConfigParse, String configlocation) {
        registerContext(applicationContext);
        this.beanConfigParse = beanConfigParse;
        loadConfigurationDocument(configlocation);
    }

    private void registerContext(ApplicationContext applicationContext) {
        registerApplicationConxt(applicationContext);
        registerBeanFatory();
        registerProxyFactory();
    }

    private void registerProxyFactory() {
        String proxyFactoryId = "proxyFactory";
        singletonObjects.put(proxyFactoryId, new ProxyFactory());
    }

    private void registerBeanFatory() {
        String beanFactoryId = "beanFactory";
        singletonObjects.put(beanFactoryId, this);
    }

    private void registerApplicationConxt(ApplicationContext applicationContext) {
        String applicationContextId = "applicationContext";
        singletonObjects.put(applicationContextId, applicationContext);
    }


    public void beanDefinitionProcess(List<BeanDefinition> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            throw new RuntimeException("解析bean为空");
        }
        beans.forEach(beanDefinition -> {
            beanDefinitions.putIfAbsent(beanDefinition.getId(), beanDefinition);
            try {
                if (!beanDefinition.isLazyInit() && beanDefinition.isSingle()) {
                    singletonObjects.putIfAbsent(beanDefinition.getId(), beanDefinition.getBeanClass().newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        beans.forEach(beanDefinition -> {
            if (!beanDefinition.isLazyInit() && beanDefinition.isSingle()) {
                createBean(beanDefinition);
            }
        });
    }

    private void createBean(BeanDefinition beanDefinition) {
        doCreateBean(beanDefinition);
    }

    private void doCreateBean(BeanDefinition beanDefinition) {
        //实例化bean
        initstanceBean(beanDefinition);
        afterBeanInitialization(beanDefinition);
    }

    private void initstanceBean(BeanDefinition beanDefinition) {
        if (null != singletonObjects.get(beanDefinition.getId())) {
            return;
        }
        Object instance;
        try {
            instance = beanDefinition.getBeanClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("实例化单例对象异常");
        }
        if (beanDefinition.isSingle() && !beanDefinition.isLazyInit()) {
            singletonObjects.put(beanDefinition.getId(), instance);
        }
    }

    public Object getBean(String name) {
        if (singletonObjects.containsKey(name)) {
            return singletonObjects.get(name);
        }
        if (!beanDefinitions.containsKey(name)) {
            throw new RuntimeException(String.format("不存在该bean", name));
        }
        BeanDefinition beanDefinition = beanDefinitions.get(name);
        Object instance;
        try {
            instance = beanDefinition.getBeanClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("实例化多例对象失败");
        }
        //如果不存在单例池中的对象，被认为是多例对象，spring不帮忙管理，只帮忙创建
        return instance;
    }

    public Object getBean(Class<?> clazz) {
        return getBean(clazz, null);
    }

    private <T> Object getBean(Class<T> clazz, Object className) {
        return getBeanNameByType(clazz);
    }

    private <T> Object getBeanNameByType(Class<T> clazz) {
        List<String> assignableType = singletonObjects.keySet().stream().filter(cls -> clazz.isAssignableFrom(singletonObjects.get(cls).getClass())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(assignableType)) {
           return null;
        }
        return singletonObjects.get(assignableType.get(0));
    }

    /**
     * bean实例化后置处理
     * @param beanDefinition
     * @return
     */
    public void afterBeanInitialization(BeanDefinition beanDefinition) {
        Object instance = singletonObjects.get(beanDefinition.getId());
        if (instance == null) {
            return;
        }
        //1、处理是否开启注解
        Transactional transactional = beanDefinition.getBeanClass().getAnnotation(Transactional.class);
        if (null == transactional) {
            //2、方法上是否打了注解，现在暂时做成只要在方法上打了注解，整个类也可以生效，后续再改细力度
            transactional = Arrays.stream(beanDefinition.getBeanClass().getMethods()).map(method -> method.getAnnotation(Transactional.class)).filter(Objects::nonNull).findFirst().orElse(null);
        }
        //3、设置是否启动事物
        setEnableTransactional(transactional, beanDefinition);
        //将使用set注入的bean赋值
        setConstructorToBean(beanDefinition, instance);
        //3、处理依赖注入Autowired,给实例对象赋值
        setAutowired(beanDefinition, instance);
        //4、给实例对象赋值
        setValueToBean(beanDefinition, instance);
    }

    public void setProxyFactoryValue() {
        Object proxyFactory = singletonObjects.get("proxyFactory");
        Arrays.stream(proxyFactory.getClass().getDeclaredFields()).forEach(field -> {
            field.setAccessible(Boolean.TRUE);
            try {
                field.set(proxyFactory, singletonObjects.get(field.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        singletonObjects.put("proxyFactory", proxyFactory);
    }

    private void setConstructorToBean(BeanDefinition beanDefinition, Object instance) {
        if (CollectionUtils.isNotEmpty(beanDefinition.getPropertyArgList())) {
            beanDefinition.getPropertyArgList().stream().filter(propertyArg -> StringUtils.isNotEmpty(propertyArg.getRef())).forEach(ref -> {
                Arrays.stream(instance.getClass().getMethods()).forEach(method -> {
                    if (method.getName().equalsIgnoreCase("set" + ref.getRef())) {
                        try {
                            method.invoke(instance, singletonObjects.get(ref.getRef()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
            //赋值完毕之后会写到单例池中
            singletonObjects.put(beanDefinition.getId(), instance);
        }
    }

    private Object setProxy(BeanDefinition beanDefinition, Object instance) {
        if (!beanDefinition.isEnableTransaction()) {
           return instance;
        }
        ProxyFactory proxyFactory = (ProxyFactory) singletonObjects.get("proxyFactory");
        if (beanDefinition.getBeanClass().getInterfaces().length == 0) {
            //没实现接口，就走cglib动态代理,反之走jdk动态代理
            TransactionProxyCreate proxyFactoryInstance = proxyFactory.getInstance(ProxyTypeEnum.CGLIB.getType());
            return proxyFactoryInstance.proxy(instance);
        }
        return proxyFactory.getInstance(ProxyTypeEnum.JDK.getType()).proxy(instance);
    }

    private void setValueToBean(BeanDefinition beanDefinition, Object instance) {
        Map<String, BeanDefinition.PropertyArg> refNameMap = beanDefinition.getPropertyArgList().stream().collect(Collectors.toMap(BeanDefinition.PropertyArg::getName, Function.identity(), (old, now) -> old));
        if (refNameMap.isEmpty()) {
            return;
        }
        if (instance.getClass().getDeclaredFields().length == 0) {
            return;
        }
        for (Field field : instance.getClass().getDeclaredFields()) {
            BeanDefinition.PropertyArg propertyArg = refNameMap.get(field.getName());
            if (null == propertyArg) {
                continue;
            }
            field.setAccessible(Boolean.TRUE);
            try {
                field.set(instance, propertyArg.getRef() == null ? propertyArg.getValue() : singletonObjects.get(propertyArg.getRef()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setAutowired(BeanDefinition beanDefinition, Object instance) {
        //获取字段上的注解，暂时只支持获取字段 todo
        List<Field> fieldList = Arrays.stream(beanDefinition.getBeanClass().getDeclaredFields()).filter(field -> null != field.getAnnotation(Autowired.class)).collect(Collectors.toList());
        //将打上注解的字段赋值到beanDefinition到property标签上
        if (CollectionUtils.isNotEmpty(fieldList)) {
            fieldList.forEach(field -> {
                try {
                    field.setAccessible(Boolean.TRUE);
                    //将依赖注入
                    field.set(instance, getBean(field.getType()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        //通过内省给数据源赋值
        if ("datasourceProperty".equals(beanDefinition.getId())) {
            beanDefinition.getPropertyArgList().forEach(propertyArg -> {
                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyArg.getName(), instance.getClass(),
                            "get" + toUpperCase(propertyArg.getName()), "set" + toUpperCase(propertyArg.getName()));
                    propertyDescriptor.getWriteMethod().invoke(instance, propertyArg.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("使用内省异常");
                }
            });
        }
        //
    }

    private String toUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void setEnableTransactional(Transactional transactional, BeanDefinition beanDefinition) {
        if (null == transactional) {
            return;
        }
        beanDefinition.setEnableTransaction(Boolean.TRUE);
    }

    public void createAutoProxy(List<BeanDefinition> beanDefinitions) {
        doCreateAutoProxy(beanDefinitions);
    }

    private void doCreateAutoProxy(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(beanDefinition -> {
            //5、判断是否需要被代理,代理类型cglib或者是jdk
            Object proxy = setProxy(beanDefinition, singletonObjects.get(beanDefinition.getId()));
            //6、将代理类对象替换原来的对象
            singletonObjects.put(beanDefinition.getId(), proxy);
        });
    }

    public void loadMvcHandleMapping() {
        //遍历ioc容器里面的对象
        singletonObjects.forEach((beanId, beanValue) -> {
            StringBuilder baseurl = new StringBuilder();
            Class<?> aClass = beanValue.getClass();
            //过滤打了@Controller注解打对象,并判断是否有打上@RequestMapping注解，如果有，就把value()值获取出来拼接上
            if (!aClass.isAnnotationPresent(Controller.class)) {
                return;
            }
            RequestMapping classAnnotation = aClass.getAnnotation(RequestMapping.class);
            if (null != classAnnotation) {
                baseurl.append(classAnnotation.value());
            }
            //遍历方法，获取每个方法打参数和请求路径
            Arrays.stream(aClass.getMethods()).forEach(method -> {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    return;
                }
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                baseurl.append(methodAnnotation.value());
                //将controller、url、method封装成handler对象
                Handler handler = GenericBuilder.of(Handler::new)
                        .with(Handler::setController, beanValue)
                        .with(Handler::setMethod, method)
                        .with(Handler::setPattern, Pattern.compile(baseurl.toString()))
                        .build();
                // 计算方法的参数位置信息  // query(HttpServletRequest request, HttpServletResponse response,String name)
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].getType() == HttpServletRequest.class || parameters[i].getType() == HttpServletResponse.class) {
                        handler.getParamIndexMapping().put(parameters[i].getType().getSimpleName(), i);
                    } else {
                        handler.getParamIndexMapping().put(parameters[i].getName(), i);
                    }
                }
                handlers.add(handler);
            });
        });
    }

    protected void loadConfigurationDocument(String applicationName) {
        InputStream resourceAsStream = ClassPathXmlApplicationContext.class.getClassLoader().getResourceAsStream(applicationName);
        if (null == resourceAsStream) {
            throw new RuntimeException("文件路径为空");
        }
        //解析bean
        List<BeanDefinition> beanDefinitions = beanConfigParse.parse(resourceAsStream);
        beanDefinitionProcess(beanDefinitions);
        //给代理工厂赋值, 代理工厂父类
        setProxyFactoryValue();
        createAutoProxy(beanDefinitions);
    }

    protected <T> void loadConfigurationDocument(Class<T> className, String applicationName){
        beanConfigParse.enableAnnotationScan(className);
        loadConfigurationDocument(applicationName);
    }

    public List<Handler> getHandlers() {
        return handlers;
    }
}
