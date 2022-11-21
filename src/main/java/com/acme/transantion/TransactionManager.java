package com.acme.transantion;

import java.sql.SQLException;

/**
 * @author ：wk
 * @date ：Created in 2022/10/30 8:48 上午
 * @description：事物管理器
 */
public interface TransactionManager {

    /**
     * 开启事物
     */
    void beginTransaction() throws SQLException;

    /**
     * 提交事物
     * @throws SQLException
     */
    void comcommit() throws SQLException;

    /**
     * 回滚事物
     * @throws SQLException
     */
    void rollback() throws SQLException;

}
