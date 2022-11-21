package com.acme.utils;

import java.util.Collection;

/**
 * @author ：wk
 * @date ：Created in 2022/10/5 10:15 下午
 * @description：集合类型的数据处理工具类
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}
