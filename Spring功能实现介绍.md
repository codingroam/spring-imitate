# 手写spring功能介绍:
## 通过ioc容器创建bean，管理bean,aop功能实现事物统一管理,集成druid数据源
## 一、ioc容器
    1、支持xml方式配置bean
    2、支持扫包，包括xml和@componentScan
    2、支持注解方式（@Service、@Component、@Repository）配置bean
    3、支持@AutoWired注解进行DI依赖注入,暂时只支持名称匹配
    4、bean支持作用域单例多例，懒加载
## 二、aop
    1、支持@Transactional注解可进行动态代理
    2、支持自动选择代理方式，jdk或者cglib
## 三、动态配置数据源,集成Druid数据源
## 四、mvc
    1、增加@Controller、@RequestMapping注解
    2、新增diapatcherServlet前端控制器
