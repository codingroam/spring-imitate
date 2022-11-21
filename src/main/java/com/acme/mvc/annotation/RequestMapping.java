package com.acme.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author ：wk
 * @date ：Created in 2022/10/10 9:59 下午
 * @description：映射参数
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";
}
