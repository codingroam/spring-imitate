package com.acme.annotation;

import java.lang.annotation.*;

/**
 * @author ：wk
 * @date ：Created in 2022/10/7 12:49 下午
 * @description：启用事物注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {

    Class<? extends Exception>[] rollbackFor() default {RuntimeException.class};
}
