package com.acme.utils;

import com.acme.annotation.Component;

import java.sql.Connection;

/**
 * @author ：wk
 * @date ：Created in 2022/10/30 8:37 上午
 * @description：数据库连接工具类
 */
@Component
public class ConnectionUtils {

    /**
     * 存放本地连接
     */
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public ConnectionUtils getInstance() {
       return new ConnectionUtils();
    }

    /**
     * 从当前线程获取连接
     */
    public Connection getCurrentThreadLocal() {
        /**
         * 判断当前线程中是否已经绑定连接，如果没有绑定，需要从连接池获取一个连接绑定到 当前线程
         */
        Connection connection = null;
        try {
            connection = threadLocal.get();
            if (connection == null) {
                // 从连接池拿连接并绑定到线程
                connection = DruidUtils.getInstance().getConnection();
                // 绑定到当前线程
                threadLocal.set(connection);
            }
        } catch (Exception e) {

        }
        return connection;
    }
}
