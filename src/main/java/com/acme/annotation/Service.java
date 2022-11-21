package com.acme.annotation;

import java.lang.annotation.*;

/**
 * @author ：wk
 * @date ：Created in 2022/10/5 12:37 上午
 * @description：
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

    String value() default "";
}
