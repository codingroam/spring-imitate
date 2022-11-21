package com.acme.mvc.annotation;

import com.acme.annotation.Component;

import java.lang.annotation.*;

/**
 * @author ：wk
 * @date ：Created in 2022/10/10 11:15 下午
 * @description：
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {

    String value() default "";
}
