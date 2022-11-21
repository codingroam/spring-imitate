package com.acme.jdbc;


import com.acme.annotation.Autowired;
import com.acme.transantion.TransactionManager;
import com.acme.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ：wk
 * @date ：Created in 2022/10/7 11:50 上午
 * @description：集成druid数据源
 */
//@Component
public class DruidDataSourceManager implements TransactionManager {

    @Autowired
    private ConnectionUtils connectionUtils;

    @Autowired
    private DatasourceProperty datasourceProperty;

    public void setDatasourceProperty(DatasourceProperty datasourceProperty) {
        if (null == datasourceProperty) {
            throw new RuntimeException("数据源为空");
        }
        if (null == datasourceProperty.getDriverClassName()) {
            throw new RuntimeException("请配置链接驱动地址");
        }
        if (null == datasourceProperty.getUrl()) {
            throw new RuntimeException("请配置连接url");
        }
        if (null == datasourceProperty.getUsername()) {
            throw new RuntimeException("请配置用户名");
        }
        if (null == datasourceProperty.getPassword()) {
            throw new RuntimeException("请配置密码");
        }
        this.datasourceProperty = datasourceProperty;
    }

    public Connection getConnection() {
       return connectionUtils.getCurrentThreadLocal();
    }
    /**
     * 开启事物
     */
    @Override
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadLocal().setAutoCommit(false);
    }

    /**
     * 提交事物
     * @throws SQLException
     */
    @Override
    public void comcommit() throws SQLException {
        connectionUtils.getCurrentThreadLocal().commit();
    }

    /**
     * 回滚事物
     * @throws SQLException
     */
    @Override
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadLocal().rollback();
    }
}
