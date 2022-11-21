package com.acme.test.dao.Impl;

import com.acme.annotation.Autowired;
import com.acme.annotation.Repository;
import com.acme.jdbc.DruidDataSourceManager;
import com.acme.test.dao.AccountDao;
import com.acme.test.pojo.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author ：wk
 * @date ：Created in 2022/10/31 10:15 上午
 * @description：
 */
@Repository
public class AccountDaoImpl implements AccountDao {

    @Autowired
    private DruidDataSourceManager druidDataSourceManager;

    @Override
    public Account queryByCardNo(String cardNo) throws Exception {
        Connection connection = druidDataSourceManager.getConnection();
        String sql = "select * from account where card_no=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, cardNo);
        ResultSet resultSet = preparedStatement.executeQuery();
        Account account = new Account();
        while (resultSet.next()) {
            account.setId(resultSet.getLong("id"));
            account.setName(resultSet.getString("name"));
            account.setCardNo(resultSet.getString("card_no"));
            account.setMoney(resultSet.getBigDecimal("money"));
        }
        resultSet.close();
        preparedStatement.close();
        return account;
    }

    @Override
    public int updateAccountByCardNo(Account account) throws Exception {
        Connection connection = druidDataSourceManager.getConnection();
        String sql = "update account set money=? where card_no=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setBigDecimal(1, account.getMoney());
        preparedStatement.setString(2, account.getCardNo());
        int i = preparedStatement.executeUpdate();
        if (i < 1) {
            throw new RuntimeException("更新失败");
        }
        return i;
    }
}
