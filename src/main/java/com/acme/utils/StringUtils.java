package com.acme.utils;

/**
 * @author ：wk
 * @date ：Created in 2022/10/5 10:15 下午
 * @description：对String类型的数据处理工具类
 */
public class StringUtils {

    public static boolean isEmpty(String value) {
        return null == value || value.length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }
}
