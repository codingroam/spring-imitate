package com.acme.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author ：wk
 * @date ：Created in 2022/10/30 8:31 上午
 * @description：配置数据库参数工具类
 */
public class DruidUtils {

    private static DruidDataSource druidDataSource = new DruidDataSource();

    static {
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/spring");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
    }
    public static DruidDataSource getInstance() {
        return druidDataSource;
    }
}
