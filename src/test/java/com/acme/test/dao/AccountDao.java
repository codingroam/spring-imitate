package com.acme.test.dao;


import com.acme.test.pojo.Account;

/**
 * @author ：wk
 * @date ：Created in 2022/10/29 11:54 下午
 * @description：
 */
public interface AccountDao {

    Account queryByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;
}
